package com.ecotech.elasticsearchtools.client;

import com.ecotech.elasticsearchtools.entity.POIEntity;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.UnknownHostException;

public class POIESClient extends ESClientBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(POIESClient.class);

    private static POIESClient instance;

    public static final String POI_INDEX_ALIAS = "poi_alias";

    public static final String POI_TYPE = "poi";

    private POIESClient() {
        try {
            init();
        } catch (UnknownHostException e) {
            LOGGER.error("Failure initiating POIESClient", e);
        }
    }

    public static POIESClient getInstance() {
        if (null == instance) {
            LOGGER.info("Initializing ProductServiceClient");
            instance = new POIESClient();
        }
        return instance;
    }

    public IndexResponse putPoiDocument(POIEntity poiInfo, String poiId)
        throws IOException {

        byte[] poiJson = mapper.writeValueAsBytes(poiInfo);
        IndexResponse response = esClient.prepareIndex(POI_INDEX_ALIAS, POI_TYPE, poiId)
            .setSource(new String(poiJson))
            .execute()
            .actionGet();
        return response;
    }

    public DeleteResponse deletePoiDocument(String poiId) {
        DeleteResponse response = esClient.prepareDelete(POI_INDEX_ALIAS, POI_TYPE, poiId)
            .execute().actionGet();

        if (!(response.getResult() == DocWriteResponse.Result.DELETED)) {
            LOGGER.warn(poiId + " not found");
        }
        return response;
    }

}
