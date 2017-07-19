package com.ecotech.elasticsearchtools.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.ecotech.common.obfuscate.ObfuscateUtil;
import com.ecotech.elasticsearchtools.client.ESSearchParameter;
import com.ecotech.elasticsearchtools.client.ESSearchParameter.PriceUnitFilter;
import com.ecotech.elasticsearchtools.document.PlaceDocument;
import com.ecotech.elasticsearchtools.document.RatePlanDocument;
import com.ecotech.elasticsearchtools.document.SpaceDocument;
import com.ecotech.productservice.type.PlaceType;
import com.ecotech.productservice.type.RatePlanType;
import com.ecotech.productservice.type.SpaceType;
import com.ecotech.productservice.type.search.SearchPlaceTypeBase;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import org.elasticsearch.common.util.CollectionUtils;

public class PlaceDocumentHelper {
    public static PlaceDocument convertToDocument(final PlaceType type) {
        if (null == type) {
            return null;
        }
        PlaceDocument doc = new PlaceDocument();
        doc.setId(ObfuscateUtil.fromObfuscatedId(type.getId()));
        doc.setEncryptedId(type.getId());
        doc.setFullName(type.getFullName());
        if (null != type.getFullName()) {
            doc.setFullNamePinYin(
                PinyinHelper.convertToPinyinString(type.getFullName(), "", PinyinFormat.WITHOUT_TONE));
            doc.setFullNamePinYinShort(PinyinHelper.getShortPinyin(type.getFullName()));
        }
        doc.setCityName(type.getCityName());
        doc.setProvinceName(type.getProvinceName());
        doc.setStarRating(type.getStarRating());
        doc.setAddress(type.getAddress());
        doc.setDistrict(type.getDistrict());
        doc.setBusinessDistrict(type.getBusinessDistrict());
        doc.setIsVerified(type.getIsVerified());
        doc.setOpenSinceDate(type.getOpenSinceDate());
        doc.setGeoLatitude(type.getGeoLatitude());
        doc.setGeoLongitude(type.getGeoLongitude());
        doc.setSizeSqMeter(type.getSizeSqMeter());
        doc.setSizeHeadcount(type.getSizeHeadcount());
        doc.setLowestHeadcount(type.getLowestHeadcount());
        doc.setHighestHeadcount(type.getHighestHeadcount());
        doc.setLowestPrice(type.getLowestPrice());
        doc.setLowestDayPrice(type.getLowestDayPrice());
        doc.setLowestHourPrice(type.getLowestHourPrice());
        doc.setLowestPriceUnit(type.getLowestPriceUnit());
        doc.setHighestPrice(type.getHighestPrice());
        doc.setHighestPriceUnit(type.getHighestPriceUnit());
        // 场的展示
        if (type.getLowestPriceUnitHours() != null) {
            doc.setLowestPriceUnitHours(type.getLowestPriceUnitHours());
        }
        doc.setShortDescription(type.getShortDescription());
        doc.setFacilityText(type.getFacilityText());
        doc.setDiningServiceText(type.getDiningServiceText());
        doc.setMiscReminderText(type.getMiscReminderText());
        doc.setSales1Name(type.getSales1Name());
        doc.setSales1PhoneNum(type.getSales1PhoneNum());
        doc.setSales1ImId(type.getSales1ImId());
        doc.setSales1Position(type.getSales1Position());
        doc.setSubmitterName(type.getSubmitterName());
        doc.setIsListed(type.getIsListed());
        doc.setReviewRating(type.getReviewRating());
        doc.setDefaultSortValue(type.getDefaultSortValue());
        doc.setPlaceTypeTags(type.getPlaceTypeTags());
        doc.setFunctionalities(type.getFunctionalities());
        doc.setPickTags(type.getPickTags());
        doc.setPlaceFacilityTags(type.getPlaceFacilityTags());
        doc.setListPrice(type.getListPrice());
        doc.setListPriceUnit(type.getListPriceUnit());

        doc.setLowestListPrice(type.getLowestListPrice());
        doc.setLowestListPriceUnit(type.getLowestListPriceUnit());
        doc.setHighestListPrice(type.getHighestListPrice());
        doc.setHighestListPriceUnit(type.getHighestListPriceUnit());
        doc.setAcceptPayment(type.getAcceptPayment());
        doc.setPromoTags(type.getPromoTags());
        doc.setReviewScore(type.getReviewScore());
        doc.setFavoritesCount(type.getFavoritesCount());
        doc.setPhoneExtNum(type.getPhoneExtNum());
        doc.setAcceptInquiry(type.getAcceptInquiry());
        doc.setInstantConfirm(type.getInstantConfirm());
        doc.setHasReceipt(type.getHasReceipt());

        doc.setMaxPlacePromo(type.getMaxPlacePromo());
        if(type.getFunctionalityShortNames() != null && type.getFunctionalityShortNames().size() != 0){
            doc.setFunctionalityShortNames(type.getFunctionalityShortNames());
        }
        if(type.getPromoTagsV2() != null && type.getPromoTagsV2().size() != 0){
            doc.setPromoTagsV2(type.getPromoTagsV2());
        }
        if (null != type.getPlaceImages() && type.getPlaceImages().size() > 0) {
            doc.setFrontpageImage(type.getPlaceImages().get(0).getUrl());
        }

        if (null != type.getGeoLatitude() && null != type.getGeoLongitude()) {
            doc.setGeoLatLng(type.getGeoLatitude().stripTrailingZeros().toPlainString() + ","
                + type.getGeoLongitude().stripTrailingZeros().toPlainString());
        }

        if (type.getSpaces() != null && type.getSpaces().size() > 0) {
            doc.setSpaces(new ArrayList<SpaceDocument>());
            for (SpaceType spaceType : type.getSpaces()) {
                SpaceDocument spaceDoc = new SpaceDocument();
                spaceDoc.setId(ObfuscateUtil.fromObfuscatedId(spaceType.getId()));
                spaceDoc.setEncryptedId(spaceType.getId());
                spaceDoc.setFullName(spaceType.getFullName());
                spaceDoc.setHeightMeter(spaceType.getHeightMeter());
                spaceDoc.setLengthMeter(spaceType.getLengthMeter());
                spaceDoc.setWidthMeter(spaceType.getWidthMeter());
                spaceDoc.setSizeSqMeter(spaceType.getSizeSqMeter());
                spaceDoc.setSizeHeadcount(spaceType.getSizeHeadcount());
                spaceDoc.setLowestPrice(spaceType.getLowestPrice());
                spaceDoc.setLowestPriceUnit(spaceType.getLowestPriceUnit());
                spaceDoc.setHeadcountBanquet(spaceType.getHeadcountBanquet());
                spaceDoc.setHeadcountBoardMeeting(spaceType.getHeadcountBoardMeeting());
                spaceDoc.setHeadcountClassroom(spaceType.getHeadcountClassroom());
                spaceDoc.setHeadcountCocktail(spaceType.getHeadcountCocktail());
                spaceDoc.setHeadcountFishbone(spaceType.getHeadcountFishbone());
                spaceDoc.setHeadcountGroup(spaceType.getHeadcountGroup());
                spaceDoc.setHeadcountRectangular(spaceType.getHeadcountRectangular());
                spaceDoc.setHeadcountTheater(spaceType.getHeadcountTheater());
                spaceDoc.setHeadcountU(spaceType.getHeadcountU());

                if (spaceType.getRatePlans() != null && spaceType.getRatePlans().size() > 0) {
                    spaceDoc.setRatePlans(new ArrayList<RatePlanDocument>());
                    for (RatePlanType rp : spaceType.getRatePlans()) {
                        RatePlanDocument rpDoc = new RatePlanDocument();
                        rpDoc.setId(ObfuscateUtil.fromObfuscatedId(rp.getId()));
                        rpDoc.setEncryptedId(rp.getId());
                        rpDoc.setDepositPrice(rp.getDepositPrice());
                        rpDoc.setDescription(rp.getDescription());
                        rpDoc.setListPrice(rp.getListPrice());
                        rpDoc.setDayPrice(rp.getDayPrice());
                        rpDoc.setHourPrice(rp.getHourPrice());
                        rpDoc.setName(rp.getName());
                        rpDoc.setOrderSubtype(rp.getOrderSubtype());
                        rpDoc.setOrderType(rp.getOrderType());
                        rpDoc.setOurPrice(rp.getOurPrice());
                        rpDoc.setPriceUnit(rp.getPriceUnit());
                        rpDoc.setPromoTags(rp.getPromoTags());
                        rpDoc.setReservedPrice(rp.getReservedPrice());
                        rpDoc.setSequenceId(rp.getSequenceId());

                        // rp增加周几搜索
                        if (rp.getWeeks() != null) {
                            String[] split = rp.getWeeks().split(",");
                            List<Integer> weekDays = new ArrayList();
                            for (String day : split) {
                                if (StringUtils.isNotBlank(day)) {
                                    weekDays.add(Integer.valueOf(day));
                                }
                            }
                            rpDoc.setWeekdays(weekDays);
                        }
                        spaceDoc.getRatePlans().add(rpDoc);
                    }
                }
                doc.getSpaces().add(spaceDoc);
            }
        }
        if (type.getFlexPayRP() != null && "ACTIVE".equals(type.getFlexPayRP().getStatus())) {
            RatePlanDocument flexPayRP = new RatePlanDocument();
            flexPayRP.setId(ObfuscateUtil.fromObfuscatedId(type.getFlexPayRP().getId()));
            flexPayRP.setName(type.getFlexPayRP().getName());
            doc.setFlexPayRP(flexPayRP);
        }
        return doc;
    }

