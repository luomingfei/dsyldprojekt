package com.mmd.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mmd.domain.dto.input.EstimationQueryInputDTO;
import com.mmd.domain.dto.input.ProductQueryInputDTO;
import com.mmd.domain.dto.order.ProductDTO;
import com.mmd.domain.dto.order.PromotionDTO;
import com.mmd.domain.dto.output.ProductOutputDTO;
import com.mmd.entity.*;
import com.mmd.persistence.EstimationPersistence;
import com.mmd.service.*;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: qiWen.xue
 * @Email: xqwQAQwq@163.com
 * @Date: 2020/3/30 11:05
 */
@RestController
@RequestMapping({"/v1/{cityId}/product"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductController {

    private static Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    private final ProductCategoryService productCategoryService;

    private final OrderService orderService;

    private final ProductSexService productSexService;

    private final TnsPjService tnsPjService;

    /**
     * 根据城市id查产品列表
     */
    @ApiOperation(value = "产品列表", notes = "根据城市id查产品列表")
    @GetMapping(value = {"/list"})
    public GlobalResult list(@PathVariable String cityId,
                             @RequestParam(value = "index", required = false, defaultValue = "1") Integer index,
                             @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit,
                             @RequestParam(value = "isRecommend", required = false) Integer isRecommend,
                             @RequestParam(value = "category", required = false) String category) {
        ProductQueryInputDTO productQueryInputDTO = new ProductQueryInputDTO();
        productQueryInputDTO.setCurrentPage(String.valueOf(index));
        productQueryInputDTO.setPageSize(String.valueOf(limit));
        productQueryInputDTO.setCityId(cityId);
        productQueryInputDTO.setCategory(category);
        productQueryInputDTO.setIsRecommend(isRecommend);
        IPage<ProductOutputDTO> products = productService.page(productQueryInputDTO.makePaging(), productQueryInputDTO);
//        List<Promotion> promotions = new ArrayList<>();
//        if (products.getRecords().size() > 0) {
//            promotions = this.productService.findOnSalePromotion();
//        }
        for (int i = 0; i < products.getRecords().size(); i++) {
            ProductOutputDTO product = products.getRecords().get(i);
            if (product.getPjnum() == null) {
                product.setPjnum(0);
            }
            //查询活动信息
//            Integer realNum = productService.getLjxdById(product.getId());
//            product.setLjxd((product.getLjxd() == null ? 0 : product.getLjxd()) + (realNum == null ? 0 : realNum));
//            for (Promotion promotion : promotions) {
//                if (product.getId().equals(promotion.getPid() + "")) {
//                    product.setPromotion(promotion);
//                    break;
//                }
//            }
            //查询对应的连单活动
            List<NumPromotion> numPromotions = productService.findNumPromotionsByPid(product.getId());
            product.setNumPromotions(numPromotions);
            products.getRecords().set(i, product);
        }
        return GlobalResult.ok(products);
    }

    /**
     * 查找所有产品类型
     *
     * @return
     */
    @GetMapping("/productCategoryList")
    public GlobalResult productCategoryList() {
        List<ProductCategory> productCategories = productCategoryService.getProductCategoryList();
        return GlobalResult.ok(productCategories);
    }

    /**
     * 获取产品详情
     *
     * @param id
     * @return
     * @throws IOException
     */
    @GetMapping("/detail")
    @ApiOperation(value = "获取产品详情", notes = "根据id获取产品详情", response = GlobalResult.class)
    @ApiImplicitParam(name = "id", value = "产品详情页产品详情信息", required = true, dataType =
            "int",
            dataTypeClass = Integer.class)
    public GlobalResult detail(Integer id) {
        //默认为1
        id = id == null || id < 1 ? 1 : id;
        log.debug("/v1/{}/product/detail?pid={}", id);
        ProductDTO productDTO = this.productService.findById(id.intValue());
        //查询活动信息
        List<Promotion> promotions = this.productService.findOnSalePromotion();
        //查询星级信息
        List<LevelInfo> leveInfo = productService.queryLevelInfoByPid(Long.valueOf(productDTO.getId()));
        productDTO.setLevelInfos(leveInfo);
        Integer realNum = productService.getLjxdById(Long.valueOf(productDTO.getId().toString()));
        productDTO.setLjxd((productDTO.getLjxd() == null ? 0 : productDTO.getLjxd()) + (realNum == null ? 0 : realNum));
        for (Promotion promotion : promotions) {
            if (productDTO.getId().equals(promotion.getPid() + "")) {
                PromotionDTO promotionDTO = new PromotionDTO();
                BeanUtils.copyProperties(promotion, promotionDTO);
                productDTO.setPromotion(promotionDTO);
                break;
            }
        }
        //查询对应的连单活动
        List<NumPromotion> numPromotions =
                productService.findNumPromotionsByPid(Long.valueOf(id.toString()));
        productDTO.setNumPromotions(numPromotions);
        return GlobalResult.ok(productDTO);
    }

    /**
     * 根据项目ID查找性别费用
     *
     * @return
     */
    @GetMapping("/productSexFee")
    @ApiOperation(value = "pid", notes = "项目id", response = GlobalResult.class)
    @ApiImplicitParam(name = "pid", value = "项目id", dataType = "query", dataTypeClass = String.class)
    public GlobalResult findProductSexFee(@RequestParam(value = "pid", required = false) String pid) {
        return GlobalResult.ok(productSexService.findProductSexByPid(pid));
    }

    @PostMapping("/commentInfo")
    @ApiOperation(value = "获取项目评价及好评率", notes = "根据id获取项目评价及好评率", response = GlobalResult.class)
    public GlobalResult commentInfo(@RequestBody List<Integer> ids) {
        return GlobalResult.ok(orderService.commentInfo(ids));
    }


    /**
     * 根据项目id及type查评价
     */
    @PostMapping(value = "/estimations")
    @ApiOperation(value = "根据项目id及type查评价", notes = "根据项目id及type查评价", response = GlobalResult.class)
    public GlobalResult listOfEstimationByType(@RequestBody EstimationQueryInputDTO estimationQueryInputDTO) {
        EstimationPersistence estimationPersistence = new EstimationPersistence();
        BeanUtils.copyProperties(estimationQueryInputDTO, estimationPersistence);
        IPage<Estimation> estimations = tnsPjService.getEstimationsByPid(estimationQueryInputDTO.makePaging(), estimationPersistence);
        if (estimations.getSize() > 0) {
            return GlobalResult.ok(estimations);
        }
        return GlobalResult.ok();
    }

    @PostMapping(value = {"/commentNum"})
    public GlobalResult commentNum(@RequestBody List<Integer> pids) {
        List<Map<String, Object>> result = tnsPjService.commentNum(pids);
        return GlobalResult.ok(result);
    }


}
