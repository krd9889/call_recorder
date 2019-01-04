package com.habra.example.com;

import android.app.Activity;
import android.app.TabActivity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TabHost;
import android.widget.Toast;

import com.mobclix.android.sdk.Mobclix;
import com.mobclix.android.sdk.MobclixAdView;
import com.mobclix.android.sdk.MobclixMMABannerXLAdView;

/**
 * Created by Виталий btn_toggle_on 08.09.13.
 */
public class MAIN extends TabActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE); // не отображать заголовок приложения
        setContentView(R.layout.extended_mail); // мы грузим содержимое из main1.xmll


    TabHost tabHost = getTabHost();

    //Таб музыки
    TabHost.TabSpec music = tabHost.newTabSpec("tab1");              //идентификатор нашего таба
    music.setIndicator(getResources().getString(R.string.app_name));                            //Название таба
    Intent photosIntent = new Intent(this, ActivityMain.class);//Класс к которому будем обращаться при переходе
    music.setContent(photosIntent);

    //Таб Фильмов
   /* TabHost.TabSpec films = tabHost.newTabSpec("tab2");             //идентификатор нашего таба
    films.setIndicator(getResources().getString(R.string.settings));                           //Название таба
    Intent songsIntent = new Intent(this, ActivityContact.class);//Класс к которому будем обращаться при переходе
    films.setContent(songsIntent);*/
    //Таб игры
    /*TabHost.TabSpec games = tabHost.newTabSpec("tab3");              //идентификатор нашего таба

    games.setIndicator("реклама");                              //Название таба
    Intent videosIntent = new Intent(this, com.mobclix.android.sdk.MobclixBrowserActivity.class);//Класс к которому будем обращаться при переходе
    games.setContent(videosIntent);
*/
    //Добавляем все наши созданные табы в TabHost
    tabHost.addTab(music);                                   //музыка
    //tabHost.addTab(films);                                   //фильмы
   // tabHost.addTab(games);                                   //игры







    }
}

