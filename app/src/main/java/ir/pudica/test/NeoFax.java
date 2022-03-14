package ir.pudica.test;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.R.id.empty;
import static android.R.id.message;
import static ir.pudica.test.R.id.drawer_layout;
import static ir.pudica.test.R.id.listView;
import static ir.pudica.test.R.id.subject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

//AppCompatActivity


public class NeoFax extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    AutoCompleteTextView text;
    private boolean connection;
    private String dbChecksum;
    private String subject;
    private String message;
    private String res;
    LinkedList<String> mLinked = new LinkedList<String>();
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neo_fax);
//
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        lv = (ListView) findViewById(R.id.lv);

        logicUnit();

    }

    long lastPress;
    Toast backpressToast;
    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastPress > 5000){
            backpressToast = Toast.makeText(getBaseContext(), "برای خروج لطفا مجدداً دکمه ی بازگشت را لمس کنید", Toast.LENGTH_LONG);
            backpressToast.show();
            lastPress = currentTime;
        } else {
            if (backpressToast != null) backpressToast.cancel();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.neo_fax, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.neofaxlogo)
        {
            finish();
            Intent newActivity = new Intent(this,NeoFax.class);
            startActivity(newActivity);
        }
        else if (id == R.id.prag)
        {
            finish();
            Intent newActivity = new Intent(this,DPAL.class);
            startActivity(newActivity);
        } else if (id == R.id.feedback)
        {
            showFeedBack();
        } else if (id == R.id.icter)
        {
            finish();
            Intent newActivity = new Intent(this,Icter.class);
            startActivity(newActivity);

        }else if(id == R.id.aboutus)
        {
            ShowAboutUs();
        }
        else if(id == R.id.exit)
        {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkFile()
    {
        String dir = String.valueOf(getFilesDir())+"/db1.sqlite";// sqlite file path
        File fl = new File(dir);
        if (fl.exists()) {

            try {
                SQLiteDatabase db = SQLiteDatabase.openDatabase(dir, null, SQLiteDatabase.OPEN_READONLY);
                Cursor c = db.rawQuery("select count(Title) from info;", null);
                if (c.moveToFirst()) {
                    int number = Integer.valueOf(c.getString(0));
                    final String[] list = new String[number];
                    c = db.rawQuery("select Title from info;", null);
                    if (c.moveToFirst()) {
                        int i = 0;
                        do {
                            list[i] = c.getString(0);
                            mLinked.add(c.getString(0));
                            i++;
                        } while (c.moveToNext());
                        db.close();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Search(list);

                                final MyListAdaptor MLA = new MyListAdaptor(NeoFax.this, mLinked);
                                lv.setAdapter(MLA);
                                lv.setFastScrollEnabled(true);
                                lv.setBackgroundColor(Color.BLACK);
                                lv.setDividerHeight(8);

                                lv.setOnItemClickListener(new OnItemClickListener() {
                                    public void onItemClick(AdapterView<?> parent, View view,
                                                            int position, long id) {

                                        Intent goToDrug = new Intent(NeoFax.this,ShowDrugs.class);
                                        goToDrug.putExtra("name",mLinked.get(position));
                                        goToDrug.putExtra("class","NEO");
                                        startActivity(goToDrug);

                                    }
                                });

                                //loadDrugs(list);

                                LinearLayout indexLayout = (LinearLayout) findViewById(R.id.side_index);

                                TextView textView;
                                String[] aa = (String[]) MLA.getSections();
                                for (int i = 0; i<aa.length ; i++) {
                                    textView = (TextView) getLayoutInflater().inflate(
                                            R.layout.side_index_item, null);
                                    textView.setText(aa[i]);

                                    final int j=i;
                                    textView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            lv.setSelection(MLA.getPositionForSection(j));
                                        }
                                    });

                                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    layoutParams.weight=1;
                                    indexLayout.addView(textView,layoutParams);

                                }
                            }
                        });

                    }
                }
            }
            catch(Exception e)
            {
                ShowRetryDialogBox();
            }
        }
        else
        {
            ShowRetryDialogBox();
        }
    }


    private void Search(String[] list)
    {
        text=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        text.setAdapter(adapter);
        text.setThreshold(1);
        text.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent goToDrug = new Intent(NeoFax.this,ShowDrugs.class);
                goToDrug.putExtra("name",text.getText().toString());
                goToDrug.putExtra("class","NEO");
                startActivity(goToDrug);
            }
        });
    }

    public Boolean chk()
    {
        ConnectivityManager cManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cManager.getActiveNetworkInfo();
        if(nInfo!=null && nInfo.isConnected())
        {
            return true;
        }
        else
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(NeoFax.this, "شما به اینترنت وصل نیستید!", Toast.LENGTH_LONG).show();
                }
            });

            return false;
        }
    }

    private void Drugs(final String[] list)
    {
        ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.drugList);
        LinearLayout ll = (LinearLayout) cl.findViewById(R.id.List);

        for(int i=0;i< list.length;i++)
        {
            TextView tx = new TextView(this);
            tx.setText(list[i]);
            tx.setTextSize(20);
            tx.setPadding(30,20,0,30);
            tx.setBackgroundResource(R.drawable.textwraper);
            tx.setTextColor(Color.BLACK);
            final int j = i;
            tx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent goToDrug = new Intent(NeoFax.this,ShowDrugs.class);
                    goToDrug.putExtra("name",list[j]);
                    goToDrug.putExtra("class","NEO");
                    startActivity(goToDrug);
                }
            });
            ll.addView(tx);
        }

    }

    private void ShowRetryDialogBox()
    {
        AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        imageDialog.setMessage("پایگاه داده دانلود نشد. آیا می خواهید دوباره امتحان کنید؟");
        imageDialog.setPositiveButton("خروج", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        imageDialog.setNegativeButton("تلاش مجدد",new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Connection().execute();
            }

    });
        imageDialog.create();
        imageDialog.show();

    }

    private void CheckDBVersion()
    {
        try {
            //File f = new File(String.valueOf(getFilesDir()) + "/db1.sqlite");
            String mob = getMD5Checksum(String.valueOf(getFilesDir()) + "/db1.sqlite");
            if(dbChecksum.length() != 0)
            {
                if(!mob.equals(dbChecksum))
                {
                    showUpdateDialog();
                }
                else
                {
                    //checkFile();
                }
            }
        }catch(Exception e)
        {

        }


    }


    private void showUpdateDialog()
    {
        AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        imageDialog.setMessage("پایگاه داده بروز نیست. آیا می خواهید پایگاه داده جدید را دانلود کنید؟");
        imageDialog.setPositiveButton("خروج", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        imageDialog.setNegativeButton("بله",new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Connection().execute();
            }

        });
        imageDialog.create();
        imageDialog.show();

    }


    public static byte[] createChecksum(String filename) throws Exception {
        InputStream fis =  new FileInputStream(filename);

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;

        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);

        fis.close();
        return complete.digest();
    }

    // see this How-to for a faster way to convert
    // a byte array to a HEX string
    public static String getMD5Checksum(String filename) throws Exception {
        byte[] b = createChecksum(filename);
        String result = "";

        for (int i=0; i < b.length; i++) {
            result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
    }





    private void getChecksum()
    {
        BufferedReader reader=null;
        try
        {
            // Defined URL  where to send data
            URL url = new URL("http://pudica.ir/Behdasht/getChecksum.php");
            // Send POST data request
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while((line = reader.readLine()) != null)
            {
                // Append server response in string
                sb.append(line);
            }


            dbChecksum = sb.toString();

        }
        catch(Exception ex)
        {
            Log.e("connection Error", "Error: " + ex.toString());
        }
        finally
        {
            try
            {
                reader.close();
            }

            catch(Exception ex) {
                Log.e("Buffer Error", "Error: " + ex.toString());
            }
        }
    }




    private class Connection extends AsyncTask {
        ProgressDialog dialog;
        @Override
        protected Object doInBackground(Object... arg0)
        {
            try {
                loadDB();
            }
            catch(Exception e)
            {
                Log.e("ERROR ---->",e.toString());
            }
            return null;
        }

        //Download DB
        public void loadDB() throws UnsupportedEncodingException
        {
            try {
                URL url = new URL("http://decoanit.ir/neofax.sqlite");
                URLConnection conection = url.openConnection();
                conection.connect();

                final int lenghtOfFile = conection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file
                OutputStream output = new FileOutputStream(String.valueOf(getFilesDir())+"/db1.sqlite");

                byte data[] = new byte[1024];
                int count =0;
                long total = 0;
                while ((count = input.read(data)) != -1)
                {
                    total += count;
                    final long finalTotal = total;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.setProgress ((int) ((finalTotal * 100) / lenghtOfFile));
                        }
                    });

                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error--------> ", e.getMessage());
            }
        }


        @Override
        protected void onPreExecute()
        {
            dialog = new ProgressDialog(NeoFax.this);
            dialog.setMessage("در حال دانلود پایگاه داده. لطفا شکیبا باشید...");
            dialog.setIndeterminate(false);
            dialog.setMax(100);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setCancelable(false);
            dialog.setProgressNumberFormat(null);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Object arg0)
        {
            if(dialog.isShowing()) {
                dialog.dismiss();
            }
            checkFile();
        }

    }



    private class getCheckSum extends AsyncTask {
        ProgressDialog pd;
        @Override
        protected Object doInBackground(Object... arg0)
        {
            if(chk()) {
                try {
                    getChecksum();
                } catch (Exception e) {
                    Log.e("ERROR ---->", e.toString());
                }
            }

            CheckDBVersion();
            return null;
        }


        @Override
        protected void onPreExecute()
        {
            pd = ProgressDialog.show(NeoFax.this, null , "در حال آماده سازی برنامه...");
            //timerDelayRemoveDialog(10000,pd);
        }

        @Override
        protected void onPostExecute(Object arg0)
        {
            pd.dismiss();
        }

    }

    public void timerDelayRemoveDialog(long time, final Dialog d){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                d.dismiss();
            }
        }, time);
    }


