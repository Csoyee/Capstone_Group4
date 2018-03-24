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

        //testOverlayMarker();  // overlay test
    }


    // overlaymarker TODO: 인자로 좌표를 넘겨서 원하는 좌표에 overlay를 찍을 수 있도록
    private void testOverlayMarker() {
        int markerID = NMapPOIflagType.PIN;
        NMapPOIdata poIData = new NMapPOIdata(2, mMapViewerResourceProvider);
        poIData.beginPOIdata(2);
        poIData.addPOIitem(128.3925046, 36.1454420, "marker1", markerID, 0);
        poIData.addPOIitem(128.3915046, 36.1354420, "marker2", markerID, 0);
        poIData.endPOIdata();

        NMapPOIdataOverlay poIdataOverlay = mOverlayManager.createPOIdataOverlay(poIData, null);
        poIdataOverlay.showAllPOIdata(0);
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

        }
    }
}