    public static SearchPlaceTypeBase convertDocumentToSearchPlaceType(PlaceDocument doc,
        ESSearchParameter esParameter) {
        if (null == doc) {
            return null;
        }
        SearchPlaceTypeBase type = new SearchPlaceTypeBase();
        type.setId(doc.getEncryptedId());
        type.setFullName(doc.getFullName());
        type.setCityName(doc.getCityName());
        type.setProvinceName(doc.getProvinceName());
        type.setStarRating(doc.getStarRating());

        type.setAddress(doc.getAddress());
        type.setDistrict(doc.getDistrict());
        type.setBusinessDistrict(doc.getBusinessDistrict());
        type.setOpenSinceDate(doc.getOpenSinceDate());
        type.setGeoLongitude(doc.getGeoLongitude());
        type.setGeoLatitude(doc.getGeoLatitude());
        type.setReviewRating(doc.getReviewRating());
        type.setPlaceTypeTags(doc.getPlaceTypeTags());
        type.setFunctionalities(doc.getFunctionalities());
        type.setPickTags(doc.getPickTags()); // Hot words
        type.setPlaceFacilityTags(doc.getPlaceFacilityTags());
        type.setFrontpageImage(doc.getFrontpageImage());
        type.setDefaultSortValue(doc.getDefaultSortValue());
        type.setAcceptPayment(doc.getAcceptPayment());
        type.setPromoTags(doc.getPromoTags());
        type.setReviewScore(doc.getReviewScore());
        type.setFavoritesCount(doc.getFavoritesCount());
        type.setFuncSortMap(doc.getFuncSortMap());
        type.setPhoneExtNum(doc.getPhoneExtNum());
        type.setAcceptInquiry(doc.getAcceptInquiry());
        // instantConfirm
        type.setInstantConfirm(doc.getInstantConfirm());
        type.setMaxPlacePromo(doc.getMaxPlacePromo());
        type.setFunctionalityShortNames(doc.getFunctionalityShortNames());
        type.setPromoTagsV2(doc.getPromoTagsV2());

        type.setHasReceipt(doc.getHasReceipt());

        BigDecimal lowestPrice = null;
        Integer lowPriceUnit = null;
        BigDecimal highestPrice = null;
        SpaceDocument spaceWithHighestPrice = null;
        SpaceDocument spaceWithLowestPrice = null;
        RatePlanDocument ratePlanWithHighestPrice = null;
        RatePlanDocument ratePlanWithLowestPrice = null;
        Integer lowestHeadcount = null;
        Integer highestHeadcount = null;
        BigDecimal heightMeterLower = null;
        BigDecimal heightMeterUpper = null;
        Integer sizeSqMeterLower = null;
        Integer sizeSqMeterUpper = null;

        if (doc.getSpaces() != null && null != esParameter) {
            int spaceMatchCount = 0;
            boolean isPriceUnit = false;
            for (SpaceDocument spaceDoc : doc.getSpaces()) {
                if (testIfSpaceMatchesSearchFilter(spaceDoc, esParameter)) {
                    ++spaceMatchCount;
                }

                if (esParameter != null && esParameter.getLowestPriceUnitFilter() != null) {
                    if (spaceDoc.getRatePlans() != null && !spaceDoc.getRatePlans().isEmpty()) {
                        for (RatePlanDocument ratePlanDoc : spaceDoc.getRatePlans()) {
                            if (ratePlanDoc.getOurPrice() != null
                                && Objects.equals(ratePlanDoc.getPriceUnit(),
                                esParameter.getLowestPriceUnitFilter().getValue())) {

                                boolean inWeeks = isInWeeks(esParameter, ratePlanDoc);
                                if (((lowestPrice == null || ratePlanDoc.getOurPrice().compareTo(lowestPrice) < 0)
                                    && inWeeks)) {
                                    lowestPrice = ratePlanDoc.getOurPrice();
                                    lowPriceUnit = ratePlanDoc.getPriceUnit();
                                    isPriceUnit = true;
                                    spaceWithLowestPrice = spaceDoc;
                                    ratePlanWithLowestPrice = ratePlanDoc;
                                }
                                if (highestPrice == null || ratePlanDoc.getOurPrice().compareTo(highestPrice) > 0) {
                                    highestPrice = ratePlanDoc.getOurPrice();
                                    lowPriceUnit = ratePlanDoc.getPriceUnit();
                                    spaceWithHighestPrice = spaceDoc;
                                    ratePlanWithHighestPrice = ratePlanDoc;
                                }
                            }
                        }
                        if (lowestHeadcount == null
                            || (spaceDoc.getSizeHeadcount() != null && spaceDoc.getSizeHeadcount() < lowestHeadcount)) {
                            lowestHeadcount = spaceDoc.getSizeHeadcount();
                        }
                        if (highestHeadcount == null || (spaceDoc.getSizeHeadcount() != null
                            && spaceDoc.getSizeHeadcount() > highestHeadcount)) {
                            highestHeadcount = spaceDoc.getSizeHeadcount();
                        }
                    }
                }

                if (esParameter != null && !isPriceUnit) {
                    if (spaceDoc.getRatePlans() != null && !spaceDoc.getRatePlans().isEmpty()) {
                        for (RatePlanDocument ratePlanDoc : spaceDoc.getRatePlans()) {
                            boolean inWeeks = isInWeeks(esParameter, ratePlanDoc);

                            if(ratePlanDoc.getOurPrice() != null){
                                if (((lowestPrice == null || ratePlanDoc.getOurPrice().compareTo(lowestPrice) < 0)
                                    && inWeeks)) {
                                    lowestPrice = ratePlanDoc.getOurPrice();
                                    lowPriceUnit = ratePlanDoc.getPriceUnit();
                                }
                            }
                        }
                    }
                }

                if (heightMeterLower == null
                    || (spaceDoc.getHeightMeter() != null
                    && spaceDoc.getHeightMeter().compareTo(heightMeterLower) == -1)) {
                    heightMeterLower = spaceDoc.getHeightMeter();
                }
                if (heightMeterUpper == null
                    || (spaceDoc.getHeightMeter() != null
                    && spaceDoc.getHeightMeter().compareTo(heightMeterLower) == 1)) {
                    heightMeterUpper = spaceDoc.getHeightMeter();
                }
                if (sizeSqMeterLower == null
                    || (spaceDoc.getSizeSqMeter() != null
                    && spaceDoc.getSizeSqMeter() < sizeSqMeterLower)) {
                    sizeSqMeterLower = spaceDoc.getSizeSqMeter();
                }
                if (sizeSqMeterUpper == null
                    || (spaceDoc.getSizeSqMeter() != null
                    && spaceDoc.getSizeSqMeter() > sizeSqMeterUpper)) {
                    sizeSqMeterUpper = spaceDoc.getSizeSqMeter();
                }
            }
            type.setSpaceMatchCount(spaceMatchCount);
        }

        type.setLowestHeightMeter(heightMeterLower);
        type.setHighestHeightMeter(heightMeterUpper);
        type.setLowestSizeSqMeter(sizeSqMeterLower);
        type.setHighestSizeSqMeter(sizeSqMeterUpper);
        // both spaces will be not null when one space with correct unit is located
        if (spaceWithLowestPrice != null && spaceWithHighestPrice != null
            && ratePlanWithLowestPrice != null && ratePlanWithHighestPrice != null) {
            // Use data from spaces to override place
            type.setSizeSqMeter(spaceWithLowestPrice.getSizeSqMeter());
            type.setSizeHeadcount(spaceWithLowestPrice.getSizeHeadcount());
            type.setLowestHeadcount(lowestHeadcount);
            type.setHighestHeadcount(highestHeadcount);
            type.setLowestPrice(lowestPrice);
            type.setLowestPriceUnit(lowPriceUnit);
            type.setHighestPrice(doc.getHighestPrice());
            type.setHighestPriceUnit(doc.getHighestPriceUnit());
            type.setListPrice(ratePlanWithLowestPrice.getListPrice());
            type.setListPriceUnit(ratePlanWithLowestPrice.getPriceUnit());
            type.setLowestListPrice(ratePlanWithLowestPrice.getListPrice());
            type.setLowestListPriceUnit(ratePlanWithLowestPrice.getPriceUnit());
            type.setHighestListPrice(doc.getHighestListPrice());
            type.setHighestListPriceUnit(doc.getHighestListPriceUnit());
        } else {
            // Keep old logic when lowestPriceUnitFilter is null
            type.setSizeSqMeter(doc.getSizeSqMeter());
            type.setSizeHeadcount(doc.getSizeHeadcount());
            type.setLowestHeadcount(doc.getLowestHeadcount());
            type.setHighestHeadcount(doc.getHighestHeadcount());
            type.setLowestPrice(lowestPrice);
            type.setLowestPriceUnit(lowPriceUnit);
            type.setHighestPrice(doc.getHighestPrice());
            type.setHighestPriceUnit(doc.getHighestPriceUnit());
            type.setListPrice(doc.getListPrice());
            type.setListPriceUnit(doc.getListPriceUnit());
            type.setLowestListPrice(doc.getLowestListPrice());
            type.setLowestListPriceUnit(doc.getLowestListPriceUnit());
            type.setHighestListPrice(doc.getHighestListPrice());
            type.setHighestListPriceUnit(doc.getHighestListPriceUnit());
        }
        type.setLowestDayPrice(doc.getLowestDayPrice());
        type.setLowestHourPrice(doc.getLowestHourPrice());
        return type;
    }

