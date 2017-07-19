package com.ecotech.elasticsearchtools.common;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import java.util.logging.Logger;

import com.ecotech.common.http.HttpClientFactory;

public class ElasticSearchUtils {
    private static final Logger LOGGER = Logger.getLogger(ElasticSearchUtils.class.getName());
    private static final CloseableHttpClient httpClient = HttpClientFactory.getHttpClientWithRetry();

    /*
     *
     * */
    public static boolean writeDataToElasticSearch(String serviceUrl, String index, String type, String data)
        throws Exception {
        HttpPost httpPost = new HttpPost(serviceUrl + index + "/" + type + "/_bulk?pretty");
        httpPost.setHeader("Content-Type", "application/json");
        StringEntity body = new StringEntity(data, "utf-8");
        body.setContentType("application/json");
        httpPost.setEntity(body);
        CloseableHttpResponse response = httpClient.execute(httpPost);
        try {
            if (response.getStatusLine().getStatusCode() != 200) {
                LOGGER.info("Status Code is " + response.getStatusLine().getStatusCode());
                return false;
            } else {
                LOGGER.info("写入成功" + new Date());
                // LOGGER.info("数据写入成功：" + serviceUrl + index + "/" + type + "/_bulk?pretty");
            }
        } finally {
            response.close();
        }
        return true;
    }

    // 0代表pointData,1代表shapeData
    public static boolean addMappingsToElasticSearch(int dataType, String serviceUrl, String index, String type)
        throws ClientProtocolException, IOException {
        String mappingType = StringUtils.EMPTY;
        if (dataType == 0)
            mappingType = "geo_point";
        else if (dataType == 1) {
            mappingType = "geo_shape";
        } else {
            return false;
        }
        String settingJsonString = "{"
            + " \"settings\": {"
            + "    \"analysis\": {"
            + "        \"analyzer\": {"
            + "            \"reverse_edge_ngram_analyzer\": {"
            + "                \"tokenizer\": \"reverse_edge_ngram_tokenizer\","
            + "                \"filter\": ["
            + "                    \"lowercase\","
            + "                    \"reverse\","
            + "                    \"edgeNGram\","
            + "                    \"reverse\""
            + "                ]"
            + "            }"
            + "        }, "
            + "        \"tokenizer\": {"
            + "            \"reverse_edge_ngram_tokenizer\": {"
            + "                \"type\": \"edgeNGram\","
            + "                \"min_gram\": \"2\","
            + "                \"max_gram\": \"15\","
            + "                \"token_chars\": ["
            + "                    \"letter\","
            + "                    \"digit\","
            + "                     \"whitespace\""
            + "                 ]"
            + "             }"
            + "         }, "
            + "         \"filter\": {"
            + "             \"edgeNGram\": {"
            + "                 \"type\": \"edgeNGram\","
            + "                 \"min_gram\": \"2\","
            + "                 \"max_gram\": \"15\","
            + "                 \"token_chars\": ["
            + "                     \"letter\","
            + "                     \"digit\","
            + "                     \"whitespace\""
            + "                 ]"
            + "             }"
            + "         }"
            + "     }"
            + " },";

        String mappingJsonString =
            "\"mappings\": {"
                + "\"" + type + "\": {"
                + "\"properties\": {"
                + "\"name\": {"
                + "\"type\": \"string\""
                + "},"
                + "\"location\": {"
                + "\"type\":\"" + mappingType + "\""
                + "},\"city\":{"
                + "\"type\": \"string\","
                + "\"index\":\"not_analyzed\""
                + "},"
                // add by guangyu
                + "\"pinyin\":{"
                + "\"type\": \"string\","
                + "\"index\":\"analyzed\","
                + "\"analyzer\":\"reverse_edge_ngram_analyzer\""
                + "},"
                // add by guangyu
                + "\"pinyinshort\":{"
                + "\"type\": \"string\","
                + "\"index\":\"analyzed\","
                + "\"analyzer\":\"reverse_edge_ngram_analyzer\""
                + "},"
                + "\"sequenceId\": {"
                + "\"type\": \"long\""
                + "}"
                + "}"
                + "}"
                + "}"
                + "}";


        mappingJsonString = settingJsonString + mappingJsonString;
        LOGGER.info("poi setting and mapping json string setting = " + settingJsonString
            + "poi setting and mapping json string mapping = " + mappingJsonString);

        HttpPut httpPut = new HttpPut(serviceUrl + index);
        httpPut.setHeader("Content-Type", "application/json");
        StringEntity body = new StringEntity(mappingJsonString, "utf-8");
        body.setContentType("application/json");
        httpPut.setEntity(body);
        CloseableHttpResponse response = httpClient.execute(httpPut);
        try {
            if (response.getStatusLine().getStatusCode() != 200) {
                LOGGER.info("Status Code is " + response.getStatusLine().getStatusCode());
                return false;
            }
        } finally {
            response.close();
        }
        return true;
    }
}
