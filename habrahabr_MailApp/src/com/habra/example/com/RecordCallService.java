package com.habra.example.com;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;



import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;
import android.widget.ToggleButton;

public class RecordCallService extends Service {
    Param param = new Param();

    private RecordCall record;

    public RecordCallService() {
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        record=new RecordCall(this);

        int res = super.onStartCommand(intent, flags, startId);

        record.start();

        if(param.isToast)
        Toast.makeText(this,
                "Запущен сервис",
                Toast.LENGTH_SHORT).show();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean notific = prefs.getBoolean( getString(R.string.pref_stroke_cond), true);

        if(notific)
        {
            int NOTIFICATION_ID = 1;
            Intent inte = new Intent(this, RecordCallService.class);
            PendingIntent pi = PendingIntent.getActivity(this, 1, inte, 0);
            Notification notification = new Notification(R.drawable.phone_blue_mini, getString(R.string.app_name), System.currentTimeMillis());
            //notification.setLatestEventInfo(this, "Title", "Text", pi);
            //notification.setLatestEventInfo(this, getString(R.string.app_name), "", pi);
            notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
            startForeground(NOTIFICATION_ID, notification);
        }
        else
        {
            int NOTIFICATION_ID = 1;
            Intent inte = new Intent(this, RecordCallService.class);
            PendingIntent pi = PendingIntent.getActivity(this, 1, inte, 0);
            Notification notification = new Notification(0, getString(R.string.app_name), System.currentTimeMillis());
            //notification.setLatestEventInfo(this, "Title", "Text", pi);
            //notification.setLatestEventInfo(this, getString(R.string.app_name), "", pi);
            notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
            startForeground(NOTIFICATION_ID, notification);
        }

        return res;
        //return START_STICKY;
        //return START_REDELIVER_INTENT;
    }


    public void onDestroy() {
        //try{
                super.onDestroy();

        if(record != null) {
            record.stop();
        }
                //record.stop();
                //record.stopRecording();

                if(param.isToast)
                Toast.makeText(this,
                        "Остановлен сервис",
                        Toast.LENGTH_SHORT).show();

        SharedPreferences sPref;
        boolean recording;
        sPref =  getSharedPreferences("ActivityMain",MODE_PRIVATE);
        recording = sPref.getBoolean("IsRecording",false);


        if(recording)
        {
            startService(new Intent(getApplicationContext(), RecordCallService.class));
            if(param.isToast)
            Toast.makeText(this,
                    "Сервис запущен из дестроя",
                    Toast.LENGTH_SHORT).show();
        }

       /*SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean notific = prefs.getBoolean( getString(R.string.pref_stroke_cond), true);

        if(notific)
        {
            stopForeground(true);
        }*/
        /*}
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }*/
    }

    public IBinder onBind(Intent intent) {
        // not supporting binding
        return null;
    }
}
