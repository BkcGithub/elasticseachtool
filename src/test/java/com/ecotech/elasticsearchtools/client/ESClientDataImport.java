package com.ecotech.elasticsearchtools.client;

import org.junit.Test;

import com.ecotech.client.ProductServiceClient;
import com.ecotech.productservice.type.PlaceType;
import com.ecotech.productservice.type.SearchResponse;

public class ESClientDataImport {

    @Test
    public void importAllPlaces() throws Exception {

        int offset = 0;

        SearchResponse searchResponse;

        do {
            searchResponse = ProductServiceClient.getInstance().getPlaces("", offset, 10, null, null, null, null, null);
            if (null == searchResponse || null == searchResponse.getPlaces()
                || searchResponse.getPlaces().size() == 0) {
                break;
            }
            offset += searchResponse.getPlaces().size();
            for (PlaceType placeType : searchResponse.getPlaces()) {
                System.out.println(placeType.getId() + " " + placeType.getFullName());
                ESClient.getInstance().putPlaceDocument(placeType);
            }

        } while (true);
    }
}
