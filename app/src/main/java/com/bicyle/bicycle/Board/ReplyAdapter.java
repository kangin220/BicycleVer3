package com.bicyle.bicycle.Board;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.bicyle.bicycle.R;

import java.util.ArrayList;

public class ReplyAdapter extends BaseAdapter {

    Context mContext;
    int layout;
    ArrayList<ReplyDTO> replyList;
    LayoutInflater inflater;

    public ReplyAdapter(Context context, int layout, ArrayList<ReplyDTO> replyList) //layout int는 id값
    {
        mContext=context;
        this.layout=layout;
        this.replyList=replyList;
        inflater= (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {
        return replyList.size();
    }

    @Override
    public Object getItem(int position) {
        return replyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if(convertView==null)
        {
            convertView=inflater.inflate(layout,null);

        }
        TextView replyWriterTV=convertView.findViewById(R.id.replyWriterTV);
        TextView replyBodyTV=convertView.findViewById(R.id.replyBodyTV);
        TextView replyDateTV=convertView.findViewById(R.id.replyDateTV);

        ReplyDTO dto=replyList.get(position);

        replyWriterTV.setText(dto.getWriter());
        replyBodyTV.setText(dto.getBody());
        replyDateTV.setText(dto.getDate());

        return convertView;
    }
}