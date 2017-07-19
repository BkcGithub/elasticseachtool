package com.ecotech.elasticsearchtools.document;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PlaceDocument {

    private Long id;

    private String encryptedId;

    private String fullName;

    private String fullNamePinYin;

    private String fullNamePinYinShort;

    private String cityName;

    private String provinceName;

    private Integer starRating;

    private String address;

    private String district;

    private String businessDistrict;

    private String isVerified;

    private Date openSinceDate;

    private BigDecimal geoLongitude;

    private BigDecimal geoLatitude;

    private Integer sizeSqMeter;

    private Integer sizeHeadcount;

    private Integer lowestHeadcount;

    private Integer highestHeadcount;

    private BigDecimal lowestPrice;

    private BigDecimal lowestDayPrice;

    private BigDecimal lowestHourPrice;

    private Integer lowestPriceUnit;

    // 场的展示
    private Integer lowestPriceUnitHours;

    private BigDecimal highestPrice;

    private Integer highestPriceUnit;

    private String shortDescription;

    private String facilityText;

    private String diningServiceText;

    private String miscReminderText;

    private String sales1Name;

    private String sales1PhoneNum;

    private String sales1ImId;

    private String sales1ImageUrl;

    private String sales1Position;

    private String submitterName;

    private String isListed;

    private Integer reviewRating;

    private Integer defaultSortValue;

    private List<String> placeTypeTags;

    private List<String> placeFacilityTags;

    private String frontpageImage;

    private List<String> functionalities;

    private List<String> pickTags;

    private String geoLatLng;

    private Map<String, Integer> funcSortMap;

    private BigDecimal listPrice;

    private Integer listPriceUnit;

    private BigDecimal lowestListPrice;

    private Integer lowestListPriceUnit;

    private BigDecimal highestListPrice;

    private Integer highestListPriceUnit;

    private String acceptPayment;

    private List<String> promoTags;

    private List<SpaceDocument> spaces;

    private Float reviewScore;
    private Integer favoritesCount;
    private RatePlanDocument flexPayRP;

    private String phoneExtNum;

    private String acceptInquiry;

    private Boolean instantConfirm;

    private String highQualityPlace;

    private List<String> promoTagsV2;

    private String hasReceipt;

    public String getHasReceipt() {
        return hasReceipt;
    }

    public void setHasReceipt(String hasReceipt) {
        this.hasReceipt = hasReceipt;
    }

    public List<String> getPromoTagsV2() {
        return promoTagsV2;
    }

    public void setPromoTagsV2(List<String> promoTagsV2) {
        this.promoTagsV2 = promoTagsV2;
    }

    public List<String> getFunctionalityShortNames() {
        return functionalityShortNames;
    }

    public void setFunctionalityShortNames(List<String> functionalityShortNames) {
        this.functionalityShortNames = functionalityShortNames;
    }

    private List<String> functionalityShortNames;



    public BigDecimal getLowestDayPrice() {
        return lowestDayPrice;
    }

    public void setLowestDayPrice(BigDecimal lowestDayPrice) {
        this.lowestDayPrice = lowestDayPrice;
    }

    public BigDecimal getMaxPlacePromo() {
        return maxPlacePromo;
    }

    public void setMaxPlacePromo(BigDecimal maxPlacePromo) {
        this.maxPlacePromo = maxPlacePromo;
    }

    private BigDecimal maxPlacePromo;

    public Boolean getInstantConfirm() {
        return instantConfirm;
    }

    public void setInstantConfirm(Boolean instantConfirm) {
        this.instantConfirm = instantConfirm;
    }

    public String getAcceptInquiry() {
        return acceptInquiry;
    }

    public void setAcceptInquiry(String acceptInquiry) {
        this.acceptInquiry = acceptInquiry;
    }

    public String getPhoneExtNum() {
        return phoneExtNum;
    }

    public void setPhoneExtNum(String phoneExtNum) {
        this.phoneExtNum = phoneExtNum;
    }

    public Integer getFavoritesCount() {
        return favoritesCount;
    }

    public void setFavoritesCount(Integer favoritesCount) {
        this.favoritesCount = favoritesCount;
    }

    public Float getReviewScore() {
        return reviewScore;
    }

    public void setReviewScore(Float reviewScore) {
        this.reviewScore = reviewScore;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public Integer getStarRating() {
        return starRating;
    }

    public void setStarRating(Integer starRating) {
        this.starRating = starRating;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getBusinessDistrict() {
        return businessDistrict;
    }

    public void setBusinessDistrict(String businessDistrict) {
        this.businessDistrict = businessDistrict;
    }

    public String getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(String isVerified) {
        this.isVerified = isVerified;
    }

    public Date getOpenSinceDate() {
        return openSinceDate;
    }

    public void setOpenSinceDate(Date openSinceDate) {
        this.openSinceDate = openSinceDate;
    }

    public BigDecimal getGeoLongitude() {
        return geoLongitude;
    }

    public void setGeoLongitude(BigDecimal geoLongitude) {
        this.geoLongitude = geoLongitude;
    }

    public BigDecimal getGeoLatitude() {
        return geoLatitude;
    }

    public void setGeoLatitude(BigDecimal geoLatitude) {
        this.geoLatitude = geoLatitude;
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

    public BigDecimal getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(BigDecimal lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public Integer getLowestPriceUnit() {
        return lowestPriceUnit;
    }

    public void setLowestPriceUnit(Integer lowestPriceUnit) {
        this.lowestPriceUnit = lowestPriceUnit;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getFacilityText() {
        return facilityText;
    }

    public void setFacilityText(String facilityText) {
        this.facilityText = facilityText;
    }

    public String getDiningServiceText() {
        return diningServiceText;
    }

    public void setDiningServiceText(String diningServiceText) {
        this.diningServiceText = diningServiceText;
    }

    public String getMiscReminderText() {
        return miscReminderText;
    }

    public void setMiscReminderText(String miscReminderText) {
        this.miscReminderText = miscReminderText;
    }

    public String getSales1Name() {
        return sales1Name;
    }

    public void setSales1Name(String sales1Name) {
        this.sales1Name = sales1Name;
    }

    public String getSales1PhoneNum() {
        return sales1PhoneNum;
    }

    public void setSales1PhoneNum(String sales1PhoneNum) {
        this.sales1PhoneNum = sales1PhoneNum;
    }

    public String getSales1ImId() {
        return sales1ImId;
    }

    public void setSales1ImId(String sales1ImId) {
        this.sales1ImId = sales1ImId;
    }

    public String getSales1ImageUrl() {
        return sales1ImageUrl;
    }

    public void setSales1ImageUrl(String sales1ImageUrl) {
        this.sales1ImageUrl = sales1ImageUrl;
    }

    public String getSales1Position() {
        return sales1Position;
    }

    public void setSales1Position(String sales1Position) {
        this.sales1Position = sales1Position;
    }

    public String getSubmitterName() {
        return submitterName;
    }

    public void setSubmitterName(String submitterName) {
        this.submitterName = submitterName;
    }

    public String getIsListed() {
        return isListed;
    }

    public void setIsListed(String isListed) {
        this.isListed = isListed;
    }

    public Integer getReviewRating() {
        return reviewRating;
    }

    public void setReviewRating(Integer reviewRating) {
        this.reviewRating = reviewRating;
    }

    public Integer getDefaultSortValue() {
        return defaultSortValue;
    }

    public void setDefaultSortValue(Integer defaultSortValue) {
        this.defaultSortValue = defaultSortValue;
    }

    public List<String> getPlaceTypeTags() {
        return placeTypeTags;
    }

    public void setPlaceTypeTags(List<String> placeTypeTags) {
        this.placeTypeTags = placeTypeTags;
    }

    public List<String> getPlaceFacilityTags() {
        return placeFacilityTags;
    }

    public void setPlaceFacilityTags(List<String> placeFacilityTags) {
        this.placeFacilityTags = placeFacilityTags;
    }

    public String getFrontpageImage() {
        return frontpageImage;
    }

    public void setFrontpageImage(String frontpageImage) {
        this.frontpageImage = frontpageImage;
    }

    public List<String> getFunctionalities() {
        return functionalities;
    }

    public void setFunctionalities(List<String> functionalities) {
        this.functionalities = functionalities;
    }

    public String getGeoLatLng() {
        return geoLatLng;
    }

    public void setGeoLatLng(String geoLatLng) {
        this.geoLatLng = geoLatLng;
    }

    @Override
    public String toString() {
        return "PlaceDocument [id=" + id + ", encryptedId=" + encryptedId
            + ", fullName=" + fullName + ", fullNamePinYin="
            + fullNamePinYin + ", fullNamePinYinShort="
            + fullNamePinYinShort + ", cityName=" + cityName
            + ", provinceName=" + provinceName + ", starRating="
            + starRating + ", address=" + address + ", district="
            + district + ", businessDistrict=" + businessDistrict
            + ", isVerified=" + isVerified + ", openSinceDate="
            + openSinceDate + ", geoLongitude=" + geoLongitude
            + ", geoLatitude=" + geoLatitude + ", sizeSqMeter="
            + sizeSqMeter + ", sizeHeadcount=" + sizeHeadcount
            + ", lowestHeadcount=" + lowestHeadcount
            + ", highestHeadcount=" + highestHeadcount + ", lowestPrice="
            + lowestPrice + ", lowestPriceUnit=" + lowestPriceUnit
            + ", highestPrice=" + highestPrice + ", highestPriceUnit="
            + highestPriceUnit + ", shortDescription=" + shortDescription
            + ", facilityText=" + facilityText + ", diningServiceText="
            + diningServiceText + ", miscReminderText=" + miscReminderText
            + ", sales1Name=" + sales1Name + ", sales1PhoneNum="
            + sales1PhoneNum + ", sales1ImId=" + sales1ImId
            + ", sales1ImageUrl=" + sales1ImageUrl + ", sales1Position="
            + sales1Position + ", submitterName=" + submitterName
            + ", isListed=" + isListed + ", reviewRating=" + reviewRating
            + ", defaultSortValue=" + defaultSortValue + ", placeTypeTags="
            + placeTypeTags + ", placeFacilityTags=" + placeFacilityTags
            + ", frontpageImage=" + frontpageImage + ", functionalities="
            + functionalities + ", pickTags=" + pickTags + ", geoLatLng="
            + geoLatLng + ", funcSortMap=" + funcSortMap + ", listPrice="
            + listPrice + ", listPriceUnit=" + listPriceUnit
            + ", lowestListPrice=" + lowestListPrice
            + ", lowestListPriceUnit=" + lowestListPriceUnit
            + ", highestListPrice=" + highestListPrice
            + ", highestListPriceUnit=" + highestListPriceUnit
            + ", acceptPayment=" + acceptPayment + ", promoTags="
            + promoTags + ", spaces=" + spaces
            + ", instantConfirm=" + instantConfirm
            + ", maxPlacePromo=" + maxPlacePromo + "]";
    }

    public String getEncryptedId() {
        return encryptedId;
    }

    public void setEncryptedId(String encryptedId) {
        this.encryptedId = encryptedId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getPickTags() {
        return pickTags;
    }

    public void setPickTags(List<String> pickTags) {
        this.pickTags = pickTags;
    }

    public String getFullNamePinYin() {
        return fullNamePinYin;
    }

    public void setFullNamePinYin(String fullNamePinYin) {
        this.fullNamePinYin = fullNamePinYin;
    }

    public String getFullNamePinYinShort() {
        return fullNamePinYinShort;
    }

    public void setFullNamePinYinShort(String fullNamePinYinShort) {
        this.fullNamePinYinShort = fullNamePinYinShort;
    }

    public Map<String, Integer> getFuncSortMap() {
        return funcSortMap;
    }

    public void setFuncSortMap(Map<String, Integer> funcSortMap) {
        this.funcSortMap = funcSortMap;
    }

    public BigDecimal getListPrice() {
        return listPrice;
    }

    public void setListPrice(BigDecimal listPrice) {
        this.listPrice = listPrice;
    }

    public Integer getListPriceUnit() {
        return listPriceUnit;
    }

    public void setListPriceUnit(Integer listPriceUnit) {
        this.listPriceUnit = listPriceUnit;
    }

    public String getAcceptPayment() {
        return acceptPayment;
    }

    public void setAcceptPayment(String acceptPayment) {
        this.acceptPayment = acceptPayment;
    }

    public List<String> getPromoTags() {
        return promoTags;
    }

    public void setPromoTags(List<String> promoTags) {
        this.promoTags = promoTags;
    }

    public Integer getLowestHeadcount() {
        return lowestHeadcount;
    }

    public void setLowestHeadcount(Integer lowestHeadcount) {
        this.lowestHeadcount = lowestHeadcount;
    }

    public Integer getHighestHeadcount() {
        return highestHeadcount;
    }

    public void setHighestHeadcount(Integer highestHeadcount) {
        this.highestHeadcount = highestHeadcount;
    }

    public BigDecimal getHighestPrice() {
        return highestPrice;
    }

    public void setHighestPrice(BigDecimal highestPrice) {
        this.highestPrice = highestPrice;
    }

    public Integer getHighestPriceUnit() {
        return highestPriceUnit;
    }

    public void setHighestPriceUnit(Integer highestPriceUnit) {
        this.highestPriceUnit = highestPriceUnit;
    }

    public BigDecimal getLowestListPrice() {
        return lowestListPrice;
    }

    public void setLowestListPrice(BigDecimal lowestListPrice) {
        this.lowestListPrice = lowestListPrice;
    }

    public Integer getLowestListPriceUnit() {
        return lowestListPriceUnit;
    }

    public void setLowestListPriceUnit(Integer lowestListPriceUnit) {
        this.lowestListPriceUnit = lowestListPriceUnit;
    }

    public BigDecimal getHighestListPrice() {
        return highestListPrice;
    }

    public void setHighestListPrice(BigDecimal highestListPrice) {
        this.highestListPrice = highestListPrice;
    }

    public Integer getHighestListPriceUnit() {
        return highestListPriceUnit;
    }

    public void setHighestListPriceUnit(Integer highestListPriceUnit) {
        this.highestListPriceUnit = highestListPriceUnit;
    }

    public List<SpaceDocument> getSpaces() {
        return spaces;
    }

    public void setSpaces(List<SpaceDocument> spaces) {
        this.spaces = spaces;
    }

    public RatePlanDocument getFlexPayRP() {
        return flexPayRP;
    }

    public void setFlexPayRP(RatePlanDocument flexPayRP) {
        this.flexPayRP = flexPayRP;
    }

    public Integer getLowestPriceUnitHours() {
        return lowestPriceUnitHours;
    }

    public void setLowestPriceUnitHours(Integer lowestPriceUnitHours) {
        this.lowestPriceUnitHours = lowestPriceUnitHours;
    }

    public String getHighQualityPlace() {
        return highQualityPlace;
    }

    public void setHighQualityPlace(String highQualityPlace) {
        this.highQualityPlace = highQualityPlace;
    }

    public BigDecimal getLowestHourPrice() {
        return lowestHourPrice;
    }

    public void setLowestHourPrice(BigDecimal lowestHourPrice) {
        this.lowestHourPrice = lowestHourPrice;
    }

}