//    private class checkInternet extends AsyncTask {
//        @Override
//        protected Object doInBackground(Object... arg0)
//        {
//            try {
//               connection =  hasActiveInternetConnection();
//            }
//            catch(Exception e)
//            {
//                Log.e("ERROR ---->",e.toString());
//            }
//            return null;
//        }
//
//
//        @Override
//        protected void onPreExecute()
//        {
//        }
//
//        @Override
//        protected void onPostExecute(Object arg0)
//        {
//            logicUnit();
//        }
//
//    }


    public static boolean hasActiveInternetConnection() {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setReadTimeout(300);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            }catch (IOException e) {
                Log.e("Error connection", e.getMessage());
                return false;
            }

    }

    public void showFavourite(View view)
    {
        Intent newActivity = new Intent(this,ShowFavourite.class);
        newActivity.putExtra("class","NEOFAX");
        startActivity(newActivity);
    }

    private void logicUnit()
    {
        File db = new File(String.valueOf(getFilesDir())+"/db1.sqlite");

        if(db.exists())
        {
            checkFile();
            new getCheckSum().execute();
        }else
        {
            if(chk())
            {
                new Connection().execute();
            }else
            {
                ShowRetryDialogBox();
            }
        }
    }

    private void showFeedBack()
    {
        AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        LayoutInflater lf = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        final View container =  lf.inflate(R.layout.feedback,null);
        imageDialog.setView(container);
        imageDialog.setPositiveButton("خروج", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        imageDialog.setNegativeButton("ارسال",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                subject = ((TextView) container.findViewById(R.id.subject)).getText().toString();
                message = ((TextView) container.findViewById(R.id.content)).getText().toString();

                if(subject.length() == 0)
                {
                    subject="khali";
                }

                if(message.length() != 0)
                {
                    new NeoFax.SendFeedBack().execute();
                }
                else
                {
                    Toast.makeText(NeoFax.this,"لطفابخش متن را خالی نگذارید!",Toast.LENGTH_LONG).show();
                    showFeedBack();
                }

            }
        });
        imageDialog.create();
        imageDialog.show();
    }

    private void sendMail()
    {
        if(connection)
        {
            BufferedReader reader=null;
            try
            {
                String data = "key="+ URLEncoder.encode("mhn","UTF-8")+"&msg="+URLEncoder.encode(message,"UTF-8")+
                        "&sbj="+URLEncoder.encode(subject,"UTF-8");
                // Defined URL  where to send data
                URL url = new URL("http://pudica.ir/Behdasht/em.php");
                // Send POST data request
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write( data );
                wr.flush();
                // Get the server response

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while((line = reader.readLine()) != null)
                {
                    // Append server response in string
                    sb.append(line + "\n");
                }


                res = sb.toString();
            }
            catch(Exception ex)
            {
                Log.e("connection Error", "Error: " + ex.toString());
            }
            finally
            {
                try
                {
                    reader.close();
                }

                catch(Exception ex) {
                    Log.e("Buffer Error", "Error: " + ex.toString());
                }
            }
        }
    }
    private class SendFeedBack extends AsyncTask {
        @Override
        protected Object doInBackground(Object... arg0)
        {
            try {
                sendMail();
            }
            catch(Exception e)
            {
                Log.e("ERROR ---->",e.toString());
            }
            return null;
        }


        @Override
        protected void onPreExecute()
        {
        }

        @Override
        protected void onPostExecute(Object arg0)
        {
            tmp();
        }

    }

    private void tmp()
    {
        try {
            if (res.contains("200")) {
                Toast.makeText(this, "نظر شما با موفقیت ارسال شد.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "متاسفانه نظر شما ارسال نشد.", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, "متاسفانه نظر شما ارسال نشد.", Toast.LENGTH_LONG).show();
        }
    }

    private void ShowAboutUs()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        String a="کاری از شرکت رایا ایده پردازان کارن";
        String b="(محمد حسین نوریان - مجتبی ولی زاده)";
        String c="به سفارش اداره ی سلامت نوزادان";
        String d="(وزارت بهداشت، درمان و آموزش کشور)";
        String e="\n";
        alertDialog.setMessage(a+e+b+e+e+c+e+d);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


    static class MyListAdaptor extends ArrayAdapter<String> implements SectionIndexer {

        HashMap<String, Integer> alphaIndexer;
        String[] sections;

        public MyListAdaptor(Context context, LinkedList<String> items) {
            super(context, R.layout.list_item, items);///////////////////////////////

            alphaIndexer = new HashMap<String, Integer>();
            int size = items.size();

            for (int x = 0; x < size; x++) {
                String s = items.get(x);
                // get the first letter of the store
                String ch = s.substring(0, 1);
                // convert to uppercase otherwise lowercase a -z will be sorted
                // after upper A-Z
                ch = ch.toUpperCase();
                // put only if the key does not exist
                if (!alphaIndexer.containsKey(ch))
                    alphaIndexer.put(ch, x);
            }

            Set<String> sectionLetters = alphaIndexer.keySet();
            // create a list from the set to sort
            ArrayList<String> sectionList = new ArrayList<String>(
                    sectionLetters);
            Collections.sort(sectionList);
            sections = new String[sectionList.size()];
            sections = sectionList.toArray(sections);
        }

        @Override
        public int getPositionForSection(int section) {
            return alphaIndexer.get(sections[section]);
        }

        @Override
        public int getSectionForPosition(int position) {
            return 0;
        }

        @Override
        public Object[] getSections() {
            return sections;
        }
    }

}
