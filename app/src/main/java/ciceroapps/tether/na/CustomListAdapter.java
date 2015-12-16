package ciceroapps.tether.na;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.firebase.androidchat.R;

import java.util.ArrayList;

/**
 * Created by Antonio on 11/29/2015.
 */
public class CustomListAdapter extends BaseAdapter {
    private ArrayList<MeetingItem> listData;
    private LayoutInflater layoutInflater;

    public CustomListAdapter(Context aContext, ArrayList<MeetingItem> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_row_layout, null);
            holder = new ViewHolder();
            holder.titleView = (TextView) convertView.findViewById(R.id.meeting_title);
            holder.dayView = (TextView) convertView.findViewById(R.id.meeting_day);
            holder.addressView = (TextView) convertView.findViewById(R.id.meeting_address);
            holder.cityView = (TextView) convertView.findViewById(R.id.meeting_city);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MeetingItem item = listData.get(position);
        holder.titleView.setText(item.getName());
        holder.dayView.setText(item.getDay()+" - "+item.getTime());
        holder.addressView.setText(item.getLocation_street());
        holder.cityView.setText(item.getLocation_city()+", "+item.getState());
        System.out.println(item.toString());
        return convertView;
    }

    static class ViewHolder {
        TextView titleView;
        TextView dayView;
        TextView addressView;
        TextView cityView;
    }
}
