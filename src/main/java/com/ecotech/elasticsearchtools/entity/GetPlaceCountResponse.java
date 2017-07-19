package com.ecotech.elasticsearchtools.entity;

import java.util.Map;

/**
 * 指定城市下所有分类对应的场地数量以及总数量
 * 
 * @author Kyrin
 *
 */
public class GetPlaceCountResponse {

    private String cityName;

    private Long count;

    // key => 场地类型 ，value => 类型对应的场地数量
    private Map<String, Long> result;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Map<String, Long> getResult() {
        return result;
    }

    public void setResult(Map<String, Long> result) {
        this.result = result;
    }

}
