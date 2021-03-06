package com.example.ddubeok;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static com.nhn.android.maps.maplib.NGeoPoint.getDistance;
import static java.lang.Math.abs;
import static java.lang.Math.atan;


public class MainActivity extends NMapActivity implements TextToSpeech.OnInitListener{

    public static final String API_KEY = "9__1zOI_pMvk5HpCOOGY"; // Client ID: client ID 맞게 수정해주세요!
    public static final String SERVER_URL = "http://13.125.247.173/controlPath.php";
    private static String TAG = "MainActivity";
    private static final String TAG_PATHARR ="pathInfo";
    private static final String TAG_FLAGARR ="flagInfo";
    private static final String TAG_ID = "id";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_LONGITUDE ="longitude";
    private static final String TAG_ANGLE ="angle";
    private static final String TAG_FLAG ="flag";
    private static final String SEARCH_FAST = "1";
    private static final String SEARCH_COMF = "2";
    private static final String SEARCH_SAFE = "3";

    HashMap<String, String > User_position = new HashMap<String, String>();
    UserState t;

    ArrayList<HashMap<String, String >> pathList = new ArrayList<HashMap<String, String>>() ;
    String mJsonString;

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


    private static boolean movable_pin_exist = false;
    private static boolean movable_pin_exist_s = false;

    TextToSpeech TTS_object ;
    Vibrator vibrator ;
    // TODO: content 확정,
    String content[] ={
            "도착지를 입력해주세요",
            "빠른길로 경로 안내를 시작합니다",
            "편안한길로 경로 안내를 시작합니다",
            "안전한길로 경로 안내를 시작합니다",
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
            "잠시 후 11시 방향 입니다",
            "경로 재탐색을 시작합니다"
    };

    static int len = -1;

    //searching
    boolean searchFlag = true;
    boolean start_default = false;
    String start_addr, end_addr;
    int path_flag;

    static EditText endPoint;
    static EditText startPoint;

    // end overlay
    NMapPOIdataOverlay start_overlay;
    NMapPOIdataOverlay end_overlay;

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

        callConvMarker();

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
                start_addr = start.getText().toString() ;
                end_addr = end.getText().toString() ;

                if((start_addr.length() == 0) || (start_addr.equals("내 위치"))) {
                    if (gpsManager.mMapLocationManager.isMyLocationFixed()) {
                        mMapController.animateTo(gpsManager.mMapLocationManager.getMyLocation());
                        start_default = true;
                    } else {
                        Toast.makeText(MainActivity.this, "GPS가 꺼져있습니다.", Toast.LENGTH_LONG).show();
                        searchFlag = false;
                    }
                }
                if(searchFlag && (end_addr.length() == 0) ) {
                    Toast.makeText(MainActivity.this, "도착지를 입력해주세요.", Toast.LENGTH_LONG).show();
                    speak(0);
                    searchFlag = false ;
                }

