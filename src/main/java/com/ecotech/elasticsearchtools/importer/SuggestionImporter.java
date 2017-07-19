package com.ecotech.elasticsearchtools.importer;

import com.ecotech.elasticsearchtools.client.ESSugClient;
import com.ecotech.elasticsearchtools.document.SugDocument;
import com.ecotech.metadataservice.client.MetaDataServiceJavaClient;
import com.ecotech.metadataservice.type.MetaDataEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SuggestionImporter {

    private static final Logger logger = LoggerFactory.getLogger(SuggestionImporter.class);

    ESSugClient esSugClient;

    MetaDataServiceJavaClient mdClient;

    public static void main(String[] args) throws Exception {
        SuggestionImporter instance = new SuggestionImporter();
        instance.createNewIndex();
    }

    public SuggestionImporter() {
        esSugClient = new ESSugClient();
        mdClient = MetaDataServiceJavaClient.getInstance();
    }

    private void switchToNewIndex(String newIndexName) {
        esSugClient.switchSugAlias(newIndexName);
    }

    public String createNewIndex() throws Exception {
        logger.info("Starting index creation");
        String newIndexName = esSugClient.createNewSugIndex();
        List<String> cities = mdClient.convertMetaToStringList(mdClient.getMetaData("biz", "cities-rc"));

        List<String> placeTypes = mdClient.convertMetaToStringList(mdClient.getMetaData("biz", "placeTypes"));
        for (String typeName : placeTypes) {
            SugDocument doc = new SugDocument();
            doc.setName(typeName);
            doc.setNamePinYin(PinyinHelper.convertToPinyinString(typeName, "", PinyinFormat.WITHOUT_TONE));
            doc.setNamePinYinShort(PinyinHelper.getShortPinyin(typeName));
            doc.setType("type");
            doc.setSearchType("type");
            doc.setDisplayType("类型");
            doc.setCity("ALL");
            esSugClient.putSugDocument(doc, newIndexName);
        }
        logger.info("Done importing types");

        MetaDataEntity entity = mdClient.getMetaData("biz", "baseFunctionTypes");
        ObjectMapper mapper = new ObjectMapper();
        List<HashMap<String, String>> listOfMaps =
            mapper.readValue(entity.getMdValue(), new TypeReference<List<HashMap<String, String>>>() {});
        List<String> funcTypes = new ArrayList<String>();
        if (null != listOfMaps)
            for (HashMap<String, String> map : listOfMaps) {
                funcTypes.add(map.get("name"));
            }

        for (String funcTypeName : funcTypes) {
            SugDocument doc = new SugDocument();
            doc.setName(funcTypeName);
            doc.setNamePinYin(PinyinHelper.convertToPinyinString(funcTypeName, "", PinyinFormat.WITHOUT_TONE));
            doc.setNamePinYinShort(PinyinHelper.getShortPinyin(funcTypeName));
            doc.setType("functionality");
            doc.setSearchType("functionality");
            doc.setDisplayType("功能类型");
            doc.setCity("ALL");
            esSugClient.putSugDocument(doc, newIndexName);
        }
        logger.info("Done importing functionalities");

        MetaDataEntity funcTypeEntity = mdClient.getMetaData("biz", "facilities");
        ObjectMapper facilityMapper = new ObjectMapper();
        List<HashMap<String, String>> facilityListMaps = facilityMapper.readValue(funcTypeEntity.getMdValue(),
            new TypeReference<List<HashMap<String, String>>>() {});
        for (HashMap<String, String> facility : facilityListMaps) {
            SugDocument doc = new SugDocument();
            doc.setName(facility.get("value"));
            doc.setNamePinYin(PinyinHelper.convertToPinyinString(facility.get("value"), "", PinyinFormat.WITHOUT_TONE));
            doc.setNamePinYinShort(PinyinHelper.getShortPinyin(facility.get("value")));
            doc.setType("facilityTag");
            doc.setSearchType("facilityTag");
            doc.setDisplayType("设施");
            doc.setCity("ALL");
            esSugClient.putSugDocument(doc, newIndexName);
        }
        logger.info("Done importing facilities");

        MetaDataEntity hotWordsEntity = mdClient.getMetaData("biz", "hotwords-rc");
        List<HashMap<String, String>> listMaps =
            mapper.readValue(hotWordsEntity.getMdValue(), new TypeReference<List<HashMap<String, String>>>() {});
        if (null == listMaps) {
            return null;
        }
        List<String> hotWordsList = new ArrayList<String>();
        for (HashMap<String, String> map : listMaps) {
            if (map.get("searchType") != null && map.get("searchType").equals("pickTags")) {
                hotWordsList.add(map.get("hotword"));
            }
        }
        for (String hotWord : hotWordsList) {
            SugDocument doc = new SugDocument();
            doc.setName(hotWord);
            doc.setNamePinYin(PinyinHelper.convertToPinyinString(hotWord, "", PinyinFormat.WITHOUT_TONE));
            doc.setNamePinYinShort(PinyinHelper.getShortPinyin(hotWord));
            doc.setType("picktag");
            doc.setSearchType("picktag");
            doc.setDisplayType("热搜");
            doc.setCity("ALL");
            esSugClient.putSugDocument(doc, newIndexName);
        }
        logger.info("Done importing picktags");

        if (null != cities) {
            for (String city : cities) {
                List<String> districts =
                    mdClient.convertMetaToStringList(mdClient.getMetaData("biz", "districts", city));
                if (null != districts) {
                    for (String districtName : districts) {
                        SugDocument doc = new SugDocument();
                        doc.setName(districtName);
                        doc.setNamePinYin(
                            PinyinHelper.convertToPinyinString(districtName, "", PinyinFormat.WITHOUT_TONE));
                        doc.setNamePinYinShort(PinyinHelper.getShortPinyin(districtName));
                        doc.setType("district");
                        doc.setSearchType("district");
                        doc.setDisplayType("行政区");
                        doc.setCity(city);
                        esSugClient.putSugDocument(doc, newIndexName);
                    }
                }
                List<String> businessDistricts =
                    mdClient.convertMetaToStringList(mdClient.getMetaData("biz", "bizDistricts", city));
                if (null != businessDistricts) {
                    for (String bizDistrictName : businessDistricts) {
                        SugDocument doc = new SugDocument();
                        doc.setName(bizDistrictName);
                        doc.setNamePinYin(
                            PinyinHelper.convertToPinyinString(bizDistrictName, "", PinyinFormat.WITHOUT_TONE));
                        doc.setNamePinYinShort(PinyinHelper.getShortPinyin(bizDistrictName));
                        doc.setType("businessDistrict");
                        doc.setSearchType("businessDistrict");
                        doc.setDisplayType("商圈");
                        doc.setCity(city);
                        esSugClient.putSugDocument(doc, newIndexName);
                    }
                }
            }
        }
        logger.info("Done importing districts and businessDistricts");

        switchToNewIndex(newIndexName);

        return newIndexName;
    }
}
