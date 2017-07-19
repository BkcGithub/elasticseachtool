package com.ecotech.elasticsearchtools.client;

import java.math.BigDecimal;
import java.util.List;

public class ESSearchParameter {

    private String city;

    private SearchType searchType;


    public enum SearchType {
        POI, LBS, PLACENAME, DISTRICT, BUSINESSDISTRICT, TYPE, FUNCTIONALITY, IDS, PICKTAG, FACILITYTAG
    }


    private String q;

    private List<Long> ids;

    private List<Integer> starRatings;
    private List<Integer> weekdays;

    private Double geoLatitude;
    private Double geoLongitude;

    private Integer distanceMeters;

    private ListedFilter listedFilter;


    public enum ListedFilter {
        ALL, LISTED, UNLISTED
    }


    private Boolean placePromotion;

    public Boolean getPlacePromotion() {
        return placePromotion;
    }

    public void setPlacePromotion(Boolean placePromotion) {
        this.placePromotion = placePromotion;
    }

    private List<String> types;
    private List<String> functionalities;
    private List<String> pickTags;

    public List<Integer> getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(List<Integer> weekdays) {
        this.weekdays = weekdays;
    }

    private List<String> facilityTags;

    private String district;
    private String businessDistrict;

    private Integer headcountLower;
    private Integer headcountUpper;
    private Integer priceLower;
    private Integer priceUpper;
    private Integer dayPriceLower;
    private Integer dayPriceUpper;
    private Integer hourPriceLower;
    private Integer hourPriceUpper;
    private List<OrderType> orderTypes;
    private Boolean instantConfirm;

    private Integer heightMeterLower;
    private Integer heightMeterUpper;
    private Integer sizeSqMeterLower;
    private Integer sizeSqMeterUpper;
    private String highQualityPlace;

    private List<String> promoTagsV2;

    private String hasReceipt;

    public String getHasReceipt() {
        return hasReceipt;
    }

    public void setHasReceipt(String hasReceipt) {
        this.hasReceipt = hasReceipt;
    }

    public Integer getDayPriceLower() {
        return dayPriceLower;
    }

    public void setDayPriceLower(Integer dayPriceLower) {
        this.dayPriceLower = dayPriceLower;
    }

    public Integer getDayPriceUpper() {
        return dayPriceUpper;
    }

    public void setDayPriceUpper(Integer dayPriceUpper) {
        this.dayPriceUpper = dayPriceUpper;
    }

    public Integer getHourPriceLower() {
        return hourPriceLower;
    }

    public void setHourPriceLower(Integer hourPriceLower) {
        this.hourPriceLower = hourPriceLower;
    }

    public Integer getHourPriceUpper() {
        return hourPriceUpper;
    }

    public void setHourPriceUpper(Integer hourPriceUpper) {
        this.hourPriceUpper = hourPriceUpper;
    }


    public enum PriceUnitFilter {
        ALL(null), DAY(24), HALFDAY(12), HOUR(1), PERSON(0);
        Integer value;

        PriceUnitFilter(Integer v) {
            value = v;
        }

        public Integer getValue() {
            return value;
        }
    }


    private BigDecimal maxPlacePromo;

    public BigDecimal getMaxPlacePromo() {
        return maxPlacePromo;
    }

    public void setMaxPlacePromo(BigDecimal maxPlacePromo) {
        this.maxPlacePromo = maxPlacePromo;
    }

    PriceUnitFilter lowestPriceUnitFilter;


    public enum SpaceLayoutFilter {

        THEATER("headcountTheater"), CLASSROOM("headcountClassroom"), U("headcountU"), RECTANGULAR(
            "headcountRectangular"), BANQUET("headcountBanquet"), COCKTAIL("headcountCocktail"), GROUP(
            "headcountGroup"), BOARDMEETING("headcountBoardMeeting"), FISHBONE("headcountFishbone");

        SpaceLayoutFilter(String name) {
            docFieldName = name;
        }

        String docFieldName;

        public String getDocFieldName() {
            return docFieldName;
        }

    }


    List<SpaceLayoutFilter> spaceLayoutFilterList;

    /**
     * Pagination
     */
    private Integer limit;
    private Integer offset;

    /**
     * Order by
     */
    private OrderByField orderByField;

    private OrderByDirection orderByDirection;


    public enum OrderByField {
        AUTO, PRICE, REVIEWRATING, DISTANCE, ID, FUNCINTERNAL, FAVORITESCOUNT, DAYPRICE, HOURPRICE
    }


    public enum OrderByDirection {
        ASC, DESC
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public Double getGeoLatitude() {
        return geoLatitude;
    }

    public void setGeoLatitude(Double geoLatitude) {
        this.geoLatitude = geoLatitude;
    }

    public Double getGeoLongitude() {
        return geoLongitude;
    }

