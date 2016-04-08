package com.gcloud.gaadi.webservices;

import android.util.Log;

import com.gcloud.gaadi.utils.GCLog;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;


public class JSONParser {

    static InputStream is = null;
    static JSONObject jsonObject = null;
    static String json = "";
    private static String sCookie = null;
    private static String response;

    // constructor
    public JSONParser() {
    }

    public static String getJSONFromUrlChat(String url) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

            // set cookie. sCookie is my static cookie string
            if (sCookie != null && sCookie.length() > 0) {
                conn.setRequestProperty("Cookie", sCookie);
            }


            GCLog.e("Response: " + url);

            // Get the response!
            int httpResponseCode = conn.getResponseCode();
            if (httpResponseCode != HttpURLConnection.HTTP_OK) {
                try {
                    throw new Exception("HTTP response code: " + httpResponseCode);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            try {

                InputStream is = conn.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                response = sb.toString();
                is.close();

                // }

            } catch (Exception e) {
                // throw new CarDekhoException(e.getMessage());
                e.printStackTrace();
                return "Could not connect to server";
            }

            Map<String, List<String>> headers = conn.getHeaderFields();

            // Get the cookie
            if (conn.getHeaderField("Set-Cookie") != null) {
                String cookie = conn.getHeaderField("Set-Cookie").split(";")[0];
                if (cookie != null && cookie.length() > 0) {
                    Log.e("Test", "Old Cookie : " + sCookie + " New Cookie"
                            + cookie);
                    sCookie = cookie;
                }
            }
            Log.e("Cookie", "Response: " + response);

			/*
             * many cookies handling: String responseHeaderName = null; for (int
			 * i=1; (responseHeaderName = conn.getHeaderFieldKey(i))!=null; i++) {
			 * if (responseHeaderName.equals("Set-Cookie")) { String cookie =
			 * conn.getHeaderField(i); } }
			 */

            // conn.disconnect();
            return response;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return "";

    }

    public JSONObject getJSONFromUrl(String url) {

        InputStream is = null;
        String result = "";
        JSONObject jArray = null;
//		//Log.v("message", "In json parser 1"+url);
        //http post
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httppost = new HttpGet(url);
//			//Log.v("message", "In json parser 2"+httppost);
            HttpResponse response = httpclient.execute(httppost);
//			//Log.v("message", "In json parser 3"+response);
            HttpEntity entity = response.getEntity();
//			//Log.v("message", "In json parser 4");
            is = entity.getContent();
//			//Log.v("message", "In json parser 5");

        } catch (Exception e) {
//			//Log.e("log_tag", "Error in http connection "+e.toString());
        }

        //convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
            ////Log.v("message 1", sb+"...");
        } catch (Exception e) {
            //Log.e("log_tag", "Error converting result "+e.toString());
        }
        ////Log.v("Result from server", result);
        //try parse the string to a JSON object
        try {
            ////Log.v("message 2", result+"...");
            jArray = new JSONObject(result);

        } catch (JSONException e) {
//			//Log.e("log_tag", "Error parsing data "+e.toString());
        }

