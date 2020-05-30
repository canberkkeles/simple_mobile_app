package edu.sabanciuniv.canberkkeleshomework3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentActivity extends AppCompatActivity implements CommentsAdapter.RecCommentsListener {

    NewsItem selectedNew;
    RecyclerView recComment;
    int selectedNewID;
    ProgressDialog prgDialog;
    List<CommentItem> data;
    CommentsAdapter adp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        recComment = findViewById(R.id.reccomment);
        selectedNew = (NewsItem)getIntent().getSerializableExtra("selectednew");

        data = new ArrayList<>();
        adp = new CommentsAdapter(data,this,this);

        recComment.setLayoutManager(new LinearLayoutManager(this));
        recComment.setAdapter(adp);

        selectedNewID = selectedNew.getId();

        GetCommentsTask task = new GetCommentsTask();
        task.execute("http://94.138.207.51:8080/NewsApp/service/news/getcommentsbynewsid/" + String.valueOf(selectedNewID));
        setTitle("Comments");



    }

    class GetCommentsTask extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            prgDialog=new ProgressDialog(CommentActivity.this);
            prgDialog.setTitle("Loading");
            prgDialog.setMessage("Please Wait");
            prgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prgDialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {
            String urlStr=strings[0];
            StringBuilder buffer=new StringBuilder();
            try {
                URL url=new URL(urlStr);
                HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                BufferedReader reader= new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line="";
                while ((line=reader.readLine())!=null){
                    buffer.append(line);
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  buffer.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            data.clear();
            try {
                JSONObject obj=new JSONObject(s);

                if (obj.getInt("serviceMessageCode")==1){
                    JSONArray arr=obj.getJSONArray("items");

                    for (int i=0;i<arr.length();i++){
                        JSONObject current=(JSONObject) arr.get(i);


                        CommentItem item= new CommentItem(current.getInt("id"),
                                current.getString("name"),
                                current.getString("text")
                        );
                        data.add(item);




                    }


                }
                else {

                }
                adp.notifyDataSetChanged();
                prgDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.comments_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.mn_addcomment){
            Intent i = new Intent(this,PostComment.class);
            i.putExtra("selectednew",selectedNew);
            startActivity(i);
        }

        if(item.getItemId() == R.id.mn_backcomment){
            Intent i = new Intent(this,NewsDetailActivity.class);
            i.putExtra("selectednew",selectedNew);
            startActivity(i);
        }

        return true;
    }
}
