package com.mmd.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mmd.domain.dto.input.StoreQueryInputDTO;
import com.mmd.domain.dto.output.ProductOutputDTO;
import com.mmd.domain.dto.output.StoreOutputDTO;
import com.mmd.domain.dto.output.TnsOutputDTO;
import com.mmd.entity.BaseQueryBean;
import com.mmd.entity.GlobalResult;
import com.mmd.entity.Store;
import com.mmd.service.StoreService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: qiWen.xue
 * @Email: xqwQAQwq@163.com
 * @Date: 2020/4/9 15:46
 */
@RestController
@RequestMapping("/store")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StoreController {

    private final StoreService storeService;

    @ApiOperation(value = "根据id查询门店信息", notes = "根据id查询门店信息")
    @ApiImplicitParam(name = "id", value = "id", paramType = "path", dataType = "Integer", dataTypeClass = Integer.class)
    @GetMapping("/{id}")
    public GlobalResult getById(@PathVariable Integer id) {
        Store store = storeService.getById(id);
        return GlobalResult.ok(store);
    }

    @ApiOperation(value = "根据项目id和技师id查询门店信息", notes = "根据项目id和技师id查询门店信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pid", value = "产品id", paramType = "query", dataType =
                    "Integer", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "tid", value = "技师id", paramType = "query", dataType =
                    "Integer", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "cid", value = "城市id", paramType = "query", dataType =
                    "Integer", dataTypeClass = Integer.class)
    }
    )
    @GetMapping("/getStores")
    public GlobalResult getStores(@RequestParam(value = "pid") Integer pid, @RequestParam(value = "tid") Integer tid, @RequestParam(value = "cid") Integer cid) {
        List<Store> stores = storeService.getStores(pid, tid, cid);
        return GlobalResult.ok(stores);
    }


    @ApiOperation(value = "分页查询门店信息列表", notes = "分页查询门店信息列表")
    @ApiImplicitParam(name = "storeQueryInputDTO", value = "用户信息页查询参数信息", required = true, paramType = "body", dataType = "StoreQueryInputDTO", dataTypeClass = StoreQueryInputDTO.class)
    @PostMapping("/getStoreList")
    public GlobalResult getStoreList(@RequestBody StoreQueryInputDTO storeQueryInputDTO) {
        IPage<StoreOutputDTO> page = storeService.page(storeQueryInputDTO.makePaging(), storeQueryInputDTO);
        return GlobalResult.ok(page);
    }

    @PostMapping("/getTechnicianAndProduct/{id}")
    @ApiOperation(value = "获取门店技师列表", notes = "获取门店技师列表")
    @ApiImplicitParam(name = "id", value = "id", paramType = "path", dataType = "Integer", dataTypeClass = Integer.class)
    public GlobalResult getTechnician(@PathVariable Integer id) {
        if (id != null) {
            List<TnsOutputDTO> list = storeService.page(id);
            return GlobalResult.ok(list);
        }
        return GlobalResult.errorMsg("门店错误,请重新选择门店！");
    }

    @PostMapping("/getProduct/{id}")
    @ApiOperation(value = "获取门店项目列表", notes = "获取门店技师列表")
    @ApiImplicitParam(name = "id", value = "id", paramType = "path", dataType = "Integer", dataTypeClass = Integer.class)
    public GlobalResult getProduct(@PathVariable Integer id) {
        if (id != null) {
            List<ProductOutputDTO> list = storeService.getProduct(id);
            return GlobalResult.ok(list);
        }
        return GlobalResult.errorMsg("门店错误,请重新选择门店！");
    }




}
