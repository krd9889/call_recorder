package com.habra.example.com;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class SettingActivity extends PreferenceActivity {

    Context ctx;
    static final int PICKFILE_RESULT_CODE = 1;

    @Override
    public void onStop()
    {
        super.onStop();
        try
        {
            stopService(new Intent(getApplicationContext(), RecordCallService.class));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // загружаем предпочтения из ресурсов
        addPreferencesFromResource(R.xml.preference);

        ctx=this;



        final EditTextPreference ed = (EditTextPreference)findPreference( getString(R.string.pref_pathview) );

                Preference customPref = (Preference) findPreference(  getString(R.string.customPref)  );



            if (customPref != null) {
                customPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                            public boolean onPreferenceClick(Preference preference) {

                                String m_chosenDir = "";
                                boolean m_newFolderEnabled = true;


                                DirectoryChooserDialog directoryChooserDialog =
                                        new DirectoryChooserDialog(SettingActivity.this,
                                                new DirectoryChooserDialog.ChosenDirectoryListener()
                                                {
                                                    @Override
                                                    public void onChosenDir(String chosenDir)
                                                    {

                                                     /*   Toast.makeText(
                                                                SettingActivity.this, "Chosen directory: " +
                                                                chosenDir, Toast.LENGTH_LONG).show();
                                                     */
                                                        if (ed != null) {
                                                            ed.setText(chosenDir);
                                                            ed.setSummary(chosenDir);
                                                        }


                                                       /* final String papka = "papka";
                                                        SharedPreferences sPref;

                                                        sPref = getPreferences(MODE_PRIVATE);
                                                        SharedPreferences.Editor ed = sPref.edit();
                                                        ed.putString(papka, chosenDir);
                                                        ed.commit();*/
                                               ///////////////////////////////////////////////////////////////////////////////////


                                                       /* SharedPreferences customSharedPreference = getSharedPreferences(
                                                                "myCustomSharedPrefs", Activity.MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = customSharedPreference
                                                                .edit();
                                                        editor.putString("myCustomPref",
                                                                "The preference has been clicked");
                                                        editor.commit();*/

                                                    }
                                                });


                                directoryChooserDialog.setNewFolderEnabled(m_newFolderEnabled);
                                // Load directory chooser dialog for initial 'm_chosenDir' directory.
                                // The registered callback will be called upon final directory selection.
                                directoryChooserDialog.chooseDirectory(m_chosenDir);
                                m_newFolderEnabled = ! m_newFolderEnabled;

                            /*    Toast.makeText(getBaseContext(),
                                        "The custom preference has been clicked",
                                        Toast.LENGTH_LONG).show();
                                SharedPreferences customSharedPreference = getSharedPreferences(
                                        "myCustomSharedPrefs", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = customSharedPreference
                                        .edit();
                                editor.putString("myCustomPref",
                                        "The preference has been clicked");
                                editor.commit();*/



                                return true;
                            }

                        });
            }









        Preference whitelist = (Preference) findPreference(  getString(R.string.pref_white_list)  );


        if (whitelist != null) {
            whitelist.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                        public boolean onPreferenceClick(Preference preference) {

                            Intent goWhite = new Intent(ctx, ActivityContact.class);
                            startActivity(goWhite);


                            return true;
                        }

                    });
        }


        Preference blacklist = (Preference) findPreference(  getString(R.string.pref_black_list)  );


        if (blacklist != null) {
            blacklist.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                        public boolean onPreferenceClick(Preference preference) {

                            Intent goBlack = new Intent(ctx, ActivityContactBlack.class);
                            startActivity(goBlack);


                            return true;
                        }

                    });
        }






       /* mListView = (ListView)findViewById(R.id.listview);

        mAdapter = new ArrayAdapter<String>(ctx,
                android.R.layout.simple_list_item_1);



        mListView.setAdapter(mAdapter);

        registerForContextMenu(mListView);*/


        //list = new ArrayList<MyData>(Arrays.asList(  ));


        //mListView.setOnItemClickListener(itemListener);

     /*   final CheckBoxPreference cb = (CheckBoxPreference)findPreference(  getString(R.string.pref_white)  );

        final CheckBoxPreference cbp = (CheckBoxPreference)findPreference(  getString(R.string.pref_black)  );


        cb.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {


                if(cbp.isChecked())
                cbp.setChecked(false);
                else
                    cbp.setChecked(true);



                return true;
            }

        });


               cbp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {


            if(cb.isChecked())
            cb.setChecked(false);
            else
                cb.setChecked(true);


                return true;
            }

        });*/






    }
}