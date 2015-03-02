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
import android.os.Bundle;

public class ToggleLightService extends Activity{

    final protected String delete_flag = "DELETED";
    private boolean ledToggle = false;
    private boolean delete = false;
    private LightApp app;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (LightApp)getApplication();
        ledToggle = app.isLEDOn();
        delete = getIntent().getBooleanExtra(delete_flag, false);
        toggleLED();
        finish();
    }

    private void toggleLED(){
        if(ledToggle || delete) {
            app.turnOffCam();
        } else{
            app.turnOnCam();
        }
    }
}
