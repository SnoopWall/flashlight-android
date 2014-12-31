/*
The Likeness, Logos and text of this app are provided as reference and may not be reproduced in a derivative work
Copyright 2014 SnoopWall LLC

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/

package com.snoopwall.flashlight;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class Light extends Activity {
    public static final int KILL = 1;
    private LinearLayout root;
    private Drawable defaultBak;
    private boolean ledToggle = false;
    private TextView ledView;
    private Button stopStartButton;
    private Button resetButton;
    private boolean settingsPressed = false;
    private boolean backlightToggle = false;
    private boolean timerRunning = false;
    private boolean timerReady = true;
    private boolean timerVisible = true;
    private TextView backlightView;
    private TextView text1;
    private TextView text2;
    private TextView hoursCount, minutesCount, secondsCount, timerArrow;
    private long hoursLeft, minutesLeft, secondsLeft;
    private LinearLayout timerStuff;
    private Timer currentTimer;
    private LightApp app;
    public Handler finisher;
    final Handler timerHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        app = (LightApp)getApplication();

        String fontPath = "icon.ttf";
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        setContentView(R.layout.activity_light);

        ledToggle = app.isLEDOn();
        backlightToggle = app.backLightOn;
        
        ledView = (TextView)findViewById(R.id.LED_ON_OFF);
        ledView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ledToggle = app.isLEDOn();
                toggleLED();
            }
        });
        ledView.setTypeface(tf);

        backlightView =  (TextView)findViewById(R.id.BACKLIGHT_ON_OFF);
        backlightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleBackLight();
            }
        });
        backlightView.setTypeface(tf);

        final Intent i = new Intent(this, Settings.class);
        Button settings = (Button) findViewById(R.id.settingsButton);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsPressed = true;
                startActivityForResult(i, 0);
            }
        });

        hoursCount = (TextView)findViewById(R.id.countHours);
        minutesCount = (TextView)findViewById(R.id.countMinutes);
        secondsCount = (TextView)findViewById(R.id.countSeconds);
        timerStuff = (LinearLayout)findViewById(R.id.timerStuff);
        timerArrow = (TextView)findViewById(R.id.timerArrow);

        timerVisible = false;
        timerStuff.setVisibility(View.GONE);
        timerArrow.setText(R.string.timer_arrow_right);
        timerArrow.setTextSize(26);

        setTimerDisplay();

        Button timerButton = (Button) findViewById(R.id.timerButton);
        timerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHideTimer();
            }
        });

        stopStartButton = (Button)findViewById(R.id.stopStartTimerBtn);
        stopStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timerRunning && timerReady){
                    timerRunning = false;
                    ledToggle = true;
                    backlightToggle = true;
                    toggleLED();
                    toggleBackLight();
                    stopStartButton.setText(R.string.start_text);
                    stopTimer(currentTimer);
                }else if(timerReady){
                    timerRunning = true;
                    if(!app.isLEDOn()){
                        ledToggle = false;
                        toggleLED();
                    }
                    if(!app.backLightOn){
                        backlightToggle = false;
                        toggleBackLight();
                    }
                    stopStartButton.setText(R.string.stop_text);
                    currentTimer = startNewTimer();
                }
            }
        });

        resetButton = (Button)findViewById(R.id.resetTimerBtn);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerRunning = false;
                timerReady = true;
                ledToggle = true;
                backlightToggle = true;
                toggleLED();
                toggleBackLight();
                stopStartButton.setText(R.string.start_text);
                stopTimer(currentTimer);
                setTimerDisplay();
            }
        });

        root  = (LinearLayout) findViewById(R.id.ROOT_LAYOUT);

        defaultBak = getResources().getDrawable(R.drawable.bak);
        text1 = (TextView) findViewById(R.id.textView2);
        text1.setText(Html.fromHtml(getString(R.string.header_text)));
        text2 = (TextView) findViewById(R.id.urlText);
        text2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.snoopwall.com"));
                startActivity(browserIntent);
            }
        });

        ImageView iv = (ImageView) findViewById(R.id.imageView);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.snoopwall.com"));
                startActivity(browserIntent);
            }
        });
        finisher = new Handler(){
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case KILL:
                        turnOffBackLight();
                        finish();
                        break;

                }
            }

        };

        Message m = Message.obtain();
        m.what = LightApp.VIEW_UPDATE;
        m.obj = this;
        app.mHandler.sendMessage(m);
        m = null;
        if(app.backLightOn) {
            backSetter(backlightView, getResources().getDrawable(R.drawable.circle_on));
            backlightView.setTextColor(getResources().getColor(R.color.textOn));
            turnOnBacklight();
        } else {
            backSetter(backlightView,getResources().getDrawable(R.drawable.circle_off));
            backlightView.setTextColor(getResources().getColor(R.color.textOff));
            turnOffBackLight();
        }
        if(app.isLEDOn()){
            backSetter(ledView,getResources().getDrawable(R.drawable.circle_on));
            ledView.setTextColor(getResources().getColor(R.color.textOn));
        } else {
            backSetter(ledView,getResources().getDrawable(R.drawable.circle_off));
            ledView.setTextColor(getResources().getColor(R.color.textOff));
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setTimerDisplay();
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(!settingsPressed){
            app.mHandler.sendEmptyMessage(LightApp.MAYBE_DONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            finish();
        }else{
            settingsPressed = false;
        }
    }

    public void toggleLED(){
        if(ledToggle) {
            backSetter(ledView,getResources().getDrawable(R.drawable.circle_off));
            ledView.setTextColor(getResources().getColor(R.color.textOff));
            app.turnOffCam();
        } else{
            backSetter(ledView,getResources().getDrawable(R.drawable.circle_on));
            ledView.setTextColor(getResources().getColor(R.color.textOn));
            app.turnOnCam();
        }
    }

    @SuppressLint("NewApi")
    private static void backSetter(View x,Drawable y){
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            x.setBackgroundDrawable(y);
        } else {
            x.setBackground(y);
        }
    }

    private void toggleBackLight(){
        if(backlightToggle) {
            backlightToggle = false;
            backSetter(backlightView, getResources().getDrawable(R.drawable.circle_off));
            backlightView.setTextColor(getResources().getColor(R.color.textOff));
            turnOffBackLight();
        } else{
            backSetter(backlightView,getResources().getDrawable(R.drawable.circle_on));
            backlightView.setTextColor(getResources().getColor(R.color.textOn));
            backlightToggle = true;
            turnOnBacklight();
        }
    }

    private void turnOnBacklight(){
        Window w = getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        lp.buttonBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        w.setAttributes(lp);
        backSetter(root, null);
        root.setBackgroundColor(getResources().getColor(android.R.color.white));
        text1.setTextColor(getResources().getColor(android.R.color.black));
        text2.setTextColor(getResources().getColor(android.R.color.black));
        if (Build.VERSION.SDK_INT >= 16) {
            stopStartButton.setBackground(getResources().getDrawable(R.drawable.scheme_snoopwall_buttons_light));
            resetButton.setBackground(getResources().getDrawable(R.drawable.scheme_snoopwall_buttons_light));
        }else{
            stopStartButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.scheme_snoopwall_buttons_light));
            resetButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.scheme_snoopwall_buttons_light));
        }
        app.backLightOn = true;
    }

    public void turnOffBackLight(){
        Window w = getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        lp.buttonBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        w.setAttributes(lp);
        backSetter(root, defaultBak);
        text1.setTextColor(getResources().getColor(android.R.color.white));
        text2.setTextColor(getResources().getColor(android.R.color.white));
        if (Build.VERSION.SDK_INT >= 16) {
            stopStartButton.setBackground(getResources().getDrawable(R.drawable.scheme_snoopwall_buttons));
            resetButton.setBackground(getResources().getDrawable(R.drawable.scheme_snoopwall_buttons));
        }else{
            stopStartButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.scheme_snoopwall_buttons));
            resetButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.scheme_snoopwall_buttons));
        }
        app.backLightOn = false;
    }

    private Timer startNewTimer(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(secondsLeft > 0){
                    secondsLeft--;
                    updateSeconds();
                }else if(secondsLeft == 0){
                    if(minutesLeft > 0){
                        secondsLeft = 59;
                        minutesLeft--;
                        updateSeconds();
                        updateMinutes();
                    }else if(minutesLeft == 0){
                        if(hoursLeft > 0) {
                            secondsLeft = 59;
                            minutesLeft = 59;
                            hoursLeft--;
                            updateSeconds();
                            updateMinutes();
                            updateHours();
                        }else if (hoursLeft == 0){
                            timerHandler.post(timeOut);
                            stopTimer(currentTimer);
                        }
                    }
                }
            }
        }, 0, 1000);
        return timer;
    }

    private void updateSeconds(){
        timerHandler.post(setSecondsText);
    }

    private void updateMinutes(){
        timerHandler.post(setMinutesText);
    }

    private void updateHours(){
        timerHandler.post(setHoursText);
    }

    final Runnable setSecondsText = new Runnable() {
        @Override
        public void run() {
            secondsCount.setText(Long.toString(secondsLeft));
        }
    };

    final Runnable setMinutesText = new Runnable() {
        @Override
        public void run() {
            minutesCount.setText(Long.toString(minutesLeft));
        }
    };

    final Runnable setHoursText = new Runnable() {
        @Override
        public void run() {
            hoursCount.setText(Long.toString(hoursLeft));
        }
    };

    final Runnable timeOut = new Runnable() {
        @Override
        public void run() {
            timerRunning = false;
            timerReady = false;
            backSetter(ledView,getResources().getDrawable(R.drawable.circle_off));
            ledView.setTextColor(getResources().getColor(R.color.textOff));
            app.turnOffCam();
            backSetter(backlightView, getResources().getDrawable(R.drawable.circle_off));
            backlightView.setTextColor(getResources().getColor(R.color.textOff));
            turnOffBackLight();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    };

    private void stopTimer(Timer timer){
        if(timer != null) {
            timer.cancel();
        }
    }

    public void setTimerDisplay(){
        long[] timerVals = app.getTimerValues();
        hoursLeft = timerVals[0];
        minutesLeft = timerVals[1];
        secondsLeft = timerVals[2];
        hoursCount.setText(Long.toString(timerVals[0]));
        minutesCount.setText(Long.toString(timerVals[1]));
        secondsCount.setText(Long.toString(timerVals[2]));
    }

    private void showHideTimer(){
        if(Build.VERSION.SDK_INT >= 11) {
            if (timerVisible) {
                timerVisible = false;
                timerStuff.setVisibility(View.GONE);
                timerArrow.setText(R.string.timer_arrow_right);
                timerArrow.setTextSize(26);
            } else {
                timerVisible = true;
                timerStuff.setVisibility(View.VISIBLE);
                timerArrow.setText(R.string.timer_arrow_down);
                timerArrow.setTextSize(32);
            }
        }else{
            Toast toast = Toast.makeText(this, R.string.old_api, Toast.LENGTH_LONG);
        }
    }
}
