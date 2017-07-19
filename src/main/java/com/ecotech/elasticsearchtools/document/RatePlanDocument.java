package com.ecotech.elasticsearchtools.document;

import java.math.BigDecimal;
import java.util.List;


public class RatePlanDocument {

    private Long id;
    private String encryptedId;
    private String name;
    private BigDecimal listPrice;
    private BigDecimal reservedPrice;
    private BigDecimal ourPrice;
    private BigDecimal depositPrice;
    private BigDecimal dayPrice;
    private BigDecimal hourPrice;
    private Integer priceUnit;
    private String orderType;
    private String orderSubtype;
    private List<String> promoTags;
    private String description;
    private Integer sequenceId;

    private List<Integer> weekdays;

    public BigDecimal getDayPrice() {
        return dayPrice;
    }

    public void setDayPrice(BigDecimal dayPrice) {
        this.dayPrice = dayPrice;
    }

    public BigDecimal getHourPrice() {
        return hourPrice;
    }

    public void setHourPrice(BigDecimal hourPrice) {
        this.hourPrice = hourPrice;
    }

    // rp增加周几搜索
    public List<Integer> getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(List<Integer> weekdays) {
        this.weekdays = weekdays;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEncryptedId() {
        return encryptedId;
    }

    public void setEncryptedId(String encryptedId) {
        this.encryptedId = encryptedId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getListPrice() {
        return listPrice;
    }

    public void setListPrice(BigDecimal listPrice) {
        this.listPrice = listPrice;
    }

    public BigDecimal getReservedPrice() {
        return reservedPrice;
    }

    public void setReservedPrice(BigDecimal reservedPrice) {
        this.reservedPrice = reservedPrice;
    }

    public BigDecimal getOurPrice() {
        return ourPrice;
    }

    public void setOurPrice(BigDecimal ourPrice) {
        this.ourPrice = ourPrice;
    }

    public BigDecimal getDepositPrice() {
        return depositPrice;
    }

    public void setDepositPrice(BigDecimal depositPrice) {
        this.depositPrice = depositPrice;
    }

    public Integer getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(Integer priceUnit) {
        this.priceUnit = priceUnit;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderSubtype() {
        return orderSubtype;
    }

    public void setOrderSubtype(String orderSubtype) {
        this.orderSubtype = orderSubtype;
    }

    public List<String> getPromoTags() {
        return promoTags;
    }

    public void setPromoTags(List<String> promoTags) {
        this.promoTags = promoTags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(Integer sequenceId) {
        this.sequenceId = sequenceId;
    }

}
