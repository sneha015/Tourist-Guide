package com.example.masterproject.touristguide;

import android.content.Context;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class Background extends AsyncTask<String,Void,String> {
    Context ctx;
    public interface OnUpdateListener{
        public void onUpdateBackground(String obj);
    }

    OnUpdateListener listener;

    public void setUpdateListener(OnUpdateListener listener){
        this.listener = listener;
    }

    Background(Context ctx){
        this.ctx = ctx;
    }

    protected void onPreExecute(){
        super.onPreExecute();
    }

    protected void onPostExecute(String obj){
        if(listener != null)
            listener.onUpdateBackground(obj);
    }

    protected void onProgressUpdate(){
        super.onProgressUpdate();
    }

    @Override
    protected String doInBackground(String... params) {
        String image_str = params[0];
        String lat = params[1];
        String longitude = params[2];
        String loc_flag = params[3];
        String server_url = "http://107.170.254.37/";
        URL url = null;

        try {
            Log.e("Entered background", "Hey");

            url = new URL(server_url);
            HttpURLConnection httpURLConnection = null;
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            String data = URLEncoder.encode("image","UTF-8")+"="+ URLEncoder.encode(image_str,"UTF-8");
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
            String response = "";
            String line = "";
            Log.e("Server Msg", "Hey");
            while ((line = bufferedReader.readLine())!=null){
                response+=line;
                Log.e("Server Msg",response);
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

//            //------------megha wiki---------------------------
            if(response.endsWith("2")) {
                if(loc_flag == "0") {
//                    Toast.makeText(ctx,  "No location" , Toast.LENGTH_SHORT).show();
                    Log.e("No location", "No location");

                    return "No location found";

                }
                else {
                    String place = response.substring(0, response.length() - 1);
                    Log.i("place", place);
                    String title1 = URLEncoder.encode(place, "UTF-8");
                    Log.e("title  name", title1);
                    String mediaWiki1 = "https://en.wikipedia.org/w/api.php?action=query&titles=" + title1 + "&list=geosearch&gscoord=" + lat + "%7C" + longitude + "&gsradius=10000&gslimit=1&format=json";

                    try {
                        URL mediaUrl1 = new URL(mediaWiki1);
                        HttpURLConnection urlMediaConnection1 = (HttpURLConnection) mediaUrl1.openConnection();
                        InputStream in1 = urlMediaConnection1.getInputStream();
                        InputStreamReader reader1 = new InputStreamReader(in1);
                        int mediaRes = reader1.read();
                        String media1 = "";
                        while (mediaRes != -1) {

                            char current = (char) mediaRes;

                            media1 += current;

                            mediaRes = reader1.read();

                        }

                        JSONObject object2 = new JSONObject(media1);

                        JSONObject query1 = object2.getJSONObject("query");

                        JSONArray geosearch1 = query1.getJSONArray("geosearch");
                        String title2 = "";

                        for (int i = 0; i < geosearch1.length(); i++) {

                            JSONObject object1 = (JSONObject) geosearch1.get(i);

                            title2 = object1.getString("title");

                            // pageId = object1.getString("pageid");

                            System.out.println(title2);

                            Log.i("json", title2);
                        }
                        String name = URLEncoder.encode(title2, "UTF-8");
                        String mediaWiki = "https://en.wikipedia.org/w/api.php?action=query&titles=" + name + "&prop=extracts&exintro=&explaintext&format=json";
                        Log.i("url", mediaWiki);


                        URL mediaUrl2 = new URL(mediaWiki);
                        HttpURLConnection urlMediaConnection2 = (HttpURLConnection) mediaUrl2.openConnection();
                        InputStream in3 = urlMediaConnection2.getInputStream();
                        InputStreamReader reader2 = new InputStreamReader(in3);
                        int mediaResponse2 = reader2.read();
                        String media2 = "";

                        while (mediaResponse2 != -1) {

                            char current = (char) mediaResponse2;

                            media2 += current;

                            mediaResponse2 = reader2.read();

                        }
                        JSONObject jObj2 = new JSONObject(new String(media2));
                        JSONObject query2 = jObj2.getJSONObject("query");
                        JSONObject pages2 = query2.getJSONObject("pages");
                        Iterator<String> keys2 = pages2.keys();

                        JSONObject page_data = null;

                        while (keys2.hasNext()) {
                            String key = keys2.next();
                            page_data = pages2.getJSONObject(key);
                            break;

                        }

                        String final_data2 = page_data.getString("extract");


                        Log.e("EXTRACT", final_data2);
                        return final_data2;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
//            //----------megha wiki end-------------------------


                //------------megha wiki part 2 start---------------------------
                // String pageId="";
                String final_data = "";
                String title = URLEncoder.encode(response, "UTF-8");
                Log.i("titlename", title);
                String mediaWiki = "https://en.wikipedia.org/w/api.php?action=query&titles=" + title + "&prop=extracts&exintro=&explaintext&format=json";
                Log.i("url", mediaWiki);


                try {
                    URL mediaUrl = new URL(mediaWiki);
                    HttpURLConnection urlMediaConnection = (HttpURLConnection) mediaUrl.openConnection();
                    InputStream in = urlMediaConnection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);
                    int mediaResponse = reader.read();
                    String media = "";

                    while (mediaResponse != -1) {

                        char current = (char) mediaResponse;

                        media += current;

                        mediaResponse = reader.read();

                    }
                    JSONObject jObj = new JSONObject(new String(media));
                    JSONObject query1 = jObj.getJSONObject("query");
                    JSONObject pages = query1.getJSONObject("pages");
                    Iterator<String> keys = pages.keys();

                    JSONObject page_data = null;

                    while (keys.hasNext()) {
                        String key = keys.next();
                        page_data = pages.getJSONObject(key);
                        break;

                    }

                    final_data = page_data.getString("extract");


                    Log.e("EXTRACT", final_data);

                    int pageContentResponse = urlMediaConnection.getResponseCode();
                    Log.i("response code url 2", Integer.toString(pageContentResponse));
                } catch (Exception e) {
                    e.printStackTrace();
                }


                //----------megha wiki end part 2-------------------------

                return final_data; // return here
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
