package com.example.ddubeok;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlayItem;
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

    Context temp;

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

        /*
        if (MainActivity.cafe) {

        }
*/
        if (MainActivity.ATM) {

            // TODO database에서 list 받아오기
            Log.e("debugging:", "show up cafe!!\n");
            int markerID = NMapPOIflagType.PIN;
            int num = 1 ; // number of node TODO: list의 element 개수 넣기
            ATMPOI = new NMapPOIdata(2, mMapViewerResourceProvider);
            ATMPOI.beginPOIdata(2); // 인자값이 의미하는바는?

            NMapPOIitem item;

            item = ATMPOI.addPOIitem( 126.971123 , 37.296374 , "ATM", markerID, 0);
            item.setRightButton(true);

            item = ATMPOI.addPOIitem( 126.973030 ,37.296250, "ATM", markerID, 0);
            item.setRightButton(true);

            item = ATMPOI.addPOIitem( 126.971981, 37.296436  , "ATM", markerID, 0);
            item.setRightButton(true);

            item = ATMPOI.addPOIitem( 126.970502, 37.297360  , "ATM", markerID, 0);
            item.setRightButton(true);

            item = ATMPOI.addPOIitem( 126.970844, 37.299330 , "ATM", markerID, 0);
            item.setRightButton(true);

            ATMPOI.endPOIdata();

            ATMdataOverlay = mOverlayManager.createPOIdataOverlay(ATMPOI, null ) ; // TODO: drawable image 수정
            ATMdataOverlay.showAllPOIdata(0);
        } else {
            // 안보이게 만들기 // 화면 한번 눌러야 overlay 만들어진거 없어짐
            ATMPOI.removeAllPOIdata();
            ATMdataOverlay.removeAllPOIdata();
        }
/*
        if (MainActivity.station) {

        }

        if (MainActivity.toilet) {

        }

        if (MainActivity.hospital) {

        }
*/
        if (MainActivity.drugstore){
                // TODO database에서 list 받아오기
                Log.e("debugging:", "show up drugstore!!\n");
            int markerID = NMapPOIflagType.PIN;
            int num = 1 ; // number of node TODO: list의 element 개수 넣기
            drugPOI = new NMapPOIdata(2, mMapViewerResourceProvider);
            drugPOI.beginPOIdata(2); // 인자값이 의미하는바는?
            NMapPOIitem item;

            item = drugPOI.addPOIitem( 126.970983, 37.296989 , "drugstore", markerID, 0);
            item.setRightButton(true);

            item = drugPOI.addPOIitem( 126.970187, 37.296765, "drugstore", markerID, 0);
            item.setRightButton(true);

            item = drugPOI.addPOIitem( 126.970892, 37.298146  , "drugstore", markerID, 0);
            item.setRightButton(true);

            item = drugPOI.addPOIitem( 126.971121,37.298370  , "drugstore", markerID, 0);
            item.setRightButton(true);

            drugPOI.endPOIdata();

            drugdataOverlay = mOverlayManager.createPOIdataOverlay(drugPOI, null) ; // TODO: drawable image 수정
            drugdataOverlay.showAllPOIdata(0);
        } else {
            // 안보이게 만들기 // 화면 한번 눌러야 overlay 만들어진거 없어짐
            drugPOI.removeAllPOIdata();
            drugdataOverlay.removeAllPOIdata();
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
