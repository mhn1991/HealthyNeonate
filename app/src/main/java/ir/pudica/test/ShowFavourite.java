package ir.pudica.test;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

public class ShowFavourite extends AppCompatActivity {
    SharedPreferences pref;
    private int counter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_favourite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundResource(R.drawable.action_bar_color);
        toolbar.setTitle("لیست دارو های پرکاربرد:");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        counter =0;
        logicUnit();
    }

    private void logicUnit()
    {
        getIntent().getStringExtra("class");
        if((getIntent().getStringExtra("class")).equals("NEOFAX"))
        {
            new NEO_FAV().execute();
            //getNeoTitle();
        }
        else
        {
            new DPAL_FAV().execute();
            //getDPALTitle();
        }
    }


//    private void getDPALTitle() {
//        String dir = String.valueOf(getFilesDir()) + "/db2.sqlite";
//        SQLiteDatabase db = SQLiteDatabase.openDatabase(dir, null, SQLiteDatabase.OPEN_READONLY);
//        Cursor c = db.rawQuery("select Title from book;", null);
//        ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.favLayout);
//        final LinearLayout ll = (LinearLayout) cl.findViewById(R.id.FavLinear);
//        if (c.moveToFirst()) {
//            do {
//                String tmp = c.getString(0);
//                tmp = tmp.split("\n")[0];
//                tmp = tmp.replaceAll(" ", "_");
//                tmp = tmp.replaceAll("/", "_");
//                final String name = tmp;
//                pref = getSharedPreferences(name, ShowFavourite.MODE_PRIVATE);
//                String State = pref.getString(name, "NoStar");
//                if (State.equals("Star")) {
//                    counter++;
//                    TextView tx = new TextView(ShowFavourite.this);
//                    tx.setText(name);
//                    tx.setTextSize(20);
//                    tx.setPadding(30, 20, 0, 30);
//                    tx.setBackgroundResource(R.drawable.textwraper);
//                    tx.setTextColor(Color.BLACK);
//                    tx.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent goToDrug = new Intent(ShowFavourite.this, ShowDrugs.class);
//                            goToDrug.putExtra("name", name);
//                            goToDrug.putExtra("class", "DPAL");
//                            goToDrug.putExtra("act", "fav");
//                            startActivity(goToDrug);
//                            finish();
//                        }
//                    });
//                    ll.addView(tx);
//                }
//            } while (c.moveToNext());
//        }
//        if (counter == 0) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    TextView tx = new TextView(ShowFavourite.this);
//                    tx.setText("هیچ آیتمی برای نمایش وجود ندارد." + " " + "برای اضافه کردن دارو در بخش دارو های پرکاربرد، آیکون ستاره را در صفحه ی هر دارو لمس نمایید.");
//                    tx.setTextSize(19);
//                    tx.setPadding(20, 80, 20, 20);
//                    tx.setTextColor(Color.WHITE);
//                    ll.addView(tx);
//                }
//            });
//        }
//    }
//
//
//    private void getNeoTitle() {
//        String dir = String.valueOf(getFilesDir()) + "/db1.sqlite";
//        SQLiteDatabase db = SQLiteDatabase.openDatabase(dir, null, SQLiteDatabase.OPEN_READONLY);
//        Cursor c = db.rawQuery("select Title from info;", null);
//        ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.favLayout);
//        final LinearLayout ll = (LinearLayout) cl.findViewById(R.id.FavLinear);
//        if (c.moveToFirst()) {
//            do {
//                final String name = c.getString(0);
//                pref = getSharedPreferences(name, ShowFavourite.this.MODE_PRIVATE);
//                String State = pref.getString(name, "NoStar");
//                if (State.equals("Star")) {
//                    counter++;
//                    TextView tx = new TextView(ShowFavourite.this);
//                    tx.setText(name);
//                    tx.setTextSize(20);
//                    tx.setPadding(30, 20, 0, 30);
//                    tx.setBackgroundResource(R.drawable.textwraper);
//                    tx.setTextColor(Color.BLACK);
//                    tx.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent goToDrug = new Intent(ShowFavourite.this, ShowDrugs.class);
//                            goToDrug.putExtra("name", name);
//                            goToDrug.putExtra("class", "NEOFAX");
//                            goToDrug.putExtra("act", "fav");
//                            startActivity(goToDrug);
//                            finish();
//                        }
//                    });
//                    ll.addView(tx);
//                }
//            } while (c.moveToNext());
//        }
//        if (counter == 0) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    TextView tx = new TextView(ShowFavourite.this);
//                    tx.setText("هیچ آیتمی برای نمایش وجود ندارد." + " " + "برای اضافه کردن دارو در بخش دارو های پرکاربرد، آیکون ستاره را در صفحه ی هر دارو لمس نمایید.");
//                    tx.setTextSize(19);
//                    tx.setPadding(20, 80, 20, 20);
//                    tx.setTextColor(Color.WHITE);
//                    ll.addView(tx);
//                }
//            });
//        }
//    }


    private class NEO_FAV extends AsyncTask  {

        ProgressDialog pd;
        @Override
        protected Object doInBackground(Object... arg0)
        {

            try {
                getNeoTitle();
            } catch (Exception e) {
                Log.e("ERROR ---->", e.toString());
            }

            return null;
        }

        private void getNeoTitle()
        {
            String dir = String.valueOf(getFilesDir())+"/db1.sqlite";
            SQLiteDatabase db = SQLiteDatabase.openDatabase(dir, null, SQLiteDatabase.OPEN_READONLY);
            Cursor c = db.rawQuery("select Title from info;", null);
            ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.favLayout);
            final LinearLayout ll = (LinearLayout) cl.findViewById(R.id.FavLinear);
            if(c.moveToFirst())
            {
                do {
                    final String name = c.getString(0);
                    pref = getSharedPreferences(name, ShowFavourite.this.MODE_PRIVATE);
                    String State = pref.getString(name,"NoStar");
                    if(State.equals("Star"))
                    {
                        counter++;
                        final TextView tx = new TextView(ShowFavourite.this);
                        tx.setText(name);
                        tx.setTextSize(20);
                        tx.setPadding(30,20,0,30);
                        tx.setBackgroundResource(R.drawable.textwraper);
                        tx.setTextColor(Color.BLACK);
                        tx.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent goToDrug = new Intent(ShowFavourite.this,ShowDrugs.class);
                                goToDrug.putExtra("name",name);
                                goToDrug.putExtra("class","NEOFAX");
                                goToDrug.putExtra("act","fav");
                                startActivity(goToDrug);
                                finish();
                            }
                        });
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ll.addView(tx);
                            }
                        });
                    }
                }while(c.moveToNext());
            }
            if(counter == 0)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView tx = new TextView(ShowFavourite.this);
                        tx.setText("هیچ آیتمی برای نمایش وجود ندارد."+" "+"برای اضافه کردن دارو در بخش دارو های پرکاربرد، آیکون ستاره را در صفحه ی هر دارو لمس نمایید.");
                        tx.setTextSize(19);
                        tx.setPadding(20,80,20,20);
                        tx.setTextColor(Color.WHITE);
                        ll.addView(tx);
                    }
                });
            }
        }


        @Override
        protected void onPreExecute()
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pd = ProgressDialog.show(ShowFavourite.this, null , "در حال آماده سازی ...");
                }
            });

        }

        @Override
        protected void onPostExecute(Object arg0)
        {
            pd.dismiss();
        }

    }



    private class DPAL_FAV extends AsyncTask  {

        ProgressDialog pd;
        @Override
        protected Object doInBackground(Object... arg0)
        {

            try {
                getDPALTitle();
            } catch (Exception e) {
                Log.e("ERROR ---->", e.toString());
            }

            return null;
        }

        private void getDPALTitle()
        {
            String dir = String.valueOf(getFilesDir())+"/db2.sqlite";
            SQLiteDatabase db = SQLiteDatabase.openDatabase(dir, null, SQLiteDatabase.OPEN_READONLY);
            Cursor c = db.rawQuery("select Title from book;", null);
            ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.favLayout);
            final LinearLayout ll = (LinearLayout) cl.findViewById(R.id.FavLinear);
            if(c.moveToFirst())
            {
                do {
                    String tmp = c.getString(0);
                    tmp = tmp.split("\n")[0];
                    tmp = tmp.replaceAll(" ","_");
                    tmp = tmp.replaceAll("/","_");
                    final String name = tmp;
                    pref = getSharedPreferences(name, ShowFavourite.MODE_PRIVATE);
                    String State = pref.getString(name,"NoStar");
                    if(State.equals("Star"))
                    {
                        counter++;
                        final TextView tx = new TextView(ShowFavourite.this);
                        tx.setText(name);
                        tx.setTextSize(20);
                        tx.setPadding(30,20,0,30);
                        tx.setBackgroundResource(R.drawable.textwraper);
                        tx.setTextColor(Color.BLACK);
                        tx.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent goToDrug = new Intent(ShowFavourite.this,ShowDrugs.class);
                                goToDrug.putExtra("name",name);
                                goToDrug.putExtra("class","DPAL");
                                goToDrug.putExtra("act","fav");
                                startActivity(goToDrug);
                                finish();
                            }
                        });
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ll.addView(tx);
                            }
                        });
                    }
                }while(c.moveToNext());
            }
            if(counter == 0)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView tx = new TextView(ShowFavourite.this);
                        tx.setText("هیچ آیتمی برای نمایش وجود ندارد."+" "+"برای اضافه کردن دارو در بخش دارو های پرکاربرد، آیکون ستاره را در صفحه ی هر دارو لمس نمایید.");
                        tx.setTextSize(19);
                        tx.setPadding(20,80,20,20);
                        tx.setTextColor(Color.WHITE);
                        ll.addView(tx);
                    }
                });
            }
        }

        @Override
        protected void onPreExecute()
        {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pd = ProgressDialog.show(ShowFavourite.this, null , "در حال آماده سازی ...");
                }
            });


        }

        @Override
        protected void onPostExecute(Object arg0)
        {
            pd.dismiss();
        }

    }
}
