package com.ecotech.elasticsearchtools.client;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.ecotech.elasticsearchtools.document.PlaceDocument;
import com.ecotech.productservice.type.PlaceType;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ESClientIntegration {

    @Test
    public void testIndexAndDelete() throws JsonParseException, JsonMappingException, IOException {

        String encryptedPlaceId = "QG16EZXN"; // 987654321

        PlaceDocument placeDocument = ESClient.getInstance().getPlaceDocument(encryptedPlaceId);
        Assert.assertNull(placeDocument);

        PlaceType testPlace = new PlaceType();
        testPlace.setId("QG16EZXN");
        ESClient.getInstance().putPlaceDocument(testPlace);

        placeDocument = ESClient.getInstance().getPlaceDocument(encryptedPlaceId);
        Assert.assertNotNull(placeDocument);
        Assert.assertEquals("QG16EZXN", placeDocument.getEncryptedId());

        ESClient.getInstance().deletePlaceDocument(encryptedPlaceId);
        placeDocument = ESClient.getInstance().getPlaceDocument(encryptedPlaceId);
        Assert.assertNull(placeDocument);
    }
}
