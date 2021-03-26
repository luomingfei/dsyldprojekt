package com.mmd.controller;

import com.mmd.entity.GlobalResult;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.menu.WxMpMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/weChar")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WeCharController {

    private final WxMpService wxService;

    @GetMapping("/wxJsapiSignature")
    @ApiOperation(value = "获取微信jssdk签名", notes = "获取微信jssdk签名")
    public GlobalResult getWxJsapiSignature(String url){
        try {
            WxJsapiSignature wxJsapiSignature = wxService.createJsapiSignature(url);
            return GlobalResult.ok(wxJsapiSignature);
        } catch (WxErrorException e) {
            return GlobalResult.errorMsg("获取JS-SDK Signature失败");
        }
    }

    @GetMapping("/getMenuService")
    public GlobalResult getMenuService(){
        try {
            WxMpMenu wxMenu = wxService.getMenuService().menuGet();
            System.out.println("AccessToken:"+wxService.getAccessToken());
            return GlobalResult.ok(wxMenu);
        } catch (WxErrorException e) {
            e.printStackTrace();
            return GlobalResult.errorMsg("获取JS-SDK Signature失败");
        }
    }



}
