package com.example.ddubeok;

import android.app.Activity;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import com.nhn.android.maps.overlay.NMapPathData;
import com.nhn.android.maps.overlay.NMapPathLineStyle;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
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
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by youngchan on 2018-04-01.
 */

public class OverlayManager extends NMapActivity {

    static NMapPOIdata cafePOI, ATMPOI, hospitalPOI, drugPOI, stationPOI, toiletPOI;
    static NMapPOIdataOverlay cafedataOverlay, ATMdataOverlay, hospitadataOverlay, drugdataOverlay, stationdataOverlay, toiletdataOverlay;

    // 편의 시설 정보 가져오기
    String myJSON;
    JSONArray peoples = null ;
    ArrayList<HashMap<String, String >> personList = new ArrayList<HashMap<String, String>>() ;

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

    // TODO: 인자로 path list 넘겨줄 수 있도록!
    public void testOverlayPath (int node_num) {
        NMapPOIdata poiData = new NMapPOIdata(2, mMapViewerResourceProvider) ;
        poiData.beginPOIdata(2);
        // 출발지, 도착지 설정
        poiData.addPOIitem(126.974422, 37.298920, "begin", NMapPOIflagType.FROM, 0) ;
        poiData.addPOIitem(126.973867, 37.296867, "end", NMapPOIflagType.TO, 0 );
        poiData.endPOIdata();

        NMapPOIdataOverlay poIdataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
        poIdataOverlay.showAllPOIdata(0);
      //  poIdataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);

        NMapPathData pathData = new NMapPathData( node_num);
        pathData.addPathPoint(126.974422, 37.298920, NMapPathLineStyle.TYPE_SOLID);
        pathData.addPathPoint(126.974393, 37.298874, 0);
        pathData.addPathPoint(126.974190, 37.298921, 0);
        pathData.addPathPoint(126.974050, 37.298506, 0);
        pathData.addPathPoint(126.973629, 37.297776, 0);
        // 요 아래부터 점선으로 표시
        pathData.addPathPoint(126.974252, 37.297438, NMapPathLineStyle.TYPE_DASH);
        pathData.addPathPoint(126.974148, 37.296909, 0);
        pathData.addPathPoint(126.973867, 37.296867,0);
        pathData.endPathData();

        NMapPathDataOverlay pathDataOverlay = mOverlayManager.createPathDataOverlay(pathData);

    }

