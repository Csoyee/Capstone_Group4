package com.example.ddubeok;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

public class ReviewComf extends Activity {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.review_comf);


    }
    public void mOnGood ( View v ) {

        Log.e("debugging: ", "Click Good");
        finish();
    }


    public void mOnFar ( View v ) {

        Log.e("debugging: ", "Click Far");
        finish();
    }


    public void mOnBad ( View v ) {

        Log.e("debugging: ", "Click Bad");
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
}
