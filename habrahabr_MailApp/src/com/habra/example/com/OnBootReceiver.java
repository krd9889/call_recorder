package com.habra.example.com;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Приемник сообщения о загрузке(перезагрузке) OS android
 */
public class OnBootReceiver extends BroadcastReceiver {
    Param param = new Param();

    SharedPreferences sPref;

    Boolean recording;

    @Override
    public void onReceive(Context context, Intent intent) {
        /*Intent activityIntent = new Intent(context,ActivityMain.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(activityIntent);*/
        sPref =  context.getSharedPreferences("ActivityMain",context.MODE_PRIVATE);
        recording = sPref.getBoolean("IsRecording",false);

        if(recording)
        {
            Intent service = new Intent(context, RecordCallService.class); // создаем намерение(intent) на запуск сервиса "CallDetectService"
            context.startService(service); // запускаем сервис
        }
        if(param.isToast)
        Toast.makeText(context,         // Всплывающее уведомление, для теста; будет удалено!!!
                "Сработал автозапуск",
                Toast.LENGTH_LONG).show();


    }


}


