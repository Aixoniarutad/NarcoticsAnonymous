package ciceroapps.tether.na;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


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
            convertView = layoutInflater.inflate(R.layout.meeting_item, null);
            holder = new ViewHolder();
            holder.placeView = (TextView) convertView.findViewById(R.id.meeting_place);
            holder.dayView = (TextView) convertView.findViewById(R.id.meeting_day);
            holder.addressView = (TextView) convertView.findViewById(R.id.meeting_place);
            holder.cityView = (TextView) convertView.findViewById(R.id.meeting_time);
            holder.distanceView = (TextView) convertView.findViewById(R.id.meeting_distance);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MeetingItem item = listData.get(position);
        holder.placeView.setText(item.getPLACE());
        holder.dayView.setText(item.getDAYCHAR()+" - "+item.getTIME());
        holder.addressView.setText(item.getADDRESS());
        holder.cityView.setText(item.getCITY()+", "+item.getSTATENAME());
        holder.distanceView.setText(Long.valueOf(item.getDISTANCE()).toString());
        return convertView;
    }

    static class ViewHolder {
        TextView placeView;
        TextView dayView;
        TextView addressView;
        TextView cityView;
        TextView distanceView;
    }
}
