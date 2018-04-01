package com.example.ddubeok;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;

/**
 * Created by youngchan on 2018-04-01.
 */

public class GPSManager extends NMapActivity{

    // 지도위 현재 위치 표시하는 오버레이
    NMapMyLocationOverlay mapMyLocationOverlay;
    // 현재위치 탐색 기능 사용 클래스
    NMapLocationManager mMapLocationManager;
    // 단말기의 나침반
    NMapCompassManager mMapCompassManager;

    NMapView MapView;   // map view from main activity
    NMapController mMapController; // controller from main activity
    Context maincontext; // context for main activity

    OverlayManager overlayManager;

    boolean startGPSFlag = false;

    public GPSManager (Context context, NMapView mapview, NMapController controller, OverlayManager overlaymanager) {

        MapView = mapview;
        mMapController = controller;
        maincontext = context;

        // location manager
        mMapLocationManager = new NMapLocationManager(context);
        mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);

        // compass Manager --> TODO 나침반에 따라서 방향 인식
        mMapCompassManager = new NMapCompassManager(MainActivity.getInstance());

        mapMyLocationOverlay = overlaymanager.mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);
    }

    private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener =
            new NMapLocationManager.OnLocationChangeListener() {
                // 위치가 변경되면 호출
                @Override
                public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {
                    if(mMapController != null) {
                        if(!startGPSFlag){
                            mMapController.animateTo(myLocation); // GPS를 지도의 중심으로 화면 이동 --> 위치 변경 시마다 해당 위치로 지도의 중심이 움직이도록 함, 처음에만 필요해서 예외 처리
                            startGPSFlag = true;
                        }
                    }
                    MainActivity.curLatitude = myLocation.getLatitude();
                    MainActivity.curLongtitude = myLocation.getLongitude();
                    return true;
                }

                // 정해진 시간 내에 위치 탐색 실패 시 호출
                @Override
                public void onLocationUpdateTimeout(NMapLocationManager locationManager) {
                }

                // 현재 위치가 지도상에 표시할 수 있는 범위를 벗어나는 경우
                @Override
                public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {
                    stopMyLocation();
                }
            };


    // Add GPS function
    public void startMyLocation() {
        if (mMapLocationManager.isMyLocationEnabled()) {
            // GPS가 켜져있는 경우
            if(!MapView.isAutoRotateEnabled()) { // 지도 회전 기능 활성화 여부 확인
                mapMyLocationOverlay.setCompassHeadingVisible(true);  // 나침반 각도 표시
                mMapCompassManager.enableCompass(); // 나침반 모니터링 시작
                MapView.setAutoRotateEnabled(true, false);  // 지도 회전 기능 활성화
            }
            MapView.invalidate();
        } else {  // 현재 위치를 탐색중이 아니면.
            Boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(false);
            if(!isMyLocationEnabled){ // 위치 탐색 불가능
                Toast.makeText(maincontext, "GPS가 꺼져있습니다.", Toast.LENGTH_LONG).show();
                Intent goToSettings = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(goToSettings);
                return;
            }
        }
    }

    public void stopMyLocation() {
        startGPSFlag = false;
        mMapLocationManager.disableMyLocation();

        Toast.makeText(maincontext, "GPS 추적 범위가 아닙니다.", Toast.LENGTH_LONG).show();

        if(MapView.isAutoRotateEnabled()) {
            mapMyLocationOverlay.setCompassHeadingVisible(false);
            mMapCompassManager.disableCompass();
            MapView.setAutoRotateEnabled(false, false);
        }
    }
}
