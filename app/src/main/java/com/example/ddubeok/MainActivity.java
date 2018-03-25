package com.example.ddubeok;

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
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapResourceProvider;


public class MainActivity extends NMapActivity {

    public static  final String API_KEY = "9__1zOI_pMvk5HpCOOGY"; // Client ID: client ID 맞게 수정해주세요!

    NMapView mMapView;
    NMapController mMapController;

    // 지도 위 오버레이 객체 드로잉에 필요한 리소스 데이터 제공 클래스
    NMapResourceProvider mMapViewerResourceProvider;
    // 오버레이 객체 관리 클래스
    NMapOverlayManager mOverlayManager;


    // 지도위 현재 위치 표시하는 오버레이
    NMapMyLocationOverlay mapMyLocationOverlay;
    // 현재위치 탐색 기능 사용 클래스
    NMapLocationManager mMapLocationManager;
    // 단말기의 나침반
    NMapCompassManager mMapCompassManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mMapView = new NMapView(this);

        mMapView.setClientId(API_KEY);

        // set the activity content to the map view
        setContentView(mMapView);

        // initialize map view
        mMapView.setClickable(true);
        mMapController = mMapView.getMapController();

        // overlay
        mMapViewerResourceProvider = new NMapViewerResourceProvider(this);
        mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);

        //testOverlayMarker(128.3925046, 36.1454420);  // overlay test

        // GPS test

        mMapLocationManager = new NMapLocationManager(this);

        mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);
        mapMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);

        startMyLocation();
    }


    // overlaymarker TODO: 인자로 좌표를 넘겨서 원하는 좌표에 overlay를 찍을 수 있도록
    private void testOverlayMarker(double longtitude, double latitude) {
        int markerID = NMapPOIflagType.PIN;
        NMapPOIdata poIData = new NMapPOIdata(2, mMapViewerResourceProvider);
        poIData.beginPOIdata(2);
        poIData.addPOIitem(longtitude, latitude, "marker1", markerID, 0);
        poIData.endPOIdata();

        NMapPOIdataOverlay poIdataOverlay = mOverlayManager.createPOIdataOverlay(poIData, null);
        poIdataOverlay.showAllPOIdata(0);
    }


    private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener =
            new NMapLocationManager.OnLocationChangeListener() {
                @Override
                public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {

                    if(mMapController != null) {
                        mMapController.animateTo(myLocation);
                    }
                    return true;
                }

                @Override
                public void onLocationUpdateTimeout(NMapLocationManager locationManager) {
                }

                @Override
                public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {
                    stopMyLocation();
                }
            };


    // Add GPS function
    private void startMyLocation() {
        if (mMapLocationManager.isMyLocationEnabled()) {
            // GPS가 켜져있는 경우
            if(!mMapView.isAutoRotateEnabled()) {
                mapMyLocationOverlay.setCompassHeadingVisible(true);
                mMapCompassManager.enableCompass();
                mMapView.setAutoRotateEnabled(true, false);
            }
            mMapView.invalidate();
        } else {  // 현재 위치를 탐색중이 아니면.
                Boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(false);
                if(!isMyLocationEnabled){
                    Toast.makeText(this, "GPS가 꺼져있습니다.", Toast.LENGTH_LONG).show();
                    Intent goToSettings = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(goToSettings);
                    return;
                }
        }
    }

    private void stopMyLocation() {
        mMapLocationManager.disableMyLocation();

        if(mMapView.isAutoRotateEnabled()) {
            mapMyLocationOverlay.setCompassHeadingVisible(false);
            mMapCompassManager.disableCompass();
            mMapView.setAutoRotateEnabled(false, false);
        }
    }

    public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {
        if (errorInfo == null) { // success
            mMapController.setMapCenter(new NGeoPoint(126.978371, 37.5666091), 11);
        } else { // fail
            // log 남기기;
            Log.e("FailLog", "onMapInitHandler Failed");
        }
    }
}
