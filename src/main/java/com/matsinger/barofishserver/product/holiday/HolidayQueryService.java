package com.matsinger.barofishserver.product.holiday;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class HolidayQueryService {

    private final String targetUrl = "http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getHoliDeInfo";
    private final String serviceKey = "vB%2FBwyq3B9fqpHyq9BQQLLCOu715sRnjDN19qeZfbMmhrSt3qN1kf8q0bMaoCtF0Gu916jxJk6SX0%2B115ishrA%3D%3D";

//    public void get

    public Holidays getOpenDataHolidayInfoResponse(String year, String month, PageRequest pageRequest) throws IOException {

        StringBuilder urlBuilder = new StringBuilder(targetUrl); /*URL*/

        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + serviceKey); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode(String.valueOf(pageRequest.getPageNumber()), "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode(String.valueOf(pageRequest.getPageSize()), "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("solYear","UTF-8") + "=" + URLEncoder.encode(year, "UTF-8")); /*연*/
        urlBuilder.append("&" + URLEncoder.encode("solMonth","UTF-8") + "=" + URLEncoder.encode(month, "UTF-8")); /*월*/

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }

        rd.close();
        conn.disconnect();
        return createHolidays(sb.toString());
    }

    public static Holidays createHolidays(String xml) {
        JSONObject json = null;

        try {
            json = XML.toJSONObject(xml);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject response = (JSONObject) json.get("response");
        JSONObject jsonBody = (JSONObject) response.get("body");
        JSONObject itemsObject = (JSONObject) jsonBody.get("items");
        String items = (String) itemsObject.get("item").toString();

        JsonElement jsonElement = JsonParser.parseString(items);
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        int holidayCount = (int) jsonBody.get("totalCount");

        ArrayList<Holiday> holidays = new ArrayList<>();
        for (int i=0; i<holidayCount; i++) {
            JsonObject item = jsonArray.get(i).getAsJsonObject();

            holidays.add(
                Holiday.builder()
                .dateName(item.get("dateName").toString().replace("\"", ""))
                .date(item.get("locdate").toString())
                .isHoliday(item.get("isHoliday").toString().replace("\"", ""))
                .build()
            );
        }

        return Holidays.builder()
                .totalCount(holidayCount)
                .holidays(holidays)
                .build();
    }
}
