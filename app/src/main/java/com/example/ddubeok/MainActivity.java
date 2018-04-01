package com.example.ddubeok;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
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


public class MainActivity extends NMapActivity {

    public static  final String API_KEY = "9__1zOI_pMvk5HpCOOGY"; // Client ID: client ID 맞게 수정해주세요!

    NMapView mMapView;
    NMapController mMapController;
    static public double curLongtitude = 0, curLatitude = 0;

    OverlayManager overlayManager;
    GPSManager gpsManager;


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

//
//    public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {
//        if (errorInfo == null) { // success
//            mMapController.setMapCenter(new NGeoPoint(126.978371, 37.5666091), 11);
//        } else { // fail
//            // log 남기기;
//            Log.e("FailLog", "onMapInitHandler Failed");
//        }
//    }


    public NMapView getViewer() {
        return mMapView;
    }

    public Context getContext() {
        Context c = MainActivity.this;
        return c;
    }
}
