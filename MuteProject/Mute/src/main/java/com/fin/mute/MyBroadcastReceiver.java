package com.fin.mute;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;


public class MyBroadcastReceiver extends BroadcastReceiver {

    AudioManager audio;
    SharedPreferences preferences;
    Vibrator v;
    NotificationManager mNotificationManager;
    int NOTIFICATION_ID;
    long when;
    int src;

    @Override
    public void onReceive(Context context, Intent intent) {



        audio=(AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NOTIFICATION_ID = 1;
        when = System.currentTimeMillis();

        //+1 e -1 ai primi due e di conseguenza al terzo
        if (preferences.getInt("UPDATE",2)==2){
            preferences.edit().clear().commit();
            preferences.edit().putInt("UPDATE",3).commit();
        }

        if(preferences.getInt("i",0)==0){
            Log.d("MyActivity", "mute to unmute");
            getParam();
            getMode();
            setMute(context);
            preferences.edit().putInt("i",1).commit();
            src = R.drawable.icon_off;
        }else{
            Log.d("MyActivity", "unmute to mute");
            setUnmute(context);
            preferences.edit().putInt("i",0).commit();
            src = R.drawable.icon_on;
        }

        RemoteViews remoteViews = new RemoteViews( context.getPackageName(), R.layout.provider);
        Log.d("MyActivity", "on create broadcast receiver");

        ComponentName myWidget = new ComponentName( context, provider.class );
        //Intent intent = new Intent(context, muteA.class);
        Intent intent1 = new Intent();
        intent1.setAction(provider.action);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent1, 0);
        remoteViews.setOnClickPendingIntent(R.id.layoutWidget, pendingIntent);
        remoteViews.setImageViewResource(R.id.icon_off, src);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(myWidget, remoteViews);
    }

    public void getMode(){
        if (audio.getRingerMode() == AudioManager.RINGER_MODE_SILENT){
            preferences.edit().putBoolean("SILENT", true).commit();
            Log.d("MyActivity", "prendo il metodo SILENT");
        }else if (audio.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE){
            preferences.edit().putBoolean("VIBRATE", true).commit();
            Log.d("MyActivity","prendo il metodo VIBRATE");
        }else if (audio.getRingerMode() == AudioManager.RINGER_MODE_NORMAL){
            preferences.edit().putBoolean("NORMAL", true).commit();
            Log.d("MyActivity","prendo il metodo NORMAL");
        }
    }

    public boolean getParam(){
        preferences.edit().putInt("RING",audio.getStreamVolume(audio.STREAM_RING)).commit();
        preferences.edit().putInt("DTMF",audio.getStreamVolume(audio.STREAM_DTMF)).commit();
        preferences.edit().putInt("MUSIC",audio.getStreamVolume(audio.STREAM_MUSIC)).commit();
        preferences.edit().putInt("ALARM",audio.getStreamVolume(audio.STREAM_ALARM)).commit();
        preferences.edit().putInt("SYSTEM",audio.getStreamVolume(audio.STREAM_SYSTEM)).commit();
        preferences.edit().putInt("NOTIFICATION",audio.getStreamVolume(audio.STREAM_NOTIFICATION)).commit();
        preferences.edit().putInt("VOICE",audio.getStreamVolume(audio.STREAM_VOICE_CALL)).commit();

        return true;
    }

    public boolean getParamCopy(){
        preferences.edit().putInt("RINGc",audio.getStreamVolume(audio.STREAM_RING)).commit();
        preferences.edit().putInt("DTMFc",audio.getStreamVolume(audio.STREAM_DTMF)).commit();
        preferences.edit().putInt("MUSICc",audio.getStreamVolume(audio.STREAM_MUSIC)).commit();
        preferences.edit().putInt("ALARMc",audio.getStreamVolume(audio.STREAM_ALARM)).commit();
        preferences.edit().putInt("SYSTEMc",audio.getStreamVolume(audio.STREAM_SYSTEM)).commit();
        preferences.edit().putInt("NOTIFICATIONc",audio.getStreamVolume(audio.STREAM_NOTIFICATION)).commit();
        preferences.edit().putInt("VOICEc",audio.getStreamVolume(audio.STREAM_VOICE_CALL)).commit();
        return true;
    }

