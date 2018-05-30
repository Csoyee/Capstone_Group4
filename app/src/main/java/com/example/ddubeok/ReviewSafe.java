package com.example.ddubeok;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ReviewSafe extends Activity {

    private static String TAG = "ReviewSafe" ;
    private static final String TAG_ID = "id";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_LONGITUDE ="longitude";

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.review_safe);
    }

    public void mOnGood ( View v ) {
        Log.e("debugging: ", "Click Good");
        pathSendingTest("http://13.125.247.173/getReview.php");
        finish();
    }


    public void mOnFar ( View v ) {
        Log.e("debugging: ", "Click Far");
        finish();
    }


    public void mOnBad ( View v ) {
        Log.e("debugging: ", "Click Bad");
        finish();
    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        // 바깥 레이어 클릭 시 안닫히게
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

    private void pathSendingTest(String url ) {
        class PathSending extends AsyncTask<String, Void, String> {
            //sample path list
            ArrayList<HashMap<String, String >> pathfortest = new ArrayList<HashMap<String, String>>() ;

            @Override
            protected String doInBackground(String... params) {

                makesampleData();
                String uri = params[0];
                HttpURLConnection conn=null;
                String postQuery = "";
                try {
                    URL url = new URL(uri);
                    JSONObject json = new JSONObject();
                    postQuery = putData();

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    OutputStream outputStream = conn.getOutputStream();
                    outputStream.write(postQuery.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();

                    int responseStatusCode = conn.getResponseCode();
                    Log.e(TAG, "POST response code - " + responseStatusCode);
                } catch (Exception e) {
                    Log.e(TAG, "sendData: Error ", e);
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    } else {
                        Log.e("Connection", "Failed");
                    }
                }
                return null;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d(TAG, "response  - " + s);
            }

            private String putData(){
                String postParameters ="";
                int num = pathfortest.size();

                if (num == 0 ){
                    Log.e("ERROR", "NODATA");
                    return "";
                }


                // TODO: 어떤 평가 결과 였는지에 따라서 결과를 보내는 column 추가.
                postParameters = postParameters + "start_latitude="+pathfortest.get(0).get(TAG_LATITUDE)
                                                +" & start_longtitude="  + pathfortest.get(0).get(TAG_LONGITUDE)
                                                + "& end_latitude=" + pathfortest.get(num - 1).get(TAG_LATITUDE)
                                                + "& end_longtitude= " + pathfortest.get(num - 1).get(TAG_LONGITUDE);
                Log.e("debugging,", postParameters + num);
                return postParameters;
            }

            private void makesampleData() {

                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(TAG_ID, "test");
                hashMap.put(TAG_LATITUDE, "12321.3");
                hashMap.put(TAG_LONGITUDE, "2321.32");

                pathfortest.add(hashMap);

                hashMap = new HashMap<>();

                hashMap.put(TAG_ID, "test2");
                hashMap.put(TAG_LATITUDE, "21321.3");
                hashMap.put(TAG_LONGITUDE, "1232.13");

                pathfortest.add(hashMap);

                hashMap = new HashMap<>();

                hashMap.put(TAG_ID, "test3");
                hashMap.put(TAG_LATITUDE, "23.213");
                hashMap.put(TAG_LONGITUDE, "14.12413");

                pathfortest.add(hashMap);
            }
        }

        PathSending g = new PathSending();

        g.execute(url);
    }
}
