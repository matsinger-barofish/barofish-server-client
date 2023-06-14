package com.matsinger.barofishserver.utils;

import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class Common {


    public ArrayList iteratorToList(Iterable data) {
        ArrayList list = new ArrayList<>();
        while (data.iterator().hasNext()) {
            list.add(data.iterator().next());
        }
        return list;
    }

    public List<Integer> str2IntList(String str) {
        if (str == null) return null;
        List<Integer> intArr = new ArrayList<>();
        String[] strArr = str.split(",");
        for (String s : strArr) {
            intArr.add(Integer.parseInt(s));
        }
        return intArr;
    }

    public String getPostWord(String str, String firstVal, String secondVal) {
        try {
            char laststr = str.charAt(str.length() - 1);
            if (laststr < 0xAC00 || laststr > 0xD7A3) {
                return str;
            }
            int lastCharIndex = (laststr - 0xAC00) % 28;
            if (lastCharIndex > 0) {
                if (firstVal.equals("으로") && lastCharIndex == 8) {
                    str += secondVal;
                } else {
                    str += firstVal;
                }
            } else {
                str += secondVal;
            }
        } catch (Exception e) {
        }
        return str;
    }

    public String validateString(String str, Long maxLen, String name) throws Exception {
        if (str == null) throw new Exception(String.format("%s 입력해주세요.", getPostWord(name, "을", "를")));
        str = str.trim();
        if (str.length() == 0) throw new Exception(String.format("%s 입력해주세요.", getPostWord(name, "을", "를")));
        if (str.length() > maxLen)
            throw new Exception(String.format("%s 최대 %d자 입니다.", getPostWord(name, "을", "를"), maxLen));
        return str;
    }

    public Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }

    public String fetch(String urlString, String method, String contentType) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", contentType);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.close();
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader((new InputStreamReader(is)));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
