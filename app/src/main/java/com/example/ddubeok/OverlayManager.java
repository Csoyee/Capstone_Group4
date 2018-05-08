package com.example.ddubeok;

import android.content.Context;
import android.util.Log;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapResourceProvider;

/**
 * Created by youngchan on 2018-04-01.
 */

public class OverlayManager extends NMapActivity {

    static NMapPOIdata cafePOI, ATMPOI, hospitalPOI, drugPOI, stationPOI, toiletPOI;
    static NMapPOIdataOverlay cafedataOverlay, ATMdataOverlay, hospitadataOverlay, drugdataOverlay, stationdataOverlay, toiletdataOverlay;


    // 지도 위 오버레이 객체 드로잉에 필요한 리소스 데이터 제공 클래스
    public NMapResourceProvider mMapViewerResourceProvider;
    // 오버레이 객체 관리 클래스
    public NMapOverlayManager mOverlayManager;

    NMapController MapController;

    public OverlayManager (Context context, NMapView mapview, NMapController mapcontroller) {
        // 생성자

        MapController = mapcontroller;

        mMapViewerResourceProvider = new NMapViewerResourceProvider( context );
        mOverlayManager = new NMapOverlayManager( context , mapview, mMapViewerResourceProvider);
    }

    // overlaymarker
    public void testOverlayMarker(double longtitude, double latitude) {
        int markerID = NMapPOIflagType.PIN;
        NMapPOIdata poIData = new NMapPOIdata(2, mMapViewerResourceProvider);
        poIData.beginPOIdata(2);
        poIData.addPOIitem(longtitude, latitude, "marker1", markerID, 0);
        poIData.endPOIdata();

        NMapPOIdataOverlay poIdataOverlay = mOverlayManager.createPOIdataOverlay(poIData, null);
        poIdataOverlay.showAllPOIdata(0);
    }

    // array overlaymarker
    public void convMarker () {
        Log.e("debugging:", "conMarker\n");

        if (MainActivity.cafe) {
            // TODO database에서 list 받아오기
            Log.e("debugging:", "show up cafe!!\n");
            int markerID = NMapPOIflagType.PIN;
            int num = 1 ; // number of node TODO: list의 element 개수 넣기
            cafePOI = new NMapPOIdata(2, mMapViewerResourceProvider);
            cafePOI.beginPOIdata(2); // 인자값이 의미하는바는?
            NMapPOIitem item;
            for (int i =0 ; i < num ; i++) { // TODO: list에 있는 걸로 overlay 만들기
                item = cafePOI.addPOIitem( 128.3925046, 36.1454420 , "test", markerID, 0); // sample data --> TODO: 위도 경도 값 수정
                item.setRightButton(true); // button 누르면 길찾기 시작하게 ? TODO: button handler 찾기
            }
            cafePOI.endPOIdata();

            cafedataOverlay = mOverlayManager.createPOIdataOverlay(cafePOI, null) ; // TODO: drawable image 수정
            cafedataOverlay.showAllPOIdata(0);
        } else {
            // 안보이게 만들기 // 화면 한번 눌러야 overlay 만들어진거 없어짐
            cafePOI.removeAllPOIdata();
            cafedataOverlay.removeAllPOIdata();
        }

        if (MainActivity.ATM) {

        }

        if (MainActivity.station) {

        }

        if (MainActivity.toilet) {

        }

        if (MainActivity.hospital) {

        }

        if (MainActivity.drugstore) {

        }
    }

    // moveable overlay Marker
    public void moveableOverlayMarker() {
        int marker1 = NMapPOIflagType.PIN;

        // set POI data
        NMapPOIdata poIData = new NMapPOIdata(1, mMapViewerResourceProvider);

        poIData.beginPOIdata(1);
        NMapPOIitem item = poIData.addPOIitem(null, "Touch and Drag to Move", marker1, 0);

        item.setPoint(MapController.getMapCenter());
        item.setFloatingMode(NMapPOIitem.FLOATING_TOUCH | NMapPOIitem.FLOATING_DRAG);
        item.setRightButton(true);

        NMapPOIdataOverlay poidataOverlay = mOverlayManager.createPOIdataOverlay(poIData, null);

        poidataOverlay.setOnFloatingItemChangeListener(onPOIdataFloatingItemChangeListener);

    }

    private final NMapPOIdataOverlay.OnFloatingItemChangeListener onPOIdataFloatingItemChangeListener = new NMapPOIdataOverlay.OnFloatingItemChangeListener() {

        @Override
        public void onPointChanged(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
            NGeoPoint point = item.getPoint();
//            findPlacemarkAtLocation(point.longitude, point.latitude);
//            item.setTitle(null);
        }
    };
}
