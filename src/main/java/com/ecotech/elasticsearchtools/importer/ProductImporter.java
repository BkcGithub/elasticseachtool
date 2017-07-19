package com.ecotech.elasticsearchtools.importer;

import java.net.UnknownHostException;
import java.util.logging.Logger;

import com.ecotech.client.ProductServiceClient;
import com.ecotech.elasticsearchtools.client.ESClient;
import com.ecotech.productservice.type.PlaceType;
import com.ecotech.productservice.type.SearchResponse;

public class ProductImporter {

    private static final Logger logger = Logger.getLogger(ProductImporter.class.getName());

    private ESClient esClient;

    public ProductImporter() throws UnknownHostException {
        esClient = ESClient.getInstance();
    }

    public void createNewIndex() throws Exception {
        String newIndexName = esClient.createNewProductIndex();
        logger.info("Nex index name : " + newIndexName);

        int offset = 0;
        int counter = 0;
        SearchResponse searchResponse;
        do {
            searchResponse = ProductServiceClient.getInstance().getPlaces("", offset, 10, null, null, null, null,
                "LISTED", null, null, null, null, null, null, "ID");
            if (null == searchResponse || null == searchResponse.getPlaces()
                || searchResponse.getPlaces().size() == 0) {
                break;
            }
            offset += searchResponse.getPlaces().size();
            for (PlaceType placeType : searchResponse.getPlaces()) {
                logger.info(placeType.getId() + " " + placeType.getFullName());
                ProductServiceClient.getInstance().importToSearch(placeType.getId(), newIndexName);
                ++counter;
                logger.info("Done importing: " + counter);
            }

        } while (true);

        esClient.switchProductAlias(newIndexName);
    }

    public static void main(String[] args) throws Exception {
        ProductImporter instance = new ProductImporter();
        instance.createNewIndex();
    }
}
