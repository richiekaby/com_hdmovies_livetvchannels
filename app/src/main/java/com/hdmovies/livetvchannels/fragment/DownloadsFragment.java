package com.hdmovies.livetvchannels.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hdmovies.livetvchannels.adapter.DownloadsAdapter;
import com.hdmovies.livetvchannels.item.ItemLatest;
import com.hdmovies.livetvchannels.util.Constant;
import com.hdmovies.livetvchannels.util.ItemOffsetDecoration;
import com.hdmovies.livetvchannels.MainActivity;
import com.hdmovies.livetvchannels.NewMessageEvent;
import com.hdmovies.livetvchannels.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

public class DownloadsFragment extends Fragment {

    //musical
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;

    // Declare variables
    public String[] FilePathStrings = new String[1000];
    public String[] FileNameStrings = new String[1000];;
    public File[] listFile = new File[1000];
    public GridView lvMain;
    File file;

    // new
    Context context;

    ArrayList<ItemLatest> mListItem;
    public RecyclerView recyclerView;
    DownloadsAdapter downloadsAdapter;
    TextView textView;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_video, container, false);

        context = ((MainActivity) requireActivity());

        mListItem = new ArrayList<>();
        ((MainActivity) requireActivity()).setToolbarTitle(getString(R.string.menu_download));
        progressBar = rootView.findViewById(R.id.progressBar);
        recyclerView = rootView.findViewById(R.id.rv_video);
        textView = rootView.findViewById(R.id.txt_no);
        recyclerView.setHasFixedSize(true);
        // Set the LazyAdapter to the GridView
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(requireActivity(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);

        if (ActivityCompat.checkSelfPermission((MainActivity) requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            new getAllVideo().execute(Constant.ALL_URL);
        } else {
            ActivityCompat.requestPermissions((MainActivity) requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
        }


        return rootView;
    }

    @SuppressLint("StaticFieldLeak")
    private class getAllVideo extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {

            // Inflate the layout for this fragment
            // Check for SD Card
            if (!Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                Toast.makeText(context, "Error...No SDCARD Found!", Toast.LENGTH_LONG)
                        .show();
            } else {
                // Locate the image folder in your SD Card

                file = new File(context.getFilesDir() + File.separator + getResources().getString(R.string.downloadfolder));

                final String outpath = context.getFilesDir() + File.separator + getResources().getString(R.string.downloadfolder) +File.separator;

                try {
                    //create output directory if it doesn't exist
                    File dir = new File (outpath);

                    if (!dir.exists())
                    {
                        dir.mkdirs();
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            if (file.isDirectory()) {
//			listFile = file.listFiles();

                final String[] okFileExtension;

                okFileExtension = new String[]{"mp4"};

                listFile = file.listFiles();

//                listFile = file.listFiles(new FileFilter(){
//                    public boolean accept(File file){
//                        for(String extension : okFileExtension){
//                            if(file.getName().endsWith(extension))
//                                return true;
//                        }
//                        return false;
//                    }
//                });

                try {
                    // Create a String array for FilePathStrings
                    FilePathStrings = new String[listFile.length];
                    // Create a String array for FileNameStrings
                    FileNameStrings = new String[listFile.length];

                    for (int i = 0; i < listFile.length; i++) {
                        // Get the path of the image file
                        FilePathStrings[i] = listFile[i].getAbsolutePath();
                        // Get the name image file
                        FileNameStrings[i] = listFile[i].getName();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return "result";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //error solved
            if(getActivity() == null)
                return;

            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            if (FilePathStrings.length < 1) {
                showToast(getString(R.string.no_data));
            } else {
                displayData();
            }
        }
    }

    private void displayData() {
        if (getActivity() != null) {
            downloadsAdapter = new DownloadsAdapter(getActivity(), FilePathStrings, FileNameStrings);
            recyclerView.setAdapter(downloadsAdapter);
        }
        if (downloadsAdapter.getItemCount() == 0) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    // This method will betextView called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewMessageEvent event) {
        Log.d("MainActivity", "From Fragment two : " + event.number);
        int number= event.number+1;

        try {
            if (ActivityCompat.checkSelfPermission((MainActivity) requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                new getAllVideo().execute(Constant.ALL_URL);
            } else {
                ActivityCompat.requestPermissions((MainActivity) requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).setToolbarTitle(getString(R.string.menu_video));
    }
}
