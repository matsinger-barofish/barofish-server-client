package com.matsinger.barofishserver.product.weeksdate.application;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.matsinger.barofishserver.product.weeksdate.Holiday;
import com.matsinger.barofishserver.product.weeksdate.Holidays;
import com.matsinger.barofishserver.product.weeksdate.domain.WeeksDate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeeksDateQueryService {

    private final String targetUrl = "http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getHoliDeInfo";
    private final String serviceKey = "vB%2FBwyq3B9fqpHyq9BQQLLCOu715sRnjDN19qeZfbMmhrSt3qN1kf8q0bMaoCtF0Gu916jxJk6SX0%2B115ishrA%3D%3D";

    public void saveThisAndNextWeeksDate() throws IOException {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = new Date();
        calendar.setTime(currentDate);

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        String paddedCurrentDate = StringUtils.leftPad(String.valueOf(currentMonth), 2, "0");
        Holidays koreanHolidays = getKoreanHolidays(String.valueOf(currentYear), paddedCurrentDate);

        if (currentMonth == 12) {
            Holidays nextMonthHolidays = getKoreanHolidays(String.valueOf(currentYear + 1), "01");
            koreanHolidays.addHolidays(nextMonthHolidays);
        }

        List<WeeksDate> weeksDates = new ArrayList<>();
        for (int i=0; i<14; i++) {

            LocalDate date = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));
            int dayOfTheWeek = calendar.get(Calendar.DAY_OF_WEEK); // 일 = 1, ... , 토 = 7

            String dateDescription = "";
            boolean isDeliveryCompanyHoliday = false;
            if (dayOfTheWeek == 1) {
                dateDescription = "일";
                isDeliveryCompanyHoliday = true;
            }
            if (dayOfTheWeek == 2) {
                dateDescription = "월";
                isDeliveryCompanyHoliday = false;
            }
            if (dayOfTheWeek == 3) {
                dateDescription = "화";
                isDeliveryCompanyHoliday = false;
            }
            if (dayOfTheWeek == 4) {
                dateDescription = "수";
                isDeliveryCompanyHoliday = false;
            }
            if (dayOfTheWeek == 5) {
                dateDescription = "목";
                isDeliveryCompanyHoliday = false;
            }
            if (dayOfTheWeek == 6) {
                dateDescription = "금";
                isDeliveryCompanyHoliday = false;
            }
            if (dayOfTheWeek == 7) {
                dateDescription = "토";
                isDeliveryCompanyHoliday = false;
            }

            for (Holiday holiday : koreanHolidays.getHolidays()) {
                if (holiday.sameDate(date.toString())) {
                    dateDescription = holiday.getDateName();

                }
            }

            weeksDates.add(
                    WeeksDate.builder()
                            .date(date.toString())
                            .isDeliveryCompanyHoliday(isDeliveryCompanyHoliday)
                            .description(dateDescription)
                            .build()
            );

            calendar.add(Calendar.DATE, 1);
        }

        weeksDates
    }

    /**
     *
     * @param year 한국의 공휴일 (추석 설날 등..)을 가져올 년도
     * @param month 한국의 공휴일 (추석 설날 등..)을 가져올 월
     * @return
     * @throws IOException
     */
    public Holidays getKoreanHolidays(String year, String month) throws IOException {

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

    public Holidays createHolidays(String xml) {
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
