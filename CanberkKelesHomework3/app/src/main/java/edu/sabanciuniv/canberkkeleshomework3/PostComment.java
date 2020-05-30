package edu.sabanciuniv.canberkkeleshomework3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PostComment extends AppCompatActivity {

    EditText addcommentname;
    EditText addcommentmessage;
    Button btnpostcomment;
    NewsItem selectedNew;
    ProgressDialog prgDialog;

    String name;
    String comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comment);

        addcommentmessage = findViewById(R.id.addcommentmessage);
        addcommentname = findViewById(R.id.addcommentname);
        btnpostcomment = findViewById(R.id.btnpostcomment);
        selectedNew = (NewsItem)getIntent().getSerializableExtra("selectednew");
        setTitle("Post Comment");
    }

    public void postCommentClicked(View v){
        PostCommentTask task = new PostCommentTask();
        task.execute("http://94.138.207.51:8080/NewsApp/service/news/savecomment",addcommentname.getText().toString(),addcommentmessage.getText().toString(),String.valueOf(selectedNew.getId()));
    }

    class PostCommentTask extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            prgDialog = new ProgressDialog(PostComment.this);
            prgDialog.setTitle("Loading");
            prgDialog.setMessage("Please wait...");
            prgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prgDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String urlStr = strings[0];
            String name = strings[1];
            String text = strings[2];
            String id = strings[3];
            StringBuilder buffer = new StringBuilder();

            JSONObject obj = new JSONObject();
            try {

                obj.put("name",name);
                obj.put("text",text);
                obj.put("news_id",id);
            } catch (JSONException e) {
                e.printStackTrace();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(PostComment.this);
                alertDialog.setTitle("OUCH!");
                alertDialog.setMessage("An error has occured.Would you like to try again?");
                alertDialog.setPositiveButton("Yes",null);
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(PostComment.this,NewsDetailActivity.class);
                        i.putExtra("selectednew",selectedNew);
                        startActivity(i);
                    }
                });
                alertDialog.show();

                //ALERT DIALOG
            }

            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type","application/json");
                conn.connect();

                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.writeBytes(obj.toString());

                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);

                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return buffer.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            prgDialog.dismiss();
            try {
                JSONObject obj = new JSONObject(s);
                if(obj.getInt("serviceMessageCode") == 1){
                    Intent i = new Intent(PostComment.this,CommentActivity.class);
                    i.putExtra("selectednew",selectedNew);
                    startActivity(i);

                }
                else{
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(PostComment.this);
                    alertDialog.setTitle("OUCH!");
                    alertDialog.setMessage("An error has occured.Would you like to try again?");
                    alertDialog.setPositiveButton("Yes",null);
                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(PostComment.this,CommentActivity.class);
                            i.putExtra("selectednew",selectedNew);
                            startActivity(i);
                        }
                    });
                    alertDialog.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.postcomment_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.mn_post_back){
            Intent i = new Intent(this,CommentActivity.class);
            i.putExtra("selectednew",selectedNew);
            startActivity(i);
        }

        return true;
    }



}
