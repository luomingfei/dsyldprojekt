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
    @ApiOperation(value = "微信公众号进入", notes = "微信公众号进入")
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
     * android : 所有android设备 mac os : iphone ipad windows
     * phone:Nokia等windows系统的手机
     * @param requestHeader 请求头
     * @return
     */
    public static String getDevice(HttpServletRequest request) {
        String requestHeader = request.getHeader("user-agent").toLowerCase();
        String[] deviceArray = new String[]{"android", "iphone", "ios", "windows phone"};
        if (requestHeader == null) {
            return "PC端";
        }
        requestHeader = requestHeader.toLowerCase();
        for (int i = 0; i < deviceArray.length; i++) {
            if (requestHeader.indexOf(deviceArray[i]) > 0) {
                return "WAP端";
            }
        }
        return "PC端";
    }

    /**
     * 给用户发送注册验证码
     */
    @GetMapping("sendTextMessage")
    @ApiOperation(value = "给用户发送验证码", notes = "给用户发送验证码", response = GlobalResult.class)
    public GlobalResult sendTextMessage(@RequestParam String mobile,HttpServletRequest request) {
        if (!isMobileDevice(request)) {
            return GlobalResult.errorException("访问异常!");
        }
        if(StringUtils.isBlank(mobile)){
            return GlobalResult.errorException("请输入手机号");
        }
        //获取手机号24小时内访问次数
        int mobileCount = verificationCodeService.getMobileCount(mobile);
        if(mobileCount>10){
            return GlobalResult.errorException("访问频繁!");
        }
        String ip = IpUtil.getIpAddr(request);
        //获取ip 24小时内访问次数
        int ipCount = verificationCodeService.getIpCount(ip);
        if(ipCount>10){
            return GlobalResult.errorException("访问频繁!");
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
            String msg = "亲爱的用户，您的验证码是" + code + "，5分钟内有效。";
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
        return GlobalResult.errorException("此手机号，请在一分钟后重试");
    }

    @PostMapping("/mpAuthUser")
    @ApiOperation(value = "公众号用户登录", notes = "用户登录")
    @ApiImplicitParam(name = "mpUserLoginDTO", value = "用户登录信息查询参数信息", required = true, paramType = "body", dataType = "MpUserLoginDTO", dataTypeClass = MpUserLoginDTO.class)
    public GlobalResult mpLogin(@Valid @RequestBody MpUserLoginDTO mpUserLoginDTO, BindingResult bindingResult) {
        paramsValidate(mpUserLoginDTO, bindingResult);
        String userName = mpUserLoginDTO.getMobile();
        VerificationCode verificationCode = verificationCodeService.getMaxCodeByMobile(userName);
        if (verificationCode == null) {
            return GlobalResult.errorMsg("请先获取注册验证码!");
        }
        Long minutes = DateUtils.computeBetween(verificationCode.getCreateDate().toInstant(), Instant.now());
        if (minutes > 5) {
            return GlobalResult.errorMsg("验证码过期，请重新获取!");
        }
        if (mpUserLoginDTO.getCode().equals(verificationCode.getVerificationCode())) {
            final String token = jwtOperator.generateToken(
                    User.builder()
                            .username(userName)
                            .build()
            );
            // 普通用户
            String type = "1";
            String tid = this.userService.findUserType(mpUserLoginDTO.getMobile());
            if (tid != null) {
                type = "2";
            }
            String uid = this.userService.findUserIdByName(userName);
            if (StringUtils.isNotBlank(uid)) {
                // 是否在黑名单
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
            // 发放优惠券，新用户专享10元优惠券
            orderService.createCoupon(user.getId() + "", "2", "新用户专享", "更多福利，尽在魔魔达！", "30", "100", 7);
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
            return GlobalResult.errorMsg("验证码输入错误!");
        }
    }


    @PostMapping("/authUser")
    @ApiOperation(value = "小程序用户登录", notes = "用户登录")
    @ApiImplicitParam(name = "loginDTO", value = "用户登录信息查询参数信息", required = true, paramType = "body", dataType = "UserLoginDTO", dataTypeClass = UserLoginDTO.class)
    public GlobalResult miniAppLogin(@Valid @RequestBody UserLoginDTO loginDTO, BindingResult bindingResult) throws WxErrorException {
        paramsValidate(loginDTO, bindingResult);
        WxMaJscode2SessionResult result = this.wxMaService.getUserService()
                .getSessionInfo(loginDTO.getCode());
        // 解密手机号码信息
        WxMaPhoneNumberInfo wxMaPhoneNumberInfo = wxMaService.getUserService().getPhoneNoInfo(result.getSessionKey(),
                loginDTO.getEncryptedData(), loginDTO.getIv());
        if (Objects.isNull(wxMaPhoneNumberInfo) || StringUtils.isBlank(wxMaPhoneNumberInfo.getPhoneNumber())) {
            return GlobalResult.errorMsg("获取手机号失败");
        }
        String userName = wxMaPhoneNumberInfo.getPhoneNumber();
        String openId = result.getOpenid();
        final String token = jwtOperator.generateToken(
                User.builder()
                        .username(userName)
                        .build()
        );
        // 普通用户
        String type = "1";
        String tid = "";
        String uid = this.userService.findUserIdByName(userName);
        if (StringUtils.isNotBlank(uid)) {
            // 是否在黑名单
            boolean blackUser = userService.isBlackUser(uid);
            Openid openid = openidService.findByUid(Integer.valueOf(uid));
            if (openid != null) {
                // 若该username对应的openid与此次登陆的openid不一致
                // 1.更新openid
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
                //orderService.createCoupon(uid, "2", "新用户专享", "更多福利，尽在魔魔达！", "30", "100", 7);
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
        // 发放优惠券，新用户专享10元优惠券
        orderService.createCoupon(user.getId() + "", "2", "新用户专享", "更多福利，尽在魔魔达！", "30", "100", 7);
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
    @ApiOperation(value = "app登录", notes = "用户登录")
    @ApiImplicitParam(name = "appLogin", value = "用户登录信息查询参数信息", required = true, paramType = "body", dataType = "appUserLoginDTO", dataTypeClass = AppUserLoginDTO.class)
    public GlobalResult appLogin(@Valid @RequestBody AppUserLoginDTO appUserLoginDTO, BindingResult bindingResult) {
        paramsValidate(appUserLoginDTO, bindingResult);
        String userName = appUserLoginDTO.getMobile();
        VerificationCode verificationCode = verificationCodeService.getMaxCodeByMobile(userName);
        if (verificationCode == null) {
            return GlobalResult.errorMsg("请先获取注册验证码!");
        }
        Long minutes = DateUtils.computeBetween(verificationCode.getCreateDate().toInstant(), Instant.now());
        if (minutes > 5) {
            return GlobalResult.errorMsg("验证码过期，请重新获取!");
        }
        if (appUserLoginDTO.getCode().equals(verificationCode.getVerificationCode())) {
            final String token = jwtOperator.generateToken(
                    User.builder()
                            .username(userName)
                            .build()
            );
            // 普通用户
            String type = "1";
            String tid = this.userService.findUserType(appUserLoginDTO.getMobile());
            if (tid != null) {
                type = "2";
            }
            String uid = this.userService.findUserIdByName(userName);
            if (StringUtils.isNotBlank(uid)) {
                // 是否在黑名单
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
            // 发放优惠券，新用户专享10元优惠券
            orderService.createCoupon(user.getId() + "", "2", "APP专享", "更多福利，尽在魔魔达！", "30", "100", 7);
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
            return GlobalResult.errorMsg("验证码输入错误!");
        }
    }


    @ApiOperation(value = "分页查询用户信息列表", notes = "根据多条件分页查询用户信息信息")
    @ApiImplicitParam(name = "managerQueryInputDTO", value = "用户信息页查询参数信息", required = true, paramType = "body", dataType = "ManagerQueryInputDTO", dataTypeClass = ManagerQueryInputDTO.class)
    @PostMapping("/page")
    @PreAuthorize("hasLogin()")
    public GlobalResult pageList(@RequestBody ManagerQueryInputDTO managerQueryInputDTO) {
        ManagerPersistence managerPersistence = new ManagerPersistence();
        BeanUtils.copyProperties(managerQueryInputDTO, managerPersistence);
        IPage<ManagerOutputDTO> managerOutputDTOPage = managerService.page(managerQueryInputDTO.makePaging(), managerPersistence);
        return GlobalResult.ok(managerOutputDTOPage);
    }

    @ApiOperation(value = "新增用户信息", notes = "新增用户信息", response = ResponseEntity.class)
    @ApiImplicitParam(name = "managerAddInputDTO", value = "用户新增信息", required = true, paramType = "body", dataType = "ManagerAddInputDTO", dataTypeClass = ManagerAddInputDTO.class)
    @PostMapping
    public ResponseEntity addManager(@Valid @RequestBody ManagerAddInputDTO managerAddInputDTO, BindingResult bindingResult) {
        paramsValidate(managerAddInputDTO, bindingResult);
        ManagerEntity manager = new ManagerEntity();
        BeanUtils.copyProperties(managerAddInputDTO, manager);
        managerService.save(manager);
        return ResponseEntity.ok().build();
    }


}
