package com.epicare.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class ServiceHandler {

    public String  performPostCall(String requestURL,
                                   HashMap<String,String> requestParams) {

        URL url;
        String response = "";

        try {
            url = new URL(requestURL);
            System.out.println(url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            //conn.setHeader("Content-type", "application/json");

            writer.write(getPostDataString(requestParams));
            System.out.println("\nPost request parmas :"+requestParams);
           // writer.write(requestParams);

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();
            Log.e("Response Code","--->>> "+responseCode);

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;


    }

    public String  performGetCall(String requestURL){

        try {
            URL obj = new URL(requestURL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            //con.setRequestProperty("User-Agent", USER_AGENT);
            int responseCode = con.getResponseCode();
            System.out.println("GET Response Code :: " + responseCode);
            System.out.println("Url="+requestURL);
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // print result
                System.out.println(response.toString());
                return response.toString();
            } else {
                System.out.println("GET request not worked");
                return "";
            }
        }catch (Exception e){
            return "";
        }

    }
    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        System.out.println("\n url post= "+result);
        return result.toString();
    }

}
