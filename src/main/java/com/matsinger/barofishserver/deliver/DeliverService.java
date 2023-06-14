package com.matsinger.barofishserver.deliver;

import com.google.gson.JsonParser;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class DeliverService {
    String SWEET_TRACKER_BASE_URL = "http://info.sweettracker.co.kr";
    @Value("${smart-parcel.apiKey}")
    private String accessKey;
    String COMPANY_LIST_URL = SWEET_TRACKER_BASE_URL + "/api/v1/companylist";
    String RECOMMEND_COMPANY_LIST_URL = SWEET_TRACKER_BASE_URL + "/api/v1/recommend";
    String TRACKING_INFO_URL = SWEET_TRACKER_BASE_URL + "/api/v1/trackingInfo";


    public List<Deliver.Company> selectDeliverCompanyList() throws ParseException {
        RestTemplate restTemplate = new RestTemplate();
        String url = COMPANY_LIST_URL + "?t_key=" + accessKey;
        String jsonString = restTemplate.getForObject(url, String.class);
        JSONObject object = new JSONObject(jsonString);
        JSONArray arrObject = object.getJSONArray("Company");
        List<Deliver.Company> companies = new ArrayList<>();
        for (int i = 0; i < arrObject.length(); i++) {
            JSONObject obj = arrObject.getJSONObject(i);
            companies.add(Deliver.Company.builder().Code(obj.getString("Code")).International(obj.getString(
                    "International").equals("true") ? true : false).Name(obj.getString("Name")).build());
        }
        return companies;
    }

    public List<Deliver.Company> selectRecommendDeliverCompanyList(String invoice) {
        RestTemplate restTemplate = new RestTemplate();
        String url = RECOMMEND_COMPANY_LIST_URL + "?t_key=" + accessKey + "&t_invoice=" + invoice;
        String jsonString = restTemplate.getForObject(url, String.class);
        JSONObject object = new JSONObject(jsonString);
        System.out.println(jsonString);
        JSONArray arrObject = object.getJSONArray("Recommend");
        List<Deliver.Company> companies = new ArrayList<>();
        for (int i = 0; i < arrObject.length(); i++) {
            JSONObject obj = arrObject.getJSONObject(i);
            companies.add(Deliver.Company.builder().Code(obj.getString("Code")).Name(obj.getString("Name")).build());
        }
        return companies;
    }

    public Deliver.TrackingInfo selectTrackingInfo(String code, String invoice) {
        RestTemplate restTemplate = new RestTemplate();
        String url = TRACKING_INFO_URL + "?t_key=" + accessKey + "&t_invoice=" + invoice + "&t_code=" + code;
        String jsonString = restTemplate.getForObject(url, String.class);
        JSONObject object = new JSONObject(jsonString);
        System.out.println("jsonString\n" + jsonString);
//        JSONObject object = object.getJSONObject("tracking_info");
        JSONArray trackingDetailList = object.getJSONArray("trackingDetails");

        Deliver.TrackingInfo
                trackingInfo =
                Deliver.TrackingInfo.builder().adUrl(object.getString("adUrl")).invoiceNo(object.getString("invoiceNo")).itemImage(
                        object.getString("itemImage")).itemName(object.getString("itemName")).level(object.getInt(
                        "level")).result(object.getString("result")).senderName(object.getString("senderName")).build();
        List<Deliver.TrackingDetails> trackingDetails = new ArrayList<>();
        for (int i = 0; i < trackingDetailList.length(); i++) {
            JSONObject obj = trackingDetailList.getJSONObject(i);
            trackingDetails.add(Deliver.TrackingDetails.builder().code(String.valueOf(obj.get("code"))).kind(String.valueOf(obj.get("kind"))).level(obj.getInt("level")).manName(obj.getString("manName")).manPic(obj.getString(
                    "manPic")).timeString(obj.getString("timeString")).where(
                    obj.getString("where")).build());
        }
        trackingInfo.setTrackingDetails(trackingDetails);
        return trackingInfo;
    }
}
