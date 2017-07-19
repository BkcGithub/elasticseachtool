package com.ecotech.elasticsearchtools.client;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



public class ESSugClientIntegration {

    ESSugClient esSugClient;

    @Before
    public void setup() {
        esSugClient = new ESSugClient();
    }

    @Test
    public void testIndexAndAliasCreation() throws IOException {
        String targetIndex = esSugClient.createNewSugIndex();
        esSugClient.switchSugAlias(targetIndex);
        String indexFound = esSugClient.getIndexUsedByAlias();
        Assert.assertEquals(targetIndex, indexFound);
    }
}
