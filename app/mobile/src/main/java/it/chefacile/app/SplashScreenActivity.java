package it.chefacile.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.v7.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity{

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final boolean firstTimeRun = getFirstTimeRun();
        
        //thread for splash screen running
        Thread screenDisplayTimer = new Thread(){
        	public void run(){
        		try {
					sleep(2000);
				} catch (InterruptedException e) {
				}finally{
                    if (firstTimeRun == true) {
                        startActivity(new Intent(SplashScreenActivity.this, IntroScreenActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } else {
                        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }

                    storeFirstTimeRun();

				}
        		finish();
        	}
        };
        screenDisplayTimer.start();
	}

    private boolean getFirstTimeRun() {
        SharedPreferences prefs = getSharedPreferences("First Time Run Value", MODE_PRIVATE);
        boolean firstTimeRun = prefs.getBoolean("firstRun", true);
        return firstTimeRun;
    }

    private void storeFirstTimeRun() {
        SharedPreferences prefs = getSharedPreferences("First Time Run Value", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstRun", false);
        editor.commit();
    }

}
