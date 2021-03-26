package com.mmd.entity;

/**
 * Created by qin on 2016/3/7.
 */
public class OrderInfo {
    private Long rq;
    private String sj;
    private Integer sl;
    private Integer fzsj;
    private Double lng;
    private Double lat;
    private Double distance;
    private Integer interval;
    private Long begin;
    private Long end;
    private Integer did;

    public Integer getDid() {
        return did;
    }

    public void setDid(Integer did) {
        this.did = did;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Long getBegin() {
        return begin;
    }

    public void setBegin(Long begin) {
        this.begin = begin;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public Long getRq() {
        return rq;
    }

    public void setRq(Long rq) {
        this.rq = rq;
    }

    public String getSj() {
        return sj;
    }

    public void setSj(String sj) {
        this.sj = sj;
    }

    public Integer getSl() {
        return sl;
    }

    public void setSl(Integer sl) {
        this.sl = sl;
    }

    public Integer getFzsj() {
        return fzsj;
    }

    public void setFzsj(Integer fzsj) {
        this.fzsj = fzsj;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