    // array overlaymarker
    public void convMarker () {
        Log.e("debugging:", "conMarker\n");


        getData("http://13.125.247.173/getConv.php");

        if (!MainActivity.cafe) {
            cafePOI.removeAllPOIdata();
            cafedataOverlay.removeAllPOIdata();
        }

        if (!MainActivity.ATM) {
            ATMPOI.removeAllPOIdata();
            ATMdataOverlay.removeAllPOIdata();
        }

        if (!MainActivity.station) {
            stationPOI.removeAllPOIdata();
            stationdataOverlay.removeAllPOIdata();
        }

        if (!MainActivity.toilet) {
            toiletPOI.removeAllPOIdata();
            toiletdataOverlay.removeAllPOIdata();
        }

        if (!MainActivity.hospital) {
            hospitalPOI.removeAllPOIdata();
            hospitadataOverlay.removeAllPOIdata();
        }

        if (!MainActivity.drugstore){
            drugPOI.removeAllPOIdata();
            drugdataOverlay.removeAllPOIdata();
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
                myJSON = result;
                try {
                    JSONObject jsonObj = new JSONObject(myJSON);
                    peoples = jsonObj.getJSONArray("result");

                    for (int i = 0; i < peoples.length(); i++) {
                        JSONObject c = peoples.getJSONObject(i);

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

                        personList.add(persons);
                    }

                    Log.e("for Debugging", peoples.length()+"");

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
                        for (int i = 0; i < peoples.length(); i++) {
                            if ( personList.get(i).get("type").equals("cafe") ) {
                                if(MainActivity.cafe) {
                                    item = cafePOI.addPOIitem( Double.parseDouble(personList.get(i).get("longtitude")) , Double.parseDouble(personList.get(i).get("latitude")) , personList.get(i).get("addr"), markerID, 0);
                                    item.setRightButton(true);
                                }
                            } else if (personList.get(i).get("type").equals("drugstore")){
                                if(MainActivity.drugstore) {
                                    item = drugPOI.addPOIitem( Double.parseDouble(personList.get(i).get("longtitude")) , Double.parseDouble(personList.get(i).get("latitude")) , personList.get(i).get("addr"), markerID, 0);
                                    item.setRightButton(true);
                                }
                            } else if (personList.get(i).get("type").equals("station")) {
                                if(MainActivity.station) {
                                    item = stationPOI.addPOIitem( Double.parseDouble(personList.get(i).get("longtitude")) , Double.parseDouble(personList.get(i).get("latitude")) , personList.get(i).get("addr"), markerID, 0);
                                    item.setRightButton(true);
                                }
                            } else if (personList.get(i).get("type").equals("hospital")) {
                                if(MainActivity.hospital) {
                                    item = hospitalPOI.addPOIitem( Double.parseDouble(personList.get(i).get("longtitude")) , Double.parseDouble(personList.get(i).get("latitude")) , personList.get(i).get("addr"), markerID, 0);
                                    item.setRightButton(true);
                                }
                            } else if (personList.get(i).get("type").equals("ATM")) {
                                if(MainActivity.ATM) {
                                    item = ATMPOI.addPOIitem( Double.parseDouble(personList.get(i).get("longtitude")) , Double.parseDouble(personList.get(i).get("latitude")) , personList.get(i).get("addr"), markerID, 0);
                                    item.setRightButton(true);
                                }
                            } else if (personList.get(i).get("type").equals("toilet")) {
                                if(MainActivity.toilet) {
                                    item = toiletPOI.addPOIitem( Double.parseDouble(personList.get(i).get("longtitude")) , Double.parseDouble(personList.get(i).get("latitude")) , personList.get(i).get("addr"), markerID, 0);
                                    item.setRightButton(true);
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
                                cafedataOverlay.removeAllPOIdata();
                            }
                            cafedataOverlay = mOverlayManager.createPOIdataOverlay(cafePOI, null) ; // TODO: drawable image
                            if(cafedataOverlay.size() > 0) {
                                cafedataOverlay.showAllPOIdata(0);
                            }
                        }

                        if (MainActivity.ATM) {
                            if(ATMdataOverlay != null) {
                                ATMdataOverlay.removeAllPOIdata();
                            }
                            ATMdataOverlay = mOverlayManager.createPOIdataOverlay(ATMPOI, null) ; // TODO: drawable image
                            if ( ATMdataOverlay.size() > 0) {
                                ATMdataOverlay.showAllPOIdata(0);
                            }
                        }

                        if (MainActivity.station) {
                            if(stationdataOverlay != null) {
                                stationdataOverlay.removeAllPOIdata();
                            }
                            stationdataOverlay = mOverlayManager.createPOIdataOverlay(stationPOI, null) ; // TODO: drawable image
                            if ( stationdataOverlay.size() > 0) {
                                stationdataOverlay.showAllPOIdata(0);
                            }
                        }

                        if (MainActivity.toilet) {
                            if(toiletdataOverlay != null) {
                                toiletdataOverlay.removeAllPOIdata();
                            }
                            toiletdataOverlay = mOverlayManager.createPOIdataOverlay(toiletPOI, null) ; // TODO: drawable image
                            if(toiletdataOverlay.size() > 0) {
                                toiletdataOverlay.showAllPOIdata(0);
                            }
                        }

                        if (MainActivity.hospital) {
                            if(hospitadataOverlay != null) {
                                hospitadataOverlay.removeAllPOIdata();
                            }
                            hospitadataOverlay = mOverlayManager.createPOIdataOverlay(hospitalPOI, null) ; // TODO: drawable image
                            if(hospitadataOverlay.size() > 0) {
                                hospitadataOverlay.showAllPOIdata(0);
                            }
                        }

                        if (MainActivity.drugstore){
                            if(drugdataOverlay != null) {
                                drugdataOverlay.removeAllPOIdata();
                            }
                            drugdataOverlay = mOverlayManager.createPOIdataOverlay(drugPOI, null) ; // TODO: drawable image
                            if(drugdataOverlay.size() > 0) {
                                drugdataOverlay.showAllPOIdata(0);
                            }
                        }

                        personList.clear();

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
