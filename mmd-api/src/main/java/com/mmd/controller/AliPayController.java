package com.mmd.controller;


import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.mmd.domain.dto.alipay.AliPayInputDTO;
import com.mmd.domain.dto.order.MassagerDTO;
import com.mmd.domain.dto.wxPay.CustomizeRechargeInputDTO;
import com.mmd.domain.dto.wxPay.RechargeInputDTO;
import com.mmd.entity.*;
import com.mmd.pay.utils.AlipayUtil;
import com.mmd.service.*;
import com.mmd.utils.TextMessageUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

@Validated
@RestController
@RequestMapping("/aliPay")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AliPayController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${ALI.notify_url}")
    private String notifyUrl;

    private static int times;
    private static Timer timer;
    private static String accountID;

    private final AccountStatementService accountStatementService;
    private final AccountLogService accountLogService;
    private final CouponService couponService;
    private final AlipayUtil alipayUtil;
    private final OrderService orderService;
    private final AccountService accountService;
    private final UserService userService;
    private final RechargeActivityService rechargeActivityService;
    private final RechargeCouponService rechargeCouponService;
    private final TnsService tnsService;

    /**
     * ???????????????
     *
     * @param aliPayInputDTO
     */
    @PostMapping
    public GlobalResult Pay(@RequestBody AliPayInputDTO aliPayInputDTO) {
        Order order = this.orderService.findById(aliPayInputDTO.getOrderId());
        if (!"1".equals(order.getZt())) {
            return GlobalResult.errorMsg("????????????");
        }
        AccountStatement as = new AccountStatement();
        as.setOperatetype(2);
        as.setOperator("user");
        as.setOid(aliPayInputDTO.getOrderId());
        as.setOperatetime(Calendar.getInstance().getTimeInMillis() / 1000);
        Coupon coupon = (aliPayInputDTO.getCid() == null) ? null : couponService.getCouponById(aliPayInputDTO.getCid());
//        BigDecimal totalFee = new BigDecimal("0.01");
        BigDecimal totalFee = new BigDecimal(order.getZje().toString()).subtract(order.getPromotionmoney()).add(order.getYjjtmoeny())
                .add(order.getLevelmoney()).subtract(order.getNumdiscount());
//        ?????????????????????
        totalFee = (coupon == null) ? totalFee : totalFee.subtract(new BigDecimal(coupon.getMoney()));
        as.setRealchange(totalFee.toString());
        as.setStatus(1);
        String tradeNo = accountStatementService.createAccountStatement(as).toString();
        try {
            //??????client
            AlipayClient alipayClient = alipayUtil.buildAliClient();
            //??????API??????
            AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();

            //SDK????????????????????????????????????????????????????????????????????????????????????sdk???model????????????(model???biz_content???????????????????????????biz_content)???
            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
            if (aliPayInputDTO.getCid() != null) {
                model.setBody(aliPayInputDTO.getCid().toString());
            }
            model.setOutTradeNo(tradeNo);
            model.setTimeoutExpress("90m");
            model.setSubject(aliPayInputDTO.getName());
            model.setTotalAmount(totalFee.toString());
            model.setProductCode("QUICK_MSECURITY_PAY");
            request.setBizModel(model);
            request.setNotifyUrl(notifyUrl+"/aliPay/callback");

            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            if(StringUtils.isNotBlank(response.getCode())&&!response.getCode().equals("10000")){
                return GlobalResult.errorMsg("????????????");
            }
            logger.info("??????????????????"+response.getBody());
            return GlobalResult.ok(response.getBody());
        } catch (AlipayApiException e) {
            return GlobalResult.errorMsg("????????????");
        }
    }


    /**
     * ???????????????????????????
     *
     * @param rechargeInputDTO
     */
    @PostMapping("/recharge")
    public GlobalResult recharge(@RequestBody RechargeInputDTO rechargeInputDTO) {
        //??????????????????
        RechargeActivity activity = rechargeActivityService.queryRechargeByRid(rechargeInputDTO.getRid().toString());
        //??????????????????
        Account account = accountService.myAccount(rechargeInputDTO.getUid().toString());
        if (activity == null) {
            return GlobalResult.errorMsg("?????????????????????");
        }
        if (activity.getStatus() != 1) {
            return GlobalResult.errorMsg("???????????????????????????");
        }
        if (account != null && account.getStatus() != 1) {
            return GlobalResult.errorMsg("??????????????????");
        }
        //????????????
        if (account == null) {
            account = new Account();
            account.setUid(Integer.parseInt(rechargeInputDTO.getUid().toString()));
            account.setRealMoney(0);
            account.setGiftMoney(0);
            account.setStatus(1);
            accountService.createAccount(account);
        }
        //????????????
        AccountStatement as = new AccountStatement();
        as.setOperatetype(12);
        as.setOperator("user");
        as.setOperatetime(Calendar.getInstance().getTimeInMillis() / 1000);
        as.setRealchange(Integer.parseInt(activity.getRealMoney().toString()) / 100.0 + "");
        as.setGiftchange(Integer.parseInt(activity.getGiftMoney().toString()) / 100.0 + "");
        as.setStatus(1);
        as.setAid(account.getId().toString());
        as.setRid(rechargeInputDTO.getRid().toString());
        String tradeNo = accountStatementService.createAccountStatement(as).toString();
        try {
            //??????client
            AlipayClient alipayClient = alipayUtil.buildAliClient();
            //??????API??????
            AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
            //SDK????????????????????????????????????????????????????????????????????????????????????sdk???model????????????(model???biz_content???????????????????????????biz_content)???
            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
            model.setOutTradeNo(tradeNo);
            model.setTimeoutExpress("30m");
            model.setSubject(activity.getInfo());
            Integer realMone = activity.getRealMoney()/100;
//            Double realMone = 0.1;
            model.setTotalAmount(realMone.toString());
            model.setProductCode("QUICK_MSECURITY_PAY");
            request.setBizModel(model);
            request.setNotifyUrl(notifyUrl+"/aliPay/rechargeCallback");
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            if(StringUtils.isNotBlank(response.getCode())&&!response.getCode().equals("10000")){
                return GlobalResult.errorMsg("????????????");
            }
            return GlobalResult.ok(response.getBody());
        } catch (AlipayApiException e) {
            return GlobalResult.errorMsg("????????????");
        }
    }


    /**
     * ??????????????????????????????
     *
     * @param rechargeInputDTO
     */
    @PostMapping("/customizeRecharge")
    public GlobalResult customizeRecharge(@RequestBody CustomizeRechargeInputDTO rechargeInputDTO) {
        //??????????????????
        Account account = accountService.myAccount(rechargeInputDTO.getUid().toString());
        if (account != null && account.getStatus() != 1) {
            return GlobalResult.errorMsg("??????????????????");
        }
        //????????????
        if (account == null) {
            account = new Account();
            account.setUid(Integer.parseInt(rechargeInputDTO.getUid().toString()));
            account.setRealMoney(0);
            account.setGiftMoney(0);
            account.setStatus(1);
            accountService.createAccount(account);
        }
        //????????????
        AccountStatement as = new AccountStatement();
        as.setOperatetype(12);
        as.setOperator("user");
        as.setOperatetime(Calendar.getInstance().getTimeInMillis() / 1000);
        as.setRealchange(rechargeInputDTO.getMoney());
        as.setGiftchange("0");
        as.setStatus(1);
        as.setAid(account.getId().toString());
        String tradeNo = accountStatementService.createAccountStatement(as).toString();
        try {
            //??????client
            AlipayClient alipayClient = alipayUtil.buildAliClient();
            //??????API??????
            AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
            //SDK????????????????????????????????????????????????????????????????????????????????????sdk???model????????????(model???biz_content???????????????????????????biz_content)???
            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
            model.setOutTradeNo(tradeNo);
            model.setTimeoutExpress("30m");
            model.setSubject("???????????????"+rechargeInputDTO.getMoney()+"?????????");
            model.setTotalAmount(rechargeInputDTO.getMoney());
            model.setProductCode("QUICK_MSECURITY_PAY");
            request.setBizModel(model);
            request.setNotifyUrl(notifyUrl+"/aliPay/customizeRechargeCallback");
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            if(StringUtils.isNotBlank(response.getCode())&&!response.getCode().equals("10000")){
                return GlobalResult.errorMsg("????????????");
            }
            return GlobalResult.ok(response.getBody());
        } catch (AlipayApiException e) {
            return GlobalResult.errorMsg("????????????");
        }
    }

    /**
     * ????????????
     *
     * @param request
     */
    @PostMapping("callback")
    public String aliPayCallback(HttpServletRequest request) throws AlipayApiException {
        logger.info("??????????????????");
        boolean signVerified = alipayUtil.rsaCheckV1(request);
        // ????????????
        if (signVerified) {
            logger.info("????????????");
            String out_trade_no = request.getParameter("out_trade_no");
            String total_fee = request.getParameter("total_amount");
            String cid = request.getParameter("body");
            String trade_no = request.getParameter("trade_no");
            String trade_status = request.getParameter("trade_status");
            //???????????????????????????????????????????????????
            if(trade_status.equals("TRADE_SUCCESS")){
                logger.info("????????????");
                String aid = out_trade_no;
                //???????????????????????????
                AccountStatement as = accountStatementService.updateAndQueryAccountStatement(aid);
                if (as == null) {
                    return ("success");
                }
                accountStatementService.updateTradeNo(as.getId(), trade_no);
                String oid = as.getOid().toString();
                //?????????????????????
                if (cid != null) {
                    couponService.updateCouponAfterPay(cid, oid);
                }
                //?????????????????????????????????
                String real = total_fee;
                orderService.updateOrderJe(Double.parseDouble(real), oid);
                this.orderService.updateOrder(oid, "alipay");
                Order order = orderService.findById(Integer.valueOf(oid));
                //????????????????????????????????????????????????
                if (!(order.getZt().equals("-1") || order.getZt().equals("3") || order.getZt().equals("4")
                        || order.getZt().equals("5") || order.getZt().equals("8"))) {
                    if (order.getTid() != 0) {
                        this.userService.UpdateOrderZt4Assign(oid, order.getZt(), "7");
                        MassagerDTO massagerDTO = tnsService.findById(order.getTid() + "");
                        //??????????????????
                        TextMessageUtil.sendText2Massager4NewOrder(massagerDTO.getPhone(), order.getId() + "");
                    } else {
                        this.userService.UpdateOrderZt(oid, order.getZt(), "2");
                    }
                }

            }else if(trade_status.equals("TRADE_FINISHED")){
                logger.info("????????????");
            }else if(trade_status.equals("TRADE_CLOSED")){
                logger.info("????????????");
            }
            return ("success");
        } else {
            logger.info("????????????");
            return ("fail");
        }
    }

    /**
     * ??????????????????
     *
     * @param request
     */
    @PostMapping("rechargeCallback")
    public String rechargeCallback(HttpServletRequest request) throws AlipayApiException {
        logger.info("????????????????????????");
        boolean signVerified = alipayUtil.rsaCheckV1(request);
        // ????????????
        if (signVerified) {
            logger.info("????????????");
            String trade_no = request.getParameter("trade_no");
            String trade_status = request.getParameter("trade_status");
            //???????????????????????????????????????????????????
            if(trade_status.equals("TRADE_SUCCESS")){
                logger.info("????????????");
                String aid = request.getParameter("out_trade_no");
                String real = request.getParameter("total_amount");
                AccountStatement as = accountStatementService.updateAndQueryAccountStatement(aid);
                if (as == null) {
                    return ("success");
                }
                accountStatementService.updateTradeNo(as.getId(), trade_no);
                RechargeActivity activity = rechargeActivityService.queryRechargeByRid(as.getRid());
                if ((Double.valueOf(real) * 100)!=(activity.getRealMoney())) {
                    logger.info("??????????????????????????????????????????" + as.getRid() + ",???????????????" + real);
                    AccountLog log = new AccountLog();
                    log.setAid(as.getAid());
                    log.setInfo("??????????????????????????????????????????" + as.getRid() + ",???????????????" + real);
                    log.setStatus(0);
                    log.setTime(System.currentTimeMillis() / 1000);
                    accountLogService.saveAccountLog(log);
                }
                accountID = as.getAid();
                times = 3;
                timer = new Timer();
                TimerTask task = new TimerTask() {
                    public void run() {
                        if (times-- <= 0) {
                            //??????3??????????????????
                            timer.cancel();
                            AccountLog log = new AccountLog();
                            log.setAid(accountID);
                            log.setInfo("????????????????????????3???");
                            log.setStatus(0);
                            log.setTime(System.currentTimeMillis() / 1000);
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
                //????????????????????????????????????????????????
                Account account = accountService.queryAccountByAid(accountID);
                List<RechargeCoupon> coupons = rechargeCouponService.queryRechargeCoupons(as.getRid());
                for (RechargeCoupon coupon : coupons) {
                    for (int i = 0; i < coupon.getCount(); i++) {
                        orderService.createCouponLimitProduct(account.getUid().toString(), "4", "???????????????", coupon.getComment(),
                                coupon.getMoney() + "", coupon.getMinPrice() + "", coupon.getExpireDay(), coupon.getPids());
                    }
                }
            }else if(trade_status.equals("TRADE_FINISHED")){
                logger.info("????????????");
            }else if(trade_status.equals("TRADE_CLOSED")){
                logger.info("????????????");
            }
            return ("success");
        } else {
            logger.info("????????????");
            return ("fail");
        }
    }

    /**
     * ?????????????????????
     *
     * @param request
     */
    @PostMapping("customizeRechargeCallback")
    public String customizeRechargeCallback(HttpServletRequest request) throws AlipayApiException {
        logger.info("???????????????????????????");
        boolean signVerified = alipayUtil.rsaCheckV1(request);
        // ????????????
        if (signVerified) {
            logger.info("????????????");
            String trade_no = request.getParameter("trade_no");
            String trade_status = request.getParameter("trade_status");
            //???????????????????????????????????????????????????
            if(trade_status.equals("TRADE_SUCCESS")){
                logger.info("????????????");
                String aid = request.getParameter("out_trade_no");
                int real = (int) (Double.valueOf(request.getParameter("total_amount"))*100);
                final AccountStatement as = accountStatementService.queryAccountStatementInCreate(aid);
                if (as == null) {
                    //????????????????????????????????????????????????????????????????????????
                    return ("fail");
                }
                accountStatementService.updateTradeNo(as.getId(), trade_no);
                //????????????3??????????????????3?????????????????????
                accountID = as.getAid();
                times = 3;
                timer = new Timer();
                TimerTask task = new TimerTask() {
                    public void run() {
                        if (times-- <= 0) {
                            //??????3??????????????????
                            timer.cancel();
                            AccountLog log = new AccountLog();
                            log.setAid(accountID);
                            log.setInfo("????????????????????????3???");
                            log.setStatus(0);
                            log.setTime(System.currentTimeMillis() / 1000);
                            accountLogService.saveAccountLog(log);
                            return;
                        }
                        Account account = accountService.queryAccountByAid(accountID);
                        AccountTemp temp = new AccountTemp();
                        temp.setAid(account.getId());
                        temp.setPreReal(account.getRealMoney());
                        temp.setPreGift(account.getGiftMoney());
                        temp.setCurrReal(temp.getPreReal() + real);
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
            }else if(trade_status.equals("TRADE_FINISHED")){
                logger.info("????????????");
            }else if(trade_status.equals("TRADE_CLOSED")){
                logger.info("????????????");
            }
            return ("success");
        } else {
            logger.info("????????????");
            return ("fail");
        }
    }



}
