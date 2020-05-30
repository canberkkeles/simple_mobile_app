package edu.sabanciuniv.canberkkeleshomework3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder>{

    List<NewsItem> newsItems;
    Context context;
    NewsItemClickedListener listener;

    public NewsAdapter(List<NewsItem> newsItems, Context context,NewsItemClickedListener listener) {
        this.newsItems = newsItems;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.news_row_layout,parent,false);

        return new NewsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, final int position) {

        holder.txtdate.setText(new SimpleDateFormat("dd/mm/yyyy").format(newsItems.get(position).getNewsDate()));
        holder.txttitle.setText(newsItems.get(position).getTitle());
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.newItemClicked(newsItems.get(position));

            }
        });

        if(newsItems.get(position).getBitmap() == null){
            new ImageDownloadTask(holder.imgnews).execute(newsItems.get(position));
        }
        else{
            holder.imgnews.setImageBitmap(newsItems.get(position).getBitmap());
        }

    }

    @Override
    public int getItemCount() {
        return newsItems.size();
    }

    public interface NewsItemClickedListener{
        public void newItemClicked(NewsItem selectedNewItem);
        }

    class NewsViewHolder extends RecyclerView.ViewHolder{

        ImageView imgnews;
        TextView txttitle;
        TextView txtdate;
        ConstraintLayout root;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);

            imgnews = itemView.findViewById(R.id.imgnews);
            txttitle = itemView.findViewById(R.id.txtlisttitle);
            txtdate = itemView.findViewById(R.id.txtlistdate);
            root = itemView.findViewById(R.id.container);
        }

    }
}
