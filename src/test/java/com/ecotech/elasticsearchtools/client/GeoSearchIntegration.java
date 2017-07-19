package com.ecotech.elasticsearchtools.client;

import org.junit.Test;
import org.junit.Assert;

import com.ecotech.elasticsearchtools.client.ESSearchParameter.OrderByDirection;
import com.ecotech.elasticsearchtools.client.ESSearchParameter.OrderByField;
import com.ecotech.elasticsearchtools.client.ESSearchParameter.SearchType;
import com.ecotech.elasticsearchtools.exception.SearchFailureException;
import com.ecotech.productservice.type.search.SearchResponseSimple;

import java.net.UnknownHostException;

public class GeoSearchIntegration {

    @Test
    public void searchGeo() throws SearchFailureException, UnknownHostException {
        ESSearchParameter esParameter = new ESSearchParameter();
        esParameter.setCity("北京");
        esParameter.setSearchType(SearchType.LBS);
        esParameter.setGeoLatitude(39.985928);
        esParameter.setGeoLongitude(116.496291);
        esParameter.setDistanceMeters(5000);
        esParameter.setOrderByDirection(OrderByDirection.ASC);
        esParameter.setOrderByField(OrderByField.DISTANCE);
        esParameter.setOffset(0);
        esParameter.setLimit(5);

        SearchResponseSimple response = ESClient.getInstance().searchPlace(esParameter);
        Assert.assertEquals(20, response.getCount());
        Assert.assertEquals(5, response.getPlaces().size());
    }

    @Test
    public void searchPlaceName() throws SearchFailureException, UnknownHostException {
        ESSearchParameter esParameter = new ESSearchParameter();
        esParameter.setCity("北京");
        esParameter.setSearchType(SearchType.PLACENAME);
        esParameter.setOrderByDirection(OrderByDirection.ASC);
        esParameter.setOrderByField(OrderByField.AUTO);
        esParameter.setOffset(0);
        esParameter.setLimit(5);

        SearchResponseSimple response = ESClient.getInstance().searchPlace(esParameter);
        Assert.assertEquals(1154, response.getCount());
        Assert.assertEquals(5, response.getPlaces().size());
    }

    @Test
    public void searchPlaceNameFilterDistrict() throws SearchFailureException, UnknownHostException {
        ESSearchParameter esParameter = new ESSearchParameter();
        esParameter.setCity("北京");
        esParameter.setSearchType(SearchType.PLACENAME);
        esParameter.setOrderByDirection(OrderByDirection.ASC);
        esParameter.setOrderByField(OrderByField.AUTO);
        esParameter.setOffset(0);
        esParameter.setLimit(5);

        SearchResponseSimple response = ESClient.getInstance().searchPlace(esParameter);
        Assert.assertEquals(1154, response.getCount());
        Assert.assertEquals(5, response.getPlaces().size());
    }

}
