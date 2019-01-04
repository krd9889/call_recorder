package com.habra.example.com;

import java.io.Serializable;

/**
 * Created by Виталий btn_toggle_on 19.09.13.
 */
public class MyData implements Serializable{

    private long id;
    private long date;
    private String time;
    private String path;
    private String title;
    private int icon;

    public MyData (long id, long date,String time,String path, String title, int icon) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.path = path;
        this.title = title;
        this.icon = icon;
    }

    public long getID () {return id;}
    public long getDate () {return date;}
    public String getTime () {return time;}
    public String getPath () {return path;}
    public String getTitle () {return title;}
    public int getIcon () {return icon;}
}