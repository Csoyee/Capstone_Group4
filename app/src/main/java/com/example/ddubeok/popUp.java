package com.example.ddubeok;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.ServiceWorkerClient;
import android.widget.Switch;

/**
 * Created by youngchan on 2018-05-05.
 */

public class popUp extends Activity {

    Switch soundSwitch;
    Switch vibSwitch;
    Switch drugStore, hospital, cafe, station, ATM, toilet;


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
        // 저장 안하고 끄기
        finish();
    }

}
