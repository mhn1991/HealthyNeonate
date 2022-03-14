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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.system.Os;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DPAL extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    AutoCompleteTextView text;
    private boolean connection;
    private String dbChecksum;
    private String subject;
    private String message;
    private String res;
    private ProgressDialog pd;
    private ListView lv;
    LinkedList<String> mLinked = new LinkedList<String>();
    private Drug[] drugs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dpal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        navigationView.setItemIconTintList(null);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //listView = (ListView) findViewById(R.id.listView);

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
        getMenuInflater().inflate(R.menu.dpal, menu);
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
        String dir = String.valueOf(getFilesDir())+"/db2.sqlite";// sqlite file path
        File fl = new File(dir);
        if (fl.exists())
        {
            try {
                SQLiteDatabase db = SQLiteDatabase.openDatabase(dir, null, SQLiteDatabase.OPEN_READONLY);
                Cursor c = db.rawQuery("select count(Title) from book;", null);
                if (c.moveToFirst()) {
                    int number = Integer.valueOf(c.getString(0));
                    final String[] list1 = new String[number];
                    String[] list2 = new String[number];
                    String[] list3 = new String[number];
                    String[] list4 = new String[number];
                    c = db.rawQuery("select Title from book;", null);
                    if (c.moveToFirst()) {
                        int i = 0;
                        drugs = new Drug[list1.length];
                        do {
                            //==================================================================

                            mLinked.add(c.getString(0));

                            int firstofsecoundtitle =0;
                            while(c.getString(0).charAt(firstofsecoundtitle)!='\n')
                            {
                                firstofsecoundtitle++;
                            }
                            list1[i]=c.getString(0).substring(0,firstofsecoundtitle);
                            if (list1[i].startsWith("?")){
                                list1[i]="B"+list1[i].substring(1);
                            }
                            //mLinked.add(list1[i]);


                            int firstofrecommendation = c.getString(0).indexOf("RECOMMENDATION:");
                            int firstofrecommendation2 = c.getString(0).indexOf("RECOMMENDATION:",firstofrecommendation+5);
                            while(c.getString(0).charAt(firstofrecommendation)!='\n')
                            {
                                firstofrecommendation--;
                            }
//
                            list2[i]=c.getString(0).substring(firstofsecoundtitle+1,firstofrecommendation);

//
                            while(c.getString(0).charAt(firstofrecommendation2)!='\n')
                            {
                                firstofrecommendation2--;
                            }
//                            Log.e(String.valueOf(i),c.getString(0));


                            list3[i]=c.getString(0).substring(firstofrecommendation+1,firstofrecommendation2-1);
                            list3[i]=list3[i].replaceAll("\n","");
//
                            list4[i]=c.getString(0).substring(firstofrecommendation2+1);
                            list4[i]=list4[i].replaceAll("\n","");


                            Drug d = new Drug(list1[i], list2[i], list3[i], list4[i]);
                            drugs[i] = d;
                            //==================================================================

                            i++;
                        } while (c.moveToNext());
                        db.close();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Search(list1);
                                loadDrugs(drugs);
                            }
                        });
                    }
                }
            }
            catch(Exception e)
            {
                Log.d("DPAL", "crash");
                e.printStackTrace();

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
        text=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        text.setAdapter(adapter);
        text.setThreshold(1);
        text.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent goToDrug = new Intent(DPAL.this,ShowDrugs.class);
                goToDrug.putExtra("name",text.getText().toString());
                goToDrug.putExtra("class","DPAL");
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
                    Toast.makeText(DPAL.this, "شما به اینترنت وصل نیستید!", Toast.LENGTH_LONG).show();
                }
            });

            return false;
        }
    }

    private void loadDrugs(final Drug[] drugs)
    {
        final DrugAdapter adapter = new DrugAdapter(this, drugs);
        lv.setFastScrollEnabled(true);
        lv.setBackgroundColor(Color.BLACK);
        lv.setDividerHeight(8);
        lv.setAdapter(adapter);

        LinearLayout indexLayout = (LinearLayout) findViewById(R.id.side_index);

        TextView textView;
        String[] aa = (String[]) adapter.getSections();
        for (int i = 0; i<aa.length ; i++) {
            textView = (TextView) getLayoutInflater().inflate(
                    R.layout.side_index_item, null);
            textView.setText(aa[i]);

            final int j=i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lv.setSelection(adapter.getPositionForSection(j));
                }
            });

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.weight=1;
            indexLayout.addView(textView,layoutParams);

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
                    new SendFeedBack().execute();
                }
                else
                {
                    Toast.makeText(DPAL.this,"لطفابخش متن را خالی نگذارید!",Toast.LENGTH_LONG).show();
                    showFeedBack();
                }

            }
        });
        imageDialog.create();
        imageDialog.show();
    }

    private void sendMail()
    {
        if(connection == true)
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
    private void ShowRetryDialogBox()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder imageDialog = new AlertDialog.Builder(DPAL.this);
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
        });
    }

    private void CheckDBVersion()
    {
        try {
            //File f = new File(String.valueOf(getFilesDir()) + "/db2.sqlite");
            String mob = getMD5Checksum(String.valueOf(getFilesDir()) + "/db2.sqlite");
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
            URL url = new URL("http://pudica.ir/Behdasht/getDPAL.php");
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
                URL url = new URL("http://pudica.ir/Behdasht/DPAL.sqlite3");
                URLConnection conection = url.openConnection();
                conection.connect();

                final int lenghtOfFile = conection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file
                OutputStream output = new FileOutputStream(String.valueOf(getFilesDir())+"/db2.sqlite");

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
            dialog = new ProgressDialog(DPAL.this);
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
            pd = ProgressDialog.show(DPAL.this, null , "در حال آماده سازی برنامه...");
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


    public void showFavourite(View view)
    {
        Intent newActivity = new Intent(this,ShowFavourite.class);
        newActivity.putExtra("class","DPAL");
        startActivity(newActivity);
    }

    private void logicUnit()
    {
        File db = new File(String.valueOf(getFilesDir())+"/db2.sqlite");
//        new checkInternet().execute();

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
        try
        {
            if (res.contains("200"))
            {
                Toast.makeText(this, "نظر شما با موفقیت ارسال شد.", Toast.LENGTH_LONG).show();
            }else{
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
}
