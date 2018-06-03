package com.example.ddubeok;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.maps.overlay.NMapPathData;
import com.nhn.android.maps.overlay.NMapPathLineStyle;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapPathDataOverlay;
import com.nhn.android.mapviewer.overlay.NMapResourceProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.chrono.MinguoChronology;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by youngchan on 2018-04-01.
 */

public class OverlayManager extends NMapActivity {

    static NMapPOIdata cafePOI, ATMPOI, hospitalPOI, drugPOI, stationPOI, toiletPOI;
    static NMapPOIdataOverlay cafedataOverlay, ATMdataOverlay, hospitadataOverlay, drugdataOverlay, stationdataOverlay, toiletdataOverlay;

    static Drawable fortest;

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

    // 지도 위의 모든 오버레이를 제거하는 함수
    public void clearOverlayPath(){
        mOverlayManager.clearOverlays();
        convMarker(); // 경로 탐색 함수 불려도 편의시설 overlay 유지 
    }

    public void testOverlayPath (ArrayList<HashMap<String, String >> pathList) {
        int node_num = pathList.size();
        NMapPOIdata poiData = new NMapPOIdata(2, mMapViewerResourceProvider) ;
        poiData.beginPOIdata(2);
        // 출발지, 도착지 설정
        if(node_num == 0) {
            Log.e("ERROR", "There is no path");
            return;
        }

        double start_latitude = Double.parseDouble(pathList.get(0).get("latitude"));
        double start_longitude = Double.parseDouble(pathList.get(0).get("longitude"));
        double end_latitude = Double.parseDouble(pathList.get(node_num-1).get("latitude"));
        double end_longitude = Double.parseDouble(pathList.get(node_num-1).get("longitude"));
        poiData.addPOIitem(start_longitude, start_latitude, "begin", NMapPOIflagType.FROM, 0) ;
        poiData.addPOIitem(end_longitude, end_latitude, "end", NMapPOIflagType.TO, 0 );
        poiData.endPOIdata();
        // 출발지, 도착지 설정 완료

        NMapPOIdataOverlay poIdataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
        poIdataOverlay.showAllPOIdata(0);

        NMapPathData pathData = new NMapPathData( node_num);
        pathData.initPathData();
        for(int i=0; i<node_num; i++){
            double node_latitude = Double.parseDouble(pathList.get(i).get("latitude"));
            double node_longitude = Double.parseDouble(pathList.get(i).get("longitude"));
            pathData.addPathPoint(node_longitude, node_latitude, NMapPathLineStyle.TYPE_SOLID);
        }
        pathData.endPathData();
        // draw path
        NMapPathDataOverlay pathDataOverlay = mOverlayManager.createPathDataOverlay(pathData);

    }

    // array overlaymarker
    public void convMarker () {

        getData("http://13.125.247.173/getConv.php");

        if (!MainActivity.cafe) {
            if(cafePOI != null) {
                mOverlayManager.removeOverlay(cafedataOverlay);
            }
        }

        if (!MainActivity.ATM) {
            if (ATMPOI != null) {
                mOverlayManager.removeOverlay(ATMdataOverlay);
            }
        }
        if (!MainActivity.station) {
            if (stationPOI != null) {
                mOverlayManager.removeOverlay(stationdataOverlay);
            }
        }

        if (!MainActivity.toilet) {
            if (toiletPOI != null) {
                mOverlayManager.removeOverlay(toiletdataOverlay);
            }
        }

        if (!MainActivity.hospital) {
            if (hospitalPOI != null) {
                mOverlayManager.removeOverlay(hospitadataOverlay);
            }
        }

        if (!MainActivity.drugstore) {
            if (drugPOI != null) {
                mOverlayManager.removeOverlay(drugdataOverlay);
            }
        }
    }

