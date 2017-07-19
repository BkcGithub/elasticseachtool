package com.ecotech.elasticsearchtools.document;

import java.math.BigDecimal;
import java.util.List;


public class SpaceDocument {

    private Long id;

    private String encryptedId;

    private String fullName;

    private BigDecimal heightMeter;

    private BigDecimal lengthMeter;

    private BigDecimal widthMeter;

    private Integer sizeSqMeter;

    private Integer sizeHeadcount;

    @Deprecated // After Rate Plan launch
    private BigDecimal lowestPrice;
    @Deprecated // After Rate Plan launch
    private Integer lowestPriceUnit;
    @Deprecated // After Rate Plan launch
    private BigDecimal listPrice;
    @Deprecated // After Rate Plan launch
    private Integer listPriceUnit;

    private Integer headcountTheater;

    private Integer headcountClassroom;

    private Integer headcountU;

    private Integer headcountRectangular;

    private Integer headcountBanquet;

    private Integer headcountCocktail;

    private Integer headcountGroup;

    private Integer headcountBoardMeeting;

    private Integer headcountFishbone;

    private List<String> promoTags;

    private List<RatePlanDocument> ratePlans;

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public BigDecimal getHeightMeter() {
        return heightMeter;
    }

    public void setHeightMeter(BigDecimal heightMeter) {
        this.heightMeter = heightMeter;
    }

    public BigDecimal getLengthMeter() {
        return lengthMeter;
    }

    public void setLengthMeter(BigDecimal lengthMeter) {
        this.lengthMeter = lengthMeter;
    }

    public BigDecimal getWidthMeter() {
        return widthMeter;
    }

    public void setWidthMeter(BigDecimal widthMeter) {
        this.widthMeter = widthMeter;
    }

    public Integer getSizeSqMeter() {
        return sizeSqMeter;
    }

    public void setSizeSqMeter(Integer sizeSqMeter) {
        this.sizeSqMeter = sizeSqMeter;
    }

    public Integer getSizeHeadcount() {
        return sizeHeadcount;
    }

    public void setSizeHeadcount(Integer sizeHeadcount) {
        this.sizeHeadcount = sizeHeadcount;
    }

    @Deprecated // After Rate Plan launch
    public BigDecimal getLowestPrice() {
        return lowestPrice;
    }

    @Deprecated // After Rate Plan launch
    public void setLowestPrice(BigDecimal lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    @Deprecated // After Rate Plan launch
    public Integer getLowestPriceUnit() {
        return lowestPriceUnit;
    }

    @Deprecated // After Rate Plan launch
    public void setLowestPriceUnit(Integer lowestPriceUnit) {
        this.lowestPriceUnit = lowestPriceUnit;
    }

    @Deprecated // After Rate Plan launch
    public BigDecimal getListPrice() {
        return listPrice;
    }

    @Deprecated // After Rate Plan launch
    public void setListPrice(BigDecimal listPrice) {
        this.listPrice = listPrice;
    }

    @Deprecated // After Rate Plan launch
    public Integer getListPriceUnit() {
        return listPriceUnit;
    }

    @Deprecated // After Rate Plan launch
    public void setListPriceUnit(Integer listPriceUnit) {
        this.listPriceUnit = listPriceUnit;
    }

    public Integer getHeadcountTheater() {
        return headcountTheater;
    }

    public void setHeadcountTheater(Integer headcountTheater) {
        this.headcountTheater = headcountTheater;
    }

    public Integer getHeadcountClassroom() {
        return headcountClassroom;
    }

    public void setHeadcountClassroom(Integer headcountClassroom) {
        this.headcountClassroom = headcountClassroom;
    }

    public Integer getHeadcountU() {
        return headcountU;
    }

    public void setHeadcountU(Integer headcountU) {
        this.headcountU = headcountU;
    }

    public Integer getHeadcountRectangular() {
        return headcountRectangular;
    }

    public void setHeadcountRectangular(Integer headcountRectangular) {
        this.headcountRectangular = headcountRectangular;
    }

    public Integer getHeadcountBanquet() {
        return headcountBanquet;
    }

    public void setHeadcountBanquet(Integer headcountBanquet) {
        this.headcountBanquet = headcountBanquet;
    }

    public Integer getHeadcountCocktail() {
        return headcountCocktail;
    }

    public void setHeadcountCocktail(Integer headcountCocktail) {
        this.headcountCocktail = headcountCocktail;
    }

    public Integer getHeadcountGroup() {
        return headcountGroup;
    }

    public void setHeadcountGroup(Integer headcountGroup) {
        this.headcountGroup = headcountGroup;
    }

    public Integer getHeadcountBoardMeeting() {
        return headcountBoardMeeting;
    }

    public void setHeadcountBoardMeeting(Integer headcountBoardMeeting) {
        this.headcountBoardMeeting = headcountBoardMeeting;
    }

    public Integer getHeadcountFishbone() {
        return headcountFishbone;
    }

    public void setHeadcountFishbone(Integer headcountFishbone) {
        this.headcountFishbone = headcountFishbone;
    }

    public List<String> getPromoTags() {
        return promoTags;
    }

    public void setPromoTags(List<String> promoTags) {
        this.promoTags = promoTags;
    }

    public List<RatePlanDocument> getRatePlans() {
        return ratePlans;
    }

    public void setRatePlans(List<RatePlanDocument> ratePlans) {
        this.ratePlans = ratePlans;
    }

}
