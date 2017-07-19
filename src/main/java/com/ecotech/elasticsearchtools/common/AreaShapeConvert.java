package com.ecotech.elasticsearchtools.common;

import com.ecotech.elasticsearchtools.entity.GeoLocationInfo;
import com.ecotech.poiservice.client.POIServiceRestClient;
import com.ecotech.poiservice.type.UpdatePOIRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Coordinate;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.GeoPolygonQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AreaShapeConvert {
    private static final Logger LOGGER = LoggerFactory.getLogger(AreaShapeConvert.class);
    private static final String CLUSTER_NAME = "elasticsearch-test-v5";
    private static Client client = null;

    public static SearchResponse getCityPointFromElasticSearchbyArea(List<Coordinate> coordinates, int from, int size,
        String serviceUrl, String index, String type) throws UnknownHostException {
        // 创建client
        Settings settings = Settings.builder().put("cluster.name", CLUSTER_NAME).build();

        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(serviceUrl), 9300));


        List<GeoPoint> points = new ArrayList<>();
        for (Coordinate cor: coordinates) {
            points.add(new GeoPoint(cor.y, cor.x));
        }

        GeoPolygonQueryBuilder geoQueryBuilder = QueryBuilders.geoPolygonQuery("location", points);


        SearchRequestBuilder sb = client.prepareSearch(index)
            .setTypes(type)
            // .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
            // .setQuery(qb)
            .setPostFilter(geoQueryBuilder)
            .addSort(new FieldSortBuilder("sequenceId").order(SortOrder.ASC))
            .setFrom(from).setSize(size).setExplain(true);
        SearchResponse response = sb.execute().actionGet();
        // SearchHits hits = response.getHits();
        System.out.println("search req:" + sb.toString());
        client.close();
        return response;
    }

    public static void wirteCityPointToElasticSearch(SearchHits hits, String serviceUrl, String index, String type,
        String city) {
        if (hits != null && hits.getHits().length > 0) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();

                StringBuilder sb = new StringBuilder();
                Calendar startCalendar = Calendar.getInstance();
                startCalendar.setTime(new Date());
                for (int i = 0; i < hits.getHits().length; i++) {
                    GeoLocationInfo geoLocationInfo =
                        objectMapper.readValue(hits.getHits()[i].getSourceAsString(), GeoLocationInfo.class);
                    String poiJson = SourceDataConvert.pointDataConvert(
                        hits.getHits()[i].getId(),
                        String.valueOf(geoLocationInfo.getLocation().getCoordinates()[1]),
                        String.valueOf(geoLocationInfo.getLocation().getCoordinates()[0]),
                        geoLocationInfo.getName(),
                        PinYinUtils.getPinyin(geoLocationInfo.getName()),
                        PinYinUtils.getPinyinShort(geoLocationInfo.getName()),
                        city);

                    sb.append(poiJson);
                }
                ElasticSearchUtils.writeDataToElasticSearch(serviceUrl, index, type, sb.toString());

                Calendar endCalendar = Calendar.getInstance();
                endCalendar.setTime(new Date());
                LOGGER.info("耗时：" + (endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis()));
            } catch (Exception e) {
                LOGGER.error("wirteCityPointToElasticSearch error:", e);
                System.out.println(e);
            }
        }
    }

    public static void wirteCityPointToDB(SearchHits hits, String city) {
        if (hits != null && hits.getHits().length > 0) {
            try {
                UpdatePOIRequest updatePOIRequest = new UpdatePOIRequest();
                updatePOIRequest.setCity(city);
                List<Long> ids = new ArrayList<>();
                updatePOIRequest.setIds(ids);
                Date startDate = new Date();
                for (int i = 0; i < hits.getHits().length; i++) {
                    ids.add(Long.parseLong(hits.getHits()[i].getId()));
                }
                if (!POIServiceRestClient.getInstance().updatePoi(updatePOIRequest)) {
                    System.out.println("update poi error,id:" + updatePOIRequest.getId());
                }
                Calendar endCalendar = Calendar.getInstance();
                endCalendar.setTime(new Date());
                LOGGER.info("耗时：" + (startDate.getTime() - new Date().getTime()));
            } catch (Exception e) {
                LOGGER.error("wirteCityPointToElasticSearch error:", e);
                System.out.println(e);
            }
        }
    }

    public static void wirteCityPointToFile(SearchHits hits, String filePath, String city) {
        if (hits != null && hits.getHits().length > 0) {
            FileWriter fileWriter = null;
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                StringBuilder sb = new StringBuilder();
                Calendar startCalendar = Calendar.getInstance();
                startCalendar.setTime(new Date());
                fileWriter = new FileWriter(new File(filePath), true);
                for (int i = 0; i < hits.getHits().length; i++) {
                    GeoLocationInfo geoLocationInfo =
                        objectMapper.readValue(hits.getHits()[i].getSourceAsString(), GeoLocationInfo.class);
                    String poiJson = SourceDataConvert.pointDataConvert(
                        hits.getHits()[i].getId(),
                        String.valueOf(geoLocationInfo.getLocation().getCoordinates()[1]),
                        String.valueOf(geoLocationInfo.getLocation().getCoordinates()[0]),
                        geoLocationInfo.getName(),
                        PinYinUtils.getPinyin(geoLocationInfo.getName()),
                        PinYinUtils.getPinyinShort(geoLocationInfo.getName()),
                        city);

                    sb.append(poiJson);
                }
                fileWriter.write(sb.toString());
                Calendar endCalendar = Calendar.getInstance();
                endCalendar.setTime(new Date());
                LOGGER.info("耗时：" + (endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis()));
            } catch (Exception e) {
                LOGGER.error("wirteCityPointToFile error:", e);
                System.out.println(e);
            } finally {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    LOGGER.error("fileWriter close error:", e);
                    System.out.println(e);
                }
            }
        }
    }



}
