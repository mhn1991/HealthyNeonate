package ir.pudica.test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URLDecoder;
import java.util.prefs.*;
public class ShowDrugs extends AppCompatActivity {
    private String name;
    SharedPreferences pref;
    private ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_drugs);
        String tmp = getIntent().getStringExtra("name");
        String[] tmp2 = tmp.split("\n");
        name = tmp2[0];
        name = name.replaceAll(" ","_");
        name = name.replaceAll("/","_");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AppBarLayout ap = (AppBarLayout) findViewById(R.id.appbar);
        ap.setBackgroundResource(R.drawable.action_bar_color);
        TextView tx = (TextView) findViewById(R.id.drugTitle);
        tx.setText(name);
        tx.setTextSize(20);
        tx.setTextColor(Color.WHITE);
        tx.setPadding(30,30,0,0);
        final ImageView Starimg = (ImageView) findViewById(R.id.starimg);

        pref = getSharedPreferences(name, this.MODE_PRIVATE);
        String State = pref.getString(name,"NoStar");
        if(State.equals("NoStar"))
        {
            Starimg.setImageResource(android.R.drawable.btn_star_big_off);
            Starimg.setTag("off");
        }
        else if(State.equals("Star"))
        {
            Starimg.setImageResource(android.R.drawable.btn_star_big_on);
            Starimg.setTag("on");
        }

        Starimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if the pStarimg.getId()
                if (String.valueOf(Starimg.getTag()).equals("off"))
                {
                    Starimg.setImageResource(android.R.drawable.btn_star_big_on);
                    Starimg.setTag("on");
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(name,"Star");
                    editor.apply();
                }
                else
                {
                    Starimg.setImageResource(android.R.drawable.btn_star_big_off);
                    Starimg.setTag("off");
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(name,"NoStar");
                    editor.apply();
                }
            }
        });

        mListView = (ListView) findViewById(R.id.listView);

        logicUnit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        String clas = getIntent().getStringExtra("class");
        String fav = getIntent().getStringExtra("act");
        try {
            if (fav.equals("fav")) {
                Intent goToDrug = new Intent(ShowDrugs.this,ShowFavourite.class);
                if (clas.equals("DPAL")) {
                    goToDrug.putExtra("class", "DPAL");
                } else {
                    goToDrug.putExtra("class", "NEOFAX");
                }
                startActivity(goToDrug);
                finish();
            }
        }catch(Exception e)
        {
            Log.e("---------->>>","ridim");
        }

    }

    private void logicUnit()
    {
        String clas = getIntent().getStringExtra("class");
        if(clas.equals("DPAL"))
        {
            ShowContent2();
        }
        else
        {
            showContent();
        }
    }



    private void showContent()
    {
        String dir = String.valueOf(getFilesDir())+"/db1.sqlite";//sql path
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dir, null, SQLiteDatabase.OPEN_READONLY);// open database
        Cursor c = db.rawQuery("select * from info where Title LIKE \"%"+name+"%\";", null);
        c.moveToFirst();
        ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.ContentLayout);
        LinearLayout ll = (LinearLayout) cl.findViewById(R.id.Contents);

        do {
            for(int i=1 ;i < c.getColumnCount();i++) {
                try {
                    if (!c.getString(i).equals("")) {
                        String tmp = c.getString(i);
                        tmp = tmp.replaceAll("%","");
                        //==============================================
                        tmp = tmp.replaceAll("Dose\n","Dose");
                        int et = tmp.indexOf("\n");
                        String otmp = tmp.substring(0, et) + "\n" + tmp.substring(et);
                        tmp = otmp;
                        //==============================================
                        TextView tx = new TextView(this);
                        tx.setBackgroundResource(R.drawable.textwraper);
                        tx.setText(URLDecoder.decode(tmp, "UTF-8"));
                        tx.setTextSize(15);
                        tx.setPadding(20, 20, 0, 20);
                        tx.setTextColor(Color.BLACK);
                        ll.addView(tx);
                    }
                }
                catch(Exception e)
                {

                }
            }
        }while(c.moveToNext());
    }

    private void ShowContent2()
    {
        String dir = String.valueOf(getFilesDir())+"/db2.sqlite";//sql path
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dir, null, SQLiteDatabase.OPEN_READONLY);// open database
        Cursor c = db.rawQuery("select * from book where Title  LIKE \"%"+name+"%\";", null);
        c.moveToFirst();

        ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.ContentLayout);
        LinearLayout ll = (LinearLayout) cl.findViewById(R.id.Contents);

        do {
            for(int i=1 ;i < c.getColumnCount();i++) {
                try {
                    if (!c.getString(i).equals("")) {
                        String tmp = c.getString(i);
                        tmp = tmp.replaceAll("%","%25");
                        //==============================================
                        int et = tmp.indexOf("\n");
                        tmp = tmp.replaceAll("\n","");
//                        tmp = tmp.replaceAll("\\.","\\.\n");
                        String otmp = tmp.substring(0,et) +"\n\n"+ tmp.substring(et);
                        tmp = otmp;

                        //==============================================
                        TextView tx = new TextView(this);
                        tx.setBackgroundResource(R.drawable.textwraper);
                        tx.setText(URLDecoder.decode(tmp, "UTF-8"));
                        tx.setTextSize(15);
                        tx.setPadding(20, 20, 0, 20);
                        tx.setTextColor(Color.BLACK);
                        ll.addView(tx);
                    }
                }
                catch(Exception e)
                {

                }
            }
        }while(c.moveToNext());
    }
}
