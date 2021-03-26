package com.mmd.controller;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mmd.TimeUtil;
import com.mmd.domain.dto.input.AddressAddInputDTO;
import com.mmd.domain.dto.input.CouponQueryInputDTO;
import com.mmd.domain.dto.input.OrderQueryInputDTO;
import com.mmd.domain.dto.massagist.MassagistDTO;
import com.mmd.domain.dto.order.*;
import com.mmd.domain.dto.output.CouponOutputDTO;
import com.mmd.entity.*;
import com.mmd.persistence.CouponPersistence;
import com.mmd.persistence.OrderPersistence;
import com.mmd.service.*;
import com.mmd.utils.Coord;
import com.mmd.utils.Distance;
import com.mmd.utils.TextMessageUtil;
import com.mmd.utils.baiduAPI.BaiduAPI;
import com.mmd.utils.baiduAPI.Geocoder;
import com.mmd.utils.baiduAPI.GeocoderByAddress;
import com.mmd.utils.baiduAPI.PlaceSuggestion;
import com.mmd.vo.user.JwtTokenResponseVO;
import com.mmd.vo.user.LoginResponseVO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: qiWen.xue
 * @Email: xqwQAQwq@163.com
 * @Date: 2020/3/30 10:58
 */
@RestController
@RequestMapping({"/v1/{userId}"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

    private static Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final OrderService orderService;
    private final CouponService couponService;
    private final ProductService productService;
    private final TnsService tnsService;
    private final TnsPjService findPjByOrderId;
    private final AddressService addressService;
    private final DiagnosisService diagnosisService;
    private final TnsPjService tnsPjService;
    private final TnsTagsService tnsTagsService;
    private final MassagerService massagerService;
    private final QyService qyService;

    @PutMapping("/updateTnsWz")
    public GlobalResult updateTnsWz(@RequestBody Tns tns) {

        if(tns.getLocation()!=null) {
            String[] point = tns.getLocation().split(",");
            if(point.length != 2) {
                return GlobalResult.errorMsg("坐标信息错误");
            }
            if(!NumberUtil.isNumber(point[0]) || !NumberUtil.isNumber(point[1])) {
                return GlobalResult.errorMsg("坐标信息错误");
            }
        }
        tnsService.updateById(tns);

        return GlobalResult.ok();
    }
    /**
     * 城市列表
     */
    @ApiOperation(value = "城市列表", notes = "城市列表查询")
    @ApiImplicitParam(name = "userId", value = "城市列表查询", required = true, paramType = "path", dataType = "String", dataTypeClass = String.class)
    @RequestMapping(value = {"/city/list"}, method = RequestMethod.GET)
    public GlobalResult listOfCity(@PathVariable String userId) {
        log.debug("/v1/{}/city/list", userId);
        return GlobalResult.ok(this.userService.findCitys());
    }

    @PostMapping("/userDefaultSex")
    @ApiOperation(value = "userDefaultSex", notes = "userDefaultSex", response = GlobalResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户id", required = true, paramType = "query", dataType =
                    "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "pid", value = "产品id", required = true, paramType = "query", dataType =
                    "int", dataTypeClass = Integer.class)
    })
    public GlobalResult userDefaultSex(@RequestBody OrderDTO orderDTO) {
        log.debug("/v1/{}/userDefaultSex?uid={}&pid={}", orderDTO.getUid(), orderDTO.getPid());
        return GlobalResult.ok(orderService.userDefaultSex(orderDTO.getUid(), orderDTO.getPid()));
    }

    @GetMapping("/userDefaultLevel")
    @ApiOperation(value = "userDefaultLevel", notes = "userDefaultLevel", response = GlobalResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户id", required = true, paramType = "query", dataType =
                    "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "pid", value = "产品id", required = true, paramType = "query", dataType =
                    "int", dataTypeClass = Integer.class)
    })
    public GlobalResult userDefaultLevel(@Valid @RequestBody OrderDTO orderDTO) {
        return GlobalResult.ok(orderService.userDefaultLevel(orderDTO.getUid(), orderDTO.getPid()));
    }

    /**
     * 用户的优惠券列表
     */
    @GetMapping("/coupon/list")
    @ApiOperation(value = "用户的优惠券列表", notes = "用户的优惠券列表", response = GlobalResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "index", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "limit", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "platform", paramType = "query", dataType = "String"),
    })
    public GlobalResult list(@RequestParam(value = "userId", required = false) String userId,
                             @RequestParam(value = "index", required = false) Integer index,
                             @RequestParam(value = "limit", required = false) Integer limit,
                             @RequestParam(value = "platform", required = false) String platform) {
        log.debug("/v1/{}/coupon/list?&index={}&limit={}", new Object[]{userId, index, limit});
        CouponQueryInputDTO couponQueryInputDTO = new CouponQueryInputDTO();
        couponQueryInputDTO.setUserId(userId);
        couponQueryInputDTO.setCurrentPage(String.valueOf(index));
        couponQueryInputDTO.setPageSize(String.valueOf(limit));
        CouponPersistence couponPersistence = new CouponPersistence();
        BeanUtils.copyProperties(couponQueryInputDTO, couponPersistence);
        IPage<CouponOutputDTO> couponpages = this.couponService.findCouponsByUserId(couponQueryInputDTO.makePaging()
                , couponPersistence);
        if (platform != null && "chubao".equals(platform)) {
            couponpages = null;
        }
        if (couponpages.getSize() > 0) {
            return GlobalResult.ok(couponpages);
        }
        return GlobalResult.ok("无可用优惠券");
    }

    /**
     * 获得用户下单时可用的优惠券
     */
    @GetMapping("/getCoupon")
    @ApiOperation(value = "获得用户下单时可用的优惠券", notes = "获得用户下单时可用的优惠券", response = GlobalResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "zje", paramType = "query", dataType = "Double"),
            @ApiImplicitParam(name = "pid", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "oid", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "platform", paramType = "query", dataType = "String"),
    })
    public GlobalResult getCoupon(@RequestParam(value = "userId", required = false) String userId,
                                  @RequestParam(value = "zje", required = false) Double zje,
                                  @RequestParam(value = "pid", required = false) String pid,
                                  @RequestParam(value = "oid", required = false) String oid,
                                  @RequestParam(value = "platform", required = false) String platform) {
        List<CouponOutputDTO> coupons = null;
        if (platform != null && "chubao".equals(platform)) {
            coupons = new ArrayList<>();
        } else {
            if (oid != null) {
                Order order = orderService.findById(Integer.parseInt(oid));
                pid = order.getPid() + "";
                zje = Double.parseDouble(order.getZje().toString());
                if (order.getSexmoney() != null) {
                    zje += Double.parseDouble(order.getSexmoney().toString());
                }
                if (order.getNumdiscount() != null) {
                    zje -= Double.parseDouble(order.getNumdiscount().toString());
                }
            }
            coupons = couponService.findCouponsCanUse(userId, pid, zje, oid);
        }
        return GlobalResult.ok(coupons);
    }

    /**
     * 根据订单状态分类查询推拿师的订单
     */
    @RequestMapping(value = {"/order/list2"}, method = RequestMethod.GET)
    @ApiOperation(value = "根据订单状态分类查询推拿师的订单", notes = "根据订单状态分类查询推拿师的订单", response = GlobalResult.class)
    public GlobalResult list2(@PathVariable String userId,@RequestParam String zt,@RequestParam Integer tid,
                              @RequestParam String index,@RequestParam String limit) {
        log.debug("/v1/{}/order/list?zt={}&index={}&limit={}&tid={}", new Object[]{userId,
                zt, index,limit,tid});
        OrderQueryInputDTO orderQueryInputDTO=new OrderQueryInputDTO();
        orderQueryInputDTO.setUserId(userId);
        orderQueryInputDTO.setZt(zt);
        orderQueryInputDTO.setTid(tid);
        orderQueryInputDTO.setCurrentPage(index);
        orderQueryInputDTO.setPageSize(limit);
        IPage<com.mmd.domain.dto.profile.OrderDTO> ordersTemp = new Page<>();

        if ("1".equals(orderQueryInputDTO.getZt()))
            orderQueryInputDTO.setZt("(7)");
        if ("2".equals(orderQueryInputDTO.getZt()))
            orderQueryInputDTO.setZt("(3,4,9)");
        if ("3".equals(orderQueryInputDTO.getZt()))
            orderQueryInputDTO.setZt("(5)");
        if ("4".equals(orderQueryInputDTO.getZt())) {
            // 查询可抢订单
//			ordersTemp = this.userService.findTakeOrders(userId, index, limit);
        } else {
            ordersTemp = orderQueryInputDTO.getTid() == null ? orderService.findOrdersOfMassagerByZt(orderQueryInputDTO)
                    : orderService.findOrdersOfMassagerByZtAndTid(orderQueryInputDTO);
        }
        if (ordersTemp.getRecords().size() > 0) {
            List<OrderShow> orders = new ArrayList<>();
            for (int i = 0; i < ordersTemp.getRecords().size(); i++) {
                com.mmd.domain.dto.profile.OrderDTO o =  ordersTemp.getRecords().get(i);
                OrderShow order = new OrderShow();

                Coord coord = Distance.fromBD2Mars(o.getLng(), o.getLat());//GetLng()
                order.setLng(coord.getLng());
                order.setLat(coord.getLat());
                order.setId(o.getId());
                order.setCity(o.getCity());
                order.setOrder_sj(TimeUtil.TimeConvert(o.getRq()) + " " + o.getSj());
                order.setMoney(o.getZje());
                order.setComplete(o.getComplete());
                ProductDTO productDTO = this.productService.findById(o.getPid());
                if (productDTO != null) {
                    order.setProduct_name(productDTO.getXmmc());
                    order.setTp(productDTO.getTp());
                } else {
                    order.setProduct_name("该项目已删除");
                }
                MassagerDTO m = null;
                if (o.getTid() == 0 || (m = tnsService.findById(o.getTid() + "")) == null) {
                    order.setTns_name("未指派");
                } else {
                    order.setTns_name(m.getXm());
                }
                order.setYjjtmoney(o.getYjjtmoeny());
                order.setZt(o.getZt());
                order.setCreatetime(TimeUtil.TimeConvertMin(o.getCreateTime()));
                order.setPaymenttype(o.getPaymenttype());
                order.setTid(o.getTid());
                if (this.findPjByOrderId.findPjByOrderId(o.getId()) == null)
                    order.setSfpj(0);
                else
                    order.setSfpj(1);
                order.setProcuct_sl(o.getSl());
                order.setProductType(productDTO.getType());
                //根据订单状态控制显示的用户信息
                if (!"5".equals(o.getZt())) {
                    AddressDTO addressDTO = this.addressService.findAddressById(o.getDid());
                    order.setContact(addressDTO.getContact());
                    order.setPhone(addressDTO.getPhone());
                    order.setAddress(addressDTO.getAddressDetail());
                    order.setMph(addressDTO.getMph());
                }
                order.setRemark(o.getRemark());
                order.setPromotionmoney(o.getPromotionmoney());
                order.setLevelmoney(o.getLevelmoney());
                order.setMassagelevel(o.getMassagelevel());
                order.setMassagersex(o.getMassagersex());
                order.setSexmoney(o.getSexmoney());
                order.setNumdiscount(o.getNumdiscount());
                order.setSettleprice(o.getSettleprice());
                order.setUid(o.getUid());
                CouponDTO couponDTO = this.couponService.getCouponByOid(o.getId() + "");
                if (couponDTO != null && couponDTO.getId() != 0) {
                    order.setCouponMoney(couponDTO.getMoney());
                } else {
                    order.setCouponMoney("0");
                }
                List<Diagnosis> diagnoses = diagnosisService.getDiagnosisListByOid(o.getId() + "");
                if (diagnoses.size() > 0) {
                    order.setIsDiagnosis(1);
                } else {
                    order.setIsDiagnosis(0);
                }
                orders.add(i, order);
            }

            return GlobalResult.ok(orders);
        }
        return GlobalResult.ok();
    }

    /**
     * 新增或更新地址
     */
    @RequestMapping(value = "/manageAddress", method = RequestMethod.POST)
    @ApiOperation(value = "新增或更新地址", notes = "新增或更新地址", response = GlobalResult.class)
    @ApiImplicitParam(name = "addressDTO", value = "地址信息", required = true, paramType = "body", dataType = "AddressAddInputDTO", dataTypeClass = AddressAddInputDTO.class)
    public GlobalResult manageAddress(@PathVariable String userId, @RequestBody AddressAddInputDTO addressDTO) {
        if (userId == null) {
            return GlobalResult.ok(0);
        }
        Address address = new Address();
        address.setUid(Integer.valueOf(userId));
        address.setId(addressDTO.getId());
        address.setAddressDetail(addressDTO.getAddressDetail());
        address.setMph(addressDTO.getMph());
        address.setPhone(addressDTO.getPhone());
        address.setContact(addressDTO.getContact());
        address.setLat(addressDTO.getLat());
        address.setLng(addressDTO.getLng());
        if (address.getId() == 0) {
            addressService.addAddress(address);
        } else {
            addressService.updateAddress(address);
        }
        return GlobalResult.ok(address.getId());
    }

    /**
     * 删除地址
     */
    @RequestMapping(value = "/deleteAddress/{userId}", method = RequestMethod.GET)
    @ApiOperation(value = "删除地址", notes = "删除地址", response = GlobalResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(value = "userId", paramType = "path", dataType = "String"),
            @ApiImplicitParam(value = "did", paramType = "query", dataType = "String")
    })
    public GlobalResult deleteAddress(@PathVariable String userId, @RequestParam(value = "did", required = true) String did) {
        addressService.deleteAddress(userId, did);
        return GlobalResult.ok(true);
    }

    /**
     * 获取用户地址列表
     */
    @GetMapping("/queryUserAddress")
    @ApiOperation(value = "获取用户地址", notes = "获取用户地址", response = GlobalResult.class)
    @ApiImplicitParam(value = "userId", paramType = "path", dataType = "String")
    public GlobalResult queryUserAddress(@PathVariable(value = "userId") String userId) {
        if (userId == null || "0".equals(userId)) {
            return GlobalResult.ok(new ArrayList<>());
        }
        return GlobalResult.ok(addressService.queryUserAddress(userId));
    }

    /**
     * 根据id获取用户地址
     */
    @GetMapping("/queryUserAddressByid")
    @ApiOperation(value = "根据id获取用户地址", notes = "根据id获取用户地址", response = GlobalResult.class)
    @ApiImplicitParam(value = "id", paramType = "query", dataType = "Integer")
    public GlobalResult queryUserAddressByid(@RequestParam(value = "id") Integer id) {
        AddressDTO addressDTO = addressService.findAddressById(id);
        return GlobalResult.ok(addressDTO);
    }

    /**
     * 获取推荐技师
     */
    @GetMapping("/myMassager")
    @ApiOperation(value = "获取推荐技师", notes = "获取推荐技师", response = GlobalResult.class)
    public GlobalResult myMassager(@PathVariable String userId, @RequestParam(value = "pid") Integer pid,
                                   @RequestParam(value = "city") String city, @RequestParam(value = "quantity") Integer quantity,
                                   @RequestParam(value = "serviceTime") String serviceTime,
                                   @RequestParam(value = "address", required = false) String address,
                                   @RequestParam(value = "massageLevel", required = false) Integer level,
                                   @RequestParam(value = "lat", required = false) Double lat,
                                   @RequestParam(value = "lng", required = false) Double lng,
                                   @RequestParam(value = "did", required = false) Integer did,
                                   @RequestParam(value = "massagerSex", required = false) String massagerSex) throws ParseException {
        ProductDTO productDTO = productService.findById(pid);
        int serviceTimeInMin = Integer.parseInt(productDTO.getFzsj()) * quantity;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long beginTime = sdf.parse(serviceTime).getTime();
        long endTime = (sdf.parse(serviceTime).getTime() + 60 * serviceTimeInMin * 1000);
        if (lat == null || lng == null) {
            GeocoderByAddress geoResult = BaiduAPI.geocoderByAddress(city + "市" + address);
            if (geoResult != null && geoResult.getStatus() == 0) {
                lat = geoResult.getResult().getLocation().getLat();
                lng = geoResult.getResult().getLocation().getLng();
            }
        }
        level = level == null ? 1 : level;
        List<Massager> myMassagers = null, nearbyMassagers = null;
        //加时通项目不再校验技师是否有空
        if (productDTO.getXmmc().contains("加时通")) {
            List<LevelInfo> infos = productService.queryLevelInfoByPid(Long.valueOf(pid.toString()));
            if (infos == null || infos.size() == 0) {
                if (massagerSex == null || "".equals(massagerSex)) {
                    myMassagers = tnsService.getMyMassagers4JiaShiNolevelNosex(userId);
                } else {
                    String sexStr = "male".equals(massagerSex) ? "男" : "女";
                    myMassagers = tnsService.getMyMassagers4JiaShiNolevelSex(userId, sexStr);
                }
            } else {
                if (massagerSex == null || "".equals(massagerSex)) {
                    myMassagers = tnsService.getMyMassagers4JiaShiLevelNosex(userId, level);
                } else {
                    String sexStr = "male".equals(massagerSex) ? "男" : "女";
                    myMassagers = tnsService.getMyMassagers4JiaShiLevelSex(userId, level, sexStr);
                }
            }
            for (int i = 0; i < myMassagers.size(); i++) {
                Massager massager = (Massager) myMassagers.get(i);
                massager.setLjxd(this.orderService.getLjxdByMassagerId(massager.getId()));
                Map massagerParam = this.tnsPjService.getMassagerParam(massager.getId());
                massager.setPj(massagerParam.get("pj").toString());
                massager.setPj_num(massagerParam.get("count").toString());
                massager.setHpl(massagerParam.get("hpl").toString());
                List<MassagistDTO> massagists = tnsTagsService.findTagsOfMassager(massager.getId());
                List<TagGroupedEntity> tagGroupedEntities = new ArrayList<>();
                BeanUtils.copyProperties(massagists, tagGroupedEntities);
                massager.setTags(tagGroupedEntities);
                myMassagers.set(i, massager);
            }
            Map<String, Object> result = new TreeMap<String, Object>();
            result.put("myMassagers", myMassagers);
            result.put("nearbyMassagers", myMassagers);
            return GlobalResult.ok(result);
        }
        //判断项目是否分星级
        List<LevelInfo> infos = productService.queryLevelInfoByPid(Long.valueOf(pid.toString()));
        if (infos == null || infos.size() == 0) {
            //无需分星级的
            if (massagerSex == null || "".equals(massagerSex)) {
                //不区分性别
                myMassagers = tnsService.getMyMassagersNolevelNosex(userId, pid, lat, lng, beginTime);
                nearbyMassagers = tnsService.getNearbyMassagersNolevelNosex(city, pid, lat, lng, beginTime);
            } else {
                String sexStr = "male".equals(massagerSex) ? "男" : "女";
                //区分性别
                myMassagers = tnsService.getMyMassagersNolevelSex(userId, pid, lat, lng, beginTime, sexStr);
                nearbyMassagers = tnsService.getNearbyMassagersNolevelSex(city, pid, lat, lng, beginTime, sexStr);
            }
        } else {
            //需分星级的
            //已点推拿师保留5个
            if (massagerSex == null || "".equals(massagerSex)) {
                //不区分性别
                myMassagers = tnsService.getMyMassagersLevelNosex(userId, pid, lat, lng, level, beginTime);
                nearbyMassagers = tnsService.getNearbyMassagersLevelNosex(city, pid, lat, lng, level, beginTime);
            } else {
                String sexStr = "male".equals(massagerSex) ? "男" : "女";
                //区分性别
                myMassagers = tnsService.getMyMassagersLevelSex(userId, pid, lat, lng, level, beginTime, sexStr);
                nearbyMassagers = tnsService.getNearbyMassagersLevelSex(city, pid, lat, lng, level, beginTime, sexStr);
            }
        }
        // 格式化时间
        serviceTime = sdf.format(sdf.parse(serviceTime));
        massagerService.filterMassagers(nearbyMassagers, serviceTime, beginTime, endTime, lat, lng, did);
        for (int i = 0; i < nearbyMassagers.size(); i++) {
            Massager massager = nearbyMassagers.get(i);
            massager.setLjxd(this.orderService.getLjxdByMassagerId(massager.getId()));
            Map massagerParam = this.tnsPjService.getMassagerParam(massager.getId());
            massager.setPj(massagerParam.get("pj").toString());
            massager.setPj_num(massagerParam.get("count").toString());
            massager.setHpl(massagerParam.get("hpl").toString());
//            List<MassagistDTO> massagistDTOList = tnsTagsService.findTagsOfMassager(massager.getId());
//            List<TagGroupedEntity> tagGroupedEntities = new ArrayList<>();
//            BeanUtils.copyProperties(massagistDTOList, tagGroupedEntities);
//            massager.setTags(tagGroupedEntities);
            nearbyMassagers.set(i, massager);
        }
        //已点技师从可选技师中获取
        if (myMassagers != null && myMassagers.size() > 0 && nearbyMassagers != null && nearbyMassagers.size() > 0) {
            List<Massager> massagers = new ArrayList<>();
            for (Massager my : myMassagers) {
                for (Massager nearby : nearbyMassagers) {
                    if (my.getId().equals(nearby.getId())) {
                        massagers.add(nearby);
                        break;
                    }
                }
            }
            myMassagers = massagers;
        } else {
            myMassagers = new ArrayList<>();
        }
        Map<String, Object> result = new TreeMap<>();
        result.put("myMassagers", myMassagers);
        result.put("nearbyMassagers", nearbyMassagers);
        return GlobalResult.ok(result);
    }

    /**
     * 判断用户是否在服务区域内
     */
    @RequestMapping(value = "/isServiceArea", method = RequestMethod.GET)
    @ApiOperation(value = "判断用户是否在服务区域内",notes = "判断用户是否在服务区域内",response = GlobalResult.class)
    public GlobalResult isServiceArea(@PathVariable String userId, @RequestParam(value = "lat", required = false) Double lat,
                                 @RequestParam(value = "lng", required = false) Double lng,
                                 @RequestParam(value = "address", required = false) String address,
                                 @RequestParam(value = "tid", required = false) String tid) {
        //判断是否在技师接单范围内
        if (StringUtils.isNotBlank(tid) && !"0".equals(tid)) {
            MassagerDTO massagerDTO = tnsService.findById(tid);
            if (massagerDTO.getOrderrange() != null && massagerDTO.getOrderrange() != 0) {
                String coord = massagerDTO.getLocation();
                Double lng2 = Double.parseDouble(coord.substring(0, coord.indexOf(",")));
                Double lat2 = Double.parseDouble(coord.substring(coord.indexOf(",") + 1, coord.length()));
                Coord c = Distance.fromMars2BD(lng2, lat2);
                Double distance = Distance.getDistance(lng, lat, c.getLng(), c.getLat());
                if (distance > massagerDTO.getOrderrange() * 1000) {
                    return GlobalResult.ok(false);
                }
            }
        }
        //判断地址是否在公司的接单范围内
        if (lat != null && lng != null) {
            Geocoder geocoder = BaiduAPI.geocoder(lat + "," + lng);
            if (geocoder != null && geocoder.getStatus() == 0) {
                if (qyService.isServiceArea(geocoder.getResult().getAddressComponent().getCity(),
                        geocoder.getResult().getAddressComponent().getDistrict()))
                    return GlobalResult.ok(true);
                else
                    return GlobalResult.ok(false);
            }
        }
        if (address != null) {
            GeocoderByAddress geoResult = BaiduAPI.geocoderByAddress(address);
            if (geoResult != null && geoResult.getStatus() == 0) {
                lat = geoResult.getResult().getLocation().getLat();
                lng = geoResult.getResult().getLocation().getLng();
                Geocoder geocoder = BaiduAPI.geocoder(lat + "," + lng);
                if (geocoder != null && geocoder.getStatus() == 0) {
                    if (qyService.isServiceArea(geocoder.getResult().getAddressComponent().getCity(),
                            geocoder.getResult().getAddressComponent().getDistrict()))
                        return GlobalResult.ok(true);
                    else
                        return GlobalResult.ok(false);
                }
            }
        }
        return GlobalResult.ok(false);
    }

    /**
     * 根据订单状态查询用户订单
     */
    @RequestMapping(value = { "/order/list" }, method = RequestMethod.GET)
    @ApiOperation(value = "根据订单状态查询用户订单",notes = "根据订单状态查询用户订单",response = GlobalResult.class)
    public GlobalResult list(@PathVariable String userId, @RequestParam(value = "zt", required = false) String zt,
                                  @RequestParam(value = "index", required = false) Integer index,
                                  @RequestParam(value = "limit", required = false) Integer limit) {
        log.debug("/v1/{}/order/list?zt={}&index={}&limit={}", new Object[] { userId, zt, index, limit });
        OrderQueryInputDTO orderQueryInputDTO=new OrderQueryInputDTO();
        orderQueryInputDTO.setZt(zt);
        orderQueryInputDTO.setUserId(userId);
        orderQueryInputDTO.setPageSize(limit.toString());
        orderQueryInputDTO.setCurrentPage(index.toString());
        OrderPersistence orderPersistence=new OrderPersistence();
        BeanUtils.copyProperties(orderQueryInputDTO,orderPersistence);
        IPage<com.mmd.domain.dto.profile.OrderDTO> ordersTemp = orderService.findOrdersByZt(orderQueryInputDTO.makePaging(), orderPersistence);
        if (ordersTemp.getRecords().size() > 0) {
            List<OrderShow> orders = new ArrayList<>();
            List<com.mmd.domain.dto.profile.OrderDTO> ordersTemps = ordersTemp.getRecords();
            for (int i = 0; i < ordersTemps.size(); i++) {
                com.mmd.domain.dto.profile.OrderDTO o =  ordersTemps.get(i);
                OrderShow order = new OrderShow();
                // 是否可以取消
                if ((o.getZt().equals("1")) || (o.getZt().equals("2"))) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String dstr = TimeUtil.TimeConvert(o.getRq().toString()) + " " + o.getSj();
                    Date date = null;
                    try {
                        date = sdf.parse(dstr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (date.getTime() < System.currentTimeMillis() - 3600000L)
                        order.setCancelflag(0);
                    else
                        order.setCancelflag(1);
                }
                order.setId(o.getId());
                order.setOrder_sj(TimeUtil.TimeConvert(o.getRq()) + " " + o.getSj());
                order.setMoney(o.getZje());
                order.setPid(o.getPid());
                order.setComplete(o.getComplete());
                ProductDTO p = productService.findById(o.getPid());
                if (p != null) {
                    order.setProduct_name(p.getXmmc());
                    order.setTp(p.getTp());
                    order.setProductType(p.getType());
                } else {
                    order.setProduct_name("该项目已删除");
                    order.setProductType(0);
                }
                if ((o.getTid() == 0) || (this.tnsService.findById(o.getTid() + "") == null)) {
                    order.setTns_name("未指派");
                } else {
                    MassagerDTO m = this.tnsService.findById(o.getTid() + "");
                    order.setTns_phone(m.getPhone());
                    order.setTns_name(m.getXm());
                }
                order.setYjjtmoney(o.getYjjtmoeny());
                order.setZt(o.getZt());
                order.setCreatetime(TimeUtil.TimeConvertMin(o.getCreateTime()));
                order.setPaymenttype(o.getPaymenttype());
                order.setTid(o.getTid());
                //是否评价
                if (this.tnsPjService.findPjByOrderId(o.getId()) == null) {
                    order.setSfpj(0);
                } else {
                    order.setSfpj(1);
                }
                order.setProcuct_sl(o.getSl());
//                AddressDTO a = this.addressService.findAddressById(o.getDid());
//                order.setContact(a.getContact());
//                order.setPhone(a.getPhone());
//                order.setAddress(a.getAddressDetail());
//                order.setMph(a.getMph());
                order.setRemark(o.getRemark());
                order.setPromotionmoney(o.getPromotionmoney());
                order.setLevelmoney(o.getLevelmoney());
                order.setMassagelevel(o.getMassagelevel());
                order.setMassagersex(o.getMassagersex());
                order.setSexmoney(o.getSexmoney());
                order.setNumdiscount(o.getNumdiscount());
                order.setSettleprice(o.getSettleprice());
                order.setUid(o.getUid());
                CouponDTO couponDTO = couponService.getCouponByOid(o.getId() + "");
                if (couponDTO != null && couponDTO.getId() != 0) {
                    order.setCouponMoney(couponDTO.getMoney());
                } else {
                    order.setCouponMoney("0");
                }

                orders.add(i, order);
            }

            return GlobalResult.ok(orders);
        }
        return GlobalResult.ok();
    }
    /**
     * 修改订单状态
     * @throws ApiException
     */
    @RequestMapping(value = { "/order/updateZt" }, method = RequestMethod.POST)
    @ApiOperation(value = "修改订单状态",notes = "修改订单状态",response = GlobalResult.class)
    public GlobalResult updateZt(@PathVariable String userId, @RequestBody Map<String, Object> map)
            throws ApiException {
        log.debug("/v1/{}/order/updateZt", userId);

        if (orderService.UpdateOrderZt(map.get("orderId").toString(), map.get("ztNow").toString(),
                map.get("ztTo").toString())) {
            Order order = orderService.findById(Integer.parseInt(map.get("orderId").toString()));
            //技师出发时给用户发信息
            if ("9".equals(map.get("ztTo").toString())) {
                String rq = TimeUtil.timestampToMMdd(order.getRq().toString());
                String now = TimeUtil.timestampToMMdd((new Date()).getTime()/1000+"");
                if(now.equals(rq)) {
                    rq = rq+"(今天)";
                }
                Address address = addressService.queryAddressById(order.getDid());
                String mobile = address.getPhone();
                String msg = "感谢您预约"+ rq + order.getSj() + "的魔魔达上门服务，技师已出发，祝您体验愉快！如需变更订单信息，请致电4006233816";
                boolean needstatus = true;// 是否需要状态报告，需要true，不需要false
                String product = null;// 产品ID
                String extno = null;// 扩展码
                try {
                    String returnString = TextMessageUtil.batchSend(mobile, msg, needstatus, product, extno);
                    log.debug(returnString);
                    // TODO 处理返回值,参见HTTP协议文档
                } catch (Exception e) {
                    // TODO 处理异常
                    e.printStackTrace();
                }
            }
            return GlobalResult.ok(true);
        }
        return GlobalResult.ok(false);
    }

    /**
     * 删除订单
     * @throws ApiException
     */
    @GetMapping(value = { "/order/delete" })
    public GlobalResult deleteOrder(@RequestParam String orderId){
        int count = orderService.deleteOrder(orderId);
        return GlobalResult.ok(count>0);
    }

    /**
     * 获取门店可选技师
     */
    @RequestMapping(value = "/storeMassager", method = RequestMethod.GET)
    @ApiOperation(value="获取门店可选技师",notes="获取门店可选技师",response = GlobalResult.class)
    public GlobalResult storeMassager(@PathVariable String userId, @RequestParam(value = "serviceTime") String serviceTime,
                                        @RequestParam(value = "quantity") int quantity, @RequestParam(value = "sid") int sid,
                                        @RequestParam(value = "pid") int pid, @RequestParam(value = "level") int level
                                        ) throws ParseException {
        ProductDTO product = productService.findById(pid);
        int serviceTimeInMin = Integer.parseInt(product.getFzsj()) * quantity;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long beginTime = sdf.parse(serviceTime).getTime();
        long endTime = (sdf.parse(serviceTime).getTime() + 60 * serviceTimeInMin * 1000);
        //判断项目是否分星级
        List<LevelInfo> infos = productService.queryLevelInfoByPid(Long.valueOf(pid));
        List<Massager> storeMassagers;
        if (infos == null || infos.size() == 0) {
            storeMassagers = tnsService.getStoreMassagers(pid, sid, 0, null);
        } else {
            storeMassagers = tnsService.getStoreMassagers(pid, sid, level, null);
        }
        // 格式化时间
        serviceTime = sdf.format(sdf.parse(serviceTime));
        //筛除已点技师没空的
        orderService.filterMassagers4Store(storeMassagers, serviceTime, beginTime, endTime);
        for (int i = 0; i < storeMassagers.size(); i++) {
            Massager massager = storeMassagers.get(i);
            massager.setLjxd(orderService.getLjxdByMassagerId(massager.getId()));
            Map massagerParam = tnsPjService.getMassagerParam(massager.getId());
            massager.setPj(massagerParam.get("pj").toString());
            massager.setPj_num(massagerParam.get("count").toString());
            massager.setHpl(massagerParam.get("hpl").toString());
            massager.setTags(userService.findTagsOfMassager(massager.getId()));
            storeMassagers.set(i, massager);
        }
        return GlobalResult.ok(storeMassagers);
    }

    @GetMapping(value = "/getSuggestion")
    public GlobalResult getSuggestion(@RequestParam(value = "query", required = false) String query,
                                             @RequestParam(value = "region", required = false) String region) {
        PlaceSuggestion placeSuggestion = BaiduAPI.getPlaceSuggestion(query, region);
        return GlobalResult.ok(placeSuggestion);
    }

    @GetMapping(value = "/getUser")
    public GlobalResult getUser(@PathVariable String userId,@RequestParam String username) {
        String type = "1";
        String tid = this.userService.findUserType(username);
        if (tid != null) {
            type = "2";
        }
        boolean blackUser = userService.isBlackUser(userId);
        return GlobalResult.ok(
                LoginResponseVO.builder()
                        .tid(tid)
                        .type(type)
                        .isBlack(blackUser)
                        .uid(userId).build()
        );
    }

}
