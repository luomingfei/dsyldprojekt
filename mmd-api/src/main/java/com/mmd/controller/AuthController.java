package com.mmd.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mmd.IpUtil;
import com.mmd.RandomUtils;
import com.mmd.domain.dto.input.ManagerAddInputDTO;
import com.mmd.domain.dto.input.ManagerQueryInputDTO;
import com.mmd.domain.dto.output.ManagerOutputDTO;
import com.mmd.domain.dto.user.AppUserLoginDTO;
import com.mmd.domain.dto.user.MpUserLoginDTO;
import com.mmd.domain.dto.user.UserLoginDTO;
import com.mmd.entity.*;
import com.mmd.persistence.ManagerPersistence;
import com.mmd.service.*;
import com.mmd.utils.DateUtils;
import com.mmd.utils.TextMessageUtil;
import com.mmd.vo.user.JwtTokenResponseVO;
import com.mmd.vo.user.LoginResponseVO;
import com.uranus.security.annotation.PreAuthorize;
import com.uranus.security.jwt.JwtOperator;
import com.uranus.security.jwt.User;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

/**
 * @author qiWen.xue
 * @date 2020-03-09 20:49
 */
@Validated
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class  AuthController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final WxMaService wxMaService;
    private final ManagerService managerService;
    private final JwtOperator jwtOperator;
    private final UserService userService;
    private final OrderService orderService;
    private final OpenidService openidService;
    private final WxMpService wxMpService;
    private final VerificationCodeService verificationCodeService;


    @GetMapping("/qrAuthorize")
    @ApiOperation(value = "?????????????????????", notes = "?????????????????????")
    public GlobalResult publicLogin(@RequestParam String code) {
        try {
            WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
            WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
            if (wxMpUser != null) {
                Openid openid = openidService.findByOpenId(wxMpUser.getOpenId());
                if (openid == null) {
                    openidService.AddOpenId(
                            Openid.builder()
                                    .openid(wxMpUser.getOpenId())
                                    .miniappId("")
                                    .headimgurl(wxMpUser.getHeadImgUrl())
                                    .city(wxMpUser.getCity())
                                    .country(wxMpUser.getCountry())
                                    .sex(wxMpUser.getSex())
                                    .province(wxMpUser.getProvince())
                                    .build(),
                            (System.currentTimeMillis() + "").substring(0, 10)
                    );
                }
            }
            return GlobalResult.ok(wxMpUser);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return GlobalResult.exception();
    }

    public static boolean isMobileDevice(HttpServletRequest request) {
        String requestHeader = request.getHeader("user-agent").toLowerCase();
        String[] deviceArray = new String[]{"android", "iphone", "ios", "windows phone"};
        if (requestHeader == null) {
            return false;
        }
        requestHeader = requestHeader.toLowerCase();
        for (int i = 0; i < deviceArray.length; i++) {
            if (requestHeader.indexOf(deviceArray[i]) > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * android : ??????android?????? mac os : iphone ipad windows
     * phone:Nokia???windows???????????????
     * @param requestHeader ?????????
     * @return
     */
    public static String getDevice(HttpServletRequest request) {
        String requestHeader = request.getHeader("user-agent").toLowerCase();
        String[] deviceArray = new String[]{"android", "iphone", "ios", "windows phone"};
        if (requestHeader == null) {
            return "PC???";
        }
        requestHeader = requestHeader.toLowerCase();
        for (int i = 0; i < deviceArray.length; i++) {
            if (requestHeader.indexOf(deviceArray[i]) > 0) {
                return "WAP???";
            }
        }
        return "PC???";
    }

    /**
     * ??????????????????????????????
     */
    @GetMapping("sendTextMessage")
    @ApiOperation(value = "????????????????????????", notes = "????????????????????????", response = GlobalResult.class)
    public GlobalResult sendTextMessage(@RequestParam String mobile,HttpServletRequest request) {
        if (!isMobileDevice(request)) {
            return GlobalResult.errorException("????????????!");
        }
        if(StringUtils.isBlank(mobile)){
            return GlobalResult.errorException("??????????????????");
        }
        //???????????????24?????????????????????
        int mobileCount = verificationCodeService.getMobileCount(mobile);
        if(mobileCount>10){
            return GlobalResult.errorException("????????????!");
        }
        String ip = IpUtil.getIpAddr(request);
        //??????ip 24?????????????????????
        int ipCount = verificationCodeService.getIpCount(ip);
        if(ipCount>10){
            return GlobalResult.errorException("????????????!");
        }
        Long minutes = null;
        VerificationCode verificationCode = verificationCodeService.getMaxCodeByMobile(mobile);
        if (verificationCode != null) {
            minutes = DateUtils.computeBetween(verificationCode.getCreateDate().toInstant(), Instant.now());
        }
        if (minutes == null || minutes >= 1) {
            String code = RandomUtils.createRandom(true, 6);
            VerificationCode verificationCodeEntity = new VerificationCode();
            verificationCodeEntity.setMobile(mobile);
            verificationCodeEntity.setVerificationCode(code);
            verificationCodeEntity.setCreateDate(new Date());
            verificationCodeEntity.setIp(ip);
            verificationCodeService.save(verificationCodeEntity);
            String msg = "????????????????????????????????????" + code + "???5??????????????????";
            boolean needstatus = true;
            String product = null;
            String extno = null;
            try {
                TextMessageUtil.batchSend(mobile, msg, needstatus, product, extno);
                return GlobalResult.ok();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return GlobalResult.errorException("???????????????????????????????????????");
    }

    @PostMapping("/mpAuthUser")
    @ApiOperation(value = "?????????????????????", notes = "????????????")
    @ApiImplicitParam(name = "mpUserLoginDTO", value = "????????????????????????????????????", required = true, paramType = "body", dataType = "MpUserLoginDTO", dataTypeClass = MpUserLoginDTO.class)
    public GlobalResult mpLogin(@Valid @RequestBody MpUserLoginDTO mpUserLoginDTO, BindingResult bindingResult) {
        paramsValidate(mpUserLoginDTO, bindingResult);
        String userName = mpUserLoginDTO.getMobile();
        VerificationCode verificationCode = verificationCodeService.getMaxCodeByMobile(userName);
        if (verificationCode == null) {
            return GlobalResult.errorMsg("???????????????????????????!");
        }
        Long minutes = DateUtils.computeBetween(verificationCode.getCreateDate().toInstant(), Instant.now());
        if (minutes > 5) {
            return GlobalResult.errorMsg("?????????????????????????????????!");
        }
        if (mpUserLoginDTO.getCode().equals(verificationCode.getVerificationCode())) {
            final String token = jwtOperator.generateToken(
                    User.builder()
                            .username(userName)
                            .build()
            );
            // ????????????
            String type = "1";
            String tid = this.userService.findUserType(mpUserLoginDTO.getMobile());
            if (tid != null) {
                type = "2";
            }
            String uid = this.userService.findUserIdByName(userName);
            if (StringUtils.isNotBlank(uid)) {
                // ??????????????????
                boolean blackUser = userService.isBlackUser(uid);
                Openid openid = openidService.findByUid(Integer.valueOf(uid));
                if(openid==null){
                    int count = this.userService.updateOpenidByUid(Integer.parseInt(uid), mpUserLoginDTO.getOpenid());
                    if(count<1){
                        openidService.AddOpenId(
                                Openid.builder()
                                        .openid(mpUserLoginDTO.getOpenid())
                                        .uid(Integer.valueOf(uid))
                                        .build(),
                                (System.currentTimeMillis() + "").substring(0, 10)
                        );
                    }
                }else{
                    if (!openid.getOpenid().equals(mpUserLoginDTO.getOpenid())&&!openid.getOpenid().equals("1")) {
                        this.openidService.clearUidByOpenid(openid.getOpenid());
                        int count = this.userService.updateOpenidByUid(Integer.parseInt(uid), mpUserLoginDTO.getOpenid());
                        if(count<1){
                            openidService.AddOpenId(
                                    Openid.builder()
                                            .openid(mpUserLoginDTO.getOpenid())
                                            .build(),
                                    (System.currentTimeMillis() + "").substring(0, 10)
                            );
                        }
                    }
                    if(openid.getOpenid().equals("1")){
                        this.userService.UpdateUidBynewOpenid(Integer.parseInt(uid),mpUserLoginDTO.getOpenid());
                    }
                }
                return GlobalResult.ok(
                        LoginResponseVO.builder()
                                .tid(tid)
                                .type(type)
                                .isBlack(blackUser)
                                .uid(uid)
                                .username(userName)
                                .isNew(false)
                                .token(JwtTokenResponseVO.builder()
                                        .token(token)
                                        .expirationTime(jwtOperator.getExpirationDateFromToken(token).getTime())
                                        .build()).build()
                );
            }
            MyUser user = new MyUser();
            user.setUsername(userName);
            int id = this.userService.addUser(user);
            this.userService.updateOpenidByUid(id, mpUserLoginDTO.getOpenid());
            // ?????????????????????????????????10????????????
            orderService.createCoupon(user.getId() + "", "2", "???????????????", "?????????????????????????????????", "30", "100", 7);
            return GlobalResult.ok(
                    LoginResponseVO.builder()
                            .type(type)
                            .tid(tid)
                            .uid(id + "")
                            .username(userName)
                            .getCoupon(true)
                            .isBlack(false)
                            .isNew(true)
                            .token(JwtTokenResponseVO.builder()
                                    .token(token)
                                    .expirationTime(jwtOperator.getExpirationDateFromToken(token).getTime())
                                    .build()).build()
            );
        } else {
            return GlobalResult.errorMsg("?????????????????????!");
        }
    }


    @PostMapping("/authUser")
    @ApiOperation(value = "?????????????????????", notes = "????????????")
    @ApiImplicitParam(name = "loginDTO", value = "????????????????????????????????????", required = true, paramType = "body", dataType = "UserLoginDTO", dataTypeClass = UserLoginDTO.class)
    public GlobalResult miniAppLogin(@Valid @RequestBody UserLoginDTO loginDTO, BindingResult bindingResult) throws WxErrorException {
        paramsValidate(loginDTO, bindingResult);
        WxMaJscode2SessionResult result = this.wxMaService.getUserService()
                .getSessionInfo(loginDTO.getCode());
        // ????????????????????????
        WxMaPhoneNumberInfo wxMaPhoneNumberInfo = wxMaService.getUserService().getPhoneNoInfo(result.getSessionKey(),
                loginDTO.getEncryptedData(), loginDTO.getIv());
        if (Objects.isNull(wxMaPhoneNumberInfo) || StringUtils.isBlank(wxMaPhoneNumberInfo.getPhoneNumber())) {
            return GlobalResult.errorMsg("?????????????????????");
        }
        String userName = wxMaPhoneNumberInfo.getPhoneNumber();
        String openId = result.getOpenid();
        final String token = jwtOperator.generateToken(
                User.builder()
                        .username(userName)
                        .build()
        );
        // ????????????
        String type = "1";
        String tid = "";
        String uid = this.userService.findUserIdByName(userName);
        if (StringUtils.isNotBlank(uid)) {
            // ??????????????????
            boolean blackUser = userService.isBlackUser(uid);
            Openid openid = openidService.findByUid(Integer.valueOf(uid));
            if (openid != null) {
                // ??????username?????????openid??????????????????openid?????????
                // 1.??????openid
                this.userService.updateUidByOpenid(Integer.parseInt(uid), openId, loginDTO.getNickname(), loginDTO.getCity(), loginDTO.getAvatarUrl(), loginDTO.getCountry(), loginDTO.getProvince());
                tid = this.userService.findUserType(userName);
                if (StringUtils.isNotBlank(tid)) {
                    type = "2";
                }
                return GlobalResult.ok(
                        LoginResponseVO.builder()
                                .tid(tid)
                                .type(type)
                                .isBlack(blackUser)
                                .isNew(false)
                                .uid(uid)
                                .username(userName)
                                .token(JwtTokenResponseVO.builder()
                                        .token(token)
                                        .expirationTime(jwtOperator.getExpirationDateFromToken(token).getTime())
                                        .build()).build()
                );
            } else {
                openidService.AddOpenId(
                        Openid.builder()
                                .openid("1")
                                .uid(Integer.parseInt(uid))
                                .miniappId(openId)
                                .headimgurl(loginDTO.getAvatarUrl())
                                .city(loginDTO.getCity())
                                .country(loginDTO.getCountry())
                                .sex(loginDTO.getGender())
                                .province(loginDTO.getProvince())
                                .nickname(loginDTO.getNickname())
                                .build(),
                        (System.currentTimeMillis() + "").substring(0, 10)
                );
                //orderService.createCoupon(uid, "2", "???????????????", "?????????????????????????????????", "30", "100", 7);
                return GlobalResult.ok(
                        LoginResponseVO.builder()
                                .type(type)
                                .tid(tid)
                                .uid(uid)
                                .isBlack(blackUser)
                                .username(userName)
                                .getCoupon(true)
                                .isNew(true)
                                .token(JwtTokenResponseVO.builder()
                                        .token(token)
                                        .expirationTime(jwtOperator.getExpirationDateFromToken(token).getTime())
                                        .build()).build()
                );
            }
        }
        MyUser user = new MyUser();
        user.setUsername(userName);
        int id = this.userService.addUser(user);
        openidService.AddOpenId(
                Openid.builder()
                        .uid(id)
                        .openid("1")
                        .miniappId(openId)
                        .headimgurl(loginDTO.getAvatarUrl())
                        .city(loginDTO.getCity())
                        .country(loginDTO.getCountry())
                        .sex(loginDTO.getGender())
                        .province(loginDTO.getProvince())
                        .nickname(loginDTO.getNickname())
                        .build(),
                (System.currentTimeMillis() + "").substring(0, 10)
        );
        // ?????????????????????????????????10????????????
        orderService.createCoupon(user.getId() + "", "2", "???????????????", "?????????????????????????????????", "30", "100", 7);
        return GlobalResult.ok(
                LoginResponseVO.builder()
                        .type(type)
                        .tid(tid)
                        .uid(id + "")
                        .username(userName)
                        .getCoupon(true)
                        .isBlack(false)
                        .isNew(true)
                        .token(JwtTokenResponseVO.builder()
                                .token(token)
                                .expirationTime(jwtOperator.getExpirationDateFromToken(token).getTime())
                                .build()).build()
        );
    }

    @PostMapping("/appAuthUser")
    @ApiOperation(value = "app??????", notes = "????????????")
    @ApiImplicitParam(name = "appLogin", value = "????????????????????????????????????", required = true, paramType = "body", dataType = "appUserLoginDTO", dataTypeClass = AppUserLoginDTO.class)
    public GlobalResult appLogin(@Valid @RequestBody AppUserLoginDTO appUserLoginDTO, BindingResult bindingResult) {
        paramsValidate(appUserLoginDTO, bindingResult);
        String userName = appUserLoginDTO.getMobile();
        VerificationCode verificationCode = verificationCodeService.getMaxCodeByMobile(userName);
        if (verificationCode == null) {
            return GlobalResult.errorMsg("???????????????????????????!");
        }
        Long minutes = DateUtils.computeBetween(verificationCode.getCreateDate().toInstant(), Instant.now());
        if (minutes > 5) {
            return GlobalResult.errorMsg("?????????????????????????????????!");
        }
        if (appUserLoginDTO.getCode().equals(verificationCode.getVerificationCode())) {
            final String token = jwtOperator.generateToken(
                    User.builder()
                            .username(userName)
                            .build()
            );
            // ????????????
            String type = "1";
            String tid = this.userService.findUserType(appUserLoginDTO.getMobile());
            if (tid != null) {
                type = "2";
            }
            String uid = this.userService.findUserIdByName(userName);
            if (StringUtils.isNotBlank(uid)) {
                // ??????????????????
                boolean blackUser = userService.isBlackUser(uid);
                return GlobalResult.ok(
                        LoginResponseVO.builder()
                                .tid(tid)
                                .type(type)
                                .isBlack(blackUser)
                                .uid(uid)
                                .username(userName)
                                .isNew(false)
                                .token(JwtTokenResponseVO.builder()
                                        .token(token)
                                        .expirationTime(jwtOperator.getExpirationDateFromToken(token).getTime())
                                        .build()).build()
                );
            }
            MyUser user = new MyUser();
            user.setUsername(userName);
            int id = this.userService.addUser(user);
            // ?????????????????????????????????10????????????
            orderService.createCoupon(user.getId() + "", "2", "APP??????", "?????????????????????????????????", "30", "100", 7);
            return GlobalResult.ok(
                    LoginResponseVO.builder()
                            .type(type)
                            .tid(tid)
                            .uid(id + "")
                            .username(userName)
                            .getCoupon(true)
                            .isBlack(false)
                            .isNew(true)
                            .token(JwtTokenResponseVO.builder()
                                    .token(token)
                                    .expirationTime(jwtOperator.getExpirationDateFromToken(token).getTime())
                                    .build()).build()
            );
        } else {
            return GlobalResult.errorMsg("?????????????????????!");
        }
    }


    @ApiOperation(value = "??????????????????????????????", notes = "?????????????????????????????????????????????")
    @ApiImplicitParam(name = "managerQueryInputDTO", value = "?????????????????????????????????", required = true, paramType = "body", dataType = "ManagerQueryInputDTO", dataTypeClass = ManagerQueryInputDTO.class)
    @PostMapping("/page")
    @PreAuthorize("hasLogin()")
    public GlobalResult pageList(@RequestBody ManagerQueryInputDTO managerQueryInputDTO) {
        ManagerPersistence managerPersistence = new ManagerPersistence();
        BeanUtils.copyProperties(managerQueryInputDTO, managerPersistence);
        IPage<ManagerOutputDTO> managerOutputDTOPage = managerService.page(managerQueryInputDTO.makePaging(), managerPersistence);
        return GlobalResult.ok(managerOutputDTOPage);
    }

    @ApiOperation(value = "??????????????????", notes = "??????????????????", response = ResponseEntity.class)
    @ApiImplicitParam(name = "managerAddInputDTO", value = "??????????????????", required = true, paramType = "body", dataType = "ManagerAddInputDTO", dataTypeClass = ManagerAddInputDTO.class)
    @PostMapping
    public ResponseEntity addManager(@Valid @RequestBody ManagerAddInputDTO managerAddInputDTO, BindingResult bindingResult) {
        paramsValidate(managerAddInputDTO, bindingResult);
        ManagerEntity manager = new ManagerEntity();
        BeanUtils.copyProperties(managerAddInputDTO, manager);
        managerService.save(manager);
        return ResponseEntity.ok().build();
    }


}
