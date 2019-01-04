package com.habra.example.com;

/**
 * Created by Виталий btn_toggle_on 05.09.13.
 */

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class HelloWidget extends AppWidgetProvider {

    SharedPreferences sPref;

    Boolean recording;
    public static String ACTION_WIDGET_RECEIVER = "ActionReceiverWidget";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Создаем новый RemoteViews
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.wig);

        sPref =  context.getSharedPreferences("ActivityMain",context.MODE_PRIVATE);
        recording = sPref.getBoolean("IsRecording",false);

        //Подготавливаем Intent для Broadcast
        Intent active = new Intent(context, HelloWidget.class);
        active.setAction(ACTION_WIDGET_RECEIVER);
        active.putExtra("msg", "Hello Habrahabr");

        //создаем наше событие
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);

        //регистрируем наше событие
        remoteViews.setOnClickPendingIntent(R.id.widget_button, actionPendingIntent);

        //RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.wig);

       // remoteViews.setTextViewText(R.id.widget_textview, "On");

        //remoteViews = buildUpdate(context);
        //RemoteViews updateViews = buildUpdate(context);
        //AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, HelloWidget.class), remoteViews);

        //обновляем виджет
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        //Ловим наш Broadcast, проверяем и выводим сообщение
        final String action = intent.getAction();
        if (ACTION_WIDGET_RECEIVER.equals(action)) {
            String msg = "null";
            try {
                msg = intent.getStringExtra("msg");
            } catch (NullPointerException e) {
                Log.e("Error", "msg = null");
            }
           // Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();


            //
            sPref =  context.getSharedPreferences("ActivityMain",context.MODE_PRIVATE);
            recording = sPref.getBoolean("IsRecording",false);

            if(recording==true)
            {
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.wig);
                views.setTextViewText(R.id.widget_textview, "OFF");
                AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, HelloWidget.class), views);

                context.stopService(new Intent(context.getApplicationContext(), RecordCallService.class));
                recording=false;

                sPref = context.getSharedPreferences("ActivityMain", context.MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putBoolean("IsRecording", recording);
                ed.commit();

            }
            else
            {
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.wig);
                views.setTextViewText(R.id.widget_textview, "ON");
                AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, HelloWidget.class), views);

                context.startService(new Intent(context.getApplicationContext(), RecordCallService.class));
                recording=true;

                sPref = context.getSharedPreferences("ActivityMain",context.MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putBoolean("IsRecording", recording);
                ed.commit();

            }


        }
        super.onReceive(context, intent);
    }

}

