    private static boolean isInWeeks(ESSearchParameter esParameter, RatePlanDocument ratePlanDoc) {
        boolean inWeeks = false;
        if (esParameter.getWeekdays() == null || esParameter.getWeekdays().size() == 0
            || ratePlanDoc.getWeekdays() == null || ratePlanDoc.getWeekdays().size() == 0) {
            return true;
        }

        if (ratePlanDoc.getWeekdays() != null && ratePlanDoc.getWeekdays().size() != 0) {
            for (Integer i : ratePlanDoc.getWeekdays()) {
                for (Integer ii : esParameter.getWeekdays()) {
                    if (i == ii) {
                        inWeeks = true;
                    }

                }
            }
        }
        return inWeeks;
    }

    private static boolean testIfSpaceMatchesSearchFilter(
        SpaceDocument spaceDoc, ESSearchParameter esParameter) {
        if (spaceDoc.getSizeHeadcount() != null) {
            if (esParameter.getHeadcountLower() != null
                && spaceDoc.getSizeHeadcount() < esParameter.getHeadcountLower()) {
                return false;
            }
            if (esParameter.getHeadcountUpper() != null
                && spaceDoc.getSizeHeadcount() > esParameter.getHeadcountUpper()) {
                return false;
            }
        }

        if (spaceDoc.getRatePlans() != null && spaceDoc.getRatePlans().size() > 0) {
            return testIfSpaceMatchesMatchesSearchFilter(spaceDoc.getRatePlans(), esParameter);
        }
        return true;
    }

