package com.hdmovies.livetvchannels.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hdmovies.livetvchannels.item.Notification;
import com.hdmovies.livetvchannels.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.MyViewHolder> {

    private List<Notification> notificationList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, date;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            date = (TextView) view.findViewById(R.id.date);
        }
    }


    public NotificationsAdapter(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.title.setText(notification.getTitle());

        String dt = notification.getLog_entdate();  // Start date current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(dt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        String output = sdf1.format(c.getTime());
        holder.date.setText(output);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }
}
