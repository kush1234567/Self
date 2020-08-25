package com.bawp.self.ui;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bawp.self.R;
import com.bawp.self.model.journalclass;
import com.squareup.picasso.Picasso;

import java.util.List;

public class journalrecyclerviewAdapter extends RecyclerView.Adapter<journalrecyclerviewAdapter.viewHolder> {
    private Context context;
    private List<journalclass> journalclasses;

    public journalrecyclerviewAdapter(Context context, List<journalclass> journalclasses) {
        this.context = context;
        this.journalclasses = journalclasses;
    }

    @NonNull
    @Override
    public journalrecyclerviewAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context)
                .inflate(R.layout.journal_row,parent,false);
        return new viewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull journalrecyclerviewAdapter.viewHolder holder, int position) {
        journalclass journalclass=journalclasses.get(position);
        String imageUrl;
        holder.title.setText(journalclass.getTile());
        holder.thoughts.setText(journalclass.getThought());
        //holder.name.setText(journalclass.getUsername());
        imageUrl=journalclass.getImageUrl();


        //1 hour ago..
        String timeago= (String) DateUtils.getRelativeTimeSpanString(journalclass.getTimeadded().getSeconds()*1000);
        holder.dateAdded.setText(timeago);


        //use picasso library to download and show image
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.image_one)//if error than it shows this image
                .fit()
                .into(holder.image);

    }

    @Override
    public int getItemCount() {

        return journalclasses.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        public TextView title,thoughts,dateAdded,name;
        public ImageView image;
        String userId;
        String username;
        public viewHolder(@NonNull View itemView,Context ctx) {
            super(itemView);
            context=ctx;
            title=itemView.findViewById(R.id.journal_title_list);
            thoughts=itemView.findViewById(R.id.journal_thought_list);
            dateAdded=itemView.findViewById(R.id.journal_timestamp_list);
            image=itemView.findViewById(R.id.journal_image_list);
            //name=itemView.findViewById(R.id.username);
        }
    }
}