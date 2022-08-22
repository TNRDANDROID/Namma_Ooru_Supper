package com.nic.nammaoorusuper.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.nic.nammaoorusuper.BuildConfig;
import com.nic.nammaoorusuper.R;
import com.nic.nammaoorusuper.databinding.SplashScreenBinding;
import com.nic.nammaoorusuper.helper.AppVersionHelper;
import com.nic.nammaoorusuper.session.PrefManager;
import com.nic.nammaoorusuper.utils.Utils;


public class SplashScreen extends AppCompatActivity implements
        AppVersionHelper.myAppVersionInterface {
    private TextView textView;
    private Button button;
    private static int SPLASH_TIME_OUT = 2000;
    private PrefManager prefManager;
    public SplashScreenBinding splashScreenBinding;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashScreenBinding = DataBindingUtil.setContentView(this, R.layout.splash_screen);
        splashScreenBinding.setActivity(this);
        prefManager = new PrefManager(this);

            if (Utils.isOnline()) {
                checkAppVersion();
            } else {
                showSignInScreen();

            }

        Utils.statuscolor(SplashScreen.this);
    }


    private void showSignInScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i = new Intent(SplashScreen.this, LoginScreen.class);

                startActivity(i);
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        }, SPLASH_TIME_OUT);
    }

    private void checkAppVersion() {
        new AppVersionHelper(this, SplashScreen.this).callAppVersionCheckApi();
    }

    @Override
    public void onAppVersionCallback(String value) {
        if (value.length() > 0 && "Update".equalsIgnoreCase(value)) {
            startActivity(new Intent(this, AppUpdateDialog.class));
            finish();
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        } else {
            showSignInScreen();
        }

    }

}