        return jArray;

    }

    public JSONArray getAddStockJsonArray1(String url) {
        InputStream is = null;
        String result = "";
        JSONArray jArray = null;
//		  //Log.v("message", "In json parser 1"+url);
        //http post
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
//		   //Log.v("message", "In json parser 2"+httpGet);
            HttpResponse response = httpclient.execute(httpGet);
//		   //Log.v("message", "In json parser 3"+response);
            HttpEntity entity = response.getEntity();
            ////Log.v("message", "In json parser 4");
            is = entity.getContent();
            ////Log.v("message", "In json parser 5");

        } catch (Exception e) {
            ////Log.e("log_tag", "Error in http connection "+e.toString());
        }

        //convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
            //////Log.v("message 1", sb+"...");
        } catch (Exception e) {
            //Log.e("log_tag", "Error converting result "+e.toString());
        }
        ////Log.v("Result from server", result);
        //try parse the string to a JSON object
        try {
            ////Log.v("message 2", result+"...");
            jArray = new JSONArray(result);

        } catch (JSONException e) {
            //Log.e("log_tag", "Error parsing data "+e.toString());
        }

        return jArray;

    }

    // return json array

    public JSONObject getJSONFromUrl(String url, List<NameValuePair> nameValuePairs) {

        InputStream is = null;
        String result = "";
        JSONObject jsonObj = null;
        //Log.v("message", "In json parser 1"+url);
        //http post
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            //Log.v("message", "In json parser 2"+httpPost);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httpPost);
            //Log.v("message", "In json parser 3"+response);
            HttpEntity entity = response.getEntity();
            //Log.v("message", "In json parser 4");
            is = entity.getContent();
            //Log.v("message", "In json parser 5");

            //Log.v("Values in nameValuePairs", ""+nameValuePairs);

        } catch (Exception e) {
            //Log.e("log_tag", "Error in http connection "+e.toString());
        }

        //convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
            ////Log.v("message 1", sb+"...");
        } catch (Exception e) {
            //Log.e("log_tag", "Error converting result "+e.toString());
        }
        ////Log.v("Result from server", result);
        //try parse the string to a JSON object
        try {
            ////Log.v("message 2", result+"...");
            jsonObj = new JSONObject(result);

        } catch (JSONException e) {
            //Log.e("log_tag", "Error parsing data "+e.toString());
        }
        return jsonObj;
    }

    public JSONArray getJSONArr(String url, List<NameValuePair> nameValuePairs) {

        InputStream is = null;
        String result = "";
        JSONArray jsonArr = new JSONArray();
        //Log.v("message", "In json parser 1"+url);
        //http post
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            //Log.v("message", "In json parser 2"+httpPost);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httpPost);
            //Log.v("message", "In json parser 3"+response);
            HttpEntity entity = response.getEntity();
            //Log.v("message", "In json parser 4");
            is = entity.getContent();
            //Log.v("message", "In json parser 5");

            //Log.v("Values in nameValuePairs", ""+nameValuePairs);

        } catch (Exception e) {
            //Log.e("log_tag", "Error in http connection "+e.toString());
        }

        //convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
            //Log.v("I* retun values.....", sb+"...");
        } catch (Exception e) {
            //Log.e("log_tag", "Error converting result "+e.toString());
        }
        ////Log.v("Result from server", result);
        //try parse the string to a JSON object
        try {
            //Log.v("message 2", result+"...");
            jsonArr = new JSONArray(result);

        } catch (JSONException e) {
            //Log.e("log_tag", "Error parsing data "+e.toString());
        }
        return jsonArr;
    }

    public JSONArray getAddStockJsonArray(String url) {
        InputStream is = null;
        String result = "";
        JSONArray jArray = null;
        //Log.v("message", "In json parser 1"+url);
        //http post
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            //Log.v("message", "In json parser 2"+httpGet);
            HttpResponse response = httpclient.execute(httpGet);
            //Log.v("message", "In json parser 3"+response);
            HttpEntity entity = response.getEntity();
            //Log.v("message", "In json parser 4");
            is = entity.getContent();
            //Log.v("message", "In json parser 5");

        } catch (Exception e) {
            //Log.e("log_tag", "Error in http connection "+e.toString());
        }

        //convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
            ////Log.v("message 1", sb+"...");
        } catch (Exception e) {
            //Log.e("log_tag", "Error converting result "+e.toString());
        }
        ////Log.v("Result from server", result);
        //try parse the string to a JSON object
        try {
            ////Log.v("message 2", result+"...");
            jArray = new JSONArray(result);

        } catch (JSONException e) {
            //Log.e("log_tag", "Error parsing data "+e.toString());
        }

        return jArray;

    }

    public JSONObject getAddStockJsonArray(String addStockWebService, List<NameValuePair> nameValuePairs) {
        JSONObject jsonObj = new JSONObject();


        InputStream is = null;
        String result = "";
//		//Log.v("message", "In json parser 1"+url);
        //http post
        try {

            HttpClient httpclient = new DefaultHttpClient();


            HttpPost httpPost = new HttpPost(addStockWebService);

            //Log.v("message", "In json parser 2"+httpPost);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpclient.execute(httpPost);
            //Log.v("message", "In json parser 3"+response);
            HttpEntity entity = response.getEntity();
            //Log.v("message", "In json parser 4");
            is = entity.getContent();
            //Log.v("message", "In json parser 5");

            //Log.v("Values in nameValuePairs", ""+nameValuePairs);

        } catch (Exception e) {
            //Log.e("log_tag", "Error in http connection "+e.toString());
        }

        //convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
            ////Log.v("message 1", sb+"...");
        } catch (Exception e) {
            //Log.e("log_tag", "Error converting result "+e.toString());
        }
        ////Log.v("Result from server", result);
        //try parse the string to a JSON object
        try {
            ////Log.v("message 2", result+"...");
            jsonObj = new JSONObject(result);

        } catch (JSONException e) {
            //Log.e("log_tag", "Error parsing data "+e.toString());
        }
        return jsonObj;

    }


}
