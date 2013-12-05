package com.fin.mute;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.RemoteViews;

public class provider extends AppWidgetProvider {

    public static final String action = "com.fin.mute.CHANGE";
    @Override
    public void onUpdate( Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds )
    {
        RemoteViews remoteViews = new RemoteViews( context.getPackageName(), R.layout.provider);
        Log.d("MyActivity", "on create provider");

        ComponentName myWidget = new ComponentName( context, provider.class );
        //Intent intent = new Intent(context, muteA.class);
        Intent intent = new Intent();
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.layoutWidget, pendingIntent);
        remoteViews.setImageViewResource(R.id.icon_off,R.drawable.icon_on);
        appWidgetManager.updateAppWidget(myWidget, remoteViews);
    }

}