    public void setMute(Context context){

        preferences.edit().putInt("mode1",audio.getRingerMode()).commit();
        Log.d("MyActivity", "setto mode1 = "+ preferences.getInt("mode1",5));


        audio.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        audio.setStreamVolume(AudioManager.STREAM_DTMF, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        audio.setStreamVolume(AudioManager.STREAM_ALARM, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        audio.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        audio.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);

        v.cancel();

        //set notification
        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.muteactivity);
        contentView.setImageViewResource(R.id.layoutWidget, R.drawable.icon_off);
        contentView.setTextViewText(R.id.layoutWidget, "Mute");
        Notification notifyObj=new Notification(R.drawable.icon_off72,"Mute",System.currentTimeMillis());

        notifyObj.setLatestEventInfo(context, "Mute", "Tap The Widget To Unmute", null);

        notifyObj.flags|=Notification.FLAG_AUTO_CANCEL;
        notifyObj.flags |= Notification.FLAG_NO_CLEAR;
        mNotificationManager.notify(NOTIFICATION_ID, notifyObj);

    }

    public void setUnmute(Context context){

        getParamCopy();

        audio.setStreamVolume(AudioManager.STREAM_RING, preferences.getInt("RING", 0), 0);
        audio.setStreamVolume(AudioManager.STREAM_DTMF, preferences.getInt("DTMF", 0) ,0);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, preferences.getInt("MUSIC", 0) ,0);
        audio.setStreamVolume(AudioManager.STREAM_ALARM, preferences.getInt("ALARM", 0), 0);
        audio.setStreamVolume(AudioManager.STREAM_SYSTEM, preferences.getInt("SYSTEM", 0),0);
        audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION, preferences.getInt("NOTIFICATION", 0),0);
        audio.setStreamVolume(AudioManager.STREAM_VOICE_CALL, preferences.getInt("VOICE", 0),0);

        preferences.edit().putInt("mode2",audio.getRingerMode()).commit();

        int a = preferences.getInt("mode1",0);
        int b = preferences.getInt("mode2",0);

        if(a!=b){
            audio.setStreamVolume(AudioManager.STREAM_RING, preferences.getInt("RINGc", 0), 0);
            audio.setStreamVolume(AudioManager.STREAM_DTMF, preferences.getInt("DTMFc", 0) ,0);
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, preferences.getInt("MUSICc", 0) ,0);
            audio.setStreamVolume(AudioManager.STREAM_ALARM, preferences.getInt("ALARMc", 0), 0);
            audio.setStreamVolume(AudioManager.STREAM_SYSTEM, preferences.getInt("SYSTEMc", 0),0);
            audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION, preferences.getInt("NOTIFICATIONc", 0),0);
            audio.setStreamVolume(AudioManager.STREAM_VOICE_CALL, preferences.getInt("VOICEc", 0),0);

            preferences.edit().putInt("mode1",0).commit();
            preferences.edit().putInt("mode2",0).commit();
        }

        //getMode();

        if(preferences.getBoolean("SILENT",false)){
            audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            preferences.edit().putBoolean("SILENT",false).commit();
            //  Log.d("MyActivity","restituisco parametri di SILENT");
        }else if(preferences.getBoolean("VIBRATE",false)){
            audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            preferences.edit().putBoolean("VIBRATE",false).commit();
            //  Log.d("MyActivity","restituisco parametri di VIBRATE");
        }else if(preferences.getBoolean("NORMAL",false)){
            audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            preferences.edit().putBoolean("NORMAL",false).commit();
            //  Log.d("MyActivity","restituisco parametri di NORMAL");
        }


        Notification notification = new Notification(R.drawable.icon_on72, context.getString(R.string.Unmute), when);
        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.muteactivity);
        contentView.setImageViewResource(R.id.layoutWidget, R.drawable.icon_on);
        contentView.setTextViewText(R.id.icon_off, "Mute");
        notification.contentView = contentView;
        mNotificationManager.notify(NOTIFICATION_ID, notification);
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

}
