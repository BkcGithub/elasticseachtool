package com.ecotech.elasticsearchtools.client;

import com.ecotech.common.obfuscate.ObfuscateUtil;
import com.ecotech.elasticsearchtools.client.ESSearchParameter.*;
import com.ecotech.elasticsearchtools.document.POIDocument;
import com.ecotech.elasticsearchtools.document.PlaceDocument;
import com.ecotech.elasticsearchtools.document.SugDocument;
import com.ecotech.elasticsearchtools.exception.SearchFailureException;
import com.ecotech.elasticsearchtools.helper.PlaceDocumentHelper;
import com.ecotech.productservice.type.PlaceType;
import com.ecotech.productservice.type.search.SearchPlaceTypeBase;
import com.ecotech.productservice.type.search.SearchResponseSimple;
import com.ecotech.productservice.type.search.SuggestionResponse;
import com.ecotech.productservice.type.search.SuggestionType;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortMode;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.*;

public class ESClient extends ESClientBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(ESClient.class);

    private static ESClient instance;

    public static final String PRODUCT_INDEX_PREFIX = "product_index_";

    public static final String PRODUCT_INDEX_ALIAS = "product_alias";

    public static final String PLACE_TYPE = "place";

    public static final String POI_INDEX_ALIAS = "poi_alias";

    public static final String POI_TYPE = "poi";

    public static final String SUG_INDEX_ALIAS = "sug_alias";

    private ESClient() {
        try {
            init();
        } catch (UnknownHostException e) {
            LOGGER.error("Failure initiating ESClient", e);
        }
    }

    public static ESClient getInstance() throws UnknownHostException {
        if (null == instance) {
            LOGGER.info("Initializing ProductServiceClient");
            instance = new ESClient();
        }
        return instance;
    }

    public IndexResponse putPlaceDocument(PlaceType placeType)
            throws JsonProcessingException {

        byte[] placeJson = mapper.writeValueAsBytes(PlaceDocumentHelper.convertToDocument(placeType));

        IndexResponse response = esClient.prepareIndex(PRODUCT_INDEX_ALIAS, PLACE_TYPE,
            Long.toString(ObfuscateUtil.fromObfuscatedId(placeType.getId())))
            .setSource(new String(placeJson))
            .execute()
            .actionGet();
        return response;
    }

    public IndexResponse putPlaceDocumentWithFuncSortMap(PlaceType placeType, Map<String, Integer> funcSortMap,
        String productIndexName) throws JsonProcessingException {

        PlaceDocument doc = PlaceDocumentHelper.convertToDocument(placeType);
        doc.setFuncSortMap(funcSortMap);
        byte[] placeJson = mapper.writeValueAsBytes(doc);

        productIndexName = StringUtils.trimToNull(productIndexName);
        if (null == productIndexName) {
            productIndexName = PRODUCT_INDEX_ALIAS;
        }

         IndexResponse response = esClient.prepareIndex(productIndexName, PLACE_TYPE,
            Long.toString(ObfuscateUtil.fromObfuscatedId(placeType.getId())))
            .setSource(new String(placeJson))
            .execute()
            .actionGet();
        return response;
    }


    public DeleteResponse deletePlaceDocument(String encryptedPlaceId) {
        DeleteResponse response = esClient.prepareDelete(PRODUCT_INDEX_ALIAS, PLACE_TYPE,
            Long.toString(ObfuscateUtil.fromObfuscatedId(encryptedPlaceId)))
            .execute().actionGet();
        if (!(response.getResult() == DocWriteResponse.Result.DELETED)) {
            LOGGER.warn(encryptedPlaceId + " not found");
        }
        return response;
    }

    public PlaceDocument getPlaceDocument(String encryptedPlaceId) throws IOException {
        GetResponse response = esClient.prepareGet(PRODUCT_INDEX_ALIAS, PLACE_TYPE,
            Long.toString(ObfuscateUtil.fromObfuscatedId(encryptedPlaceId)))
            .execute()
            .actionGet();
        byte[] documentAsBytes = response.getSourceAsBytes();
        LOGGER.info("Document bytes is: " + documentAsBytes);
        if (documentAsBytes == null || documentAsBytes.length == 0) {
            return null;
        }
        PlaceDocument placeDocument = mapper.readValue(documentAsBytes, PlaceDocument.class);
        return placeDocument;
    }

    public SearchResponseSimple searchPlace(ESSearchParameter esParameter) throws SearchFailureException {
        LOGGER.info("ESSearchParameter : " + esParameter.toString());

        removeSearchTypeIfOverridenByFilters(esParameter);

        if (esParameter.getSearchType() == SearchType.PLACENAME || esParameter.getSearchType() == null) {
            // find suggestion to override search when searchType is placeName or null

            LOGGER.info("Place name search type or null search type : " + esParameter.getSearchType());
            List<SuggestionType> sugList = new ArrayList<>();
            boolean shouldGetSug = StringUtils.trimToNull(esParameter.getCity()) != null
                && StringUtils.trimToNull(esParameter.getQ()) != null;
            if (shouldGetSug) {
                findSuggestionInSugIndex(esParameter.getCity(), esParameter.getQ(), sugList);
            }
            SuggestionType suggestionType = pickSuggestionTypeFromSugIndex(sugList, esParameter.getQ());

            if (suggestionType == null) {
                LOGGER.info("No suggestions for SugIndex");
                sugList.clear();
                if (shouldGetSug) {
                    findSuggestionInPOI(esParameter.getCity(), esParameter.getQ(), sugList);
                    if (sugList.size() > 0 && esParameter.getQ() != null) {
                        for (SuggestionType sug : sugList) {
                            if (esParameter.getQ().equalsIgnoreCase(sug.getName())
                                || esParameter.getQ().equalsIgnoreCase(sug.getPinyin())
                                || esParameter.getQ().equalsIgnoreCase(sug.getPinyinshort())) {
                                suggestionType = sug;
                                break;
                            }
                        }
                    }
                }
            }

            if (suggestionType == null) {
                LOGGER.info("No suggestions for POI");
                sugList.clear();
                if (shouldGetSug) {
                    findSuggestionInPlace(esParameter.getCity(), esParameter.getQ(), sugList);
                }
                if (sugList.size() > 0) {
                    suggestionType = sugList.get(0);
                }
            }
            LOGGER.info("Final suggestion type : " + ReflectionToStringBuilder.toString(suggestionType));

            if (suggestionType != null) {
                ESSearchParameter.SearchType realSearchType = ESSearchParameter.SearchType.valueOf(
                    suggestionType.getSearchType().toUpperCase());
                esParameter.setSearchType(realSearchType);
                if (SearchType.POI == realSearchType) {
                    esParameter.setSearchType(SearchType.LBS);
                    esParameter.setOrderByField(OrderByField.DISTANCE);
                    esParameter.setOrderByDirection(OrderByDirection.ASC);
                    esParameter.setGeoLatitude(suggestionType.getLat().doubleValue());
                    esParameter.setGeoLongitude(suggestionType.getLng().doubleValue());
                }
                if (SearchType.PLACENAME != realSearchType) {
                    esParameter.setQ(suggestionType.getName());
                }
            } else {
                LOGGER.info("No suggestions for places.");
            }
        }
        LOGGER.info("Overrided ESSearchParameter : " + esParameter.toString());

        // Build search response object
        SearchResponseSimple wrapperResponse = new SearchResponseSimple();
        wrapperResponse.setOffset(esParameter.getOffset());
        wrapperResponse.setLimit(esParameter.getLimit());
        List<SearchPlaceTypeBase> placeList = new ArrayList<SearchPlaceTypeBase>();
        wrapperResponse.setPlaces(placeList);

        // Build search filter
        BoolQueryBuilder boolFilterBuilder = convertToFilterBuilder(esParameter);
        if (boolFilterBuilder == null) {
            return wrapperResponse;
        }

        SearchRequestBuilder sBuilder = convertToSearchRequestBuilder(esParameter, boolFilterBuilder);

        LOGGER.info("Search request: " + sBuilder.toString());

        SearchResponse esResponse = sBuilder.execute().actionGet();

        LOGGER.info("Search hits: {}", esResponse.getHits().getTotalHits());
        LOGGER.info("Search took: {} ms", esResponse.getTook().getMillis());

        wrapperResponse.setCount(esResponse.getHits().getTotalHits());
        SearchHit[] results = esResponse.getHits().getHits();

        List<SearchPlaceTypeBase> esReturnedList = toSearchPlaceTypeList(esParameter, results);

        LOGGER.info("Total hits: " + esReturnedList.size());

        if (esParameter.getSearchType() == SearchType.PLACENAME) {
            SearchPlaceTypeBase basePlaceForLBS = null;
            if (esParameter.getOffset() == 0 && esParameter.getLimit() >= 1 && esReturnedList.size() == 1
                && esReturnedList.get(0).getGeoLatitude() != null
                && esReturnedList.get(0).getGeoLongitude() != null) {
                // When search start from offset=0, limit>1 and returned only 1 item
                basePlaceForLBS = esReturnedList.get(0);

            } else if (esParameter.getOffset() > 0 && esParameter.getLimit() > 0 && esReturnedList.size() == 0) {
                // When search start from larger than 0 and returned 0 item, possibly the search
                // from offset 0 returns only 1 item
                // Use offset 0 to see if first page has only 1 item
                int offsetHolder = esParameter.getOffset();
                int limitHolder = esParameter.getLimit();
                esParameter.setOffset(0);
                esParameter.setLimit(10);
                LOGGER.info("Look back with offset = 0, ESSearchParameter : " + esParameter.toString());

                BoolQueryBuilder lookBackFilterBuilder = convertToFilterBuilder(esParameter);
                SearchRequestBuilder lookBackBuilder =
                    convertToSearchRequestBuilder(esParameter, lookBackFilterBuilder);
                SearchResponse lookBackESResponse = lookBackBuilder.execute().actionGet();

                LOGGER.info("Look back search hits: {}", lookBackESResponse.getHits().getTotalHits());
                LOGGER.info("Look back search took: {} ms", lookBackESResponse.getTook().getMillis());
                esParameter.setOffset(offsetHolder);
                esParameter.setLimit(limitHolder);

                SearchHit[] lookBackResults = lookBackESResponse.getHits().getHits();
                List<SearchPlaceTypeBase> lookBackReturnedList = toSearchPlaceTypeList(esParameter, lookBackResults);
                if (lookBackReturnedList.size() == 1 && lookBackReturnedList.get(0).getGeoLatitude() != null
                    && lookBackReturnedList.get(0).getGeoLongitude() != null) {
                    basePlaceForLBS = lookBackReturnedList.get(0);
                }
            }
            if (null != basePlaceForLBS) {
                esParameter.setSearchType(SearchType.LBS);
                esParameter.setOrderByField(OrderByField.DISTANCE);
                esParameter.setOrderByDirection(OrderByDirection.ASC);
                esParameter.setGeoLatitude(basePlaceForLBS.getGeoLatitude().doubleValue());
                esParameter.setGeoLongitude(basePlaceForLBS.getGeoLongitude().doubleValue());

                LOGGER.info("Second overrided ESSearchParameter : " + esParameter.toString());

                BoolQueryBuilder secondFilterBuilder = convertToFilterBuilder(esParameter);
                SearchRequestBuilder secondBuilder = convertToSearchRequestBuilder(esParameter, secondFilterBuilder);
                SearchResponse secondESResponse = secondBuilder.execute().actionGet();

                LOGGER.info("Second search hits: {}", secondESResponse.getHits().getTotalHits());
                LOGGER.info("Second search took: {} ms", secondESResponse.getTook().getMillis());

                wrapperResponse.setCount(secondESResponse.getHits().getTotalHits());
                results = secondESResponse.getHits().getHits();
                esReturnedList = toSearchPlaceTypeList(esParameter, results);
            }
        }

        if (esParameter.getSearchType() == SearchType.IDS && esParameter.getIds() != null) {
            Map<Long, SearchPlaceTypeBase> idPlaceMap = new HashMap<Long, SearchPlaceTypeBase>();
            for (SearchPlaceTypeBase placeType : esReturnedList) {
                idPlaceMap.put(ObfuscateUtil.fromObfuscatedId(placeType.getId()), placeType);
            }
            for (Long id : esParameter.getIds()) {
                if (idPlaceMap.get(id) != null) {
                    wrapperResponse.getPlaces().add(idPlaceMap.get(id));
                }
            }
        } else {
            wrapperResponse.getPlaces().addAll(esReturnedList);
        }

        return wrapperResponse;
    }

    private void removeSearchTypeIfOverridenByFilters(
        ESSearchParameter esParameter) {
        if (null == esParameter.getSearchType()) {
            return;
        }

        switch (esParameter.getSearchType()) {
            case TYPE:
                if (esParameter.getTypes() != null && esParameter.getTypes().size() > 0) {
                    LOGGER.info("Removing searchType due to type filter");
                    esParameter.setSearchType(null);
                    esParameter.setQ(null);
                }
                break;
            case FUNCTIONALITY:
                if (esParameter.getFunctionalities() != null && esParameter.getFunctionalities().size() > 0) {
                    LOGGER.info("Removing searchType due to functionality filter");
                    esParameter.setSearchType(null);
                    esParameter.setQ(null);
                }
                break;
            case BUSINESSDISTRICT:
            case DISTRICT:
            case POI:
            case LBS:
                if (esParameter.getDistrict() != null || esParameter.getBusinessDistrict() != null) {
                    LOGGER.info("Removing searchType due to district or businessDistrict filter");
                    esParameter.setSearchType(null);
                    esParameter.setQ(null);
                    esParameter.setGeoLatitude(null);
                    esParameter.setGeoLongitude(null);
                    if (esParameter.getOrderByField() == ESSearchParameter.OrderByField.DISTANCE) {
                        esParameter.setOrderByField(ESSearchParameter.OrderByField.AUTO);
                        esParameter.setOrderByDirection(ESSearchParameter.OrderByDirection.DESC);
                    }
                }
                break;
        }
    }

    private List<SearchPlaceTypeBase> toSearchPlaceTypeList(
        ESSearchParameter esParameter, SearchHit[] results)
        throws SearchFailureException {
        List<SearchPlaceTypeBase> esReturnedList = new ArrayList<SearchPlaceTypeBase>();
        for (SearchHit hit : results) {
            PlaceDocument placeDocument;
            try {
                placeDocument = mapper.readValue(hit.getSourceRef()
                    .streamInput(), PlaceDocument.class);

                SearchPlaceTypeBase placeType = PlaceDocumentHelper
                    .convertDocumentToSearchPlaceType(placeDocument, esParameter);
                if (esParameter.getOrderByField() == OrderByField.DISTANCE && hit.getSortValues().length > 0) {
                    Double distance = (Double) hit.getSortValues()[0];
                    placeType.setDistance(distance.longValue());
                }
                esReturnedList.add(placeType);
            } catch (Exception e) {
                LOGGER.error("PlaceDocument parsing and conversion had some exception", e);
                throw new SearchFailureException(e);
            }
        }
        return esReturnedList;
    }

    private SearchRequestBuilder convertToSearchRequestBuilder(
        ESSearchParameter esParameter, BoolQueryBuilder boolFilterBuilder) {
        SearchRequestBuilder sBuilder = esClient
            .prepareSearch(PRODUCT_INDEX_ALIAS)
            .setPostFilter(boolFilterBuilder)
            .setFrom(esParameter.getOffset())
            .setSize(esParameter.getLimit()).setExplain(true);

        QueryBuilder qb = null;
        if (esParameter.getSearchType() == SearchType.PLACENAME) {
            String name = StringUtils.trimToNull(esParameter.getQ());
            if (null != name) {
                qb = QueryBuilders.matchPhraseQuery("fullName", esParameter.getQ());
            }
        } else if (esParameter.getSearchType() == SearchType.IDS) {
            if (null != esParameter.getIds()) {
                List<String> decryptedIdStrings = new ArrayList<String>(esParameter.getIds().size());
                for (Long id : esParameter.getIds()) {
                    decryptedIdStrings.add(id.toString());
                }
                String[] queryIds = decryptedIdStrings.toArray(new String[decryptedIdStrings.size()]);
                LOGGER.debug("Query Ids: " + Arrays.toString(queryIds));
                qb = QueryBuilders.idsQuery().addIds(queryIds);
            }
        }
        if (qb != null) {
            // Put query in if there's one
            sBuilder.setQuery(qb);
        }

        // set order by
        if (esParameter.getSearchType() == SearchType.IDS) {
            // Needs no specific sort
        } else if (esParameter.getOrderByField() == OrderByField.DISTANCE) {
            if (esParameter.getGeoLatitude() != null
                && esParameter.getGeoLongitude() != null) {
                sBuilder.addSort(new GeoDistanceSortBuilder("geoLatLng", esParameter.getGeoLatitude(), esParameter.getGeoLongitude())
                    .sortMode(SortMode.MIN)
                    .order(findSortOrder(esParameter.getOrderByDirection()))
                    .geoDistance(GeoDistance.SLOPPY_ARC)
                    .unit(DistanceUnit.METERS));
            } else {
                LOGGER.error("Failed sorting by distance, lat: {}, lng: {} are not valid.",
                    esParameter.getGeoLatitude(), esParameter.getGeoLongitude());
            }
        } else if (esParameter.getOrderByField() == OrderByField.PRICE) {
            // When lowestPriceUnitFilter is present and search result is ordered by price, need to
            // order by lowestPrice from space and filtered by priceUnit
            FieldSortBuilder fieldSortBuilder = new FieldSortBuilder("spaces.ratePlans.ourPrice")
                .setNestedPath("spaces.ratePlans")
                .sortMode(SortMode.MIN)
                .order(findSortOrder(esParameter.getOrderByDirection()))
                .unmappedType("long");
            if (esParameter.getLowestPriceUnitFilter() != null
                && esParameter.getLowestPriceUnitFilter().value != null) {
                fieldSortBuilder.setNestedFilter(QueryBuilders.termQuery("spaces.ratePlans.priceUnit",
                    esParameter.getLowestPriceUnitFilter().value));
            }
            sBuilder.addSort(fieldSortBuilder);
        } else if (esParameter.getOrderByField() == OrderByField.DAYPRICE) {
            // When lowestPriceUnitFilter is present and search result is ordered by price, need to
            // order by lowestPrice from space and filtered by priceUnit
            FieldSortBuilder fieldSortBuilder = new FieldSortBuilder("spaces.ratePlans.dayPrice")
                .setNestedPath("spaces.ratePlans")
                .sortMode(SortMode.MIN)
                .order(findSortOrder(esParameter.getOrderByDirection()))
                .unmappedType("long");
            if (esParameter.getLowestPriceUnitFilter() != null
                && esParameter.getLowestPriceUnitFilter().value != null) {
                fieldSortBuilder.setNestedFilter(QueryBuilders.termQuery("spaces.ratePlans.priceUnit",
                    esParameter.getLowestPriceUnitFilter().value));
            }
            sBuilder.addSort(fieldSortBuilder);
        } else if (esParameter.getOrderByField() == OrderByField.HOURPRICE) {
            // When lowestPriceUnitFilter is present and search result is ordered by price, need to
            // order by lowestPrice from space and filtered by priceUnit
            FieldSortBuilder fieldSortBuilder = new FieldSortBuilder("spaces.ratePlans.hourPrice")
                .setNestedPath("spaces.ratePlans")
                .sortMode(SortMode.MIN)
                .order(findSortOrder(esParameter.getOrderByDirection()))
                .unmappedType("long");
            if (esParameter.getLowestPriceUnitFilter() != null
                && esParameter.getLowestPriceUnitFilter().value != null) {
                fieldSortBuilder.setNestedFilter(QueryBuilders.termQuery("spaces.ratePlans.priceUnit",
                    esParameter.getLowestPriceUnitFilter().value));
            }
            sBuilder.addSort(fieldSortBuilder);
        } else {
            String sortField = findSortField(esParameter);
            if (sortField == null) {
                LOGGER.info("Can't match any sort field for OrderByField: {}", esParameter.getOrderByField());
            } else {
                sBuilder.addSort(new FieldSortBuilder(sortField).order(findSortOrder(esParameter.getOrderByDirection()))
                    .unmappedType("long"));
            }
        }
        return sBuilder;
    }

    /**
     * Build search filter based on ESSearchParamter
     *
     * @param esParameter
     * @return Null if search filter can't be built
     * @throws SearchFailureException
     */
    private BoolQueryBuilder convertToFilterBuilder(ESSearchParameter esParameter) throws SearchFailureException {

        if (esParameter.getSearchType() == SearchType.POI) {
            if (StringUtils.trimToNull(esParameter.getQ()) == null) {
                return null; // When Q=null && searchType=POI, there's no way to build search filter
            }
            // For POI search, update esParameter with POI lat & lng so that it's converted to LBS
            // search
            QueryBuilder poiQb = QueryBuilders.matchPhraseQuery("name", esParameter.getQ());
            SearchRequestBuilder poiSearchBuilder = esClient
                .prepareSearch(POI_INDEX_ALIAS)
                .setQuery(poiQb)
                .setFrom(0)
                .setSize(SUGGESTION_SIZE).setExplain(true); // set size here
            if (StringUtils.isNotBlank(esParameter.getCity())) {
                TermQueryBuilder cityFb = QueryBuilders.termQuery("city", esParameter.getCity());
                poiSearchBuilder.setPostFilter(cityFb);
            }
            SearchResponse poiResponse = poiSearchBuilder.execute().actionGet();
            LOGGER.info("POI search hits: {}", poiResponse.getHits().getTotalHits());
            LOGGER.info("POI search took: {} ms", poiResponse.getTook().getMillis());

            SearchHit[] poiResults = poiResponse.getHits().getHits();

            if (poiResults.length < 1) {
                return null; // Found no matching POI
            }

            try {
                POIDocument poiDocument = mapper.readValue(poiResults[0].getSourceRef()
                    .streamInput(), POIDocument.class);
                LOGGER.info("Found POI name: {}, lat: {}, lng: {}",
                    poiDocument.getName(),
                    poiDocument.getLocation().getLat(),
                    poiDocument.getLocation().getLon());
                // Override geo lat & Lng with the first item from POI search
                esParameter.setGeoLatitude(poiDocument.getLocation().getLat().doubleValue());
                esParameter.setGeoLongitude(poiDocument.getLocation().getLon().doubleValue());
            } catch (Exception e) {
                LOGGER.error("PlaceDocument parsing and conversion had some exception", e);
                throw new SearchFailureException(e);
            }
        } else if (esParameter.getSearchType() == SearchType.TYPE) {
            if (StringUtils.trimToNull(esParameter.getQ()) != null) {
                esParameter.setTypes(new ArrayList<String>());
                esParameter.getTypes().add(esParameter.getQ());
            }
        } else if (esParameter.getSearchType() == SearchType.DISTRICT) {
            esParameter.setDistrict(esParameter.getQ());
        } else if (esParameter.getSearchType() == SearchType.BUSINESSDISTRICT) {
            esParameter.setBusinessDistrict(esParameter.getQ());
        } else if (esParameter.getSearchType() == SearchType.PICKTAG) {
            if (StringUtils.trimToNull(esParameter.getQ()) != null) {
                esParameter.setPickTags(new ArrayList<String>());
                esParameter.getPickTags().add(esParameter.getQ());
            }
        } else if (esParameter.getSearchType() == SearchType.FUNCTIONALITY) {
            esParameter.setFunctionalities(new ArrayList<String>());
            esParameter.getFunctionalities().add(esParameter.getQ());
            if (esParameter.getOrderByField() == OrderByField.AUTO) {
                // override sorting with functionality internal
                esParameter.setOrderByField(OrderByField.FUNCINTERNAL);
            }
        } else if (esParameter.getSearchType() == SearchType.PLACENAME) {
            if (esParameter.getFunctionalities() != null && esParameter.getFunctionalities().size() == 1
                && esParameter.getOrderByField() == OrderByField.AUTO) {
                esParameter.setOrderByField(OrderByField.FUNCINTERNAL);
            }
        } else if (esParameter.getSearchType() == SearchType.FACILITYTAG) {
            esParameter.setFacilityTags(new ArrayList<String>());
            esParameter.getFacilityTags().add(esParameter.getQ());
        }

        BoolQueryBuilder boolFilterBuilder = QueryBuilders.boolQuery();

        if (StringUtils.isNotBlank(esParameter.getCity())) {
            TermQueryBuilder cityFilter = QueryBuilders.termQuery("cityName", esParameter.getCity());
            boolFilterBuilder.must(cityFilter);
        }

        //增加场地是否支持发票
        if(StringUtils.isNotEmpty(esParameter.getHasReceipt())){
            TermQueryBuilder receiptFilter = QueryBuilders.termQuery("hasReceipt",esParameter.getHasReceipt());
            boolFilterBuilder.must(receiptFilter);
        }

        if (esParameter.getListedFilter() != null) {
            TermQueryBuilder listedFilter = null;
            if (esParameter.getListedFilter() == ListedFilter.LISTED) {
                listedFilter = QueryBuilders.termQuery("isListed", "Y");

            } else if (esParameter.getListedFilter() == ListedFilter.UNLISTED) {
                listedFilter = QueryBuilders.termQuery("isListed", "N");
            }
            if (null != listedFilter) {
                boolFilterBuilder.must(listedFilter);
            }
        }

        // 灵活付筛选
        if (esParameter.getOrderTypes() != null && esParameter.getOrderTypes().contains(OrderType.FLEXPAY)) {
            ExistsQueryBuilder existsQueryBuilder = QueryBuilders.existsQuery("flexPayRP");
            boolFilterBuilder.should(existsQueryBuilder);
        }

        // With this, the incoming geo lat and lng is used even when searchType is not LBS
        if (esParameter.getGeoLatitude() != null && esParameter.getGeoLongitude() != null
            && esParameter.getDistanceMeters() != null
            && esParameter.getSearchType() == SearchType.LBS) {
            QueryBuilder geoFilterBuilder = QueryBuilders
                .geoDistanceQuery("geoLatLng")
                .point(esParameter.getGeoLatitude(),
                    esParameter.getGeoLongitude())
                .distance(esParameter.getDistanceMeters(),
                    DistanceUnit.METERS)
                .optimizeBbox("memory")
                .geoDistance(GeoDistance.SLOPPY_ARC);
            boolFilterBuilder.must(geoFilterBuilder);
        }

        if (esParameter.getTypes() != null && esParameter.getTypes().size() > 0) {
            TermsQueryBuilder typesFilter = QueryBuilders.termsQuery("placeTypeTags", esParameter.getTypes());
            boolFilterBuilder.must(typesFilter);
        }

        // 增加星级搜索
        if (esParameter.getStarRatings() != null && esParameter.getStarRatings().size() > 0
            && esParameter.getStarRatings().get(0) != null) {
            boolFilterBuilder.must(QueryBuilders.termsQuery("starRating", esParameter.getStarRatings()));
        }

        if (esParameter.getFunctionalities() != null && esParameter.getFunctionalities().size() > 0
            && esParameter.getFunctionalities().get(0) != null) {
            TermsQueryBuilder funcsFilter =
                QueryBuilders.termsQuery("functionalities", esParameter.getFunctionalities());
            boolFilterBuilder.must(funcsFilter);
        }

        if (esParameter.getPickTags() != null && esParameter.getPickTags().size() > 0) {
            TermsQueryBuilder picksFilter = QueryBuilders.termsQuery("pickTags", esParameter.getPickTags());
            boolFilterBuilder.must(picksFilter);
        }

        if (esParameter.getFacilityTags() != null && esParameter.getFacilityTags().size() > 0) {
            // All Tags must be matched
            for (String facilityTag : esParameter.getFacilityTags()) {
                TermQueryBuilder oneFacilityFilter = QueryBuilders.termQuery("placeFacilityTags", facilityTag);
                boolFilterBuilder.must(oneFacilityFilter);
            }
        }

        if (esParameter.getPromoTagsV2() != null && esParameter.getPromoTagsV2().size() > 0) {
            // All Tags must be matched
            for (String promoV2Tag : esParameter.getPromoTagsV2()) {
                TermQueryBuilder oneFacilityFilter = QueryBuilders.termQuery("promoTagsV2", promoV2Tag);
                boolFilterBuilder.must(oneFacilityFilter);
            }
        }

        if (esParameter.getDistrict() != null) {
            boolFilterBuilder.must(QueryBuilders.termQuery("district", esParameter.getDistrict()));
        }

        if (esParameter.getBusinessDistrict() != null) {
            boolFilterBuilder.must(QueryBuilders.termQuery("businessDistrict", esParameter.getBusinessDistrict()));
        }

        // instantConfirm
        if (esParameter.getInstantConfirm() != null) {
            boolFilterBuilder.must(QueryBuilders.termQuery("instantConfirm", esParameter.getInstantConfirm()));
        }

        if (esParameter.getPlacePromotion() != null) {
            if (esParameter.getPlacePromotion() == true) {
                boolFilterBuilder.must(QueryBuilders.rangeQuery("maxPlacePromo").from("0"));
            }

            if (esParameter.getPlacePromotion() == false) {
                boolFilterBuilder.mustNot(QueryBuilders.rangeQuery("maxPlacePromo").from("0"));
            }
        }

        // ------------------ Nested Filtered Query BEGIN ------------------------------------------
        boolean useSpaceNestedFilter = false;
        BoolQueryBuilder qbSpace = QueryBuilders.boolQuery();

        if (esParameter.getHeadcountLower() != null
            && esParameter.getHeadcountUpper() != null) {
            if (esParameter.getSpaceLayoutFilterList() != null && esParameter.getSpaceLayoutFilterList().size() > 0) {
                BoolQueryBuilder layoutBoolQuery = QueryBuilders.boolQuery();
                for (SpaceLayoutFilter spaceLayoutFilter : esParameter.getSpaceLayoutFilterList()) {
                    layoutBoolQuery.should(QueryBuilders.rangeQuery("spaces." + spaceLayoutFilter.getDocFieldName())
                        .from(esParameter.getHeadcountLower()).to(esParameter.getHeadcountUpper()));
                }
                qbSpace.must(layoutBoolQuery);
                useSpaceNestedFilter = true;
            } else {
                qbSpace.must(QueryBuilders.rangeQuery("spaces.sizeHeadcount")
                    .from(esParameter.getHeadcountLower())
                    .to(esParameter.getHeadcountUpper()));
                useSpaceNestedFilter = true;
            }
        } else {
            if (esParameter.getSpaceLayoutFilterList() != null && esParameter.getSpaceLayoutFilterList().size() > 0) {
                BoolQueryBuilder layoutBoolQuery = QueryBuilders.boolQuery();
                for (SpaceLayoutFilter spaceLayoutFilter : esParameter.getSpaceLayoutFilterList()) {
                    layoutBoolQuery
                        .should(QueryBuilders.rangeQuery("spaces." + spaceLayoutFilter.getDocFieldName()).gt(0));
                }
                qbSpace.must(layoutBoolQuery);
                useSpaceNestedFilter = true;
            }
        }

        //层高
        if (esParameter.getHeightMeterLower() != null
            && esParameter.getHeightMeterUpper() != null) {
            qbSpace.must(QueryBuilders.rangeQuery("spaces.heightMeter")
                .from(esParameter.getHeightMeterLower())
                .to(esParameter.getHeightMeterUpper()));
            useSpaceNestedFilter = true;
        }

        //面积
        if (esParameter.getSizeSqMeterUpper() != null
            && esParameter.getSizeSqMeterLower() != null) {
            qbSpace.must(QueryBuilders.rangeQuery("spaces.sizeSqMeter")
                .from(esParameter.getSizeSqMeterLower())
                .to(esParameter.getSizeSqMeterUpper()));
            useSpaceNestedFilter = true;
        }


        boolean useNestedRatePlanFilter = false;
        BoolQueryBuilder qbRatePlan = QueryBuilders.boolQuery();

        if (esParameter.getPriceLower() != null
            && esParameter.getPriceUpper() != null) {
            qbRatePlan.must(QueryBuilders.rangeQuery("spaces.ratePlans.ourPrice")
                .from(esParameter.getPriceLower())
                .to(esParameter.getPriceUpper()));
            useNestedRatePlanFilter = true;
        }
        if (esParameter.getDayPriceLower() != null
            && esParameter.getDayPriceUpper() != null) {
            qbRatePlan.must(QueryBuilders.rangeQuery("spaces.ratePlans.dayPrice")
                .from(esParameter.getDayPriceLower())
                .to(esParameter.getDayPriceUpper()));
            useNestedRatePlanFilter = true;
        }
        if (esParameter.getHourPriceLower() != null
            && esParameter.getHourPriceUpper() != null) {
            qbRatePlan.must(QueryBuilders.rangeQuery("spaces.ratePlans.hourPrice")
                .from(esParameter.getHourPriceLower())
                .to(esParameter.getHourPriceUpper()));
            useNestedRatePlanFilter = true;
        }

        if (esParameter.getLowestPriceUnitFilter() != null
            && esParameter.getLowestPriceUnitFilter() != PriceUnitFilter.ALL) {
            qbRatePlan.must(
                QueryBuilders.termQuery("spaces.ratePlans.priceUnit", esParameter.getLowestPriceUnitFilter().value));
            useNestedRatePlanFilter = true;
        }

        if (esParameter.getWeekdays() != null
            && esParameter.getWeekdays().size() != 0) {
            qbRatePlan.must(
                QueryBuilders.termsQuery("spaces.ratePlans.weekdays", esParameter.getWeekdays()));
            useNestedRatePlanFilter = true;

            /*
             * TermsFilterBuilder oneFacilityFilter =
             * QueryBuilders.termsFilter("spaces.ratePlans.weekdays", esParameter.getWeekdays());
             * boolFilterBuilder.must(oneFacilityFilter);
             */

        }
        // 一口价筛选
        BoolQueryBuilder qbRatePlan2 = QueryBuilders.boolQuery();
        if (esParameter.getOrderTypes() != null && esParameter.getOrderTypes().contains(OrderType.PREPAY)) {
            qbRatePlan2.should(QueryBuilders.fuzzyQuery("spaces.ratePlans.orderType", OrderType.PREPAY.name()));
            boolFilterBuilder.should(QueryBuilders.nestedQuery("spaces.ratePlans", qbRatePlan2, ScoreMode.Avg));
        }

        QueryBuilder fbRatePlan = QueryBuilders.nestedQuery("spaces.ratePlans", qbRatePlan, ScoreMode.Avg);

        BoolQueryBuilder qbSpaceAndRatePlan =  QueryBuilders.boolQuery();
        if (useSpaceNestedFilter) {
            qbSpaceAndRatePlan.must(qbSpace);
        }
        if (useNestedRatePlanFilter) {
            qbSpaceAndRatePlan.must(fbRatePlan);
        }

        if (useSpaceNestedFilter || useNestedRatePlanFilter) {
            boolFilterBuilder.must(QueryBuilders.nestedQuery("spaces", qbSpaceAndRatePlan, ScoreMode.Avg));
        }
        // ------------------ Nested Filtered Query END ------------------------------------------

        return boolFilterBuilder;
    }

    private SuggestionType pickSuggestionTypeFromSugIndex(List<SuggestionType> sugList, String q) {
        if (sugList == null || sugList.size() == 0) {
            return null;
        }
        // 采用Sug的优先级：商圈，行政区，功能类型，类型，热搜词，设施（name，pinyin或者pinyinshort的完全匹配）
        for (SuggestionType sugType : sugList) {
            if (SearchType.BUSINESSDISTRICT == SearchType.valueOf(sugType.getSearchType().toUpperCase())) {
                return sugType;
            }
        }

        for (SuggestionType sugType : sugList) {
            if (SearchType.DISTRICT == SearchType.valueOf(sugType.getSearchType().toUpperCase())) {
                return sugType;
            }
        }

        for (SuggestionType sugType : sugList) {
            if (SearchType.FUNCTIONALITY == SearchType.valueOf(sugType.getSearchType().toUpperCase())) {
                return sugType;
            }
        }

        for (SuggestionType sugType : sugList) {
            if (SearchType.TYPE == SearchType.valueOf(sugType.getSearchType().toUpperCase())) {
                return sugType;
            }
        }

        for (SuggestionType sugType : sugList) {
            if (SearchType.PICKTAG == SearchType.valueOf(sugType.getSearchType().toUpperCase())) {
                return sugType;
            }
        }

        for (SuggestionType sugType : sugList) {
            if (SearchType.FACILITYTAG == SearchType.valueOf(sugType.getSearchType().toUpperCase())
                && q != null
                && (q.equalsIgnoreCase(sugType.getName())
                || q.equalsIgnoreCase(sugType.getPinyin())
                || q.equalsIgnoreCase(sugType.getPinyinshort()))) {
                return sugType;
            }
        }

        return null;
    }

    /**
     * Convert to Elastic Search's sort order
     *
     * @return
     */
    private SortOrder findSortOrder(OrderByDirection orderByDirection) {
        if (orderByDirection == OrderByDirection.DESC) {
            return SortOrder.DESC;
        } else {
            return SortOrder.ASC;
        }
    }

    /**
     * Find sort field for all except distance
     *
     * @return
     */
    private String findSortField(ESSearchParameter esParameter) {
        if (null == esParameter.getOrderByField()) {
            return null;
        }
        switch (esParameter.getOrderByField()) {
            case AUTO: {
                return "defaultSortValue";
            }
            case REVIEWRATING: {
                return "reviewScore";
            }
            case ID: {
                return "id";
            }
            case FUNCINTERNAL: {
                if (esParameter.getFunctionalities() == null || esParameter.getFunctionalities().get(0) == null) {
                    return null;
                }
                return "funcSortMap." + esParameter.getFunctionalities().get(0);
            }
            case FAVORITESCOUNT: {
                return "favoritesCount";
            }
            default:
                return null;
        }
    }

    public static final int SUGGESTION_SIZE = 10;

    private void findSuggestionInPlace(String city, String q, List<SuggestionType> sugList)
        throws SearchFailureException {
        findSuggestionInPlace(city, q, sugList, SUGGESTION_SIZE);
    }

    private void findSuggestionInPlace(String city, String q, List<SuggestionType> sugList, int retSugNum)
        throws SearchFailureException {

        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        qb.should(QueryBuilders.matchPhraseQuery("fullName", q));
        qb.should(QueryBuilders.matchPhraseQuery("fullNamePinYin", q.replace(" ", "")));
        qb.should(QueryBuilders.matchPhraseQuery("fullNamePinYinShort", q.replace(" ", "")));
        qb.minimumNumberShouldMatch(1);

        BoolQueryBuilder boolFilterBuilder = QueryBuilders.boolQuery();
        TermQueryBuilder listedFilter = QueryBuilders.termQuery("isListed", "Y");
        boolFilterBuilder.must(listedFilter);
        TermQueryBuilder cityFilter = QueryBuilders.termQuery("cityName", city);
        boolFilterBuilder.must(cityFilter);

        SearchRequestBuilder sBuilder = esClient
            .prepareSearch(PRODUCT_INDEX_ALIAS)
            .setQuery(qb)
            .setPostFilter(boolFilterBuilder)
            .setFrom(0)
            .setSize(retSugNum).setExplain(true);
        SearchResponse esResponse = sBuilder.execute().actionGet();
        LOGGER.info("Product search hits: {}", esResponse.getHits().getTotalHits());
        LOGGER.info("Product search took: {} ms", esResponse.getTook().getMillis());

        SearchHit[] results = esResponse.getHits().getHits();

        for (SearchHit hit : results) {
            PlaceDocument placeDocument;
            try {
                placeDocument = mapper.readValue(hit.getSourceRef()
                    .streamInput(), PlaceDocument.class);
                SuggestionType sug = new SuggestionType();
                sug.setName(placeDocument.getFullName());
                sug.setSearchType("placeName");
                sug.setType(PLACE_TYPE);
                sug.setDisplayType("场地");
                sug.setLat(placeDocument.getGeoLatitude());
                sug.setLng(placeDocument.getGeoLongitude());
                sugList.add(sug);
            } catch (Exception e) {
                LOGGER.error("PlaceDocument parsing and conversion had some exception", e);
                throw new SearchFailureException(e);
            }
        }

    }

    private void findSuggestionInSugIndex(String city, String q,
        List<SuggestionType> sugList) throws SearchFailureException {
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        qb.should(QueryBuilders.matchPhraseQuery("name", q));
        qb.should(QueryBuilders.matchPhraseQuery("namePinYin", q.replace(" ", "")));
        qb.should(QueryBuilders.matchPhraseQuery("namePinYinShort", q.replace(" ", "")));
        qb.minimumNumberShouldMatch(1);

        TermsQueryBuilder cityFilter = QueryBuilders.termsQuery("city", "ALL", city);

        SearchRequestBuilder sBuilder = esClient
            .prepareSearch(SUG_INDEX_ALIAS)
            .setQuery(qb)
            .setPostFilter(cityFilter)
            .setFrom(0)
            .setSize(SUGGESTION_SIZE).setExplain(true);
        SearchResponse esResponse = sBuilder.execute().actionGet();
        LOGGER.info("Sug search hits: {}", esResponse.getHits().getTotalHits());
        LOGGER.info("Sug search took: {} ms", esResponse.getTook().getMillis());

        SearchHit[] results = esResponse.getHits().getHits();

        for (SearchHit hit : results) {
            try {
                SugDocument sugDocument = mapper.readValue(hit.getSourceRef()
                    .streamInput(), SugDocument.class);
                SuggestionType sug = new SuggestionType();
                sug.setName(sugDocument.getName());
                sug.setSearchType(sugDocument.getSearchType());
                sug.setType(sugDocument.getType());
                sug.setDisplayType(sugDocument.getDisplayType());
                sug.setLat(sugDocument.getLat());
                sug.setLng(sugDocument.getLng());
                sugList.add(sug);
            } catch (Exception e) {
                LOGGER.error("SugDocument parsing and conversion had some exception", e);
                throw new SearchFailureException(e);
            }
        }
    }

    public SuggestionResponse findSuggestion(String city, String q) throws SearchFailureException {
        List<SuggestionType> sugList = new ArrayList<SuggestionType>(SUGGESTION_SIZE);
        SuggestionResponse sugResponse = new SuggestionResponse();
        sugResponse.setSuggestions(sugList);

        findSuggestionInSugIndex(city, q, sugList);
        LOGGER.info("Num of SugIndex : " + sugList.size());

        // If no SugIndex will increase the number of POI suggestions.
        int retPOISugNum = 3;
        if (sugList.size() == 0) {
            retPOISugNum = 5;
        }

        if (sugList.size() < SUGGESTION_SIZE) {
            findSuggestionInPOI(city, q, sugList, retPOISugNum);
        }
        LOGGER.info("Num of SugList : " + sugList.size());

        if (sugList.size() < SUGGESTION_SIZE) {
            findSuggestionInPlace(city, q, sugList, SUGGESTION_SIZE - sugList.size());
        }
        return sugResponse;
    }

    /**
     * @param city
     * @param q
     * @param sugList
     * @throws SearchFailureException
     */
    protected void findSuggestionInPOI(String city, String q,
        List<SuggestionType> sugList) throws SearchFailureException {
        int retSugNum = SUGGESTION_SIZE - sugList.size();
        findSuggestionInPOI(city, q, sugList, retSugNum);
    }

    /**
     * @param city
     * @param q
     * @param sugList
     * @throws SearchFailureException
     */
    protected void findSuggestionInPOI(String city, String q,
        List<SuggestionType> sugList, int retSugNum) throws SearchFailureException {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("name", q));
        boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("pinyin", q.replace(" ", "")));
        boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("pinyinshort", q.replace(" ", "")));
        boolQueryBuilder.minimumNumberShouldMatch(1);

        TermQueryBuilder poiCityFilter = QueryBuilders.termQuery("city", city);
        SearchRequestBuilder poiSearchBuilder = esClient
            .prepareSearch(POI_INDEX_ALIAS)
            .setPostFilter(poiCityFilter)
            .setQuery(boolQueryBuilder)
            .setFrom(0)
            .setSize(retSugNum).setExplain(true); // set size here
        SearchResponse poiResponse = poiSearchBuilder.execute().actionGet();
        LOGGER.info("POI search hits: {}", poiResponse.getHits().getTotalHits());
        LOGGER.info("POI search took: {} ms", poiResponse.getTook().getMillis());

        SearchHit[] poiResults = poiResponse.getHits().getHits();

        for (SearchHit hit : poiResults) {
            POIDocument poiDocument;
            try {
                poiDocument = mapper.readValue(hit.getSourceRef()
                    .streamInput(), POIDocument.class);
                SuggestionType sug = new SuggestionType();
                sug.setName(poiDocument.getName());
                sug.setSearchType(POI_TYPE);
                sug.setType(POI_TYPE);
                sug.setDisplayType("地标");
                sug.setLat(poiDocument.getLocation().getLat());
                sug.setLng(poiDocument.getLocation().getLon());
                sug.setPinyin(poiDocument.getPinyin());
                sug.setPinyinshort(poiDocument.getPinyinshort());
                sugList.add(sug);
            } catch (Exception e) {
                LOGGER.error("PlaceDocument parsing and conversion had some exception", e);
                throw new SearchFailureException(e);
            }
        }
    }

    /**
     * Create new product index
     *
     * @return new of new index
     * @throws IOException
     */
    public String createNewProductIndex() throws IOException {
        Date date = new Date();
        String indexName = PRODUCT_INDEX_PREFIX + Long.toString(date.getTime());
        String productMappingJson = getMappingJson("/product.json");
        createIndex(indexName, productMappingJson);
        return indexName;
    }

    /**
     * Public API to update Product Alias to point to a new ProductIndex
     *
     * @param newProductIndex
     */
    public void switchProductAlias(final String newProductIndex) {
        switchAliasToIndex(PRODUCT_INDEX_ALIAS, newProductIndex);
    }

    public Long count(String cityName, String functionality) {

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.matchQuery("isListed", "Y"));
        if (functionality != null) {
            boolQuery.must(QueryBuilders.matchQuery("functionalities", functionality));
        }

        if (cityName != null) {
            boolQuery.must(QueryBuilders.matchQuery("cityName", cityName));
        }

        SearchResponse response = esClient.prepareSearch()
            .setIndices(PRODUCT_INDEX_ALIAS)
            .setTypes(PLACE_TYPE)
            .setSize(0)
            .setQuery(boolQuery).setQuery(boolQuery)
            .execute().actionGet();
        return response.getHits().getTotalHits();
    }

}
