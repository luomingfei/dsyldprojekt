package com.mmd.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mmd.domain.dto.comment.CustomService;
import com.mmd.domain.dto.comment.EstimationDTO;
import com.mmd.domain.dto.comment.Tag;
import com.mmd.domain.dto.profile.operateDTO;
import com.mmd.entity.Estimation;
import com.mmd.entity.GlobalResult;
import com.mmd.service.OrderService;
import com.mmd.service.TnsPjService;
import com.mmd.service.TnsTagsService;
import com.mmd.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tomwang
 */
@Validated
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommentController {
    private final OrderService orderService;
    private final UserService userService;
    private final TnsTagsService tnsTagsService;
    private final TnsPjService tnsPjService;

    @ApiOperation(value = "点评分类")
    @GetMapping("/queryTypeAndSort")
    public GlobalResult queryTypeAndSort() {
        Map result = new HashMap();
        Map<String, String> sub = new HashMap<>();
        List<Map<String, String>> types = new ArrayList();
        sub.put("code", "all");
        sub.put("info", "全部分类");
        types.add(sub);
        sub = new HashMap<>();
        sub.put("code", "(1)");
        sub.put("info", "轻疗师");
        types.add(sub);
        sub = new HashMap<>();
        sub.put("code", "(3)");
        sub.put("info", "专家");
        types.add(sub);
        sub = new HashMap<>();
        sub.put("code", "(0,2)");
        sub.put("info", "技师");
        types.add(sub);
        result.put("types", types);
        //排序
        List<Map<String, String>> sorts = new ArrayList();
        sub = new HashMap<>();
        sub.put("code", "distance");
        sub.put("info", "离我最近");
        sorts.add(sub);
        sub = new HashMap<>();
        sub.put("code", "comment");
        sub.put("info", "好评最多");
        sorts.add(sub);
        result.put("sorts", sorts);
        return GlobalResult.ok(result);
    }

    @PostMapping("/operateComment")
    @ApiOperation(value = "点赞评论", notes = "点赞评论", response = GlobalResult.class)
    public GlobalResult operateComment(@RequestBody operateDTO operateDTO) {

        boolean qq = tnsPjService.operateComment(operateDTO);
        if (qq == true) {
            return GlobalResult.ok("操作成功");
        }
        return GlobalResult.errorMsg("操作失败，请稍后再试");
    }

    @PostMapping("/addEstimation")
    public GlobalResult addEstimation(@RequestBody Map<String, Object> map) {
        Estimation estimation = new Estimation();
        estimation.setUid(map.get("uid").toString());
        estimation.setCreatetime(((System.currentTimeMillis() * 1000L) + "").substring(0, 10));
        estimation.setOid(Integer.parseInt(map.get("orderid").toString()));
        estimation.setPjlevel(map.get("pj_level").toString());
        estimation.setSkill(Integer.parseInt(map.get("skill").toString()));
        estimation.setAttitude(Integer.parseInt(map.get("attitude").toString()));
        estimation.setOnTime(Integer.parseInt(map.get("onTime").toString()));
        try {
            if (userService.isBlackUser(estimation.getUid()) || StringUtils.isEmpty(estimation.getUid())) {
                return GlobalResult.errorMsg("状态异常，无法评论!");
            }
        } catch (Exception e) {
            return GlobalResult.errorMsg("服务器异常!");
        }
        if(estimation.getOid()!=0){
            Estimation estimation1 = userService.findEstimation(estimation.getUid(),estimation.getOid());
            if(estimation1!=null){
                return GlobalResult.errorMsg("请勿重复评价");
            }
        }
        switch (estimation.getPjlevel()) {
            case "1":
                estimation.setPjlx(3);
                break;
            case "2":
                estimation.setPjlx(2);
                break;
            case "3":
                estimation.setPjlx(2);
                break;
            case "4":
                estimation.setPjlx(1);
                break;
            case "5":
                estimation.setPjlx(1);
                break;
            default:
                estimation.setPjlx(0);
                break;
        }

        estimation.setPjnr(map.get("pjnr") == null ? "" : map.get("pjnr").toString());
        if (map.get("tid") != null) {
            estimation.setTid(map.get("tid").toString());
        } else {
            estimation.setTid(this.orderService.findById(estimation.getOid()).getTid() + "");
        }
        if (map.get("uid") != null) {

            estimation.setUid(map.get("uid").toString());
        } else {
            estimation.setUid(this.orderService.findById(estimation.getOid()).getUid().toString());
        }
        estimation.setUsername(userService.findUserNameById(estimation.getUid()));
        boolean ret = this.userService.AddEstimation(estimation);
        if (ret) {
            List<String> tags = (List<String>) map.get("tags");
            if (tags != null && tags.size() > 0) {
                for (int i = 0; i < tags.size(); i++) {
                    Tag tag = new Tag();
                    tag.setTag(tags.get(i));
                    tag.setPjid(estimation.getId() + "");
                    if (map.get("tid") != null) {
                        tag.setTid(map.get("tid").toString());
                    } else {
                        tag.setTid(orderService.findById(estimation.getOid()).getTid() + "");
                    }
                    tnsTagsService.addTag(tag);
                }
            }
        }
        return GlobalResult.ok(ret);
    }
}
