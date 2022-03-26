package com.hdmovies.livetvchannels.adapter;

import android.content.Context;
import android.graphics.Movie;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hdmovies.livetvchannels.fragment.CategoryListFragment;
import com.hdmovies.livetvchannels.item.Hash;
import com.hdmovies.livetvchannels.item.ItemLatest;
import com.hdmovies.livetvchannels.util.Constant;
import com.hdmovies.livetvchannels.MainActivity;
import com.hdmovies.livetvchannels.R;

import java.util.ArrayList;
import java.util.List;

public class HashAdapter extends RecyclerView.Adapter<HashAdapter.MyViewHolder> {

    private List<Hash> hashList;

    private List<Movie> movieList = new ArrayList<>();

    private HomeAllAdapter mAdapter;

    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre, videocount;
        public Button btn_cat_video_lis;
        public RecyclerView recyclerView;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.txt_cat_video_lis);
//            genre = (TextView) view.findViewById(R.id.genre);
//            year = (TextView) view.findViewById(R.id.year);
            videocount = (TextView) view.findViewById(R.id.txt_cat_video_no_lis);
            btn_cat_video_lis = (Button) view.findViewById(R.id.btn_cat_video_lis);
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_movie);
        }
    }


    public HashAdapter(Context context, List<Hash> hashList) {
        this.context = context;
        this.hashList = hashList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hash_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Hash hash = hashList.get(position);
        holder.title.setText(hash.getTitle());
//        System.out.println("Rajan_viewcount"+hash.getViewcount());
        holder.videocount.setText(hash.getVideocount() + " Videos");
//        holder.genre.setText(hash.getGenre());
//        holder.year.setText(hash.getYear());

        final ArrayList<ItemLatest> movieArrayList = hash.getMovieArrayList();

        mAdapter = new HomeAllAdapter(context, movieArrayList);

        holder.recyclerView.setHasFixedSize(true);

        // vertical RecyclerView
        // keep movie_list_row.xml width to `match_parent`
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);

        // vertical RecyclerView
        // keep movie_list_row.xml width to `match_parent`
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 1, LinearLayoutManager.HORIZONTAL, false);

        // horizontal RecyclerView
        // keep movie_list_row.xml width to `wrap_content`
        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        holder.recyclerView.setLayoutManager(mLayoutManager);

        // adding inbuilt divider line
//        holder.recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));

        // adding custom divider line with padding 16dp
        // holder.recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.HORIZONTAL, 16));
        holder.recyclerView.setItemAnimator(new DefaultItemAnimator());

        holder.recyclerView.setAdapter(mAdapter);

//        // row click listenerMyDividerItemDecoration
//        holder.recyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, holder.recyclerView, new RecyclerTouchListener.ClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//
//            }
//        }));

        holder.btn_cat_video_lis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Rajan_hash.getId()"+hash.getId());
                Constant.CATEGORY_IDD = hash.getId();
                Constant.CATEGORY_TITLEE = hash.getTitle();

                AppCompatActivity activity = (AppCompatActivity) v.getContext();

                CategoryListFragment categoryListFragment = new CategoryListFragment();
                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();

                Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.Container);
                fragmentTransaction.hide(currentFragment);
                fragmentTransaction.add(R.id.Container, categoryListFragment, Constant.CATEGORY_TITLEE);
                fragmentTransaction.addToBackStack(Constant.CATEGORY_TITLEE);
                fragmentTransaction.commit();
                ((MainActivity)context).setToolbarTitle(Constant.CATEGORY_TITLEE);


//                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, categoryListFragment).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return hashList.size();
    }
}
