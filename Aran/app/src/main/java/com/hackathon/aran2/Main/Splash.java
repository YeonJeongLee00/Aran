package com.hackathon.aran2.Main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.hackathon.aran2.Login.LoginActivity;
import com.hackathon.aran2.R;

public class Splash extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler hd = new Handler();
        hd.postDelayed(new splashHandler(), 2000);
    }

    private class splashHandler implements Runnable {
        public void run(){
            startActivity(new Intent(getApplication(), LoginActivity.class));
            Splash.this.finish();
        }

    }

    @Override
    public void onBackPressed(){

    }
}
