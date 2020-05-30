package edu.sabanciuniv.canberkkeleshomework3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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

public class MainActivity extends AppCompatActivity {

    RecyclerView newsRecView;
    Spinner spnewscategory;
    List<NewsItem> data;
    ProgressDialog prgDialog;
    NewsAdapter adp;
    ArrayAdapter<Category> adpCat;
    List<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spnewscategory = findViewById(R.id.spnewscategory);


        categories = new ArrayList<>();
        adpCat = new ArrayAdapter<Category>(this,android.R.layout.simple_list_item_1,categories);
        spnewscategory.setAdapter(adpCat);
        GetCategoriesTask getCategory = new GetCategoriesTask();
        getCategory.execute("http://94.138.207.51:8080/NewsApp/service/news/getallnewscategories");
        spnewscategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category selectedCat = (Category)spnewscategory.getSelectedItem();
                int idCat = selectedCat.getId();
                NewsTask task = new NewsTask();
                if(idCat != 0)
                    task.execute("http://94.138.207.51:8080/NewsApp/service/news/getbycategoryid/" + String.valueOf(idCat));
                else
                    task.execute("http://94.138.207.51:8080/NewsApp/service/news/getall");

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        newsRecView = findViewById(R.id.newsrec);
        data = new ArrayList<>();
        adp = new NewsAdapter(data, this, new NewsAdapter.NewsItemClickedListener() {
            @Override
            public void newItemClicked(NewsItem selectedNewItem) {
                Intent i = new Intent(MainActivity.this,NewsDetailActivity.class);
                i.putExtra("selectednew",selectedNewItem);
                startActivity(i);
            }
        });
        newsRecView.setLayoutManager(new LinearLayoutManager(this));
        newsRecView.setAdapter(adp);
        NewsTask task = new NewsTask();
        task.execute("http://94.138.207.51:8080/NewsApp/service/news/getall");
        setTitle("News");


    }


    class GetCategoriesTask extends AsyncTask<String,Void,String>{
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
            categories.clear();
            categories.add(new Category("All",0));
            try {
                JSONObject obj = new JSONObject(s);
                if(obj.getInt("serviceMessageCode") == 1){
                    JSONArray arr = obj.getJSONArray("items");


                    for(int i = 0 ; i < arr.length() ; i++){
                        JSONObject current = (JSONObject) arr.get(i);
                        Category item = new Category(current.getString("name"),current.getInt("id"));
                        categories.add(item);

                    }
                }
                else{

                }

                adpCat.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e("DEV",e.getMessage());
            }
        }
    }

    class NewsTask extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute() {
            prgDialog = new ProgressDialog(MainActivity.this);
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
            data.clear();
            try {
                JSONObject obj = new JSONObject(s);
                if(obj.getInt("serviceMessageCode") == 1){
                    JSONArray arr = obj.getJSONArray("items");


                    for(int i = 0 ; i < arr.length() ; i++){
                        JSONObject current = (JSONObject) arr.get(i);
                        long date = current.getLong("date");
                        Date objDate = new Date(date);
                        NewsItem item = new NewsItem(current.getInt("id"),current.getString("title"),current.getString("text"),current.getString("image"),objDate);
                        data.add(item);

                    }
                }
                else{

                }

                adp.notifyDataSetChanged();
                prgDialog.dismiss();
            } catch (JSONException e) {
                Log.e("DEV",e.getMessage());
            }
        }
    }
}
