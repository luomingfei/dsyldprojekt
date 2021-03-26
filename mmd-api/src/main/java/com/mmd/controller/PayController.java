package com.mmd.controller;


import com.mmd.MapUtil;
import com.mmd.domain.dto.order.MassagerDTO;
import com.mmd.domain.dto.wxPay.*;
import com.mmd.entity.*;
import com.mmd.service.*;
import com.mmd.utils.LocalHttpClient;
import com.mmd.utils.TextMessageUtil;
import com.mmd.utils.XMLConverUtil;
import com.mmd.utils.wxpay.ExpireSet;
import com.mmd.utils.wxpay.PayApi;
import com.mmd.utils.wxpay.SignatureUtil;
import com.mmd.utils.wxpay.StreamUtils;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.*;


/**
 * <p>
 * 支付
 * </p>
 *
 * @author dsc
 * @since 2020年4月14日
 */
@Validated
@RestController
@RequestMapping("/wxPay")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PayController {

    @Value("${wx.miniapp.appid}")
    private String miniAppId;

    @Value("${wx.mch-id}")
    private String MCH_ID;

    @Value("${wx.key}")
    private String key;

    @Value("${wx.url}")
    private String notifyUrl;

    @Value("${wx.miniapp.secret}")
    private String secret;

    @Value("${wx.mp.appid}")
    private String mpAppId;

    @Value("${wx.mp.secret}")
    private String mpSecret;

    private static int times;
    private static Timer timer;
    private static String accountID;

    private final OrderService orderService;
    private final AccountStatementService accountStatementService;
    private final CouponService couponService;
    private final OpenidService openidService;
    private final UserService userService;
    private final RechargeActivityService rechargeActivityService;
    private final AccountService accountService;
    private final AccountLogService accountLogService;
    private final RechargeCouponService rechargeCouponService;
    private final TnsService tnsService;

    private static Logger log = LoggerFactory.getLogger(PayController.class);

    private static ExpireSet<String> expireSet = new ExpireSet<>(60);

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs.getRequest();
    }

    /**
     * 余额支付
     */
    @RequestMapping(value = { "/balancePay" }, method = { RequestMethod.POST })
    @ApiOperation(value = "余额支付",notes = "余额支付",response = GlobalResult.class)
    public GlobalResult balancePay (@RequestBody Map<String, Object> map) {
        String oid = map.get("oid").toString(), uid = map.get("uid").toString(),
                cid = map.get("cid") == null ? null : map.get("cid").toString();
        Account account = accountService.myAccount(uid);
        if (account == null) {
            return GlobalResult.errorMsg("暂未开通账户");
        }
        if (account.getStatus() != 1) {
            return GlobalResult.errorMsg("账户状态异常");
        }
        Order order = orderService.findById(Integer.parseInt(oid));
        if (order == null) {
            return GlobalResult.errorMsg("未知订单");
        }
        if (!"1".equals(order.getZt()) && !"6".equals(order.getZt())) {
            return GlobalResult.errorMsg("订单已支付或已取消");
        }
        Coupon coupon = null;
        double fee = Double.parseDouble(order.getZje().toString()) + Double.parseDouble(order.getLevelmoney().toString())
                + Double.parseDouble(order.getYjjtmoeny().toString()) + Double.parseDouble(order.getSexmoney().toString())
                - Double.parseDouble(order.getNumdiscount().toString());
        if (StringUtils.isNotBlank(cid)) {
            coupon = couponService.getCouponById(Integer.parseInt(cid));
        }
        if (coupon != null) {
            fee -= Double.parseDouble(coupon.getMoney());
        }
        int settle = (int)(fee * 100);
        int balance = Integer.parseInt(account.getGiftMoney().toString()) + Integer.parseInt(account.getRealMoney().toString());
        if (settle > balance) {
            return GlobalResult.errorMsg("账户余额不足");
        }
        int realChange = 0, giftChange = 0, realBalance = 0, giftBalance = 0;
        if (Integer.parseInt(account.getGiftMoney().toString()) > settle) {
            giftChange = settle;
        } else {
            giftChange = Integer.parseInt(account.getGiftMoney().toString());
            realChange = settle - giftChange;
        }
        realBalance = Integer.parseInt(account.getRealMoney().toString()) - realChange;
        giftBalance = Integer.parseInt(account.getGiftMoney().toString()) - giftChange;
        //生成流水
        AccountStatement as = new AccountStatement();
        as.setOperatetype(11);
        as.setOperator("user");
        as.setOid(Integer.parseInt(oid));
        as.setOperatetime(Calendar.getInstance().getTimeInMillis() / 1000);
        as.setRealchange(realChange / 100.0 + "");
        as.setGiftchange(giftChange / 100.0 + "");
        as.setRealbalance(realBalance / 100.0 + "");
        as.setGiftbalance(giftBalance / 100.0 + "");
        as.setStatus(1);
        as.setAid(account.getId().toString());
        accountStatementService.createAccountStatement(as);
        //更新账户余额
        AccountTemp temp = new AccountTemp();
        temp.setAid(account.getId());
        temp.setPreReal(Integer.parseInt(account.getRealMoney().toString()));
        temp.setPreGift(Integer.parseInt(account.getGiftMoney().toString()));
        temp.setCurrReal(realBalance);
        temp.setCurrGift(giftBalance);
        if (accountService.updateAccountBalance(temp)) {
            //更新流水状态
            accountStatementService.updateAccountStatement(as.getId().toString(), 2);
            //更新优惠券
            couponService.updateCouponAfterPay(cid, oid);
            //更新订单信息
            if (order.getTid() == 0) {
                orderService.updateOrderAfterBalancePay(oid, fee, "2");
            } else {
                orderService.updateOrderAfterBalancePay(oid, fee, "7");
                MassagerDTO massagerDTO = tnsService.findById(order.getTid() + "");
                //向技师发短信
                TextMessageUtil.sendText2Massager4NewOrder(massagerDTO.getPhone(), order.getId() + "");
                //向技师发消息
//                if (massagerDTO.getOpenid() != null && massagerDTO.getOpenid().trim().length() > 0) {
//                    SendMessage.sendMessage2Massager(mas.getOpenid(), order,
//                            userService.findUserNameById(order.getUid()),
//                            userService.findAddressById(order.getDid()).getAddressDetail(), APPID, APP_SECRET);
                //               }
            }
            //向值班人员发送提醒
            //List<CustomService> list = adminService.findCustomServices();
//            SendMessage.sendMessage2CustomService(order, list, APPID, APP_SECRET);
        } else {
            return GlobalResult.errorMsg("支付失败，请重试");
        }
        return GlobalResult.ok();
    }


    /**
     * 微信支付下单并生成密钥
     *
     * @param wxPayInputDTO
     * @return
     */
    @PostMapping
    public GlobalResult wxPay(@RequestBody WxPayInputDTO wxPayInputDTO) {
        Order order = this.orderService.findById(wxPayInputDTO.getOrderId());
        if (!"1".equals(order.getZt())) {
            return GlobalResult.errorMsg("订单已支付或已取消");
        }
        //生成流水
        AccountStatement as = new AccountStatement();
        as.setOperatetype(1);
        as.setOperator("user");
        as.setOid(wxPayInputDTO.getOrderId());
        as.setOperatetime(Calendar.getInstance().getTimeInMillis() / 1000);
        Coupon coupon = (wxPayInputDTO.getCid() == null) ? null : couponService.getCouponById(wxPayInputDTO.getCid());
        //BigDecimal totalFee = new BigDecimal("0.01");
        BigDecimal totalFee = new BigDecimal(order.getZje().toString()).subtract(order.getPromotionmoney()).add(order.getYjjtmoeny())
                .add(order.getLevelmoney()).subtract(order.getNumdiscount());
        //减除优惠卷价格
        totalFee = (coupon == null) ? totalFee : totalFee.subtract(new BigDecimal(coupon.getMoney()));
        as.setRealchange(totalFee.toString());
        as.setStatus(1);
        Integer asId = accountStatementService.createAccountStatement(as);
        //统一支付
        Unifiedorder uo = new Unifiedorder();
        Openid openid = openidService.findByUid(order.getUid());
        if (openid == null) {
            return GlobalResult.errorMsg("用户信息异常");
        }
        String appid;
        if(StringUtils.isBlank(wxPayInputDTO.getType())){
            if(StringUtils.isBlank(openid.getMiniappId())){
                return GlobalResult.errorMsg("用户信息异常");
            }
            uo.setOpenid(openid.getMiniappId());
            appid = this.miniAppId;
        }else{
            if(StringUtils.isBlank(openid.getOpenid())){
                return GlobalResult.errorMsg("用户信息异常");
            }
            uo.setOpenid(openid.getOpenid());
            appid = this.mpAppId;
        }
        uo.setAppid(appid);
        uo.setTrade_type("JSAPI");
        uo.setMch_id(this.MCH_ID);
        uo.setNonce_str(UUID.randomUUID().toString().replaceAll("-", ""));
        if (wxPayInputDTO.getCid() != null) {
            uo.setAttach(wxPayInputDTO.getCid().toString());
        }
        uo.setBody(wxPayInputDTO.getName());
        uo.setOut_trade_no(asId.toString());
        log.info("out_trade_no：" + asId);
        uo.setTotal_fee(totalFee.multiply(new BigDecimal(100)).intValue() + "");
        uo.setSpbill_create_ip(getRequest().getRemoteAddr());
        uo.setNotify_url(notifyUrl + "wxPay/callback");
        //下单
        UnifiedorderResult unifiedorderResult = PayApi.payUnifiedorder(uo, this.key);
        accountStatementService.updateTradeNo(as.getId(), unifiedorderResult.getPrepay_id());
        //生成密钥对象
        PayJsRequest payJsRequest = new PayJsRequest();
        String package_ = "prepay_id=" + unifiedorderResult.getPrepay_id();
        payJsRequest.setAppId(appid);
        payJsRequest.setNonceStr(UUID.randomUUID().toString().replaceAll("-", ""));
        payJsRequest.setPackage_(package_);
        payJsRequest.setSignType("MD5");
        payJsRequest.setTimeStamp(System.currentTimeMillis() / 1000 + "");
        Map<String, String> mapS = MapUtil.objectToMap(payJsRequest);
        String paySign = SignatureUtil.generateSign(mapS, key);
        payJsRequest.setPaySign(paySign);
        return GlobalResult.ok(payJsRequest);
    }


    @PostMapping("/recharge")
    public GlobalResult recharge(@RequestBody RechargeInputDTO rechargeInputDTO) {
        //查询充值信息
        RechargeActivity activity = rechargeActivityService.queryRechargeByRid(rechargeInputDTO.getRid().toString());
        //查询账户信息
        Account account = accountService.myAccount(rechargeInputDTO.getUid().toString());
        if (activity == null) {
            return GlobalResult.errorMsg("未知的充值活动");
        }
        if (activity.getStatus() != 1) {
            return GlobalResult.errorMsg("该充值活动已经结束");
        }
        if (account != null && account.getStatus() != 1) {
            return GlobalResult.errorMsg("账户状态异常");
        }
        //创建账户
        if (account == null) {
            account = new Account();
            account.setUid(Integer.parseInt(rechargeInputDTO.getUid().toString()));
            account.setRealMoney(0);
            account.setGiftMoney(0);
            account.setStatus(1);
            accountService.createAccount(account);
        }
        //生成流水
        AccountStatement as = new AccountStatement();
        as.setOperatetype(12);
        as.setOperator("user");
        as.setOperatetime(Calendar.getInstance().getTimeInMillis() / 1000);
        as.setRealchange(Integer.parseInt(activity.getRealMoney().toString()) / 100.0 + "");
        as.setGiftchange(Integer.parseInt(activity.getGiftMoney().toString()) / 100.0 + "");
        as.setStatus(1);
        as.setAid(account.getId().toString());
        as.setRid(rechargeInputDTO.getRid().toString());
        accountStatementService.createAccountStatement(as);
        //统一支付
        Unifiedorder uo = new Unifiedorder();
        Openid openid = openidService.findByUid(rechargeInputDTO.getUid());
        if (openid == null) {
            return GlobalResult.errorMsg("用户信息异常");
        }
        String appid;
        if(StringUtils.isBlank(rechargeInputDTO.getType())){
            if(StringUtils.isBlank(openid.getMiniappId())){
                return GlobalResult.errorMsg("用户信息异常");
            }
            uo.setOpenid(openid.getMiniappId());
            appid = this.miniAppId;
        }else{
            if(StringUtils.isBlank(openid.getOpenid())){
                return GlobalResult.errorMsg("用户信息异常");
            }
            uo.setOpenid(openid.getOpenid());
            appid = this.mpAppId;
        }
        uo.setAppid(appid);
        uo.setTrade_type("JSAPI");
        uo.setMch_id(this.MCH_ID);
        uo.setNonce_str(UUID.randomUUID().toString().replaceAll("-", ""));
        if (rechargeInputDTO.getCid() != null) {
            uo.setAttach(rechargeInputDTO.getCid().toString());
        }
        uo.setBody(activity.getInfo());
        uo.setOut_trade_no(as.getId().toString());
        uo.setTotal_fee(activity.getRealMoney().toString());
        //uo.setTotal_fee("1");
        uo.setSpbill_create_ip(getRequest().getRemoteAddr());
        uo.setNotify_url(notifyUrl + "wxPay/rechargeCallback");
        UnifiedorderResult unifiedorderResult = PayApi.payUnifiedorder(uo, this.key);
        accountStatementService.updateTradeNo(as.getId(), unifiedorderResult.getPrepay_id());
        //生成密钥对象
        PayJsRequest payJsRequest = new PayJsRequest();
        String package_ = "prepay_id=" + unifiedorderResult.getPrepay_id();
        payJsRequest.setAppId(appid);
        payJsRequest.setNonceStr(UUID.randomUUID().toString().replaceAll("-", ""));
        payJsRequest.setPackage_(package_);
        payJsRequest.setSignType("MD5");
        payJsRequest.setTimeStamp(System.currentTimeMillis() / 1000 + "");
        Map<String, String> mapS = MapUtil.objectToMap(payJsRequest);
        String paySign = SignatureUtil.generateSign(mapS, key);
        payJsRequest.setPaySign(paySign);
        return GlobalResult.ok(payJsRequest);
    }

    @PostMapping("/customizeRecharge")
    public GlobalResult customizeRecharge(@RequestBody CustomizeRechargeInputDTO rechargeInputDTO) {
        //查询账户信息
        Account account = accountService.myAccount(rechargeInputDTO.getUid().toString());
        if (account != null && account.getStatus() != 1) {
            return GlobalResult.errorMsg("账户状态异常");
        }
        //创建账户
        if (account == null) {
            account = new Account();
            account.setUid(Integer.parseInt(rechargeInputDTO.getUid().toString()));
            account.setRealMoney(0);
            account.setGiftMoney(0);
            account.setStatus(1);
            accountService.createAccount(account);
        }
        //生成流水
        AccountStatement as = new AccountStatement();
        as.setOperatetype(12);
        as.setOperator("user");
        as.setOperatetime(Calendar.getInstance().getTimeInMillis() / 1000);
        as.setRealchange(rechargeInputDTO.getMoney());
        as.setGiftchange("0");
        as.setStatus(1);
        as.setAid(account.getId().toString());
        accountStatementService.createAccountStatement(as);
        //统一支付
        Unifiedorder uo = new Unifiedorder();
        Openid openid = openidService.findByUid(rechargeInputDTO.getUid());
        if (openid == null) {
            return GlobalResult.errorMsg("用户信息异常");
        }
        String appid;
        if(StringUtils.isBlank(rechargeInputDTO.getType())){
            if(StringUtils.isBlank(openid.getMiniappId())){
                return GlobalResult.errorMsg("用户信息异常");
            }
            uo.setOpenid(openid.getMiniappId());
            appid = this.miniAppId;
        }else{
            if(StringUtils.isBlank(openid.getOpenid())){
                return GlobalResult.errorMsg("用户信息异常");
            }
            uo.setOpenid(openid.getOpenid());
            appid = this.mpAppId;
        }
        uo.setAppid(appid);
        uo.setTrade_type("JSAPI");
        uo.setMch_id(this.MCH_ID);
        uo.setNonce_str(UUID.randomUUID().toString().replaceAll("-", ""));
        uo.setBody("自定义金额充值");
        uo.setOut_trade_no(as.getId().toString());
        BigDecimal totalFee = new BigDecimal(rechargeInputDTO.getMoney());
        uo.setTotal_fee(totalFee.multiply(new BigDecimal(100)).intValue() + "");
        //uo.setTotal_fee("1");
        uo.setSpbill_create_ip(getRequest().getRemoteAddr());
        uo.setNotify_url(notifyUrl + "wxPay/customizeRechargeCallback");
        UnifiedorderResult unifiedorderResult = PayApi.payUnifiedorder(uo, this.key);
        accountStatementService.updateTradeNo(as.getId(), unifiedorderResult.getPrepay_id());
        //生成密钥对象
        PayJsRequest payJsRequest = new PayJsRequest();
        String package_ = "prepay_id=" + unifiedorderResult.getPrepay_id();
        payJsRequest.setAppId(appid);
        payJsRequest.setNonceStr(UUID.randomUUID().toString().replaceAll("-", ""));
        payJsRequest.setPackage_(package_);
        payJsRequest.setSignType("MD5");
        payJsRequest.setTimeStamp(System.currentTimeMillis() / 1000 + "");
        Map<String, String> mapS = MapUtil.objectToMap(payJsRequest);
        String paySign = SignatureUtil.generateSign(mapS, key);
        payJsRequest.setPaySign(paySign);
        return GlobalResult.ok(payJsRequest);
    }

    @PostMapping("/callback")
    public void callback(HttpServletRequest request, HttpServletResponse response) throws IOException, ParserConfigurationException, SAXException {
        String xml = StreamUtils.copyToString(request.getInputStream(), Charset.forName("utf-8"));
        Map<String, String> map = MapUtil.xmlToMap(xml);
        if (expireSet.contains(map.get("transaction_id"))) {
            return;
        }
        String sign = SignatureUtil.generateSign(map, this.key);
        if (!sign.equals(map.get("sign"))) {
            MchNotifyResult result = new MchNotifyResult();

            result.setReturn_code("FAIL");
            result.setReturn_msg("ERROR");
            response.getOutputStream().write(XMLConverUtil.convertToXML(result).getBytes());
        } else {
            String aid = map.get("out_trade_no");
            //改变流水状态并查询
            AccountStatement as = accountStatementService.updateAndQueryAccountStatement(aid);
            String oid = as.getOid().toString();
            //更新优惠券信息
            if (map.get("attach") != null) {
                String cid =  map.get("attach");
                couponService.updateCouponAfterPay(cid, oid);
            }
            //更新订单金额，支付方式
            String real = map.get("total_fee");
            orderService.updateOrderJe(Double.parseDouble(real) / 100, oid);
            this.orderService.updateOrder(oid, "wxpay");
            Order order = orderService.findById(Integer.valueOf(oid));
            //更新订单状态，并根据需要提醒技师
            if (!(order.getZt().equals("-1") || order.getZt().equals("3") || order.getZt().equals("4")
                    || order.getZt().equals("5") || order.getZt().equals("8"))) {
                if (order.getTid() != 0) {
                    this.userService.UpdateOrderZt4Assign(oid, order.getZt(), "7");
                    MassagerDTO massagerDTO = tnsService.findById(order.getTid() + "");
                    //向技师发短信
                    TextMessageUtil.sendText2Massager4NewOrder(massagerDTO.getPhone(), order.getId() + "");
                } else {
                    this.userService.UpdateOrderZt(oid, order.getZt(), "2");
                }
            }
            MchPayNotify payNotify =  XMLConverUtil.convertToObject(MchPayNotify.class, xml);
            expireSet.add(payNotify.getTransaction_id());
            MchNotifyResult result = new MchNotifyResult();
            result.setReturn_code("SUCCESS");
            result.setReturn_msg("OK");
            response.getOutputStream().write(XMLConverUtil.convertToXML(result).getBytes());
        }
    }

    @RequestMapping(value = {"/rechargeCallback"}, method = RequestMethod.POST)
    public void rechargeCallback(HttpServletRequest request, HttpServletResponse response) throws IOException, ParserConfigurationException, SAXException {
        String xml = StreamUtils.copyToString(request.getInputStream(), Charset.forName("utf-8"));
        Map<String, String> map = MapUtil.xmlToMap(xml);
        if (expireSet.contains(map.get("transaction_id"))) {
            return;
        }
        if (!SignatureUtil.generateSign(map, this.key).equals(map.get("sign"))) {
            //签名验证失败
            MchNotifyResult result = new MchNotifyResult();
            result.setReturn_code("FAIL");
            result.setReturn_msg("ERROR");
            response.getOutputStream().write(XMLConverUtil.convertToXML(result).getBytes());
            return;
        }
        String aid = map.get("out_trade_no");
        String real = map.get("total_fee");
        final AccountStatement as = accountStatementService.queryAccountStatementInCreate(aid);
        if (as == null) {
            //没有流水记录或流水记录状态不为创建状态则直接返回
            return;
        }
        final RechargeActivity activity = rechargeActivityService.queryRechargeByRid(as.getRid());
        if (!real.equals(activity.getRealMoney())) {
            AccountLog log = new AccountLog();
            log.setAid(as.getAid());
            log.setInfo("充值账户金额异常，充值活动：" + as.getRid() + ",实付金额：" + real);
            log.setStatus(0);
            log.setTime(System.currentTimeMillis() / 1000);
            accountLogService.saveAccountLog(log);
        }
        //最多执行3次更新账户，3次均失败则报错
        accountID = as.getAid();
        times = 3;
        timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                if (times-- <= 0) {
                    //失败3次，记录异常
                    timer.cancel();
                    AccountLog log = new AccountLog();
                    log.setAid(accountID);
                    log.setInfo("更新账户金额失败3次");
                    log.setStatus(0);
                    log.setTime(new Date().getTime() / 1000);
                    accountLogService.saveAccountLog(log);
                    return;
                }
                Account account = accountService.queryAccountByAid(accountID);
                AccountTemp temp = new AccountTemp();
                temp.setAid(account.getId());
                temp.setPreReal(account.getRealMoney());
                temp.setPreGift(account.getGiftMoney());
                temp.setCurrReal(temp.getPreReal() + activity.getRealMoney());
                temp.setCurrGift(temp.getPreGift() + activity.getGiftMoney());
                if (accountService.updateAccountBalance(temp)) {
                    as.setRealbalance(temp.getCurrReal() / 100.0 + "");
                    as.setGiftbalance(temp.getCurrGift() / 100.0 + "");
                    as.setStatus(2);
                    accountStatementService.updateAccountStatement4Recharge(as);
                    timer.cancel();
                }
            }
        };
        timer.schedule(task, 0L, 1000L);
        //对充值优惠券的充值活动发放优惠券
        Account account = accountService.queryAccountByAid(accountID);
        List<RechargeCoupon> coupons = rechargeCouponService.queryRechargeCoupons(as.getRid());
        for (RechargeCoupon coupon : coupons) {
            for (int i = 0; i < coupon.getCount(); i++) {
                orderService.createCouponLimitProduct(account.getUid().toString(), "4", "充值享豪礼", coupon.getComment(),
                        coupon.getMoney() + "", coupon.getMinPrice() + "", coupon.getExpireDay(), coupon.getPids());
            }
        }
        //更新订单金额，支付方式
        MchPayNotify payNotify =  XMLConverUtil.convertToObject(MchPayNotify.class, xml);
        expireSet.add(payNotify.getTransaction_id());
        MchNotifyResult result = new MchNotifyResult();
        result.setReturn_code("SUCCESS");
        result.setReturn_msg("OK");
        response.getOutputStream().write(XMLConverUtil.convertToXML(result).getBytes());
    }


    @RequestMapping(value = {"/customizeRechargeCallback"}, method = RequestMethod.POST)
    public void customizeRechargeCallback(HttpServletRequest request, HttpServletResponse response) throws IOException, ParserConfigurationException, SAXException {
        String xml = StreamUtils.copyToString(request.getInputStream(), Charset.forName("utf-8"));
        Map<String, String> map = MapUtil.xmlToMap(xml);
        if (expireSet.contains(map.get("transaction_id"))) {
            return;
        }
        if (!SignatureUtil.generateSign(map, this.key).equals(map.get("sign"))) {
            //签名验证失败
            MchNotifyResult result = new MchNotifyResult();
            result.setReturn_code("FAIL");
            result.setReturn_msg("ERROR");
            response.getOutputStream().write(XMLConverUtil.convertToXML(result).getBytes());
            return;
        }
        String aid = map.get("out_trade_no");
        String real = map.get("total_fee");
        final AccountStatement as = accountStatementService.queryAccountStatementInCreate(aid);
        if (as == null) {
            //没有流水记录或流水记录状态不为创建状态则直接返回
            return;
        }

        //最多执行3次更新账户，3次均失败则报错
        accountID = as.getAid();
        times = 3;
        timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                if (times-- <= 0) {
                    //失败3次，记录异常
                    timer.cancel();
                    AccountLog log = new AccountLog();
                    log.setAid(accountID);
                    log.setInfo("更新账户金额失败3次");
                    log.setStatus(0);
                    log.setTime(new Date().getTime() / 1000);
                    accountLogService.saveAccountLog(log);
                    return;
                }
                Account account = accountService.queryAccountByAid(accountID);
                AccountTemp temp = new AccountTemp();
                temp.setAid(account.getId());
                temp.setPreReal(account.getRealMoney());
                temp.setPreGift(account.getGiftMoney());
                temp.setCurrReal(temp.getPreReal() + Integer.valueOf(real));
                temp.setCurrGift(temp.getPreGift());
                if (accountService.updateAccountBalance(temp)) {
                    as.setRealbalance(temp.getCurrReal() / 100.0 + "");
                    as.setGiftbalance(temp.getCurrGift() / 100.0 + "");
                    as.setStatus(2);
                    accountStatementService.updateAccountStatement4Recharge(as);
                    timer.cancel();
                }
            }
        };
        timer.schedule(task, 0L, 1000L);

        //更新订单金额，支付方式
        MchPayNotify payNotify =  XMLConverUtil.convertToObject(MchPayNotify.class, xml);
        expireSet.add(payNotify.getTransaction_id());
        MchNotifyResult result = new MchNotifyResult();
        result.setReturn_code("SUCCESS");
        result.setReturn_msg("OK");
        response.getOutputStream().write(XMLConverUtil.convertToXML(result).getBytes());
    }

    @GetMapping("offlinePay")
    public GlobalResult offlinePay(@RequestParam Integer orderId){
        Order order = this.orderService.findById(orderId);
        if (!"1".equals(order.getZt())) {
            return GlobalResult.errorMsg("订单已支付或已取消");
        }
        if (order.getTid() != 0) {
            this.userService.UpdateOrderZt4Assign(orderId.toString(), order.getZt(), "7");
            order.setZt("7");
            MassagerDTO massagerDTO = tnsService.findById(order.getTid() + "");
            //向技师发短信
            TextMessageUtil.sendText2Massager4NewOrder(massagerDTO.getPhone(), order.getId() + "");
        } else {
            this.userService.UpdateOrderZt(orderId.toString(), order.getZt(), "2");
            order.setZt("2");
        }
        double settlePrice = Double.parseDouble(order.getZje().toString()) - Double.parseDouble(order.getPromotionmoney().toString())
                + Double.parseDouble(order.getYjjtmoeny().toString()) + Double.parseDouble(order.getLevelmoney().toString());
        this.orderService.updateOrderJe(settlePrice, orderId.toString());
        this.orderService.updateOrder(orderId.toString(), "xxpay");
        return GlobalResult.ok();
    }

    /**
     * 微信支付退款
     */
    @PostMapping(value = {"/getMoneyBack"}, produces = "application/json")
    public SecapiPayRefundResult getMoneyBack(String oid, int refund_fee) {
        //生成微信退款流水
        AccountStatement as = new AccountStatement();
        as.setOperatetype(3);
        as.setOperator("admin");
        as.setOid(Integer.parseInt(oid));
        as.setOperatetime(Calendar.getInstance().getTimeInMillis() / 1000);
        as.setRealchange(refund_fee / 100.0 + "");
        as.setStatus(1);
        accountStatementService.createAccountStatement(as);
        //退款
        SecapiPayRefund secapiPayRefund = new SecapiPayRefund();
        secapiPayRefund.setMch_id(MCH_ID);
        secapiPayRefund.setNonce_str(UUID.randomUUID().toString().replaceAll("-", ""));
        secapiPayRefund.setOp_user_id(MCH_ID);
        //从流水表中查找记录，没有记录则说明是老订单
        Order order = orderService.findById(Integer.parseInt(oid));
        if(order.getWay().equals("Wechat")){
            secapiPayRefund.setAppid(this.mpAppId);
        }else{
            secapiPayRefund.setAppid(this.miniAppId);
        }
        String aid = orderService.queryAidByOid(oid);
        if (aid == null) {
            secapiPayRefund.setOut_trade_no(oid);
        } else {
            secapiPayRefund.setOut_trade_no(aid);
        }
        secapiPayRefund.setRefund_fee(refund_fee);
        secapiPayRefund.setTotal_fee((int) (Double.parseDouble(order.getSettleprice().toString()) * 100));
        secapiPayRefund.setOut_refund_no("tk" + oid);
        LocalHttpClient.initMchKeyStore("PKCS12", "F:\\momoda\\证书\\apiclient_cert.p12", MCH_ID, 0, 0);
        SecapiPayRefundResult result = PayApi.secapiPayRefund(secapiPayRefund, key);
        if (result.getResult_code().equals("SUCCESS")) {
            userService.UpdateOrderZt(oid, "8", "-1");
            BigDecimal originalSettlePrice = new BigDecimal(order.getSettleprice().toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal refund = new BigDecimal(refund_fee / 100.0).setScale(2, BigDecimal.ROUND_HALF_UP);
            orderService.updateOrderJe(originalSettlePrice.subtract(refund).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(), oid);
            accountStatementService.updateAccountStatement(as.getId().toString(), 2);
        } else {
            //退款失败
            accountStatementService.updateAccountStatement(as.getId().toString(), 0);
        }
        return result;
    }


}
