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
import android.app.Application;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class LightApp extends Application {
    public static final int MAYBE_DONE = 1;
    public static final int VIEW_UPDATE = 2;
    private Timer finisher = null;
    private Light act = null;
    private Camera cam = null;
    public Boolean backLightOn = false;
    public Boolean camOn = false;

    public final Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch(msg.what){
                case MAYBE_DONE:
                    maybeDone();
                    break;
                case VIEW_UPDATE:
                    setView((Light) msg.obj);
                    break;
            }
        }

    };

    public void setView(Light x){
        if(finisher != null){
            finisher.cancel();
            finisher.purge();
            finisher = null;
        }
        act = x;
    }

    public void onCreate() {
        super.onCreate();
    }

    private void iAmDone(){
        if(finisher != null){
            finisher.cancel();
            finisher.purge();
            finisher = null;
        }
        turnOffCam();
        if(act != null){
            act.finisher.sendEmptyMessage(Light.KILL);

            act = null;
        }
    }

    public void maybeDone(){
        if(finisher != null){
            finisher.cancel();
            finisher.purge();
            finisher = null;
        }
        TimerTask x = new TimerTask() {
            @Override
            public void run() {
                iAmDone();
            }
        };
        finisher = new Timer();
        finisher.schedule(x,2000);
    }

    @SuppressLint("NewApi")
    public void turnOnCam(){
        if(camOn) {
            turnOffCam();
        }
        camOn = true;
        try {
            cam = Camera.open();
            // Nexus
            if (Build.VERSION.SDK_INT >= 11) {
                cam.setPreviewTexture(new SurfaceTexture(0));
            }
            // Default
            Camera.Parameters params = cam.getParameters();

            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            cam.setParameters(params);

            cam.startPreview();

        } catch (Exception e){
            turnOffCam();
            Log.w("info", "Cam Error " + e.toString());
        }
    }

    public void turnOffCam(){
        try{
            cam.stopPreview();
            cam.release();
        }catch(Exception e){

        }
        camOn = false;
        cam = null;
    }

    public boolean getLEDState(){
        if(camOn) {return true;}
        else      {return false;}
    }
}
