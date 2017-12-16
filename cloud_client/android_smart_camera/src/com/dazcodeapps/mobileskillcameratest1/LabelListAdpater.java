package com.dazcodeapps.mobileskillcameratest1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class LabelListAdpater extends BaseAdapter {


    private Context mContext;
    private static LayoutInflater inflater = null;
    public ArrayList<RekognitionLabel> smart_labels = new ArrayList<RekognitionLabel>();

    public LabelListAdpater(Context c) {
        mContext = c;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    public int getCount() {
        if (smart_labels != null) {
            return smart_labels.size();
        } else {
            return 0;
        }
    }

    public Object getItem(int position) {
        if (smart_labels != null && smart_labels.get(position) != null) {
            return smart_labels.get(position);
        } else {
            return null;
        }
    }

    public long getItemId(int position) {
        return 0;
    }


    static class ViewHolder {
        TextView smartLabelName;
        TextView smartLabelConfidence;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View displayView = convertView;

        if (displayView == null) {
            displayView = inflater.inflate(R.layout.smart_labels_list, null);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.smartLabelName = (TextView) displayView.findViewById(R.id.smartLabelName);
            viewHolder.smartLabelConfidence = (TextView) displayView.findViewById(R.id.smartLabelConfidence);
            displayView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) displayView.getTag();


        holder.smartLabelName.setText(smart_labels.get(position).labelName);
        holder.smartLabelConfidence.setText(smart_labels.get(position).labelConfidence);


        return displayView;
    }


}