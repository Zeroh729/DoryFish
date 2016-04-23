package zeroh720.doryfish.util;

import android.provider.SyncStateContract;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import zeroh720.doryfish.values.Constants;

public final class ServerUtils {

    public String getRequest(String path, HashMap requestData, boolean isHashed) {
        String strResult = "";
        URL url;
        String body ="";
        HttpURLConnection conn = null;

        try {
            url = new URL(path);

            if(!isHashed) {
                body = buildRequestPostParameters(requestData);
            }else{
                body = buildHashedRequestPostParameters(requestData);
            }
            byte[] bytes = body.getBytes();

            conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(true);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type","application/json");

            int status = conn.getResponseCode();
            String result = "";
            String line = "";

            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            } else {
                InputStream stream = conn.getInputStream();
                InputStreamReader isReader = new InputStreamReader(stream);

                BufferedReader br = new BufferedReader(isReader);
                while ((line = br.readLine()) != null) {
                    result += line;
                }

                br.close();
            }
            strResult = result;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + path);
        } catch(IOException e){
            e.printStackTrace(System.out );
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

//        System.out.println("RESULT : " + strResult);
        return strResult;
    }


    public String postRequest(String path, HashMap requestData, boolean isHashed) {
        String strResult = "";
        URL url;
        String body ="";
        HttpURLConnection conn = null;

        try {
            url = new URL(path);

            if(!isHashed) {
                body = buildRequestPostParameters(requestData);
            }else{
                body = buildHashedRequestPostParameters(requestData);
            }
            byte[] bytes = body.getBytes();

            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();

            int status = conn.getResponseCode();
            String result = "";
            String line = "";

            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            } else {
                InputStream stream = conn.getInputStream();
                InputStreamReader isReader = new InputStreamReader(stream);

                BufferedReader br = new BufferedReader(isReader);
                while ((line = br.readLine()) != null) {
                    result += line;
                }

                br.close();
            }
            strResult = result;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + path);
        } catch(IOException e){
            e.printStackTrace(System.out );
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

//        System.out.println("RESULT : " + strResult);
        return strResult;
    } // end doPostJSONFromUrl


    private String buildRequestPostParameters(HashMap data) {
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator iterator = data.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry param = (Map.Entry) iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }

        return bodyBuilder.toString();
    }

    private String buildHashedRequestPostParameters(HashMap data) {
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator iterator = data.entrySet().iterator();

        try {
            while (iterator.hasNext()) {
                Map.Entry param = (Map.Entry) iterator.next();
                bodyBuilder.append(param.getKey()).append('=')
                        .append(URLEncoder.encode(param.getValue().toString(), "UTF-8"));
                if (iterator.hasNext()) {
                    bodyBuilder.append('&');
                }
            }
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace(System.out);
        }
        return bodyBuilder.toString();
    }
}