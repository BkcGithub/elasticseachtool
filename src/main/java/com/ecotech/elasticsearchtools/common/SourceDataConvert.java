package com.ecotech.elasticsearchtools.common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ecotech.poiservice.client.POIServiceRestClient;
import com.ecotech.poiservice.type.AddPOIRequest;
import com.ecotech.poiservice.type.GetPOIResponse;
import com.ecotech.poiservice.type.POIInfo;

public class SourceDataConvert {
    private static final Logger LOGGER = LoggerFactory.getLogger(SourceDataConvert.class);

    // 0代表pointData,1代表shapeData
    public static void writeDataFromFileToElasticSearch(int dataType, String sourceFilePath, String serviceUrl,
        String index, String type, String city) throws IOException {
        FileInputStream fileInputStream = null;
        BufferedReader bufferedReader = null;
        try {
            fileInputStream = new FileInputStream(sourceFilePath);
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String data = null;
            long id = 0;
            StringBuilder sb = new StringBuilder();
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(new Date());
            while ((data = bufferedReader.readLine()) != null) {
                String[] poiArray = data.split("\\,");
                if (poiArray != null && poiArray.length == 3) {
                    String poiJson = StringUtils.EMPTY;
                    if (dataType == 0)
                        poiJson = pointDataConvert(String.valueOf(++id), poiArray[0], poiArray[1], poiArray[2],
                            PinYinUtils.getPinyin(poiArray[2]), PinYinUtils.getPinyinShort(poiArray[2]), city);
                    else if (dataType == 1) {
                        poiJson =
                            shapeDataConvertWithSeqId(String.valueOf(++id), poiArray[0], poiArray[1], poiArray[2], id);
                    } else {
                        return;
                    }
                    sb.append(poiJson);
                    if (id % 10000 == 0) {
                        ElasticSearchUtils.writeDataToElasticSearch(serviceUrl, index, type, sb.toString());
                        sb.delete(0, sb.length());
                        if (LOGGER.isDebugEnabled())
                            LOGGER.debug("已写入" + index);
                    }
                }
            }
            // 执行最后一次写入
            if (sb.length() > 0) {
                ElasticSearchUtils.writeDataToElasticSearch(serviceUrl, index, type, sb.toString());
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("已写入" + index);
            }
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(new Date());
            LOGGER.info("共耗时：" + (endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis()));
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                fileInputStream.close();
                bufferedReader.close();
            } catch (IOException e) {
                throw e;
            }
        }
    }

    public static void writeDataFromDBToElasticSearch(int dataType, String serviceUrl, String index, String type,
        String city) throws IOException {
        try {
            int offset = 0;
            int limit = 1000;
            // GetPOIResponse getPOIResponse =
            // POIServiceRestClient.getInstance().getPois(city,offset, limit);
            String poiJson = StringUtils.EMPTY;
            Date startDate = new Date();
            while (true) {
                GetPOIResponse resp = POIServiceRestClient.getInstance().getPois(city, null, offset, limit);
                StringBuilder sb = new StringBuilder();
                if (resp.getPois() != null && resp.getPois().size() > 0) {
                    for (POIInfo poiInfo : resp.getPois()) {
                        if (dataType == 0)
                            poiJson = pointDataConvert(poiInfo.getId().toString(), poiInfo.getLatitude().toString(),
                                poiInfo.getLongitude().toString(), poiInfo.getName(), poiInfo.getPinyin(),
                                poiInfo.getPinyinShort(), poiInfo.getCity());
                        else if (dataType == 1) {
                            poiJson = shapeDataConvert(poiInfo.getId().toString(), poiInfo.getLatitude().toString(),
                                poiInfo.getLongitude().toString(), poiInfo.getName());
                        } else {
                            return;
                        }
                        sb.append(poiJson);
                    }
                    ElasticSearchUtils.writeDataToElasticSearch(serviceUrl, index, type, sb.toString());
                } else {
                    break;
                }
                offset += limit;
            }
            LOGGER.info("共耗时：" + (startDate.getTime() - new Date().getTime()));
        } catch (Exception e) {
            System.out.println("writeDataFromDBToElasticSearch error:" + e.getMessage());
        }
    }

    public static void writeDataFromFileToDB(String sourceFilePath) throws IOException {
        FileInputStream fileInputStream = null;
        BufferedReader bufferedReader = null;
        try {
            fileInputStream = new FileInputStream(sourceFilePath);
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String data = null;
            Date startDate = new Date();
            List<AddPOIRequest> addPOIRequests = new ArrayList<>();
            long index = 0;
            while ((data = bufferedReader.readLine()) != null) {
                // if (++index > 1500000) {
                String[] poiArray = data.split("\\,");
                if (poiArray != null && poiArray.length == 3) {
                    AddPOIRequest addPOIRequest = new AddPOIRequest();
                    addPOIRequest.setLatitude(new BigDecimal(poiArray[0]));
                    addPOIRequest.setLongitude(new BigDecimal(poiArray[1]));
                    addPOIRequest.setName(poiArray[2]);
                    addPOIRequest.setPinyin(PinYinUtils.getPinyin(poiArray[2]));
                    addPOIRequest.setPinyinShort(PinYinUtils.getPinyinShort(poiArray[2]));
                    addPOIRequests.add(addPOIRequest);
                    if (++index % 5000 == 0) {
                        boolean result = POIServiceRestClient.getInstance().addPOIs(addPOIRequests);
                        if (!result) {
                            System.out.println("写入失败");
                        }
                        addPOIRequests.clear();
                    }
                }
                // }
            }
            // 执行最后一次写入
            boolean result = POIServiceRestClient.getInstance().addPOIs(addPOIRequests);
            if (!result) {
                System.out.println("写入失败");
            }
            LOGGER.info("共耗时：" + (new Date().getTime() - startDate.getTime()));
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                fileInputStream.close();
                bufferedReader.close();
            } catch (IOException e) {
                throw e;
            }
        }
    }

    public static String pointDataConvert(String id, String lat, String lon, String name, String pinyin,
        String pinyinShort, String city) {
        // String
        // poiJson="{\"index\":{\"_id\":\""+id+"\"}}\n{\"location\":\""+lat+","+lon+"\",\"name\":\""+name+"\"}\n";
        String poiJson = "{\"index\":{\"_id\":\"" + id + "\"}}\n{\"location\":{\"lat\":" + lat + ",\"lon\":" + lon
            + "},\"name\":\"" + name + "\",\"pinyin\":\"" + pinyin + "\",\"pinyinshort\":\""
            + pinyinShort + "\",\"city\":\"" + city + "\"}\n";
        // String
        // poiJson="{\"index\":{\"_id\":\""+id+"\"}}\n{\"location\":{\"lat\":"+lat+",\"lon\":"+lon+"},\"name\":\""+name+"\"}\n";
        return poiJson;
    }

    public static String shapeDataConvert(String id, String lat, String lon, String name) {
        // shapeDate,例如：{"name" : "Dam Square, Amsterdam","location" : {"type" :
        // "point", "coordinates" : [ 116.31012, 40.00376 ]}}
        String poiJson = "{\"index\":{\"_id\":\"" + id + "\"}}\n{\"location\":{\"type\":\"point\",\"coordinates\":["
            + lon + "," + lat + "]},\"name\":\"" + name + "\"}\n";
        return poiJson;
    }

    public static String shapeDataConvertWithSeqId(String id, String lat, String lon, String name, long sequenceId) {
        // shapeDate,例如：{"name" : "Dam Square, Amsterdam","location" : {"type" :
        // "point", "coordinates" : [ 116.31012, 40.00376 ]}}
        String poiJson = "{\"index\":{\"_id\":\"" + id + "\"}}\n{\"location\":{\"type\":\"point\",\"coordinates\":["
            + lon + "," + lat + "]},\"name\":\"" + name + "\",\"sequenceId\":" + sequenceId + "}\n";
        return poiJson;
    }

}
