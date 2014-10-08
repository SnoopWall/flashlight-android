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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;




public class Light extends Activity {
    public static final int KILL = 1;
    private RelativeLayout root;
    private  Drawable defaultBak;
    private boolean ledToggle = false;
    private TextView ledView;
    private boolean backlightToggle = false;
    private TextView backlightView;
    private TextView text1;
    private TextView text2;
    private LightApp app;
    public Handler finisher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        String fontPath = "icon.ttf";
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        setContentView(R.layout.activity_light);

        ledView = (TextView)findViewById(R.id.LED_ON_OFF);
        ledView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        root  = (RelativeLayout) findViewById(R.id.ROOT_LAYOUT);

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
        app = (LightApp)getApplication();
        Message m = Message.obtain();
        m.what = LightApp.VIEW_UPDATE;
        m.obj = this;
        app.mHandler.sendMessage(m);
        m = null;
        if(app.backLightOn) {
            backlightToggle = true;
            backSetter(backlightView, getResources().getDrawable(R.drawable.circle_on));
            turnOnBacklight();
        } else{
            backSetter(backlightView,getResources().getDrawable(R.drawable.circle_off));

            backlightToggle = false;
            turnOffBackLight();
        }
        if(app.camOn){
            app.turnOnCam();
            backSetter(ledView,getResources().getDrawable(R.drawable.circle_on));

            ledToggle = true;

        } else {
            ledToggle = false;
            backSetter(ledView,getResources().getDrawable(R.drawable.circle_off));

        }
    }




    private void toggleLED(){
        if(ledToggle) {
            ledToggle = false;
            backSetter(ledView,getResources().getDrawable(R.drawable.circle_off));

            app.turnOffCam();
        } else{
            backSetter(ledView,getResources().getDrawable(R.drawable.circle_on));

            ledToggle = true;
            app.turnOnCam();
        }
    }
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
            turnOffBackLight();
        } else{
            backSetter(backlightView,getResources().getDrawable(R.drawable.circle_on));

            backlightToggle = true;
            turnOnBacklight();
        }
    }

    protected void onStop() {
        super.onStop();
        app.mHandler.sendEmptyMessage(LightApp.MAYBE_DONE);


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
        app.backLightOn = false;
    }
}
