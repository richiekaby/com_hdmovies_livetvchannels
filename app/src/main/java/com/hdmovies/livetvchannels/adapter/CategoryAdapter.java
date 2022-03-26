package com.hdmovies.livetvchannels.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hdmovies.livetvchannels.item.ItemCategory;
import com.hdmovies.livetvchannels.R;

import java.util.ArrayList;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ItemRowHolder> {

    private ArrayList<ItemCategory> dataList;
    private Context mContext;

    public CategoryAdapter(Context context, ArrayList<ItemCategory> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cat_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemRowHolder holder, final int position) {
        final ItemCategory singleItem = dataList.get(position);

        holder.text.setText(singleItem.getCategoryName());
        Glide.with(mContext).load(singleItem.getCategoryImageUrl()).placeholder(R.drawable.loading).centerCrop().crossFade().diskCacheStrategy(DiskCacheStrategy.RESULT).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView text;
        LinearLayout lyt_parent;
        CardView card_view;

        private ItemRowHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
            card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
