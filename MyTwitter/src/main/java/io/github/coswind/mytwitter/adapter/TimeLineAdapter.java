package io.github.coswind.mytwitter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import io.github.coswind.mytwitter.R;
import twitter4j.ResponseList;
import twitter4j.Status;

/**
 * Created by coswind on 14-2-20.
 */
public class TimeLineAdapter extends BaseAdapter {
    private ResponseList<Status> statuses;

    private LayoutInflater layoutInflater;

    public TimeLineAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    public ResponseList<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(ResponseList<Status> statuses) {
        this.statuses = statuses;
    }

    @Override
    public int getCount() {
        if (statuses == null) return 0;
        return statuses.size();
    }

    @Override
    public Object getItem(int position) {
        if (statuses == null) return null;
        return statuses.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (statuses == null) return 0;
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.time_line_item, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.text_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Status status = statuses.get(position);

        viewHolder.textView.setText(status.getText());

        return convertView;
    }

    static class ViewHolder {
        TextView textView;
    }
}
