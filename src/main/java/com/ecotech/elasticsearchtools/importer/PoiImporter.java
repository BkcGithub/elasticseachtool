package com.ecotech.elasticsearchtools.importer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ecotech.elasticsearchtools.common.ESClusterEndpoint;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ecotech.elasticsearchtools.common.AreaShapeConvert;
import com.ecotech.elasticsearchtools.common.ElasticSearchUtils;
import com.ecotech.elasticsearchtools.common.SourceDataConvert;

public class PoiImporter {
    private static final int SIZE = 1000;
    private static final Logger LOGGER = LoggerFactory.getLogger(PoiImporter.class);
    private static final String POI_FILE_DIR = "/Users/jerryz/POI/poi.csv";

    //Updated to elasticsearch v5
    private static final String TEST_ELK_IP = ESClusterEndpoint.TEST_ENDPOINT;
    private static final String TEST_ELK_URL = "http://" + ESClusterEndpoint.TEST_ENDPOINT + ":9200/";
    private static final String PROD_ELK_URL = "http://" + ESClusterEndpoint.PROD_ENDPOINT +  ":9200/";

    private static final String POI_ALL_INDEX_NAME = "poiall";
    private static final String POI_TYPE = "poi";

    public static void main(String[] args) {
        try {
            // 数据从文件导入db
            // importPoiAllToDB();

            // 数据从db/file导入elk
            // importPoiAllToElk("file");

            // 从测试elk将poi城市数据导入新的elk/db
             //exportPoiByCityFromElk("武汉", "db");
             //exportPoiByCityFromElk("成都", "db");

            // 从测试db将城市数据导入elk
            List<String> cities = new ArrayList<String>() {
                {
                    add("北京");
                    add("上海");
                    add("广州");
                    add("深圳");
                    add("杭州");
                    add("南京");
                    add("武汉");
                    add("成都");
                }
            };
            importPoiToElkByCitiesFromDb(null, cities);
        } catch (Exception e) {
            System.out.println("import data error:" + e.getMessage());
            LOGGER.error("import data error:", e);
            e.printStackTrace();
        }
    }

    /**
     * 已经填好城市的POI数据从DB导入elk的搜索用index, used by importPoiToElkByCitiesFromDB
     * @param indexName
     * @param city
     * @throws Exception
     */
    private static void importPoiToElkByCityFromDb(String indexName, String city) throws Exception {
        String poiIndexName = StringUtils.EMPTY;
        String esUrl = StringUtils.EMPTY;
        String systemDomain = System.getenv("ECO_DOMAIN");
        if ("prod".equals(systemDomain)) {
            esUrl = PROD_ELK_URL;
        } else {
            esUrl = TEST_ELK_URL;
        }
        if (StringUtils.isNotBlank(indexName)) {
            poiIndexName = indexName;
        } else {
            poiIndexName = "poi_index" + new Date().getTime();
            // 新建mapping：插入数据前需要先创建mapping,此处的mapping为point
            ElasticSearchUtils.addMappingsToElasticSearch(0, esUrl, poiIndexName, POI_TYPE);
        }
        SourceDataConvert.writeDataFromDBToElasticSearch(0, esUrl, poiIndexName, POI_TYPE, city);
    }

    /**
     * 已经填好城市的POI数据从DB导入elk的搜索用index
     * @param indexName
     * @param cities
     * @throws Exception
     */
    public static void importPoiToElkByCitiesFromDb(String indexName, List<String> cities) throws Exception {
        String systemDomain = System.getenv("ECO_DOMAIN");
        String esUrl;
        if ("prod".equals(systemDomain)) {
            esUrl = PROD_ELK_URL;
        } else {
            esUrl = TEST_ELK_URL;
        }
        String poiIndexName;
        if (StringUtils.isNotBlank(indexName)) {
            poiIndexName = indexName;
        } else {
            poiIndexName = "poi_index" + new Date().getTime();
            // 新建mapping：插入数据前需要先创建mapping,此处的mapping为point
            ElasticSearchUtils.addMappingsToElasticSearch(0, esUrl, poiIndexName, POI_TYPE);
        }
        for (String city : cities) {
            importPoiToElkByCityFromDb(poiIndexName, city);
        }
    }

    /**
     * 根据coordinates将elk数据按城市导出
     * @param city
     * @param to
     * @throws Exception
     */
    private static void exportPoiByCityFromElk(String city, String to) throws Exception {
        int offset = 0;
        String poiIndexName = "poi_index" + new Date().getTime();
        if (to.equals("elk")) {
            // 新建mapping：插入数据前需要先创建mapping,此处的mapping为point
            boolean result = ElasticSearchUtils.addMappingsToElasticSearch(0, TEST_ELK_URL, poiIndexName, POI_TYPE);
        }
        while (true) {
            System.out.println("offset=" + offset);
            // 从poiall中搜索数据
            SearchResponse searchResponse = AreaShapeConvert.getCityPointFromElasticSearchbyArea(
                CityCoordinates.getCoordinates(city), offset, SIZE, TEST_ELK_IP, POI_ALL_INDEX_NAME, POI_TYPE);
            if (searchResponse.getHits() == null || searchResponse.getHits().getHits().length == 0)
                return;
            else {
                switch (to) {
                    case "elk":
                        AreaShapeConvert.wirteCityPointToElasticSearch(searchResponse.getHits(), TEST_ELK_URL,
                            poiIndexName, POI_TYPE, city);
                    case "db":
                        AreaShapeConvert.wirteCityPointToDB(searchResponse.getHits(), city);
                    default:
                        break;
                }
                offset += SIZE;
            }
        }
    }

    /**
     * 全量数据从db/file导入elk
     * @param from
     * @throws Exception
     */
    private static void importPoiAllToElk(String from) throws Exception {
        // 新建mapping：插入shape数据,mapping为shape,用于找出不同城市的poi
        ElasticSearchUtils.addMappingsToElasticSearch(1, TEST_ELK_URL, POI_ALL_INDEX_NAME, POI_TYPE);
        // 插入数据
        switch (from) {
            case "file": //20170117更新：测试可用
                SourceDataConvert.writeDataFromFileToElasticSearch(1, POI_FILE_DIR, TEST_ELK_URL, POI_ALL_INDEX_NAME,
                        POI_TYPE, null);
                break;
            case "db":// 特别慢，待优化 20170117更新：代码不可用，poiservice服务不支持全量取出所有poi
                SourceDataConvert.writeDataFromDBToElasticSearch(1, TEST_ELK_URL, POI_ALL_INDEX_NAME, POI_TYPE, null);
            default:
                break;
        }
    }

    /**
     * 全量数据从文件导入db, 现在数据库中都已经有了poi数据，理论上这个函数今后不应再用到
     * @throws Exception
     */
    private static void importPoiAllToDB() throws Exception {
        SourceDataConvert.writeDataFromFileToDB(POI_FILE_DIR);
    }

}
