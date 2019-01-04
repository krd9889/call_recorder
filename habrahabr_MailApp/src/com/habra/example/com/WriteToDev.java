package com.habra.example.com;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * Created by Виталий btn_toggle_on 14.10.13.
 */
public class WriteToDev extends Activity {

    ImageButton send;
    EditText address, subject, emailtext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_dev);

        // Наши поля и кнопка
        send = (ImageButton) findViewById(R.id.emailsendbutton);
        address = (EditText) findViewById(R.id.emailaddressET);
        subject = (EditText) findViewById(R.id.emailsubjectET);
        emailtext = (EditText) findViewById(R.id.emailtext);

        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

                emailIntent.setType("plain/text");
                // Кому
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                        new String[] { address.getText().toString() });
                // Зачем
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                        subject.getText().toString());
                // О чём
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                        emailtext.getText().toString());
                // С чем
                /*emailIntent.putExtra(
                        android.content.Intent.EXTRA_STREAM,
                        Uri.parse("file://"
                                + Environment.getExternalStorageDirectory()
                                + "/Клипы/SOTY_ATHD.mp4"));

                emailIntent.setType("text/video");*/
                // Поехали!
                WriteToDev.this.startActivity(Intent.createChooser(emailIntent,
                        getString(R.string.send_to_dev)));
            }
        });
    }

}
