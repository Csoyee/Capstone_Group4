package com.example.ddubeok;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;

import java.util.Locale;


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
        mMapView.setClientId(API_KEY);

        // set the activity content to the map view
        setContentView(mMapView);

        MapViewSetting();

        // overlay object
        overlayManager = new OverlayManager(this, mMapView, mMapController);


        // GPS object
        gpsManager = new GPSManager(this, mMapView, mMapController, overlayManager);

        gpsManager.startMyLocation(); // 내 위치 찾기 함수 호출

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
        mMapController = mMapView.getMapController();

    }



    public NMapView getViewer() {
        return mMapView;
    }

    public Context getContext() {
        Context c = MainActivity.this;
        return c;
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
