package com.example.ddubeok;

import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;


public class MainActivity extends NMapActivity implements TextToSpeech.OnInitListener{

    public static  final String API_KEY = "9__1zOI_pMvk5HpCOOGY"; // Client ID: client ID 맞게 수정해주세요!

    NMapView mMapView;
    NMapController mMapController;
    static public double curLongtitude = 0, curLatitude = 0;

    OverlayManager overlayManager;
    GPSManager gpsManager;

    TextToSpeech myTTS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mMapView = new NMapView(this);
        mMapController = mMapView.getMapController();

        mMapView.setClientId(API_KEY);

        // set the activity content to the map view
//        setContentView(mMapView);
        GridLayout MapContainer = (GridLayout) findViewById(R.id.gridLayout);

        MapContainer.addView(mMapView);

        MapViewSetting();

        // overlay object
        overlayManager = new OverlayManager(this, mMapView, mMapController);


        // GPS object
        gpsManager = new GPSManager(this, mMapView, mMapController, overlayManager);

        gpsManager.startMyLocation(); // 내 위치 찾기 함수 호출

        // 버튼 누르면 내 위치로 돌아옴. TODO 버튼 디자인.
        FloatingActionButton GPSButton = (FloatingActionButton) findViewById(R.id.GPSbutton) ;
        GPSButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMapController.animateTo(gpsManager.mMapLocationManager.getMyLocation());
            }
        });

        // overlayManager.moveableOverlayMarker(); // 클릭해서 이동가능한 overlay marker

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

    @Override
    public void onStop() {
        super.onStop();
        if(myTTS != null)   myTTS.shutdown();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(myTTS != null)   myTTS.shutdown();
    }

    @Override
    public void onInit(int i) {
        String fortest = "티티에스 테스트.";

        myTTS.speak(fortest, TextToSpeech.QUEUE_FLUSH, null);
    }
}
