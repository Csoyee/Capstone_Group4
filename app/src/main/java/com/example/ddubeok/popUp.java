package com.example.ddubeok;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.ServiceWorkerClient;
import android.widget.Switch;
import android.widget.Toast;

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
 * Created by youngchan on 2018-05-05.
 */

public class popUp extends Activity {

    Switch soundSwitch;
    Switch vibSwitch;
    Switch drugStore, hospital, cafe, station, ATM, toilet;

    /*
    String myJSON;
    JSONArray pathArray = null;
    ArrayList<HashMap<String, String>> pathList = new ArrayList<HashMap<String, String>>();
*/

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.settingpop);
        soundSwitch = (Switch) findViewById(R.id.soundSwitch);
        vibSwitch = (Switch) findViewById(R.id.vibSwitch);
        drugStore = (Switch) findViewById(R.id.drugSwitch);
        hospital = (Switch) findViewById(R.id.hosSwitch);
        cafe = (Switch) findViewById(R.id.cafeSwitch);
        station = (Switch ) findViewById(R.id.subwaySwitch);
        ATM = (Switch) findViewById(R.id.ATMSwitch);
        toilet = (Switch) findViewById(R.id.toiletSwitch);

        setting();

 //       if(ATM.isChecked())
//        {
//            getData("http://13.125.247.173/table.php");

//            Toast.makeText(this, pathList.get(0).get("latitude"), Toast.LENGTH_LONG).show();
//        }
    }

    private void setting () {
        soundSwitch.setChecked(MainActivity.sound);
        vibSwitch.setChecked(MainActivity.vibration);
        drugStore.setChecked(MainActivity.drugstore);
        hospital.setChecked(MainActivity.hospital);
        cafe.setChecked(MainActivity.cafe);
        station.setChecked(MainActivity.station);
        ATM.setChecked(MainActivity.ATM);
        toilet.setChecked(MainActivity.toilet);

    }
    public void mOnClose ( View v ) {
        MainActivity.sound = soundSwitch.isChecked();
        MainActivity.vibration = vibSwitch.isChecked();
        MainActivity.drugstore = drugStore.isChecked();
        MainActivity.hospital = hospital.isChecked();
        MainActivity.cafe = cafe.isChecked();
        MainActivity.station = station.isChecked();
        MainActivity.ATM = ATM.isChecked();
        MainActivity.toilet = toilet.isChecked();

        MainActivity m = new MainActivity();
        m.callConvMarker();

        finish();
    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        // 바깥 레이어 클릭 시 안닫히게
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        // 저장 하고 끄기
        MainActivity.sound = soundSwitch.isChecked();
        MainActivity.vibration = vibSwitch.isChecked();
        MainActivity.drugstore = drugStore.isChecked();
        MainActivity.hospital = hospital.isChecked();
        MainActivity.cafe = cafe.isChecked();
        MainActivity.station = station.isChecked();
        MainActivity.ATM = ATM.isChecked();
        MainActivity.toilet = toilet.isChecked();

        MainActivity m = new MainActivity();
        m.callConvMarker();

        finish();
    }

}
