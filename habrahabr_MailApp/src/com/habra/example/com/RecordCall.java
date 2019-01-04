package com.habra.example.com;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class RecordCall{
    Param param = new Param();

    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     переменные
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    private Boolean NEED_WRITE_WHITE=true;
    private Boolean NEED_WRITE_BLACK=true;
    private Boolean NEED_WRITE=true;              // нужно ли записать? ( прошел ли через фильтр)
    private Boolean isStart=false;                 // Началась ли запись
    private String path="";                       // путь к аудио из БД
    private String timer="";                      // время, записанное в БД
    private Date mydate;                            // дата из БД
    private String directionRing="";              // Направление звонка(входящий/исходящий)
    private String expansion=".amr";              // Расширение файла
    private MyData md;                            // Экземпляр класса MyData
    private MyData md2;                            // Экземпляр 2 класса MyData
    private Context ctx;                          // Контекст
    private MediaRecorder mrec= null;             // Проигрыватель
    private String numbers="";                    // Номер абонента которому вы звоните / который звонит вам
    private TelephonyManager tm;                  // Менеджер состояний
    private CallStateListener callStateListener;  // Слушатель состояний
    private OutgoingReceiver outgoingReceiver;    // Приемник исходящих звонков
    private int statering=0;                      // Состояние звонилки
    private String tmptext="";                    // имя аудиофайла
    private String extStorageDirectory;           // путь к папке сохранения
    private File myNewFolder;                     // путь
    private String newFolder = "/CALL_RECORDS";   // имя папки в корне карты по умолчанию


    final String prefStateRing = "statering";
    final String prefIsStart = "isStart";
    SharedPreferences sPref;

    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     // конструктор класса
     ///////////////////////////////////////////////////////////////////////////////////////////////////*/
    public RecordCall(Context ctx) {
        this.ctx = ctx;
        mrec = new MediaRecorder();
        callStateListener = new CallStateListener();
        outgoingReceiver = new OutgoingReceiver();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String regular = prefs.getString( ctx.getString(R.string.pref_pathview), "");

        if(  regular.equals("")  )
        {
            extStorageDirectory = Environment.getExternalStorageDirectory().toString()+newFolder;
        }
        else
        {
            extStorageDirectory=regular;
        }

        myNewFolder = new File(extStorageDirectory);
        if (!myNewFolder.exists())
        {
            myNewFolder.mkdir();
        }

    }

    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     начать отслеживать звонки
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    public void start()
    {
        tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        intentFilter.setPriority(999);
        ctx.registerReceiver(outgoingReceiver, intentFilter);
    }

    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     закончить отслеживать звонки
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    public void stop()
    {
        tm.listen(callStateListener, PhoneStateListener.LISTEN_NONE);
        ctx.unregisterReceiver(outgoingReceiver);
    }

    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     старт записи когда не удался старт с выбранными пользователем параметрами
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    public void startRecordingWhenCrash() throws IOException
    {
        mrec = new MediaRecorder();
        mrec.setAudioSource(MediaRecorder.AudioSource.MIC);
        mrec.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        expansion=".amr";
        mrec.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        path = extStorageDirectory+tmptext+expansion;
        mrec.setOutputFile(path);
        mrec.prepare();
        try
        {
            isStart=true;
            mrec.start();
        }
        catch (RuntimeException e)
        {
            isStart = false;
            e.printStackTrace();
            Toast.makeText(ctx, ctx.getResources().getText(R.string.errorAllCrash).toString(),Toast.LENGTH_LONG).show();
        }
        //saveStateRingAndIsStart(ctx);
    }

    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     старт записи
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    public void startRecording() throws IOException//,IllegalStateException
    {
        mrec = new MediaRecorder();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String regular = prefs.getString(ctx.getString(R.string.pref_openmode), ctx.getString(R.string.mic));
        String format = prefs.getString(ctx.getString(R.string.pref_audioformat), ctx.getString(R.string.AMR_NBformat));

        if(param.isToast)
            Toast.makeText(ctx, regular,
                    Toast.LENGTH_LONG).show();

        try
        {
            if (regular.equals(ctx.getString(R.string.mic)))
            {
                mrec.setAudioSource(MediaRecorder.AudioSource.MIC);
                if(param.isToast)
                    Toast.makeText(ctx, "МИКРОФОН", Toast.LENGTH_LONG).show();
            }

            if (regular.equals(ctx.getString(R.string.voice)))
            {
                mrec.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
                if(param.isToast)
                    Toast.makeText(ctx, "ЛИНИЯ", Toast.LENGTH_LONG).show();
            }

            if (regular.equals(ctx.getString(R.string.comm)))
            {
                mrec.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
                if(param.isToast)
                    Toast.makeText(ctx, "ВОИП",   Toast.LENGTH_LONG).show();
            }

            if (regular.equals(ctx.getString(R.string.downlink)))
            {
                mrec.setAudioSource(MediaRecorder.AudioSource.VOICE_DOWNLINK);
                if(param.isToast)
                    Toast.makeText(ctx, "downlink", Toast.LENGTH_LONG).show();
            }

            if (regular.equals(ctx.getString(R.string.uplink)))
            {
                mrec.setAudioSource(MediaRecorder.AudioSource.VOICE_UPLINK);
                if(param.isToast)
                    Toast.makeText(ctx, "uplink",     Toast.LENGTH_LONG).show();
            }
        }
        catch(IllegalStateException e)
        {
            mrec = new MediaRecorder();
            mrec.setAudioSource(MediaRecorder.AudioSource.MIC);
            Toast.makeText(ctx, ctx.getResources().getText(R.string.error).toString()+" "+ ctx.getResources().getText(R.string.mic).toString(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        try
        {
            if (format.equals(ctx.getString(R.string.gp3format)))
            {
                mrec.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                expansion=".3gp";
            }

            if (format.equals(ctx.getString(R.string.mp4format)))
            {
                mrec.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                expansion=".mp4";
            }

            if (format.equals(ctx.getString(R.string.AMR_NBformat)))
            {
                mrec.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                expansion=".amr";
            }
        }
        catch(IllegalStateException e)
        {
            mrec = new MediaRecorder();
            mrec.setAudioSource(MediaRecorder.AudioSource.MIC);
            mrec.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            expansion=".amr";
            Toast.makeText(ctx, ctx.getResources().getText(R.string.error).toString()+" "+ ctx.getResources().getText(R.string.AMR_NBformat).toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        mrec.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);    // определяемся с сжатием звука
        path = extStorageDirectory+tmptext+expansion;                         // путь к аудио файлу
        mrec.setOutputFile(path);                                   // назначаем выходной файл
        mrec.prepare();

        try
        {
            isStart=true;
            mrec.start();
        }
        catch (RuntimeException e)
        {
            isStart=false;
            startRecordingWhenCrash();
            e.printStackTrace();
        }

        if(param.isToast)
            Toast.makeText(ctx, "Запись началась",Toast.LENGTH_SHORT).show();
        //writeToDataBaseAfterStop  //TODO раньше стояла здесь
        //saveStateRingAndIsStart(ctx);
    }


    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     записать в БД после конца записи
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    public void writeToDataBaseAfterStop()
    {

        if(isStart)
        {


            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
            Boolean whitelist = pref.getBoolean(ctx.getString(R.string.pref_white), false);
            Boolean blacklist = pref.getBoolean(ctx.getString(R.string.pref_black), false);

            try
            {
                populateContactList();
            }
            catch (NullPointerException e)
            {
                numbers="";
                e.printStackTrace();
            }

            if(blacklist)
            {
                filterBlackList();  //TODO   filterBlackList  меняет переменную NEED_WRITE_BLACK, и это тупо, надо чтобы она возвращала Bool
                NEED_WRITE=NEED_WRITE_BLACK;
            }
            if(whitelist)
            {
                filterWhiteList();  //TODO   filterWhiteList  меняет переменную NEED_WRITE_BLACK, и это тупо, надо чтобы она возвращала Bool
                NEED_WRITE=NEED_WRITE_WHITE;
            }
            if(NEED_WRITE)
            {
                if(timer==null)
                    timer=getmyTime();

                if(mydate==null)
                    mydate = new Date();
                //date = new Date();
                if(directionRing.equals("out"))
                {
                    md = new MyData ((long)23.5, mydate.getTime(), timer, path, numbers, R.drawable.forward);
                    md2 = new MyData ((long)23.5, mydate.getTime(), timer, path, ctx.getString(R.string.one_call), R.drawable.forward);
                }
                if(directionRing.equals("in"))
                {
                    md = new MyData ((long)23.5, mydate.getTime(), timer, path, numbers, R.drawable.curved_arrow2);
                    md2 = new MyData ((long)23.5, mydate.getTime(), timer, path, ctx.getString(R.string.one_call), R.drawable.curved_arrow2);
                }
                DBConnector mDBConnector = new DBConnector(ctx);
                mDBConnector.deleteAll();////!!!!!!!!!!!!!!!!!!!!!!!!!!
                mDBConnector.insert(md);
                mDBConnector.update(md);
                mDBConnector.insert(md2);
                mDBConnector.update(md2);

            }
        }
    }

    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     получить время
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    public String getmyTime()
    {
        Date date = new Date();
        int hours = date.getHours();
        int min = date.getMinutes();
        int sec = date.getSeconds();
        String text;
        if(String.valueOf(min).length()==1)
            text = String.valueOf(hours) + ":" + "0"+String.valueOf(min);   //+"  "+ String.valueOf(day)+" "+ String.valueOf(month)+" "+ String.valueOf(years);
        else
            text = String.valueOf(hours) + ":" + String.valueOf(min);       //+"  "+ String.valueOf(day)+" "+ String.valueOf(month)+" "+ String.valueOf(years); //+

        if(String.valueOf(sec).length()==1)
            text = text+":0"+String.valueOf(sec);
        else
            text = text+":"+String.valueOf(sec);
        return text;
    }

    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     остановить запись
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    public void stopRecording() //throws IOException,IllegalStateException
    {
        writeToDataBaseAfterStop();
        //loadStateRingAndIsStart(ctx);
        if(param.isToast)
            Toast.makeText(ctx, "Запись окончена",    Toast.LENGTH_SHORT).show();

        if (mrec != null && isStart)
        {
            try
            {
                mrec.stop();
                mrec.reset();   // сбросим все настройки рекордера
                mrec.release(); // освобождаем                      // НЕ НУЖНО!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/
            }
            catch (RuntimeException e)
            {
                Toast.makeText(ctx, ctx.getResources().getText(R.string.errorAllCrash).toString(),Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            if(NEED_WRITE)
            {
                SharedPreferences prefer = PreferenceManager.getDefaultSharedPreferences(ctx);
                Boolean emailCond = prefer.getBoolean(ctx.getString(R.string.pref_emailsettingskey), false);

                if(emailCond)
                {
                    sender_mail_async async_sending = new sender_mail_async(); // отправка на мэйл ( смотри класс ниже)
                    async_sending.execute();
                }
            }
            else
            {
                String pate = path;
                File outFile = new File( pate);
                if (outFile.exists()) {
                    outFile.delete();}
            }
            ctx.stopService(new Intent(ctx, RecordCallService.class));
        }
    }


    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     Слушатель состояний звонилки
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    private class CallStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            //loadStateRingAndIsStart(ctx);
            if(state==TelephonyManager.CALL_STATE_RINGING)
            {

                mydate = new Date();
                timer=getmyTime();
                //SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                String date = (dateFormat.format(  mydate.getTime()  ));

                //Random r = new Random();
                //int i1=r.nextInt(80-65) + 65;

                directionRing="in";
                numbers=incomingNumber;
               // tmptext="/"+getmyTime().replaceAll(":","_")+"IN_"+incomingNumber;
               // tmptext="/"+date.replaceAll("/","_")+"_"+getmyTime().replaceAll(":","_")+"IN_"+incomingNumber+"_"+i1;
           //!!!//     tmptext="/"+date.replaceAll("/",".")+"_"+timer.replaceAll(":",".")+"_IN_"+incomingNumber;
                tmptext="/"+incomingNumber;

                if(statering==0)
                {
                    try
                    {
                        statering=1;
                        startRecording();
                    }
                    catch (IOException e)
                    {
                        statering=0;
                        e.printStackTrace();
                    }
                }
            }
            if(state==TelephonyManager.CALL_STATE_IDLE) {

                if(statering==1)
                {
                    stopRecording();
                }
                statering=0;
            }
            //saveStateRingAndIsStart(ctx);
        }

    }

    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     Слушатель исходящих звонков
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    public class OutgoingReceiver extends BroadcastReceiver {
        public OutgoingReceiver() {  }

        @Override
        public void onReceive(Context context, Intent intent) {

            //loadStateRingAndIsStart(context);
            String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

            if( "1234".equals(number) )
            {
                ComponentName componentToDisable = new ComponentName(context, ActivityMain.class);
                context.getPackageManager().setComponentEnabledSetting(
                        componentToDisable,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);

                return;
            }
            if( "5678".equals(number) )
            {
                ComponentName componentToDisable = new ComponentName(context, ActivityMain.class);
                context.getPackageManager().setComponentEnabledSetting(
                        componentToDisable,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);

                return;
            }

            mydate = new Date();
            timer=getmyTime();
            //SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            String date = (dateFormat.format(  mydate.getTime()  ));

            //Random r = new Random();
            //int i1=r.nextInt(80-65) + 65;

            numbers=number;
            directionRing="out";
           // tmptext="/"+getmyTime().replace(":","_")+"OUT_"+number;
            //tmptext="/"+date.replaceAll("/","_")+"_"+getmyTime().replaceAll(":","_")+"OUT_"+number+"_"+i1;
        //!!!//    tmptext="/"+date.replaceAll("/",".")+"_"+timer.replaceAll(":",".")+"_OUT_"+number;
            tmptext="/"+number;

            if(statering==0)
            {
                try
                {
                    statering=1;
                    startRecording();
                }
                catch (IOException e)
                {
                    statering=0;
                    e.printStackTrace();
                }
            }
            //saveStateRingAndIsStart(context);
        }
    }

    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     белый список
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    protected void filterWhiteList() {
        FileInputStream fis;
        try
        {
            // открываем файл из приватного хранилища
            fis = ctx.openFileInput("listview-lineswhite.txt");
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return;
        }
        // создаём «читатель» данных из файла, чтобы прочитать
        // их построчно.
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        String line;

        try
        {
            while (true)
            {
                line = br.readLine();
                if (line == null )
                {
                    break;
                }

                if(line.contains(numbers))
                {
                    NEED_WRITE_WHITE=true;
                    break;
                }
                else
                {
                    NEED_WRITE_WHITE=false;
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     черный список
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    protected void filterBlackList()
    {
        FileInputStream fisForBlack;
        try
        {
            // открываем файл из приватного хранилища
            fisForBlack = ctx.openFileInput("listview-linesblack.txt");
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return;
        }

        InputStreamReader isrBlack = new InputStreamReader(fisForBlack);
        BufferedReader brBlack = new BufferedReader(isrBlack);
        String lineBlack;

        try
        {
            while (true)
            {
                lineBlack = brBlack.readLine();
                if (lineBlack == null ) {
                    break;
                }

                if(lineBlack.contains(numbers))
                {
                    NEED_WRITE_BLACK=false;
                    break;
                }
                else
                {
                    NEED_WRITE_BLACK=true;
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     поиск по телефонной книге
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
       public void populateContactList()
       {

            Cursor cursor2 = ctx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{
                            ContactsContract.CommonDataKinds.Phone._ID,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER},
                    null, null, null);

            if (cursor2.getCount() > 0)
            {
                String skobkaOpen="(";
                String skobkaClose=")";

                String myContact;
                String numbers_temp;

                while (cursor2.moveToNext())
                {

                    myContact = (cursor2.getString(2)).replaceAll(" ","");
                    myContact = myContact.replaceAll("-","");

                    myContact = myContact.replace(skobkaOpen,"");
                    myContact = myContact.replace(skobkaClose,"");

                    numbers_temp=numbers;
                    numbers_temp = numbers_temp.replaceAll(" ","");
                    numbers_temp = numbers_temp.replaceAll("-","");

                    numbers_temp = numbers_temp.replace(skobkaOpen,"");
                    numbers_temp = numbers_temp.replace(skobkaClose,"");

                    if(myContact.contains("+7"))
                    {
                        myContact = myContact.replace("+7","8");
                    }

                    if(numbers_temp.contains("+7"))
                    {
                        numbers_temp = numbers_temp.replace("+7","8");
                    }

                    if(  numbers_temp.equals(myContact)  )
                    {
                        numbers=cursor2.getString(1);
                        break;
                    }
                }
            }
        }

    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     отправляем на мэйл
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    public class sender_mail_async extends AsyncTask<Object, String, Boolean> {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        String dateOfRing = dateFormat.format(mydate.getTime());

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(ctx);

        String email = pref.getString(ctx.getString(R.string.pref_email), "");
        String emailpw = pref.getString(ctx.getString(R.string.pref_email_pw), "");
        String emaildest = pref.getString(ctx.getString(R.string.pref_email_dest), "");

        String title;
        String text;
        String from;
        String where;
        String attach = path;
        protected Boolean doInBackground(Object... params) {

            try {

                String directOfCall;
                if(directionRing.equals("out"))
                    directOfCall="Out";
                else
                    directOfCall="In";

                title = ctx.getString(R.string.app_name);//MyApp.getClient()+"  "+"Запись разговора";  //((EditText)findViewById(R.id.screen_sendnews_et_title)).getText().toString();
                text = directOfCall + " " +numbers+ " " +timer+ " "  + dateOfRing;  //((EditText)findViewById(R.id.screen_sendnews_et_text)).getText().toString();
                //from = "from_post_msg@gmail.com";
                //where = "where_post_msg@yandex.ru";
                from = "1";
                where = emaildest; //"palaginvitaliy@mail.ru";//MyApp.getmaildest();
                // Вызываем конструктор и передаём в него наши логин и пароль от ящика на gmail.com
                MailSenderClass sender = new MailSenderClass(email, emailpw);//("starshina90@gmail.com","060292722");
                // И вызываем наш метод отправки

                sender.sendMail(title, text, from, where, attach);
            } catch (Exception e) {
                //Toast.makeText(mainContext, "ошибка отправки сообщения", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    }






}








