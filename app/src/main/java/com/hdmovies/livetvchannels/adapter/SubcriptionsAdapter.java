package com.hdmovies.livetvchannels.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hdmovies.livetvchannels.config.config;
import com.hdmovies.livetvchannels.item.Subcription;
import com.hdmovies.livetvchannels.R;

import java.util.List;

public class SubcriptionsAdapter extends RecyclerView.Adapter<SubcriptionsAdapter.MyViewHolder> {

    private List<Subcription> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            genre = (TextView) view.findViewById(R.id.genre);
            year = (TextView) view.findViewById(R.id.year);
        }
    }


    public SubcriptionsAdapter(List<Subcription> moviesList) {
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subcription_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Subcription subcription = moviesList.get(position);
        holder.title.setText(subcription.getTitle());
        holder.genre.setText(config.currency + " "+subcription.getPrice());
        holder.year.setText("for " + subcription.getDays()+" Days");
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
