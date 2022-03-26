package com.hdmovies.livetvchannels.fragment;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hdmovies.livetvchannels.adapter.AllVideoAdapter;
import com.hdmovies.livetvchannels.favorite.DatabaseHelper;
import com.hdmovies.livetvchannels.item.ItemLatest;
import com.hdmovies.livetvchannels.util.ItemOffsetDecoration;
import com.hdmovies.livetvchannels.R;

import java.util.ArrayList;


public class FavoriteFragment extends Fragment {

    ArrayList<ItemLatest> mListItem;
    public RecyclerView recyclerView;
    AllVideoAdapter allVideoAdapter;
    TextView textView;
    DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorite, container, false);

        mListItem = new ArrayList<>();
        databaseHelper = new DatabaseHelper(getActivity());

        recyclerView = rootView.findViewById(R.id.rv_video);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(requireActivity(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        textView = rootView.findViewById(R.id.txt_no);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mListItem = databaseHelper.getFavourite();
        displayData();
    }

    private void displayData() {

        if (getActivity() != null) {
            allVideoAdapter = new AllVideoAdapter(getActivity(), mListItem);
            recyclerView.setAdapter(allVideoAdapter);

            if (allVideoAdapter.getItemCount() == 0) {
                textView.setVisibility(View.VISIBLE);
            } else {
                textView.setVisibility(View.GONE);
            }
        }
    }
}
