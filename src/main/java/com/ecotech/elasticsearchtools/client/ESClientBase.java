package com.ecotech.elasticsearchtools.client;

import com.ecotech.common.json.CreateObjectMapper;
import com.ecotech.elasticsearchtools.common.ESClusterEndpoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequestBuilder;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by jerryz on 12/01/2017.
 */
public class ESClientBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ESClientBase.class);

    protected Client esClient;

    protected ObjectMapper mapper;

    protected void init() throws UnknownHostException {
        final String systemDomain = System.getenv("ECO_DOMAIN");
        LOGGER.info("System domain: " + systemDomain);

        if (systemDomain != null && (systemDomain.equals("prod") || systemDomain.equals("gamma"))) {
            Settings settings = Settings.builder()
                    .put("cluster.name", "elasticsearch-prod-v5")
                    .put("client.transport.sniff", true)
                    .put("transport.tcp.compress", true)
                    .build();
            esClient = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ESClusterEndpoint.PROD_ENDPOINT), 9300));
        } else {
            Settings settings = Settings.builder()
                    .put("cluster.name", "elasticsearch-test-v5")
                    .put("client.transport.sniff", true)
                    .put("transport.tcp.compress", true)
                    .build();
            esClient = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ESClusterEndpoint.TEST_ENDPOINT), 9300));
        }
        mapper = CreateObjectMapper.instance();
    }

    protected String getMappingJson(final String path) {
        InputStream inCfg = getClass().getResourceAsStream(path);
        StringBuffer stringBuffer = new StringBuffer();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inCfg, "utf-8"));
            String line = reader.readLine();
            while (line != null) {
                stringBuffer.append(line);
                line = reader.readLine();
            }
            return stringBuffer.toString();
        } catch (IOException e) {
            LOGGER.error("Error getting Mapping Json", e);
        }
        return null;
    }

    /**
     * Return the first index matched
     * @param aliasName
     * @return
     */
    protected String getIndexUsedByAlias(final String aliasName) {
        IndicesAdminClient iac = esClient.admin().indices();
        ImmutableOpenMap<String, List<AliasMetaData>> map =
                iac.getAliases(new GetAliasesRequest(aliasName))
                        .actionGet().getAliases();

        final Iterator<String> stringIterator = map.keysIt();
        if (stringIterator != null  && stringIterator.hasNext()) {
            String indexName = stringIterator.next();
            LOGGER.info("Alias {} pointing to index {}", aliasName, indexName);
            return indexName;
        } else {
            LOGGER.info("Alias {} does not point to an index", aliasName);
            return null;
        }
    }

    protected void switchAliasToIndex(final String aliasName, final String newIndexName) {
        IndicesAdminClient iac = esClient.admin().indices();
        ImmutableOpenMap<String, List<AliasMetaData>> map =
                iac.getAliases(new GetAliasesRequest(aliasName))
                        .actionGet().getAliases();

        IndicesAliasesRequestBuilder aliasRequest = iac.prepareAliases();

        final Iterator<String> stringIterator = map.keysIt();
        while (stringIterator != null  && stringIterator.hasNext()) {
            String indexToRelease = stringIterator.next();
            LOGGER.info("Release Alias {} from index {}", aliasName, indexToRelease);
            aliasRequest.removeAlias(indexToRelease, aliasName);
        }
        LOGGER.info("Alias {} switching to index {}", aliasName, newIndexName);
        aliasRequest.addAlias(newIndexName, aliasName)
                .execute().actionGet();
        System.out.println("Done switching index alias.");
    }

    protected void createIndex(String indexName, String mappingJson) {
        LOGGER.info("Creating index: " + indexName);
        CreateIndexRequestBuilder createIndexRequestBuilder = esClient.admin().indices()
                .prepareCreate(indexName)
                .setSource(mappingJson); // ok for settings and mappings mixed
        createIndexRequestBuilder.execute().actionGet();
    }
}
