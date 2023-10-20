package com.matsinger.barofishserver.product.weeksdate.application;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.matsinger.barofishserver.product.weeksdate.dto.Holiday;
import com.matsinger.barofishserver.product.weeksdate.repository.WeeksDateRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeeksDateQueryService {

    private final String targetUrl = "http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getHoliDeInfo";
    private final String serviceKey = "vB%2FBwyq3B9fqpHyq9BQQLLCOu715sRnjDN19qeZfbMmhrSt3qN1kf8q0bMaoCtF0Gu916jxJk6SX0%2B115ishrA%3D%3D";
    
    private final WeeksDateRepository weeksDateRepository;

    /**
     *
     * @param year 한국의 공휴일 (추석 설날 등..)을 가져올 년도
     * @param month 한국의 공휴일 (추석 설날 등..)을 가져올 월
     * @return
     * @throws IOException
     */
    public List<Holiday> getKoreanHolidays(String year, String month) throws IOException {

        StringBuilder urlBuilder = new StringBuilder(targetUrl); /*URL*/

        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + serviceKey); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("0", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("28", "UTF-8")); /*한 페이지 결과 수*/
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

    public List<Holiday> createHolidays(String xml) {
        JSONObject json = null;

        try {
            json = XML.toJSONObject(xml);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject response = (JSONObject) json.get("response");
        JSONObject jsonBody = (JSONObject) response.get("body");
        if ((int) jsonBody.get("totalCount") == 0) {
            return new ArrayList<>();
        }
        JSONObject itemsObject = (JSONObject) jsonBody.get("items");
        String items = (String) itemsObject.get("item").toString();

        JsonElement jsonElement = JsonParser.parseString(items);

        ArrayList<Holiday> holidays = new ArrayList<>();
        // json에 오브젝트가 하나면
        if (!jsonElement.isJsonArray()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            holidays.add(
                    Holiday.builder()
                            .dateName(jsonObject.get("dateName").toString().replace("\"", ""))
                            .date(jsonObject.get("locdate").toString())
                            .isHoliday(jsonObject.get("isHoliday").toString().replace("\"", ""))
                            .build()
            );
        }

        // json에 오브젝트가 여러개면
        if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            int holidayCount = (int) jsonBody.get("totalCount");

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
        }

        return holidays;
    }
}