    private static boolean testIfSpaceMatchesMatchesSearchFilter(
        List<RatePlanDocument> ratePlanDocs, ESSearchParameter esParameter) {
        boolean isMatched = false;
        for (RatePlanDocument ratePlanDoc : ratePlanDocs) {
            if (isMatched == true)
                break;
            isMatched = isMatched || testIfRatePlanMatchesMatchesSearchFilter(ratePlanDoc, esParameter);
        }
        return isMatched;
    }

    private static boolean testIfRatePlanMatchesMatchesSearchFilter(
        RatePlanDocument ratePlanDoc, ESSearchParameter esParameter) {
        if (ratePlanDoc.getOurPrice() != null) {
            if (esParameter.getPriceLower() != null
                && ratePlanDoc.getOurPrice().compareTo(new BigDecimal(esParameter.getPriceLower())) < 0) {
                return false;
            }
            if (esParameter.getPriceUpper() != null
                && ratePlanDoc.getOurPrice().compareTo(new BigDecimal(esParameter.getPriceUpper())) > 0) {
                return false;
            }
        }

        if (esParameter != null && esParameter.getLowestPriceUnitFilter() != null) {
            if (esParameter.getLowestPriceUnitFilter() != PriceUnitFilter.ALL
                && !Objects.equals(esParameter.getLowestPriceUnitFilter().getValue(), ratePlanDoc.getPriceUnit())) {
                return false;
            }
        }
        return true;
    }
}