                if(searchFlag) {
                    // 지도 위 오버레이 모두 제거하기
                    overlayManager.clearOverlayPath();
                    // post Data and get Data
                    controlData(SERVER_URL, SEARCH_FAST);

                    if(t != null)
                        t.interrupt();
                    t = new UserState();
                    t.start();

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
                start_addr ="";
                end_addr = "";
                start_addr = start.getText().toString() ;
                end_addr = end.getText().toString() ;


                if((start_addr.length() == 0) || (start_addr.equals("내 위치"))) {
                    if (gpsManager.mMapLocationManager.isMyLocationFixed()) {
                        mMapController.animateTo(gpsManager.mMapLocationManager.getMyLocation());
                        start_default = true;
                    } else {
                        Toast.makeText(MainActivity.this, "GPS가 꺼져있습니다.", Toast.LENGTH_LONG).show();
                        searchFlag = false;
                    }
                }
                if(searchFlag && (end_addr.length() == 0) ) {
                    Toast.makeText(MainActivity.this, "도착지를 입력해주세요.", Toast.LENGTH_LONG).show();
                    speak(0);
                    searchFlag = false ;
                }

                if(searchFlag) {
                    // 지도 위 오버레이 모두 제거하기
                    overlayManager.clearOverlayPath();
                    // post Data and get Data
                    controlData(SERVER_URL, SEARCH_COMF);
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
                start_addr = start.getText().toString() ;
                end_addr = end.getText().toString() ;

                if((start_addr.length() == 0) || (start_addr.equals("내 위치"))) {
                    if (gpsManager.mMapLocationManager.isMyLocationFixed()) {
                        mMapController.animateTo(gpsManager.mMapLocationManager.getMyLocation());
                        start_default = true;
                    } else {
                        Toast.makeText(MainActivity.this, "GPS가 꺼져있습니다.", Toast.LENGTH_LONG).show();
                        searchFlag = false;
                    }
                }
                if(searchFlag && (end_addr.length() == 0) ) {
                    Toast.makeText(MainActivity.this, "도착지를 입력해주세요.", Toast.LENGTH_LONG).show();
                    speak(0);
                    searchFlag = false ;
                }

                if(searchFlag) {
                    // 지도 위 오버레이 모두 제거하기
                    overlayManager.clearOverlayPath();
                    // post Data and get Data
                    controlData(SERVER_URL, SEARCH_SAFE);
                    startActivity( new Intent(MainActivity.this, ReviewSafe.class )) ;
                }
            }
        });

        Button select_end = (Button) findViewById(R.id.overlay);
        select_end.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick (View view ) {

                if(!movable_pin_exist) {
                    end_overlay = overlayManager.moveableOverlayMarker(); // 클릭해서 이동가능한 overlay marker
                    movable_pin_exist = true;
                } else {
                    overlayManager.removeMoveableOverlay(end_overlay);
                    movable_pin_exist = false;
                }
            }
        });

        Button select_start = (Button) findViewById(R.id.overlay_s);
        select_start.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick (View view ) {

                if(!movable_pin_exist_s) {
                    start_overlay = overlayManager.moveableStartMarker(); // 클릭해서 이동가능한 overlay marker
                    movable_pin_exist_s = true;
                } else {
                    overlayManager.removeMoveableOverlay(start_overlay);
                    movable_pin_exist_s = false;
                }
            }
        });

    }

    public class UserState extends Thread {
        @Override
        public void run() {
            int i = 0;
            int tail = 0;
            int currentNode = 1;
            int stack = 0;
            double angle = 0;

            while(true){
                tail++;
                if(tail > 2)
                {
                    NGeoPoint temp = new NGeoPoint();
                    temp.latitude = Double.parseDouble(pathList.get(currentNode).get("latitude"));
                    temp.longitude = Double.parseDouble(pathList.get(currentNode).get("longitude"));

                    NGeoPoint myLocation = gpsManager.mMapLocationManager.getMyLocation();

                    Log.d("distance : ", String.valueOf(getDistance(temp, myLocation)));
                    if(getDistance(temp, myLocation) < 5)
                    {
                        if(Double.parseDouble(pathList.get(currentNode).get("angle")) < 15 || Double.parseDouble(pathList.get(currentNode).get("angle")) > 345 )
                        {
                            Log.d("TG : ", "직진 방향");
                        }
                        else if(Double.parseDouble(pathList.get(currentNode).get("angle")) >= 15 && Double.parseDouble(pathList.get(currentNode).get("angle")) < 45)
                        {
                            //1시 방향
                            speak(9);
                        }
                        else if(Double.parseDouble(pathList.get(currentNode).get("angle")) >= 45 && Double.parseDouble(pathList.get(currentNode).get("angle")) < 75)
                        {
                            //2??諛⑺뼢
                            speak(10);
                        }
                        else if(Double.parseDouble(pathList.get(currentNode).get("angle")) >= 75 && Double.parseDouble(pathList.get(currentNode).get("angle")) < 105)
                        {
                            //3??諛⑺뼢
                            speak(11);
                        }
                        else if(Double.parseDouble(pathList.get(currentNode).get("angle")) >= 105 && Double.parseDouble(pathList.get(currentNode).get("angle")) < 135)
                        {
                            //4??諛⑺뼢
                            speak(12);
                        }
                        else if(Double.parseDouble(pathList.get(currentNode).get("angle")) >= 135 && Double.parseDouble(pathList.get(currentNode).get("angle")) < 165)
                        {
                            //5??諛⑺뼢
                            speak(13);
                        }
                        else if(Double.parseDouble(pathList.get(currentNode).get("angle")) >= 165 && Double.parseDouble(pathList.get(currentNode).get("angle")) < 195)
                        {
                            //6??諛⑺뼢
                            speak(14);
                        }
                        else if(Double.parseDouble(pathList.get(currentNode).get("angle")) >= 195 && Double.parseDouble(pathList.get(currentNode).get("angle")) < 225)
                        {
                            //7??諛⑺뼢
                            speak(15);
                        }
                        else if(Double.parseDouble(pathList.get(currentNode).get("angle")) >= 225 && Double.parseDouble(pathList.get(currentNode).get("angle")) < 255)
                        {
                            //8??諛⑺뼢
                            speak(16);
                        }
                        else if(Double.parseDouble(pathList.get(currentNode).get("angle")) >= 255 && Double.parseDouble(pathList.get(currentNode).get("angle")) < 285)
                        {
                            //9??諛⑺뼢
                            speak(17);
                        }
                        else if(Double.parseDouble(pathList.get(currentNode).get("angle")) >= 285 && Double.parseDouble(pathList.get(currentNode).get("angle")) < 315)
                        {
                            //10??諛⑺뼢
                            speak(18);
                        }
                        else if(Double.parseDouble(pathList.get(currentNode).get("angle")) >= 315 && Double.parseDouble(pathList.get(currentNode).get("angle")) < 345)
                        {
                            //11??諛⑺뼢
                            speak(19);
                        }

                        tail = 0;
                        currentNode= currentNode + 1;

                        if(currentNode == pathList.size())
                        {
                            speak(5);
                            return;
                        }
                    }
                }

                if(gpsManager.mMapLocationManager.isMyLocationFixed()) {
                    NGeoPoint myLocation = gpsManager.mMapLocationManager.getMyLocation();

                    User_position.put("latitude"+String.valueOf(tail % 5), String.valueOf(myLocation.getLatitude()));
                    User_position.put("longtitude"+String.valueOf(tail % 5), String.valueOf(myLocation.getLongitude()));
                }

                if(tail > 5)
                {
                    NGeoPoint temp1 = new NGeoPoint();
                    NGeoPoint temp2 = new NGeoPoint();
                    NGeoPoint current = new NGeoPoint();

                    temp1.latitude = Double.parseDouble(pathList.get(currentNode-1).get("latitude"));
                    temp1.longitude = Double.parseDouble(pathList.get(currentNode-1).get("longitude"));

                    temp2.latitude = Double.parseDouble(pathList.get(currentNode).get("latitude"));
                    temp2.longitude = Double.parseDouble(pathList.get(currentNode).get("longitude"));

                    angle = 0;

                    while(i < 5)
                    {
                        current.latitude = Double.parseDouble(User_position.get("latitude"+String.valueOf(i % 5)));
                        current.longitude = Double.parseDouble(User_position.get("longtitude"+String.valueOf(i % 5)));

                        angle += getAngle(temp1, temp2) - getAngle(temp1, current);
                        i++;
                    }

                    i = 0;

                    Log.d("angle : ", String.valueOf(abs(angle / 5)));
                    // TODO 경로 재탐색.
                    //경로 이탈 발생했을 경우(20도 이상)
                    if(abs(angle / 5) > 20 && tail % 5 == 0)
                    {
                        stack++;
                        speak(6);
                    }
                    else
                        stack = 0;

                    //경로 이탈한지 5초 경과했을 경우
                    if(stack > 4)
                    {
                        stack = 0;
                        speak(20);

                        overlayManager.clearOverlayPath();
                        controlData(SERVER_URL, SEARCH_FAST);

                    }


                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

    public double getAngle(NGeoPoint n1, NGeoPoint n2) {
        double angle = atan((n2.latitude - n1.latitude) / (n2.longitude - n1.longitude));
        angle = angle*(180 / 3.141592);
        if (angle < 0) {
            angle = 90.0 + abs(angle);
        }
        else {
            angle = 90.0 - angle;
        }
        if (n2.longitude < n1.longitude) {
            angle += 180.0;
        }

        return angle;
    }


    private void MapViewSetting () {
        startPoint = (EditText) findViewById(R.id.StartText);
        endPoint = (EditText) findViewById(R.id.EndText);
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
            // TODO 원인 찾아서 해결하기.
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
        Log.e("??", key+"??");
        if(sound) {
            TTS_object.speak(content[key], TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    public void controlData(String url , final String search_flag) {
        class ControlJsonData extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                HttpURLConnection conn=null;
                try {
                    URL url = new URL(uri);
                    String postParameters ="";
                    JSONObject json = new JSONObject();
                    if(start_addr.indexOf(" / ") == -1) {
                        if (start_default) {
                            postParameters = postParameters + "latitude=" + gpsManager.mMapLocationManager.getMyLocation().getLatitude()
                                    + "& longitude=" + gpsManager.mMapLocationManager.getMyLocation().getLongitude();
                        } else {
                            //postParameters = postParameters+"latitude=NULL& longitude=NULL &";
                            postParameters = postParameters + "startaddr=" + start_addr;
                        }
                    } else {
                        String[] temp = start_addr.split(" / ");
                        postParameters = postParameters+ "latitude="+temp[0]
                                + "& longitude=" + temp[1];

                    }

                    if(end_addr.indexOf(" / ") == -1) {
                        postParameters=postParameters+"& endaddr="+ end_addr;
                    } else {
                        String[] temp = end_addr.split(" / ");
                        postParameters = postParameters+ "& end_latitude="+temp[0]
                                + "& end_longtitude=" + temp[1];
                    }

                    postParameters=postParameters+"& path_type="+ search_flag;

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    OutputStream outputStream = conn.getOutputStream();
                    outputStream.write(postParameters.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();

                    int responseStatusCode = conn.getResponseCode();
                    Log.d(TAG, "POST response code - " + responseStatusCode);

                    InputStream inputStream;
                    if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                        inputStream = conn.getInputStream();
                    }
                    else{
                        inputStream = conn.getErrorStream();
                    }


                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    while((line = bufferedReader.readLine()) != null){
                        sb.append(line);
                    }

                    bufferedReader.close();

                    // get pathList from server
                    mJsonString = sb.toString();
                    pathList = getPathData();
                    path_flag = getFlagData();

                    return sb.toString();
                } catch (Exception e) {
                    Log.d(TAG, "InsertData: Error ", e);
                    return new String("Error: " + e.getMessage());
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    } else {
                        Log.e("Connection", "Failed");
                    }
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d(TAG, "response  - " + s);
                if(search_flag.equals(SEARCH_FAST)) {
                    speak(1);
                } else if (search_flag.equals(SEARCH_COMF)) {
                    speak(2);
                    //return ;
                } else if (search_flag.equals(SEARCH_SAFE)) {
                    speak(3);
                    //return ;
                }
                //Toast.makeText(MainActivity.this,"size :"+pathList.size(), Toast.LENGTH_LONG).show();

                if(path_flag == 0){ // 0 : 시작과 끝 정상적으로 존재 및 연결
                    // Toast.makeText(MainActivity.this,"size :"+pathList.size(), Toast.LENGTH_LONG).show();
                    // overlay test 오버레이 그리기
                    overlayManager.testOverlayPath(pathList);
                }else if(path_flag == 1){ // 1 : 시작 노드와 도착 노드 둘다 일치하는 노드가 서버에 없음
                    Toast.makeText(MainActivity.this,"Error Code : "+path_flag+", 시작 노드와 도착 노드 둘다 일치하는 노드가 서버에 없음.", Toast.LENGTH_LONG).show();
                }else if(path_flag == 2){ // 2 : 시작 노드와 일치하는 노드가 서버에 없음
                    Toast.makeText(MainActivity.this,"Error Code : "+path_flag+", 시작 노드와 일치하는 노드가 서버에 없음.", Toast.LENGTH_LONG).show();
                }else if(path_flag == 3){ // 3 : 도착 노드와 일치하는 노드가 서버에 없음
                    Toast.makeText(MainActivity.this,"Error Code : "+path_flag+", 도착 노드와 일치하는 노드가 서버에 없음.", Toast.LENGTH_LONG).show();
                }else if(path_flag == 4){ // 4 : 시작 노드와 도착 노드 사이 연결이 안되어 있음
                    Toast.makeText(MainActivity.this,"Error Code : "+path_flag+", 시작 노드와 도착 노드 사이 연결이 안되어 있음. / 서버로 요청이 너무 많습니다.", Toast.LENGTH_LONG).show();
                }
                movable_pin_exist = false ;
                movable_pin_exist_s = false ;
            }

            private ArrayList<HashMap<String, String >> getPathData(){
                ArrayList<HashMap<String, String >> path = new ArrayList<HashMap<String, String>>() ;
                try{
                    JSONObject jsonObject = new JSONObject(mJsonString);
                    JSONArray jsonArray = jsonObject.getJSONArray(TAG_PATHARR);

                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject item = jsonArray.getJSONObject(i);

                        String id = item.getString(TAG_ID);
                        String latitude = item.getString(TAG_LATITUDE);
                        String longitude = item.getString(TAG_LONGITUDE);
                        String angle = item.getString(TAG_ANGLE);

                        HashMap<String, String> hashMap = new HashMap<>();

                        hashMap.put(TAG_ID, id);
                        hashMap.put(TAG_LATITUDE, latitude);
                        hashMap.put(TAG_LONGITUDE, longitude);
                        hashMap.put(TAG_ANGLE, angle);

                        path.add(hashMap);
                    }
                }catch(JSONException e){
                    Log.d(TAG, "showResult : ", e);
                }
                return path;
            }

            private int getFlagData(){
                int flag = 0;
                try{
                    JSONObject jsonObject = new JSONObject(mJsonString);
                    JSONArray jsonArray = jsonObject.getJSONArray(TAG_FLAGARR);
                    JSONObject item_f = jsonArray.getJSONObject(0);
                    flag = item_f.getInt(TAG_FLAG);

                }catch(JSONException e){
                    Log.d(TAG, "showResult : ", e);
                }
                return flag;
            }
        }

        ControlJsonData g = new ControlJsonData();
        g.execute(url);
    }



    public static void setStartPoint(String fortest) {
        startPoint.setText(fortest, TextView.BufferType.EDITABLE);
    }

    public static void setEndPoint(String fortest) {
        endPoint.setText(fortest, TextView.BufferType.EDITABLE);
    }
}
