package edu.sabanciuniv.canberkkeleshomework3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewsDetailActivity extends AppCompatActivity{

    NewsItem selectedNew;
    int selectedNewID;
    ImageView imgdetail;
    TextView txtdetailtitle;
    TextView txtdetaildate;
    TextView txtdetaildetails;
    ProgressDialog prgDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        imgdetail = findViewById(R.id.imgdetail);
        txtdetailtitle = findViewById(R.id.txtdetailtitle);
        txtdetaildate = findViewById(R.id.txtdetaildate);
        txtdetaildetails = findViewById(R.id.txtdetaildetails);

        selectedNew = (NewsItem) getIntent().getSerializableExtra("selectednew");
        selectedNewID  = selectedNew.getId();


        //imgdetail.setImageResource(selectedNew.getimgId());
        //txtdetailtitle.setText(selectedNew.getTitle());
        //txtdetaildate.setText(new SimpleDateFormat("dd/mm/yyyy").format(selectedNew.getNewsDate()));
        //txtdetaildetails.setText(selectedNew.getText());
        GetNewsByID task = new GetNewsByID();
        task.execute("http://94.138.207.51:8080/NewsApp/service/news/getnewsbyid/" + String.valueOf(selectedNewID));
        getSupportActionBar().setTitle("News Details");

    }
    class GetNewsByID extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute() {
            prgDialog = new ProgressDialog(NewsDetailActivity.this);
            prgDialog.setTitle("Loading");
            prgDialog.setMessage("Please wait...");
            prgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prgDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String urlStr = strings[0];
            StringBuilder buffer = new StringBuilder();
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line = "";
                while((line = reader.readLine()) !=null){
                    buffer.append(line);
                }


            } catch (MalformedURLException e) {
                Log.e("DEV",e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("DEV",e.getMessage());
                e.printStackTrace();
            }

            return buffer.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject obj = new JSONObject(s);
                if(obj.getInt("serviceMessageCode") == 1){
                    JSONArray arr = obj.getJSONArray("items");

                        JSONObject current = (JSONObject) arr.get(0);
                        long date = current.getLong("date");
                        Date objDate = new Date(date);
                        NewsItem item = new NewsItem(current.getInt("id"),current.getString("title"),current.getString("text"),current.getString("image"),objDate);
                        txtdetaildetails.setText(item.getText());
                        txtdetailtitle.setText(item.getTitle());
                        txtdetaildate.setText(new SimpleDateFormat("dd/mm/yyyy").format(item.getNewsDate()));

                        new ImageDownloadTask(imgdetail).execute(item);


                }
                else{

                }

                prgDialog.dismiss();
            } catch (JSONException e) {
                Log.e("DEV",e.getMessage());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.mn_comment){
            Intent i = new Intent(this,CommentActivity.class);
            i.putExtra("selectednew",selectedNew);
            startActivity(i);
        }

        if(item.getItemId() == R.id.mn_back){
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
        }

        return true;
    }
}
