package com.habra.example.com;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;

import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.mobclix.android.sdk.Mobclix;
import com.mobclix.android.sdk.MobclixAdView;
import com.mobclix.android.sdk.MobclixMMABannerXLAdView;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


/**
 * Created by Виталий btn_toggle_on 08.09.13.
 */
public class ActivityMain extends Activity {

    private TextView curTimeTV;
    private Button buttonPlayStop;
    private MediaPlayer mediaPlayer=null;
    private SeekBar seekBar;
    final String IsRecording = "IsRecording";
    SharedPreferences sPref;
    boolean recording;
    private final Handler handler = new Handler();
    private final Context context = this;
    DBConnector mDBConnector;
    Context mContext;
    ListView mListView;
    myListAdapter mAdapter;
    private String newFolder = "/CALL_RECORDS";
    private String extStorageDirectory;
    private File myNewFolder;
    EditText et;
    Button btRate;
    Button btPRO;
    Button btnMobileSecurity;

    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     сохранение настроек записи
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    void saveIsRecording()
    {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean(IsRecording, recording);
        ed.commit();
        //Toast.makeText(this, "Text saved", Toast.LENGTH_SHORT).show();
    }

    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     загрузка настроек
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    void loadIsRecording()
    {
        sPref = getPreferences(MODE_PRIVATE);
        recording = sPref.getBoolean(IsRecording,false);
    }

    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     регистрация кнопки для оценки и задание ее видимости
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    public void rateApp()
    {
            ((Button)findViewById(R.id.btnOption)).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (v.getId() == R.id.btnOption)
                    {
                        openOptionsMenu();
                    }
                }
            });


        btRate  = (Button)findViewById(R.id.btRate);



        ((Button)findViewById(R.id.btRate)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (v.getId() == R.id.btRate) {


                    Intent i = new Intent(Intent.ACTION_VIEW);

                    i.setData(Uri.parse("market://details?id=com.habra.example.com"));////https://play.google.com/store/apps/details?id=com.habra.example.com

                    mContext.startActivity(i);

                }

            }
        });

        btPRO  = (Button)findViewById(R.id.btnPRO);

        ((Button)findViewById(R.id.btnPRO)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (v.getId() == R.id.btnPRO) {


                    Intent i = new Intent(Intent.ACTION_VIEW);

                    i.setData(Uri.parse("market://details?id=com.habra.example.call_recorder"));////https://play.google.com/store/apps/details?id=com.habra.example.com

                    mContext.startActivity(i);

                }

            }
        });

        btnMobileSecurity = (Button)findViewById(R.id.btnMobileSecurity);
        //com.habra.example.comMobileSecurity

        ((Button)findViewById(R.id.btnMobileSecurity)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (v.getId() == R.id.btnMobileSecurity) {


                    Intent i = new Intent(Intent.ACTION_VIEW);

                    i.setData(Uri.parse("market://details?id=com.habra.example.comFreeFreeRecorder"));////https://play.google.com/store/apps/details?id=com.habra.example.com

                    mContext.startActivity(i);

                }

            }
        });

        SharedPreferences prefer = PreferenceManager
                .getDefaultSharedPreferences(mContext);

        Boolean isRating = prefer.getBoolean(mContext.getString(R.string.pref_rating), false);

        Boolean isWantPRO = prefer.getBoolean(mContext.getString(R.string.pref_pro), false);

        Boolean isWantMobSec = prefer.getBoolean(mContext.getString(R.string.pref_mobile_security), false);

        if(isRating)
        {
            btRate.setVisibility(View.INVISIBLE);
        }
        else
        {
            btRate.setVisibility(View.VISIBLE);
        }

        if(isWantPRO)
        {
            btPRO.setVisibility(View.INVISIBLE);
        }
        else
        {
            btPRO.setVisibility(View.VISIBLE);
        }
        if(isWantMobSec)
        {
            btnMobileSecurity.setVisibility(View.INVISIBLE);
        }
        else
        {
            btnMobileSecurity.setVisibility(View.VISIBLE);
        }




    }

    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     регистрация функции поиска
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    public void searchFunction()
    {
        et  = (EditText)findViewById(R.id.filter);
        et.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable s)
            {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }
            public void onTextChanged(CharSequence s,int start, int before, int count)
            {
                String text = et.getText().toString();
                int textlength = et.getText().length();
                if(textlength>0)
                {
                    mDBConnector = new DBConnector (mContext);
                    mListView = (ListView)findViewById(R.id.list);
                    mAdapter = new myListAdapter(mContext, mDBConnector.selectForSearch(text));
                    mListView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
                else
                {
                    mDBConnector = new DBConnector (mContext);
                    mListView = (ListView)findViewById(R.id.list);
                    mAdapter = new myListAdapter(mContext, mDBConnector.selectAll());
                    mListView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     регистрация кнопки поиска
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    public void searchButton()
    {
        ((Button)findViewById(R.id.SearchButton)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (v.getId() == R.id.SearchButton)
                {
                    if( et.getVisibility()==View.INVISIBLE )
                    {
                        et.setVisibility(View.VISIBLE);
                        et.setFocusable(true);
                    }
                    else
                    {
                        et.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     регистрируем переключатель
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    public void ToggleButtonOnOffListener()
    {
        final ToggleButton OnOff = (ToggleButton)findViewById(R.id.speech_btn);

        SharedPreferences sPref;
        sPref = getPreferences(MODE_PRIVATE);
        recording = sPref.getBoolean("IsRecording",false);

        OnOff.setChecked(recording);

        //нажатие кнопки speakbtn
        ((ToggleButton)findViewById(R.id.speech_btn)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (v.getId() == R.id.speech_btn)
                {
                    loadIsRecording();
                    if (!recording)
                    {
                        recording = true;
                        //RemoteViews views = new RemoteViews(getPackageName(), R.layout.wig);
                        //views.setTextViewText(R.id.widget_textview, "ON");
                        //AppWidgetManager.getInstance(mContext).updateAppWidget(new ComponentName(mContext, HelloWidget.class), views);
                        saveIsRecording();
                        OnOff.setChecked(recording);
                        startService(new Intent(getApplicationContext(), RecordCallService.class));
                    }
                    else
                    {
                        recording = false;
                        //RemoteViews views = new RemoteViews(getPackageName(), R.layout.wig);
                        //views.setTextViewText(R.id.widget_textview, "OFF");
                        //AppWidgetManager.getInstance(mContext).updateAppWidget(new ComponentName(mContext, HelloWidget.class), views);
                        saveIsRecording();
                        OnOff.setChecked(recording);
                        stopService(new Intent(getApplicationContext(), RecordCallService.class));
                    }
                }
            }
        });
    }


    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     регистрация слушателя тапа по элементу списка
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    public void SimpleTapFunc()
    {
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                buttonPlayStop = (Button) findViewById(R.id.ButtonPlayStop);
                if (buttonPlayStop.getText() == getString(R.string.play_str))
                {
                    MyData mdat = mDBConnector.select(id);
                    String pat = mdat.getPath();
                    File myFile = new File(pat);

                    if (myFile.exists())
                    {
                        Uri uri = Uri.parse(pat);
                        try
                        {
                            initViews(uri); //TODO null exception here
                        }
                        catch ( NullPointerException e )
                        {
                            Toast.makeText(mContext, mContext.getResources().getText(R.string.errorInitView).toString(),Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                        seekBar.setEnabled(true);
                        buttonPlayStop.setEnabled(true);

                        if (buttonPlayStop.getText() == getString(R.string.play_str))
                        {
                            buttonPlayStop.setText(getString(R.string.pause_str));
                            buttonPlayStop.setBackgroundResource(R.drawable.pause);
                            try
                            {
                                mediaPlayer.start(); //TODO null exception here
                                startPlayProgressUpdater();
                            }
                            catch (IllegalStateException e)
                            {
                                mediaPlayer.pause();
                                e.printStackTrace();
                                buttonPlayStop.setText(getString(R.string.play_str));
                                buttonPlayStop.setBackgroundResource(R.drawable.play);
                            }
                            catch (NullPointerException e)
                            {
                                Toast.makeText(mContext, mContext.getResources().getText(R.string.errorTryFindFileOnSdcard).toString(),Toast.LENGTH_SHORT).show();
                                buttonPlayStop.setText(getString(R.string.play_str));
                                buttonPlayStop.setBackgroundResource(R.drawable.play);
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            buttonPlayStop.setText(getString(R.string.play_str));
                            buttonPlayStop.setBackgroundResource(R.drawable.play);
                            mediaPlayer.pause();
                        }
                    }
                }
            }
        };
        mListView.setOnItemClickListener(itemListener);  //setOnItemClickListener(itemListener);
    }

    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     метод onCreate
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Mobclix.onCreate(this);
        setContentView(R.layout.ban);

        mContext = this;
        mDBConnector = new DBConnector (this);
        mListView = (ListView)findViewById(R.id.list);
        mAdapter = new myListAdapter(mContext, mDBConnector.selectAll());
        mListView.setAdapter(mAdapter);
        registerForContextMenu(mListView);

        ToggleButtonOnOffListener();
        //////////////////////////////////////////
        searchFunction();
        rateApp();
        searchButton();
        SimpleTapFunc();
        ///////////////////////////////////////////
    }

    public void playAndStop(View v){
        if (buttonPlayStop.getText() == getString(R.string.play_str))
        {
            buttonPlayStop.setText(getString(R.string.pause_str));
            buttonPlayStop.setBackgroundResource(R.drawable.pause);
            try
            {
                mediaPlayer.start(); //TODO null exception here
                startPlayProgressUpdater();
            }
            catch (IllegalStateException e)
            {
                mediaPlayer.pause();
                e.printStackTrace();
                buttonPlayStop.setText(getString(R.string.play_str));
                buttonPlayStop.setBackgroundResource(R.drawable.play);
            }
            catch (NullPointerException e)
            {
                Toast.makeText(mContext, mContext.getResources().getText(R.string.errorTryFindFileOnSdcard).toString(),Toast.LENGTH_SHORT).show();
                buttonPlayStop.setText(getString(R.string.play_str));
                buttonPlayStop.setBackgroundResource(R.drawable.play);
                e.printStackTrace();
            }
        }
        else
        {
            buttonPlayStop.setText(getString(R.string.play_str));
            buttonPlayStop.setBackgroundResource(R.drawable.play);
            mediaPlayer.pause();

        }
    }
    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     метод startPlayProgressUpdater
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    public void startPlayProgressUpdater()
    {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        setProgressText();

        if (mediaPlayer.isPlaying())
        {
            Runnable notification = new Runnable() {
                public void run() {
                    startPlayProgressUpdater();
                }
            };
            handler.postDelayed(notification,1000);
        }
        else
        {
            mediaPlayer.pause();
            buttonPlayStop.setText(getString(R.string.play_str));
            buttonPlayStop.setBackgroundResource(R.drawable.play);
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            setProgressText();
        }
    }
    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     метод setProgressText
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    protected void setProgressText()
    {
        final int HOUR = 60*60*1000;
        final int MINUTE = 60*1000;
        final int SECOND = 1000;
        int durationInMillis = mediaPlayer.getDuration();
        int curVolume = mediaPlayer.getCurrentPosition();
        int durationHour = durationInMillis/HOUR;
        int durationMint = (durationInMillis%HOUR)/MINUTE;
        int durationSec = (durationInMillis%MINUTE)/SECOND;
        int currentHour = curVolume/HOUR;
        int currentMint = (curVolume%HOUR)/MINUTE;
        int currentSec = (curVolume%MINUTE)/SECOND;

        if(durationHour>0)
        {
            curTimeTV.setText(String.format("%02d:%02d:%02d/%02d:%02d:%02d",currentHour,currentMint,currentSec, durationHour,durationMint,durationSec));
        }
        else
        {
            curTimeTV.setText(String.format("%02d:%02d/%02d:%02d",currentMint,currentSec, durationMint,durationSec));
        }
    }

    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     метод initViews
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    private void initViews(Uri uri) throws NullPointerException{

        buttonPlayStop = (Button) findViewById(R.id.ButtonPlayStop);
        curTimeTV = (TextView)findViewById(R.id.CurTime);
        mediaPlayer = MediaPlayer.create(this,uri);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(mediaPlayer.getDuration());
        setProgressText();

        seekBar.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                seekChange(v);
                return false;
            }
        });
    }

    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     метод seekChange
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    private void seekChange(View v)
    {
        try
        {
            if(mediaPlayer.isPlaying())
            {
                SeekBar sb = (SeekBar)v;
                mediaPlayer.seekTo(sb.getProgress());
                setProgressText();
            }
            if(!mediaPlayer.isPlaying())
            {
                SeekBar sb = (SeekBar)v;
                mediaPlayer.seekTo(sb.getProgress());
                setProgressText();
            }
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }

    }


    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     метод onCreateContextMenu
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     метод создаем ContextMenu
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {

          /*  case R.id.open:

                buttonPlayStop = (Button) findViewById(R.id.ButtonPlayStop);
                if (buttonPlayStop.getText() == getString(R.string.play_str))
                {
                    MyData mdat = mDBConnector.select(info.id);
                    String pat = mdat.getPath();
                    File myFile = new File( pat);

                    if (myFile.exists())
                    {
                        Uri uri = Uri.parse(pat);
                        try
                        {
                            initViews(uri); //TODO null exception here
                        }
                        catch ( NullPointerException e )
                        {
                            Toast.makeText(mContext, mContext.getResources().getText(R.string.errorInitView).toString(),Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                        buttonPlayStop.setEnabled(true);

                        if (buttonPlayStop.getText() == getString(R.string.play_str))
                        {
                            buttonPlayStop.setText(getString(R.string.pause_str));
                            buttonPlayStop.setBackgroundResource(R.drawable.pause);
                            try
                            {
                                mediaPlayer.start(); //TODO null exception here
                                startPlayProgressUpdater();
                            }
                            catch (IllegalStateException e)
                            {
                                mediaPlayer.pause();
                                e.printStackTrace();
                                buttonPlayStop.setText(getString(R.string.play_str));
                                buttonPlayStop.setBackgroundResource(R.drawable.play);
                            }
                            catch (NullPointerException e)
                            {
                                Toast.makeText(mContext, mContext.getResources().getText(R.string.errorTryFindFileOnSdcard).toString(),Toast.LENGTH_SHORT).show();
                                buttonPlayStop.setText(getString(R.string.play_str));
                                buttonPlayStop.setBackgroundResource(R.drawable.play);
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            buttonPlayStop.setText(getString(R.string.play_str));
                            buttonPlayStop.setBackgroundResource(R.drawable.play);
                            mediaPlayer.pause();
                        }
                    }
                }
               return true;

            /*case R.id.edit:
                Intent i = new Intent(mContext, AddActivity.class);
                MyData md = mDBConnector.select(info.id);
                i.putExtra("MyData", md);
                startActivityForResult (i, UPDATE_ACTIVITY);
                updateList();
                return true;*/

            case R.id.delete:
                MyData mdata = mDBConnector.select(info.id);

                String pate = mdata.getPath();
                File outFile = new File( pate);
                if (outFile.exists()) {
                    outFile.delete();}

                mDBConnector.delete (info.id);

                updateList();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     метод создаем onCreateOptionsMenu
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     метод создаем onOptionsItemSelected
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle item selection
        switch (item.getItemId())
        {
            case R.id.writeToDeveloper:

                Intent goWriteToDev = new Intent(mContext, WriteToDev.class);
                startActivity(goWriteToDev);
                return true;

            case R.id.deleteAll:

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(  getString(R.string.deleteAll) + "?"  );
                builder.setCancelable(false);
                builder.setPositiveButton( getString(R.string.yesAnswer) ,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                mDBConnector.deleteAll();
                                extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                                myNewFolder = new File(extStorageDirectory + newFolder);
                                myDelete(myNewFolder); // моя функция для удаления папки
                                updateList();
                            }
                        });

                builder.setNegativeButton( getString(R.string.noAnswer) ,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();
                            }
                        });
                // выводим диалоговое окно
                builder.show();

                /** /////////////////////////////////////////////////////////////////*/
                SharedPreferences sPref;
                boolean recording;
                sPref =  getSharedPreferences("ActivityMain",MODE_PRIVATE);
                recording = sPref.getBoolean("IsRecording",false);

                if(recording)
                {
                    mContext.stopService(new Intent(mContext, RecordCallService.class));
                }

                /** /////////////////////////////////////////////////////////////////*/

                return true;
            /** /////////////////////////////////////////////////////////////////*/
            case R.id.action_settings:
                Intent intent = new Intent();
                intent.setClass(this, SettingActivity.class);
                startActivity(intent);

                return true;
            /** /////////////////////////////////////////////////////////////////*/
            case R.id.exit:
                finish();
                return true;
            /** /////////////////////////////////////////////////////////////////*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     метод updateList
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    private void updateList ()
    {
        mAdapter.setArrayMyData(mDBConnector.selectAll());
        mAdapter.notifyDataSetChanged();
    }

    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     class myListAdapter extends BaseAdapter
     //////////////////////////////////////////////*/
    class myListAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;
        private ArrayList<MyData> arrayMyData;

        public myListAdapter (Context ctx, ArrayList<MyData> arr) {
            mLayoutInflater = LayoutInflater.from(ctx);
            setArrayMyData(arr);
        }

        public ArrayList<MyData> getArrayMyData() {
            return arrayMyData;
        }

        public void setArrayMyData(ArrayList<MyData> arrayMyData) {
            this.arrayMyData = arrayMyData;
        }

        public int getCount () {
            return arrayMyData.size();
        }

        public Object getItem (int position) {

            return position;
        }

        public long getItemId (int position) {
            MyData md = arrayMyData.get(position);
            if (md != null) {
                return md.getID();
            }
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            try
            {
                if (convertView == null)
                    convertView = mLayoutInflater.inflate(R.layout.item, null);

                ImageView vIcon = (ImageView)convertView.findViewById(R.id.Icon);
                TextView vTitle = (TextView)convertView.findViewById(R.id.Title);
                TextView vDate = (TextView)convertView.findViewById(R.id.Date);
                TextView vTime = (TextView)convertView.findViewById(R.id.Time);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                MyData md = arrayMyData.get(position);
                vDate.setText(dateFormat.format(md.getDate()));
                vTime.setText(md.getTime());
                vTitle.setText(md.getTitle());
                vIcon.setImageResource(md.getIcon());
            }
            catch (NullPointerException e)
            {
                e.printStackTrace();
            }

            return convertView;
        }
    } // end myAdapter


    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     onStart
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    @Override
    public void onStart()
    {
        super.onStart();
        setContentView(R.layout.ban);
        mDBConnector = new DBConnector (this);
        mListView = (ListView)findViewById(R.id.list);
        mAdapter = new myListAdapter(mContext, mDBConnector.selectAll());
        mListView.setAdapter(mAdapter);
        registerForContextMenu(mListView);

        ToggleButtonOnOffListener();
        //////////////////////////////////////////
        searchFunction();
        rateApp();
        searchButton();
        SimpleTapFunc();
        ///////////////////////////////////////////
    }

    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     onResume
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    @Override
    public void onResume()
    {
        super.onResume();
        setContentView(R.layout.ban);
        mDBConnector = new DBConnector (this);
        mListView = (ListView)findViewById(R.id.list);
        mAdapter = new myListAdapter(mContext, mDBConnector.selectAll());
        mListView.setAdapter(mAdapter);
        registerForContextMenu(mListView);

        ToggleButtonOnOffListener();
        //////////////////////////////////////////
        searchFunction();
        rateApp();
        searchButton();
        SimpleTapFunc();
        ///////////////////////////////////////////
    }

    /** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     функция удаления папки
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    public void myDelete(File file)
    {
        if(!file.exists())
            return;
        if(file.isDirectory())
        {
            for(File f : file.listFiles())
                myDelete(f);
            file.delete();
        }
        else
        {
            file.delete();
        }
    }


    @Override
    public void onStop()
    {
        super.onStop();
        try
        {
            mediaPlayer.pause();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


/** /////////////////////////////////////////////////////////////////////////////////////////////////////////////
 КОНЕЦ АКТИВИТИ
 //////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
}





