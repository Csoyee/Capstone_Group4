package com.example.ddubeok;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends NMapActivity implements TextToSpeech.OnInitListener{

    public static  final String API_KEY = "9__1zOI_pMvk5HpCOOGY"; // Client ID: client ID 맞게 수정해주세요!

    NMapView mMapView;
    NMapController mMapController;
    static public double curLongtitude = 0, curLatitude = 0;

    static  OverlayManager overlayManager;
    static  GPSManager gpsManager;

    // convinience 정보, setting에 따라서 바뀜
    public static boolean drugstore = false;
    public static boolean cafe = false;
    public static boolean hospital = false;
    public static boolean station = false;
    public static boolean ATM = false;
    public static boolean toilet = false;

    public static boolean sound = true;
    public static boolean vibration = true;

    TextToSpeech TTS_object ;
    Vibrator vibrator ;
    // TODO: content 확정,
    String content[] ={
            "도착지를 입력해주세요",
            "빠른 길로 경로 안내를 시작합니다",
            "편안한 길로 경로 안내를 시작합니다",
            "안전한 길로 경로 안내를 시작합니다",
            "목적지 부근입니다",
            "목적지에 도착하였습니다, 안내를 종료합니다",
            "경로를 이탈 하였습니다",
            "잠시 후 좌회전 입니다",
            "잠시 후 우회전 입니다",
            "잠시 후 1시 방향 입니다",
            "잠시 후 2시 방향 입니다",
            "잠시 후 3시 방향 입니다",
            "잠시 후 4시 방향 입니다",
            "잠시 후 5시 방향 입니다",
            "잠시 후 6시 방향 입니다",
            "잠시 후 7시 방향 입니다",
            "잠시 후 8시 방향 입니다",
            "잠시 후 9시 방향 입니다",
            "잠시 후 10시 방향 입니다",
            "잠시 후 11시 방향 입니다"
    };

    static int len = -1;

    //searching
    boolean searchFlag = true;
    boolean start_default = false;
    String start_node, end_node;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapView = new NMapView(this);
        mMapController = mMapView.getMapController();

        mMapView.setClientId(API_KEY);

        TTS_object = new TextToSpeech(this, this);

        // set the activity content to the map view
        GridLayout MapContainer = (GridLayout) findViewById(R.id.gridLayout);

        MapContainer.addView(mMapView);

        MapViewSetting();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // overlay object
        overlayManager = new OverlayManager(this, mMapView, mMapController);


        // GPS object
        gpsManager = new GPSManager(this, mMapView, mMapController, overlayManager);

        gpsManager.startMyLocation(); // 내 위치 찾기 함수 호출


        // 버튼 누르면 내 위치로 돌아옴. TODO 버튼 디자인, 사진 크기 키우기
        FloatingActionButton GPSButton = (FloatingActionButton) findViewById(R.id.GPSbutton) ;
        GPSButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(gpsManager.mMapLocationManager.isMyLocationEnabled()) {
                    mMapController.animateTo(gpsManager.mMapLocationManager.getMyLocation());
                } else {
                    Toast.makeText(MainActivity.this, "GPS가 꺼져있습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });

        // TODO : activity design 다시 하기!
        FloatingActionButton SettingButton = (FloatingActionButton) findViewById(R.id.SettingButton) ;
        SettingButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick (View view) {
                // setting pop up 창 띄우기
                Log.e("Dubug: ", "setting 창을 띄워야 한다!\n");
                startActivity( new Intent(MainActivity.this, popUp.class ));
            }
        });

        // TODO: node 정보 받아오기
        Button Fast = (Button) findViewById(R.id.FAST);

        Fast.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick (View view ) {
                searchFlag = true;
                start_default = false;
                EditText start = (EditText) findViewById(R.id.StartText);
                EditText end = (EditText) findViewById(R.id.EndText);
                start_node = start.getText().toString() ;
                end_node = end.getText().toString() ;

                if((start_node.length() == 0) || (start_node.equals("내 위치"))) {
                    if (gpsManager.mMapLocationManager.isMyLocationEnabled()) {
                        mMapController.animateTo(gpsManager.mMapLocationManager.getMyLocation());
                        start_default = true;
                    } else {
                        Toast.makeText(MainActivity.this, "GPS가 꺼져있습니다.", Toast.LENGTH_LONG).show();
                        searchFlag = false;
                    }
                }
                if(searchFlag && (end_node.length() == 0) ) {
                    Toast.makeText(MainActivity.this, "도착지를 입력해주세요.", Toast.LENGTH_LONG).show();
                    speak(0);
                    searchFlag = false ;
                }

                if(searchFlag) {
                    // POST data
                    putData("http://13.125.247.173/startend.php");
                    // Test
                }
            }
        });

        Button Comfort = (Button) findViewById(R.id.COMFORT);
        Comfort.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick (View view ) {
                searchFlag = true;
                start_default = false;
                EditText start = (EditText) findViewById(R.id.StartText);
                EditText end = (EditText) findViewById(R.id.EndText);
                start_node ="";
                end_node = "";
                start_node = start.getText().toString() ;
                end_node = end.getText().toString() ;


                if((start_node.length() == 0) || (start_node.equals("내 위치"))) {
                    if (gpsManager.mMapLocationManager.isMyLocationEnabled()) {
                        mMapController.animateTo(gpsManager.mMapLocationManager.getMyLocation());
                        start_default = true;
                    } else {
                        Toast.makeText(MainActivity.this, "GPS가 꺼져있습니다.", Toast.LENGTH_LONG).show();
                        searchFlag = false;
                    }
                }
                if(searchFlag && (end_node.length() == 0) ) {
                    Toast.makeText(MainActivity.this, "도착지를 입력해주세요.", Toast.LENGTH_LONG).show();
                    speak(0);
                    searchFlag = false ;
                }

                if(searchFlag) {
                    // POST data
                    putData("http://13.125.247.173/startend.php");
                    startActivity( new Intent(MainActivity.this, ReviewComf.class )) ;
                }
            }
        });

        Button Safe = (Button) findViewById(R.id.SAFE);
        Safe.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick (View view ) {
                searchFlag = true;
                start_default = false;
                EditText start = (EditText) findViewById(R.id.StartText);
                EditText end = (EditText) findViewById(R.id.EndText);
                start_node = start.getText().toString() ;
                end_node = end.getText().toString() ;

                if((start_node.length() == 0) || (start_node.equals("내 위치"))) {
                    if (gpsManager.mMapLocationManager.isMyLocationEnabled()) {
                        mMapController.animateTo(gpsManager.mMapLocationManager.getMyLocation());
                        start_default = true;
                    } else {
                        Toast.makeText(MainActivity.this, "GPS가 꺼져있습니다.", Toast.LENGTH_LONG).show();
                        searchFlag = false;
                    }
                }
                if(searchFlag && (end_node.length() == 0) ) {
                    Toast.makeText(MainActivity.this, "도착지를 입력해주세요.", Toast.LENGTH_LONG).show();
                    speak(0);
                    searchFlag = false ;
                }

                if(searchFlag) {
                    // POST data
                    putData("http://13.125.247.173/startend.php");
                    startActivity( new Intent(MainActivity.this, ReviewSafe.class )) ;
                }
            }
        });

      //  overlayManager.moveableOverlayMarker(); // 클릭해서 이동가능한 overlay marker
        // Path 함수 테스트
        overlayManager.testOverlayPath(8);

    }


    private void MapViewSetting () {
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();
        mMapView.setBuiltInZoomControls(true, null);
    }

    public void callConvMarker() {
        try {
            overlayManager.convMarker();
        } catch (NullPointerException e) {
            Log.e("error occurred:", "NULLPOINT in overlayManager\n");
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if(TTS_object != null)   TTS_object.shutdown();
        for(int index = 0 ; index < len ; index ++ ){
            if (TTS_object!=null){
                TTS_object.shutdown();
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(TTS_object != null)   TTS_object.shutdown();

        for(int index = 0 ; index < len ; index ++ ){
            if (TTS_object!=null){
                TTS_object.shutdown();
            }
        }
    }

    @Override
    public void onInit(int i) {
        len = content.length;
    }

    // 진동 알람 함수
    private void runVibrator(int time_vib){
        if(vibration) {
            for (int i = 0; i < time_vib; i++) {
                vibrator.vibrate(500);
            }
        }
    }

    private void speak(int key){
        if(sound) {
            TTS_object.speak(content[key], TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    public void putData ( String url ) {
        class PutDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                HttpURLConnection conn=null;
                try {
                    URL url = new URL(uri);
                    String postParameters ="";
                    JSONObject json = new JSONObject();
                    if (start_default) {
                        postParameters = postParameters+ "latitude="+gpsManager.mMapLocationManager.getMyLocation().getLatitude()
                                + "& longtitude=" + gpsManager.mMapLocationManager.getMyLocation().getLongitude();
                    } else {
                        postParameters = postParameters+"latitude=NULL& longtitude=NULL &";
                        postParameters = postParameters+"startnode="+start_node;
                    }
                    postParameters=postParameters+"& endnode="+end_node;

                    speak(3);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    speak(4);
                    OutputStream outputStream = conn.getOutputStream();
                    outputStream.write(postParameters.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();

                    int responseStateus = conn.getResponseCode();
                   // Log.e("TAG", "POST response code-" + responseStateus);

                } catch (Throwable t) {
                    Toast.makeText(MainActivity.this, "Request failed:" + t.toString(), Toast.LENGTH_LONG).show();
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
            protected void onPostExecute(String result) {
                // TODO: speak function 빠른길(1), 편한길(2), 안전한 길(3) 구분.
                speak(1);
                runVibrator(1); // for test
            }
        }
        PutDataJSON g = new PutDataJSON();
        g.execute(url);
    }

    String myJSON;
    JSONArray pathArray = null ;
    ArrayList<HashMap<String, String >> pathList = new ArrayList<HashMap<String, String>>() ;

    public void getPathData ( String url ) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;
                }
            }
            @Override
            protected void onPostExecute(String result) {
                myJSON = result;
                try {
                    JSONObject jsonObj = new JSONObject(myJSON);
                    pathArray = jsonObj.getJSONArray("result");

                    for (int i = 0; i < pathArray.length(); i++) {
                        JSONObject c = pathArray.getJSONObject(i);
                        HashMap<String, String> path = new HashMap<String, String>();
                        path.put("latitude", c.getString("latitude"));
                        path.put("longtitude", c.getString("longtitude"));
                        path.put("angle", c.getString("angle"));
                        pathList.add(path);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

}
