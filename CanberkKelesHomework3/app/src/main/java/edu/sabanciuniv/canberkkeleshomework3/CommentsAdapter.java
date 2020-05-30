package edu.sabanciuniv.canberkkeleshomework3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>{

    List<CommentItem> comments;
    Context context;
    CommentsAdapter.RecCommentsListener listener;

    public CommentsAdapter(List<CommentItem> comments, Context context,CommentsAdapter.RecCommentsListener listener) {
        this.comments = comments;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.comments_row_layout,parent,false);
        CommentsViewHolder holder = new CommentsViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder holder, int position) {

        holder.txtcomment.setText(comments.get(position).getMessage());
        holder.txtcommentowner.setText(comments.get(position).getName());


    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public interface RecCommentsListener{

    }

    class CommentsViewHolder extends RecyclerView.ViewHolder{

        TextView txtcommentowner;
        TextView txtcomment;
        ConstraintLayout root;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            txtcommentowner = itemView.findViewById(R.id.txtcommentowner);
            txtcomment = itemView.findViewById(R.id.txtcomment);
            root = itemView.findViewById(R.id.comment_container);
        }

    }
}