    public void getData ( String url ) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {

                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;
                }
            }
            @Override
            protected void onPostExecute(String result) {
                String myJSON;

                myJSON = result;
                try {
                    JSONArray conv = null ;
                    JSONObject jsonObj = new JSONObject(myJSON);
                    conv = jsonObj.getJSONArray("result");
                    ArrayList<HashMap<String, String >> convList = new ArrayList<HashMap<String, String>>() ;

                    for (int i = 0; i < conv.length(); i++) {
                        JSONObject c = conv.getJSONObject(i);

                        String id = c.getString("nodeID");
                        String type = c.getString("type");
                        String addr = c.getString("addr");
                        String latitude = c.getString("latitude");
                        String longtitude = c.getString("longtitude");

                        HashMap<String, String> persons = new HashMap<String, String>();

                        persons.put("nodeID", id);
                        persons.put("type", type);
                        persons.put("addr", addr);
                        persons.put("latitude", latitude);
                        persons.put("longtitude", longtitude);

                        convList.add(persons);
                    }

                    Log.e("for Debugging", conv.length()+"");

                    if (MainActivity.ATM || MainActivity.toilet || MainActivity.cafe || MainActivity.hospital || MainActivity.station || MainActivity.drugstore) {
                        int markerID = NMapPOIflagType.PIN;
                        ATMPOI = new NMapPOIdata(2, mMapViewerResourceProvider);
                        hospitalPOI = new NMapPOIdata(2, mMapViewerResourceProvider);
                        stationPOI = new NMapPOIdata(2, mMapViewerResourceProvider);
                        drugPOI = new NMapPOIdata(2, mMapViewerResourceProvider);
                        toiletPOI = new NMapPOIdata(2, mMapViewerResourceProvider);
                        cafePOI = new NMapPOIdata(2, mMapViewerResourceProvider);

                        ATMPOI.beginPOIdata(2);
                        hospitalPOI.beginPOIdata(2);
                        stationPOI.beginPOIdata(2);
                        drugPOI.beginPOIdata(2);
                        toiletPOI.beginPOIdata(2);
                        cafePOI.beginPOIdata(2);

                        NMapPOIitem item;
                        for (int i = 0; i < conv.length(); i++) {
                            if ( convList.get(i).get("type").equals("cafe") ) {
                                if(MainActivity.cafe) {
                                    item = cafePOI.addPOIitem( Double.parseDouble(convList.get(i).get("longtitude")) , Double.parseDouble(convList.get(i).get("latitude")) , convList.get(i).get("addr"), markerID, 0);
                                }
                            } else if (convList.get(i).get("type").equals("drugstore")){
                                if(MainActivity.drugstore) {
                                    item = drugPOI.addPOIitem( Double.parseDouble(convList.get(i).get("longtitude")) , Double.parseDouble(convList.get(i).get("latitude")) , convList.get(i).get("addr"), markerID, 0);
                                }
                            } else if (convList.get(i).get("type").equals("station")) {
                                if(MainActivity.station) {
                                    item = stationPOI.addPOIitem( Double.parseDouble(convList.get(i).get("longtitude")) , Double.parseDouble(convList.get(i).get("latitude")) , convList.get(i).get("addr"), markerID, 0);
                                }
                            } else if (convList.get(i).get("type").equals("hospital")) {
                                if(MainActivity.hospital) {
                                    item = hospitalPOI.addPOIitem( Double.parseDouble(convList.get(i).get("longtitude")) , Double.parseDouble(convList.get(i).get("latitude")) , convList.get(i).get("addr"), markerID, 0);

                                }
                            } else if (convList.get(i).get("type").equals("ATM")) {
                                if(MainActivity.ATM) {
                                    item = ATMPOI.addPOIitem( Double.parseDouble(convList.get(i).get("longtitude")) , Double.parseDouble(convList.get(i).get("latitude")) , convList.get(i).get("addr"), markerID, 0);

                                }
                            } else if (convList.get(i).get("type").equals("toilet")) {
                                if(MainActivity.toilet) {
                                    item = toiletPOI.addPOIitem( Double.parseDouble(convList.get(i).get("longtitude")) , Double.parseDouble(convList.get(i).get("latitude")) , convList.get(i).get("addr"), markerID, 0);
                                }
                            }

                        }

                        ATMPOI.endPOIdata();
                        stationPOI.endPOIdata();
                        hospitalPOI.endPOIdata();
                        drugPOI.endPOIdata();
                        toiletPOI.endPOIdata();
                        cafePOI.endPOIdata();

                        if (MainActivity.cafe) {
                            if(cafedataOverlay != null) {
                                mOverlayManager.removeOverlay(cafedataOverlay);
                            }
                            if(cafePOI != null) {
                                cafedataOverlay = mOverlayManager.createPOIdataOverlay(cafePOI, null); // TODO: drawable image
                                cafedataOverlay.setOnStateChangeListener(onPOIdataStateItemChangeListener);

                                if (cafedataOverlay.size() > 0) {
                                    cafedataOverlay.showAllPOIdata(0);
                                }
                            }
                        }

                        if (MainActivity.toilet) {
                            if(toiletdataOverlay != null ) {
                                mOverlayManager.removeOverlay(toiletdataOverlay);
                            }
                            if(toiletPOI != null) {
                                toiletdataOverlay = mOverlayManager.createPOIdataOverlay(toiletPOI, null); // TODO: drawable image
                                toiletdataOverlay.setOnStateChangeListener(onPOIdataStateItemChangeListener);

                                if (toiletdataOverlay.size() > 0) {
                                    toiletdataOverlay.showAllPOIdata(0);
                                }
                            }
                        }

                        if (MainActivity.ATM) {
                            if(ATMdataOverlay != null) {
                                mOverlayManager.removeOverlay(ATMdataOverlay);
                            }
                            if(ATMPOI != null) {

                                ATMdataOverlay = mOverlayManager.createPOIdataOverlay(ATMPOI, null); // TODO: drawable image
                                ATMdataOverlay.setOnStateChangeListener(onPOIdataStateItemChangeListener);

                                if (ATMdataOverlay.size() > 0) {
                                    ATMdataOverlay.showAllPOIdata(0);
                                }
                            }
                        }

                        if (MainActivity.station) {
                            if(stationdataOverlay != null ) {
                                mOverlayManager.removeOverlay(stationdataOverlay);
                            }
                            if (stationPOI != null) {
                                stationdataOverlay = mOverlayManager.createPOIdataOverlay(stationPOI, null); // TODO: drawable image
                                stationdataOverlay.setOnStateChangeListener(onPOIdataStateItemChangeListener);

                                if (stationdataOverlay.size() > 0) {
                                    stationdataOverlay.showAllPOIdata(0);
                                }
                            }
                        }


                        if (MainActivity.hospital) {
                            if(hospitadataOverlay != null ) {
                                mOverlayManager.removeOverlay(hospitadataOverlay);
                            }
                            if (hospitalPOI != null) {
                                hospitadataOverlay = mOverlayManager.createPOIdataOverlay(hospitalPOI, null); // TODO: drawable image
                                hospitadataOverlay.setOnStateChangeListener(onPOIdataStateItemChangeListener);

                                if (hospitadataOverlay.size() > 0) {
                                    hospitadataOverlay.showAllPOIdata(0);
                                }
                            }
                        }

                        if (MainActivity.drugstore){
                            if(drugdataOverlay != null ) {
                                mOverlayManager.removeOverlay(drugdataOverlay);
                            }
                            if(drugPOI != null) {
                                drugdataOverlay = mOverlayManager.createPOIdataOverlay(drugPOI, null); // TODO: drawable image
                                drugdataOverlay.setOnStateChangeListener(onPOIdataStateItemChangeListener);
                                if (drugdataOverlay.size() > 0) {
                                    drugdataOverlay.showAllPOIdata(0);
                                }
                            }
                        }

                        convList.clear();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    // moveable overlay Marker
    public NMapPOIdataOverlay moveableOverlayMarker() {
        int marker1 = NMapPOIflagType.PIN;

        // set POI data
        NMapPOIdata poIData = new NMapPOIdata(1, mMapViewerResourceProvider);

        poIData.beginPOIdata(1);
        NMapPOIitem item = poIData.addPOIitem(null, "도착지 지정", marker1, 0);

        item.setPoint(MapController.getMapCenter());
        item.setFloatingMode(NMapPOIitem.FLOATING_TOUCH | NMapPOIitem.FLOATING_DRAG);

        NMapPOIdataOverlay poidataOverlay = mOverlayManager.createPOIdataOverlay(poIData, null);

        poidataOverlay.setOnFloatingItemChangeListener(onPOIdataFloatingItemChangeListener);
        poidataOverlay.setOnStateChangeListener(onMoveableStateItemChangeListener);

        return poidataOverlay ;
    }

    public void  removeMoveableOverlay (NMapPOIdataOverlay target) {
        mOverlayManager.removeOverlay(target);
    }
    private final NMapPOIdataOverlay.OnStateChangeListener onPOIdataStateItemChangeListener = new NMapPOIdataOverlay.OnStateChangeListener() {
        @Override
        public void onFocusChanged(NMapPOIdataOverlay nMapPOIdataOverlay, NMapPOIitem nMapPOIitem) {
            // when you click overlay --> nothing to do
        }

        @Override
        public void onCalloutClick(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
            // when you click Callout of overlay --> setText: edit text
            MainActivity.setEndPoint(item.getTitle());
        }
    };

    private final NMapPOIdataOverlay.OnStateChangeListener onMoveableStateItemChangeListener = new NMapPOIdataOverlay.OnStateChangeListener() {
        @Override
        public void onFocusChanged(NMapPOIdataOverlay nMapPOIdataOverlay, NMapPOIitem nMapPOIitem) {
            // when you click overlay --> nothing to do
        }

        @Override
        public void onCalloutClick(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
            // when you click Callout of overlay --> setText: edit text
            NGeoPoint point = item.getPoint();
            MainActivity.setEndPoint(Math.ceil(point.getLatitude() * 1000000)/1000000+" / "+Math.ceil(point.getLongitude()*1000000)/1000000);
        }
    };

    private final NMapPOIdataOverlay.OnFloatingItemChangeListener onPOIdataFloatingItemChangeListener = new NMapPOIdataOverlay.OnFloatingItemChangeListener() {

        @Override
        public void onPointChanged(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
            NGeoPoint point = item.getPoint();
//            findPlacemarkAtLocation(point.longitude, point.latitude);
//            item.setTitle(null);
        }

    };
}