    public void setGeoLongitude(Double geoLongitude) {
        this.geoLongitude = geoLongitude;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<String> getFunctionalities() {
        return functionalities;
    }

    public void setFunctionalities(List<String> functionalities) {
        this.functionalities = functionalities;
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

    public Integer getHeadcountLower() {
        return headcountLower;
    }

    public void setHeadcountLower(Integer headcountLower) {
        this.headcountLower = headcountLower;
    }

    public Integer getHeadcountUpper() {
        return headcountUpper;
    }

    public void setHeadcountUpper(Integer headcountUpper) {
        this.headcountUpper = headcountUpper;
    }

    public Integer getPriceLower() {
        return priceLower;
    }

    public void setPriceLower(Integer priceLower) {
        this.priceLower = priceLower;
    }

    public Integer getPriceUpper() {
        return priceUpper;
    }

    public void setPriceUpper(Integer priceUpper) {
        this.priceUpper = priceUpper;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public OrderByField getOrderByField() {
        return orderByField;
    }

    public void setOrderByField(OrderByField orderByField) {
        this.orderByField = orderByField;
    }

    public OrderByDirection getOrderByDirection() {
        return orderByDirection;
    }

    public void setOrderByDirection(OrderByDirection orderByDirection) {
        this.orderByDirection = orderByDirection;
    }

    public Integer getDistanceMeters() {
        return distanceMeters;
    }

    public void setDistanceMeters(Integer distanceMeters) {
        this.distanceMeters = distanceMeters;
    }

    public SearchType getSearchType() {
        return searchType;
    }

    public void setSearchType(SearchType searchType) {
        this.searchType = searchType;
    }


    public List<String> getPickTags() {
        return pickTags;
    }

    public void setPickTags(List<String> pickTags) {
        this.pickTags = pickTags;
    }

    public ListedFilter getListedFilter() {
        return listedFilter;
    }

    public void setListedFilter(ListedFilter listedFilter) {
        this.listedFilter = listedFilter;
    }

    public List<Long> getIds() {
        return ids;
    }

    public List<Integer> getStarRatings() {
        return starRatings;
    }

    public void setStarRatings(List<Integer> starRatings) {
        this.starRatings = starRatings;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public List<String> getFacilityTags() {
        return facilityTags;
    }

    public void setFacilityTags(List<String> facilityTags) {
        this.facilityTags = facilityTags;
    }

    public PriceUnitFilter getLowestPriceUnitFilter() {
        return lowestPriceUnitFilter;
    }

    public void setLowestPriceUnitFilter(PriceUnitFilter lowestPriceUnitFilter) {
        this.lowestPriceUnitFilter = lowestPriceUnitFilter;
    }

    public List<SpaceLayoutFilter> getSpaceLayoutFilterList() {
        return spaceLayoutFilterList;
    }

    public void setSpaceLayoutFilterList(
        List<SpaceLayoutFilter> spaceLayoutFilterList) {
        this.spaceLayoutFilterList = spaceLayoutFilterList;
    }

    public enum OrderType {
        FLEXPAY, PREPAY, CASHBACK
    }

    public List<OrderType> getOrderTypes() {
        return orderTypes;
    }

    public void setOrderTypes(List<OrderType> orderTypes) {
        this.orderTypes = orderTypes;
    }


    public Boolean getInstantConfirm() {
        return instantConfirm;
    }

    public void setInstantConfirm(Boolean instantConfirm) {
        this.instantConfirm = instantConfirm;
    }

    public Integer getHeightMeterLower() {
        return heightMeterLower;
    }

    public void setHeightMeterLower(Integer heightMeterLower) {
        this.heightMeterLower = heightMeterLower;
    }

    public Integer getHeightMeterUpper() {
        return heightMeterUpper;
    }

    public void setHeightMeterUpper(Integer heightMeterUpper) {
        this.heightMeterUpper = heightMeterUpper;
    }

    public Integer getSizeSqMeterLower() {
        return sizeSqMeterLower;
    }

    public void setSizeSqMeterLower(Integer sizeSqMeterLower) {
        this.sizeSqMeterLower = sizeSqMeterLower;
    }

    public Integer getSizeSqMeterUpper() {
        return sizeSqMeterUpper;
    }

    public void setSizeSqMeterUpper(Integer sizeSqMeterUpper) {
        this.sizeSqMeterUpper = sizeSqMeterUpper;
    }


    public String getHighQualityPlace() {
        return highQualityPlace;
    }

    public void setHighQualityPlace(String highQualityPlace) {
        this.highQualityPlace = highQualityPlace;
    }

    public List<String> getPromoTagsV2() {
        return promoTagsV2;
    }

    public void setPromoTagsV2(List<String> promoTagsV2) {
        this.promoTagsV2 = promoTagsV2;
    }

    @Override
    public String toString() {
        return "ESSearchParameter [city=" + city + ", searchType=" + searchType
            + ", q=" + q + ", ids=" + ids + ", starRatings=" + starRatings + ", geoLatitude=" + geoLatitude
            + ", geoLongitude=" + geoLongitude + ", distanceMeters="
            + distanceMeters + ", listedFilter=" + listedFilter
            + ", types=" + types + ", functionalities=" + functionalities
            + ", pickTags=" + pickTags + ", facilityTags=" + facilityTags
            + ", district=" + district + ", businessDistrict="
            + businessDistrict + ", headcountLower=" + headcountLower
            + ", headcountUpper=" + headcountUpper + ", priceLower="
            + priceLower + ", priceUpper=" + priceUpper
            + ", lowestPriceUnitFilter=" + lowestPriceUnitFilter
            + ", spaceLayoutFilterList=" + spaceLayoutFilterList
            + ", limit=" + limit + ", offset=" + offset
            + ", orderByField=" + orderByField
            + ", orderByDirection=" + orderByDirection
            + ", instantConfirm=" + instantConfirm
            + ", heightMeterLower=" + heightMeterLower
            + ", heightMeterUpper=" + heightMeterUpper
            + ", sizeSqMeterLower=" + sizeSqMeterLower
            + ", sizeSqMeterUpper=" + sizeSqMeterUpper
            + "]";
    }

}
