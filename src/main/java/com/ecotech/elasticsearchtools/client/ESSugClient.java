package com.ecotech.elasticsearchtools.client;

import com.ecotech.elasticsearchtools.document.SugDocument;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.elasticsearch.action.index.IndexResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;

public class ESSugClient extends ESClientBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(ESSugClient.class);

    public static final String SUG_INDEX_ALIAS = "sug_alias";

    public static final String SUG_TYPE = "sug";

    public static final String SUG_INDEX_PREFIX = "sug_index_";

    public ESSugClient() {
        try {
            init();
        } catch (UnknownHostException e) {
            LOGGER.error("Failure initiating ESSugClient", e);
        }
    }

    public String createNewSugIndex() throws IOException {
        Date date = new Date();
        String indexName = SUG_INDEX_PREFIX + Long.toString(date.getTime());
        String mappingJson = getMappingJson("/sug.json");
        createIndex(indexName, mappingJson);
        return indexName;
    }

    public String getIndexUsedByAlias() {
        return super.getIndexUsedByAlias(SUG_INDEX_ALIAS);
    }

    /**
     * Switch index alias to newSugIndex
     * 
     * @param newSugIndex
     */
    public void switchSugAlias(final String newSugIndex) {
        switchAliasToIndex(SUG_INDEX_ALIAS, newSugIndex);
    }

    public IndexResponse putSugDocument(SugDocument doc, String indexName)
            throws JsonProcessingException {
        byte[] json = mapper.writeValueAsBytes(doc);

        IndexResponse response = esClient.prepareIndex(indexName, SUG_TYPE, null)
            .setSource(new String(json))
            .execute()
            .actionGet();
        return response;
    }
}
