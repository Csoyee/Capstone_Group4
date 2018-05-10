package com.example.ddubeok;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;

import org.w3c.dom.Text;


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
    String content[] ={
            "빠른 길로 경로 안내를 시작합니다",
            "편안한 길로 경로 안내를 시작합니다",
            "안전한 길로 경로 안내를 시작합니다",
            "목적지 부근입니다",
            "목적지에 도착하였습니다, 안내를 종료합니다",
            "경로를 이탈 하였습니다",
            "잠시 후 좌회전 입니다",
            "잠시 후 우회전 입니다",
            "잠시 후 N시 방향 입니다"
    };

    static int len = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //안내 멘트 리스트, 몇시 방향 안내는 추후 추가!
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapView = new NMapView(this);
        mMapController = mMapView.getMapController();

        mMapView.setClientId(API_KEY);

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


        // TODO : activity design 다시 하기! [hospital 등 편의 시설 정보 추가]
        FloatingActionButton SettingButton = (FloatingActionButton) findViewById(R.id.SettingButton) ;
        SettingButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick (View view) {
                // setting pop up 창 띄우기
                Log.e("Dubug: ", "setting 창을 띄워야 한다!\n");
                startActivity( new Intent(MainActivity.this, popUp.class ));
            }
        });

        Button Fast = (Button) findViewById(R.id.FAST);
        Fast.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick (View view ) {
                // dijkstra path from database table
                speak(0);
                runVibrator(1);
                Toast.makeText(MainActivity.this, "빠른 길 안내를 시작합니다.", Toast.LENGTH_LONG).show();
            }
        });

        Button Comfort = (Button) findViewById(R.id.COMFORT);
        Comfort.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick (View view ) {
                // dijkstra path from database table
                speak(1);
                Toast.makeText(MainActivity.this, "편안한 길 안내를 시작합니다.", Toast.LENGTH_LONG).show();
            }
        });

        Button Safe = (Button) findViewById(R.id.SAFE);
        Safe.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick (View view ) {
                // dijkstra path from database table
                speak(2);
                Toast.makeText(MainActivity.this, "안전한 길 안내를 시작합니다.", Toast.LENGTH_LONG).show();
            }
        });

      //  overlayManager.moveableOverlayMarker(); // 클릭해서 이동가능한 overlay marker

        TTS_object = new TextToSpeech(this, this);

        /* TODO: TTS 객체 리스트(혹은 array) 만들어 필요에 따라 객체 생성.
        if(myTTS == null || !myTTS.isSpeaking()) {
            myTTS = new TextToSpeech(this, this);
        }
        // TTS 주석 처리
        */
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
//      String fortest = "티티에스 테스트.";
    }

    private void runVibrator(int time_vib){
        for (int i = 0 ; i < time_vib ; i ++ ) {
            vibrator.vibrate(1000);
        }
    }

    private void speak(int key){
        TTS_object.speak(content[key], TextToSpeech.QUEUE_FLUSH, null, null);
    }
    //상황별 key값을 받아 리스트의 멘트를 음성 출력 하는 메소드
}
