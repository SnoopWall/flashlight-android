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

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onDeleted(Context context, int[] appWidgetIds){
        super.onDeleted(context, appWidgetIds);

        Toast.makeText(context, "You have deleted your Privacy Flashlight Widget.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(context, ToggleLightService.class);
        intent.putExtra("DELETED", true);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        try {
            pendingIntent.send(context, 0, intent);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisabled(Context context){
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent){
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context,AppWidgetManager appWidgetManager, int[] appWidgetIds){
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        final int N = appWidgetIds.length;

        for (int i=0; i<N; i++){
            int appWidgetId = appWidgetIds[i];

            Intent intent = new Intent(context, ToggleLightService.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setOnClickPendingIntent(R.id.WIDGET_ON_OFF, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
