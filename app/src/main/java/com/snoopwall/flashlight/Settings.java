/*
The Likeness, Logos and text of this app are provided as reference and may not be reproduced in a derivative work
Copyright 2014 SnoopWall, Inc.

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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.TextView;

public class Settings extends Activity {

    private NumberPicker hours, minutes, seconds;
    private TextView hoursText, minutesText, secondsText, timerTitle;
    private CheckBox autoOnOffOption, autoLedOption, autoScreenOption;
    private TextView autoOffInfo, autoLedInfo, autoScreenInfo;
    private boolean autoOffSetting, autoLedSetting, autoScreenSetting;
    private boolean savePressed, backPressed = false;
    final protected String autoOffString = "AUTO_OFF";
    final protected String autoLedString = "AUTO_LED";
    final protected String autoScreenString = "AUTO_SCREEN";
    final protected String hoursString = "HOURS";
    final protected String minutesString = "MINUTES";
    final protected String secondsString = "SECONDS";
    private LightApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        app = (LightApp) getApplication();

        String clientAppName = getResources().getString(R.string.app_name);
        SharedPreferences preferences = getSharedPreferences(clientAppName, Context.MODE_PRIVATE);
        autoOffSetting = preferences.getBoolean(autoOffString, false);
        autoLedSetting = preferences.getBoolean(autoLedString, false);
        autoScreenSetting = preferences.getBoolean(autoScreenString, false);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.settings_layout);

        hours = (NumberPicker)findViewById(R.id.hours);
        minutes = (NumberPicker)findViewById(R.id.minutes);
        seconds = (NumberPicker)findViewById(R.id.seconds);
        hoursText = (TextView)findViewById(R.id.hours_text);
        minutesText = (TextView)findViewById(R.id.minutes_text);
        secondsText = (TextView)findViewById(R.id.seconds_text);
        timerTitle = (TextView)findViewById(R.id.timer_title);
        autoOnOffOption = (CheckBox)findViewById(R.id.autoCheckBox);
        autoLedOption = (CheckBox)findViewById(R.id.autoLEDCheckBox);
        autoScreenOption = (CheckBox)findViewById(R.id.autoScreenCheckBox);
        autoOffInfo = (TextView)findViewById(R.id.autoOffInfo);
        autoLedInfo = (TextView)findViewById(R.id.autoOnLEDInfo);
        autoScreenInfo = (TextView)findViewById(R.id.autoOnScreenInfo);
        Button saveButton = (Button) findViewById(R.id.saveBtn);

        if(Build.VERSION.SDK_INT >= 11) {
            int maxNumbers = 59;
            hours.setMaxValue(maxNumbers);
            minutes.setMaxValue(maxNumbers);
            seconds.setMaxValue(maxNumbers);
            hours.setMinValue(0);
            minutes.setMinValue(0);
            seconds.setMinValue(0);
            hours.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            minutes.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            seconds.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

            long[] timerVals = app.getTimerValues();
            hours.setValue((int) timerVals[0]);
            minutes.setValue((int) timerVals[1]);
            seconds.setValue((int) timerVals[2]);
        }else{
            hours.setVisibility(View.GONE);
            minutes.setVisibility(View.GONE);
            seconds.setVisibility(View.GONE);
            hoursText.setVisibility(View.GONE);
            minutesText.setVisibility(View.GONE);
            secondsText.setVisibility(View.GONE);
            timerTitle.setText(R.string.old_api);
            timerTitle.setTextSize(12);
        }

        if(autoOffSetting){
            autoOnOffOption.setText(R.string.on);
            autoOnOffOption.setChecked(true);
            autoOffInfo.setText(R.string.auto_off_active);
        }else{
            autoOnOffOption.setText(R.string.off);
            autoOnOffOption.setChecked(false);
            autoOffInfo.setText(R.string.auto_off_not_active);
        }

        if(autoLedSetting){
            autoLedOption.setText(R.string.on);
            autoLedOption.setChecked(true);
            autoLedInfo.setText(R.string.auto_on_led_active);
        }else{
            autoLedOption.setText(R.string.off);
            autoLedOption.setChecked(false);
            autoLedInfo.setText(R.string.auto_on_led_not_active);
        }

        if(autoScreenSetting){
            autoScreenOption.setText(R.string.on);
            autoScreenOption.setChecked(true);
            autoScreenInfo.setText(R.string.auto_on_screen_active);
        }else{
            autoScreenOption.setText(R.string.off);
            autoScreenOption.setChecked(false);
            autoScreenInfo.setText(R.string.auto_on_screen_not_active);
        }

        autoOnOffOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (autoOnOffOption.isChecked()) {
                    autoOnOffOption.setText(R.string.on);
                    autoOffInfo.setText(R.string.auto_off_active);
                    autoOffSetting = true;
                } else {
                    autoOnOffOption.setText(R.string.off);
                    autoOffInfo.setText(R.string.auto_off_not_active);
                    autoOffSetting = false;
                }
            }
        });

        autoLedOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (autoLedOption.isChecked()) {
                    autoLedOption.setText(R.string.on);
                    autoLedInfo.setText(R.string.auto_on_led_active);
                    autoLedSetting = true;
                } else {
                    autoLedOption.setText(R.string.off);
                    autoLedInfo.setText(R.string.auto_on_led_not_active);
                    autoLedSetting = false;
                }
            }
        });

        autoScreenOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (autoScreenOption.isChecked()) {
                    autoScreenOption.setText(R.string.on);
                    autoScreenInfo.setText(R.string.auto_on_screen_active);
                    autoScreenSetting = true;
                } else {
                    autoScreenOption.setText(R.string.off);
                    autoScreenInfo.setText(R.string.auto_on_screen_not_active);
                    autoScreenSetting = false;
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAutoOffState(autoOffSetting);
                setAutoLedState(autoLedSetting);
                setAutoScreenState(autoScreenSetting);
                if(Build.VERSION.SDK_INT >= 11) {
                    setTimerValues(hours.getValue(), minutes.getValue(), seconds.getValue());
                }
                savePressed = true;
                finish();
            }
        });

    }

    @Override
    protected void onStop(){
        super.onStop();
        setResult(RESULT_OK);
        if(!savePressed && !backPressed) {
            app.mHandler.sendEmptyMessage(LightApp.MAYBE_DONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            finish();
        }else{
            savePressed = false;
            backPressed = false;
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backPressed = true;
        return;
    }

    private void setAutoOffState(boolean b){
        String appName = getResources().getString(R.string.app_name);
        SharedPreferences prefs = this.getSharedPreferences(appName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(autoOffString, b);
        editor.commit();
    }

    private void setAutoLedState(boolean b){
        String appName = getResources().getString(R.string.app_name);
        SharedPreferences prefs = this.getSharedPreferences(appName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(autoLedString, b);
        editor.commit();
    }

    private void setAutoScreenState(boolean b){
        String appName = getResources().getString(R.string.app_name);
        SharedPreferences prefs = this.getSharedPreferences(appName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(autoScreenString, b);
        editor.commit();
    }

    private  void setTimerValues(long hours, long minutes, long seconds){
        String appName = getResources().getString(R.string.app_name);
        SharedPreferences prefs = this.getSharedPreferences(appName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if(hours == 0 && minutes == 0 && seconds == 0){
            editor.putLong(hoursString, hours);
            editor.putLong(minutesString, minutes);
            editor.putLong(secondsString, 5);
        }else {
            editor.putLong(hoursString, hours);
            editor.putLong(minutesString, minutes);
            editor.putLong(secondsString, seconds);
        }
        editor.commit();
    }

}
