package com.habra.example.com;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;

/**
 * Created by Виталий btn_toggle_on 16.10.13.
 */
public class ActivityContactBlack extends Activity {


    // Comparator for Ascending Order
    public static Comparator<String> StringAscComparator = new Comparator<String>() {

        public int compare(String app1, String app2) {

            String stringName1 = app1;
            String stringName2 = app2;

            return stringName1.compareToIgnoreCase(stringName2);
        }
    };

    //Comparator for Descending Order
    public static Comparator<String> StringDescComparator = new Comparator<String>() {

        public int compare(String app1, String app2) {

            String stringName1 = app1;
            String stringName2 = app2;

            return stringName2.compareToIgnoreCase(stringName1);
        }
    };


    private Context ctx;
    protected ArrayAdapter<String> mAdapter;
    private static final int PICKFILE_RESULT_CODE = 1;

    ListView mListView;
    //ArrayAdapter<String> mAdapter;
    String selectedItem;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.ban);


        setContentView(R.layout.contact_list);

        ctx = this;




        mAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);

        restoreListViewLines();

        //populateContactList();

        mAdapter.sort(StringAscComparator);
        //И, наконец, назначаем адаптер виджету Listview:

        mListView = (ListView)findViewById(R.id.listview);
        mListView.setAdapter(mAdapter);




        // восстановим содержимое списка при запуске
        // restoreListViewLines();

        // populateContactList();

        //Collections.sort(mListView, StringAscComparator);



     /*   AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // TODO Auto-generated method stub
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) parent.getSelectedItem();

                mListView.setItemChecked(position, true);

            }


        };*/

     /*   mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            View view;

            public void onItemClick(AdapterView<?> ListView, View view, int position, long id) {
                CheckedTextView textView = (CheckedTextView)view;
                textView.setChecked(!textView.isChecked());

                this.view = view;

            }

        });*/

        // mListView.setOnItemClickListener(itemListener);  //setOnItemClickListener(itemListener);


        // adapter = new ArrayAdapter<String>(this,
        //R.layout.list_item, list); //вместо fl стало list



        ImageButton button = (ImageButton)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {



                // mAdapter.add("test");
                Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pickIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(pickIntent, PICKFILE_RESULT_CODE );

            }
        });






        AdapterView.OnItemLongClickListener itemLongListener = new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v,
                                           int position, long id) {
                // сохраним выбранный пункт в глобальной переменной
                selectedItem = parent.getItemAtPosition(position).toString();

                // Build new AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setMessage(  getString(R.string.delete) + "?");
                builder.setCancelable(false);
                builder.setPositiveButton(  getString(R.string.yesAnswer),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                mAdapter.remove(selectedItem);
                                mAdapter.notifyDataSetChanged();

                            }
                        });
                builder.setNegativeButton(getString(R.string.noAnswer),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();
                            }
                        });

                // выводим диалоговое окно
                builder.show();

                return true;

            }

        };

        mListView.setOnItemLongClickListener(itemLongListener);




    }



  /*  public void populateContactList() {

        String CONTACTS="";




        Cursor cursor2 = ctx.getContentResolver().query(ContactsContract.CommonDataKinds.phone_blue.CONTENT_URI,
                new String[]{
                        ContactsContract.CommonDataKinds.phone_blue._ID,
                        ContactsContract.CommonDataKinds.phone_blue.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.phone_blue.NUMBER},
                null,null, null);

        // String[] pno=new String[cursor2.getCount()];
        //startManagingCursor(cursor2);

        if (cursor2.getCount() > 0)
        {int i=0;
            while (cursor2.moveToNext())
            {
                // process them as you want
                //Log.i("DATA", " ID " + cursor2.getString(0) + " NAME" + cursor2.getString(btn_toggle_off) + " PHONE " + cursor2.getString(btn_toggle_on));
                // pno[i++]=(String)cursor2.getString(0).toString() +"  "+       (String)cursor2.getString(btn_toggle_off);
                //  pno[i++]="ID "+cursor2.getString(0)+" NAME "+cursor2.getString(btn_toggle_off)+" PHONE "+cursor2.getString(btn_toggle_on);

                //if(numbers.equals(cursor2.getString(btn_toggle_on)))
               // {
                //    numbers=cursor2.getString(btn_toggle_off);

                //    break;
               // }

                CONTACTS =cursor2.getString(btn_toggle_off) +"\n"+ cursor2.getString(btn_toggle_on);
                mAdapter.add(CONTACTS);
                //CONTACTS+="ID: "+cursor2.getString(0)+" NAME: "+cursor2.getString(btn_toggle_off)+" PHONE: "+cursor2.getString(btn_toggle_on)+"\n";
                //Toast.makeText(ctx," ID "+cursor2.getString(0)+" NAME"+cursor2.getString(btn_toggle_off)+" PHONE "+cursor2.getString(btn_toggle_on),
                //   Toast.LENGTH_LONG).show();
            }


            //tmptext=CONTACTS;



        }

    }*/








    @Override
    protected void onStop() {
        super.onStop();

        // save
        saveListViewLines();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch(requestCode){
            case PICKFILE_RESULT_CODE:
                if(resultCode == RESULT_OK){
                    // String FilePath = data.getData().getPath();

                    Uri FileUri = data.getData();



// Получаем из URI контакты
////////////////////////////////////////////////////////////////////////////////
                    String name="";
                    String email="";
                    String phone = "";

                    Cursor cursor;  // Cursor object
                    String mime;    // MIME type
                    int dataIdx;    // Index of DATA1 column
                    int mimeIdx;    // Index of MIMETYPE column
                    int nameIdx;    // Index of DISPLAY_NAME column

                    // Get the name
                    cursor = getContentResolver().query(FileUri,
                            new String[] { ContactsContract.Contacts.DISPLAY_NAME },
                            null, null, null);
                    if (cursor.moveToFirst()) {
                        nameIdx = cursor.getColumnIndex(
                                ContactsContract.Contacts.DISPLAY_NAME);
                        name = cursor.getString(nameIdx);

                        // Set up the projection
                        String[] projection = {
                                ContactsContract.Data.DISPLAY_NAME,
                                ContactsContract.Contacts.Data.DATA1,
                                ContactsContract.Contacts.Data.MIMETYPE };

                        // Query ContactsContract.Data
                        cursor = getContentResolver().query(
                                ContactsContract.Data.CONTENT_URI, projection,
                                ContactsContract.Data.DISPLAY_NAME + " = ?",
                                new String[] { name },
                                null);

                        if (cursor.moveToFirst()) {
                            // Get the indexes of the MIME type and data
                            mimeIdx = cursor.getColumnIndex(
                                    ContactsContract.Contacts.Data.MIMETYPE);
                            dataIdx = cursor.getColumnIndex(
                                    ContactsContract.Contacts.Data.DATA1);

                            // Match the data to the MIME type, store in variables
                            do {
                                mime = cursor.getString(mimeIdx);
                                if (ContactsContract.CommonDataKinds.Email
                                        .CONTENT_ITEM_TYPE.equalsIgnoreCase(mime)) {
                                    email = cursor.getString(dataIdx);
                                }
                                if (ContactsContract.CommonDataKinds.Phone
                                        .CONTENT_ITEM_TYPE.equalsIgnoreCase(mime)) {
                                    phone = cursor.getString(dataIdx);
                                    phone = PhoneNumberUtils.formatNumber(phone);
                                }
                            } while (cursor.moveToNext());
                        }
                    }
////////////////////////////////////////////////////////////////////////////////





                    mAdapter.add(name+" "+phone+" "+email);

                    mAdapter.sort(StringAscComparator);
                }
                break;
        }
    }

















    protected void saveListViewLines() {
        FileOutputStream fos;
        try {
            // открываем файл в приватном пространстве приложения
            fos = openFileOutput("listview-linesblack.txt", Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        int cnt = mAdapter.getCount();
        String item;

        // считываем все элементы из адаптера и сохраняем их в файл
        for (int i=0; i<cnt; ++i) {
            try {
                item = mAdapter.getItem(i) + "\n";
                fos.write( item.getBytes("UTF-8") );
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






    protected void restoreListViewLines() {
        FileInputStream fis;
        try {
            // открываем файл из приватного хранилища
            fis = openFileInput("listview-linesblack.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // создаём «читатель» данных из файла, чтобы прочитать
        // их построчно.
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        String line;
        //String line2;
        //String line3;
        try {
            while (true) {
                line = br.readLine();
                //line2 = br.readLine();
                // line3=line+line2;
                if (line == null ) {
                    break;
                }
                // и каждую успешно считанную строчку добавляем
                // в список через адаптер
                mAdapter.add(line);
            }
        } catch (IOException e) {
        }
    }


}
