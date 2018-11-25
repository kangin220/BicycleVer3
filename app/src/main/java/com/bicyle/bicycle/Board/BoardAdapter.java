package com.bicyle.bicycle.Board;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bicyle.bicycle.Data.DataManager;
import com.bicyle.bicycle.R;
import com.bicyle.bicycle.util.MyUtil;
import java.util.ArrayList;
import java.util.Collections;

public class BoardAdapter extends BaseAdapter implements Filterable {
    Filter kindFilter, likeFilter;
    Context mContext;
    int layout;
    ArrayList<BoardDTO> boardList;
    ArrayList<BoardDTO> filteredBoardList;
    LayoutInflater inflater;

    public BoardAdapter(Context context, int layout, ArrayList<BoardDTO> boardList) //layout int는 id값
    {
        mContext=context;
        this.layout=layout;
        this.boardList=boardList;
        filteredBoardList = boardList;
        inflater= (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {
        return filteredBoardList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredBoardList.get(position);
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
        TextView boardKind_titleTV=convertView.findViewById(R.id.board_boardKind_titleTV);boardKind_titleTV.setSelected(true);
        TextView board_writer_dateTV=convertView.findViewById(R.id.board_writer_dateTV);board_writer_dateTV.setSelected(true);
        TextView board_likeTV=convertView.findViewById(R.id.board_likeTV);board_likeTV.setSelected(true);


//        TextView titleTV=convertView.findViewById(R.id.titleTV);titleTV.setSelected(true);
//        TextView writerTV=convertView.findViewById(R.id.writerTV);writerTV.setSelected(true);
//        TextView dateTV=convertView.findViewById(R.id.dateTV);dateTV.setSelected(true);
//        TextView likeTV=convertView.findViewById(R.id.likeTV);likeTV.setSelected(true);
//        TextView boardKindTV= convertView.findViewById(R.id.boardkindTV);boardKindTV.setSelected(true);


        BoardDTO dto=filteredBoardList.get(position);

        String boardString=MyUtil.getBoardKind(dto.getBoardKind());
        boardKind_titleTV.setText("["+ boardString+"]   "+ dto.getTitle());
        board_writer_dateTV.setText(dto.getWriter()+"    "+dto.getDate());
        board_likeTV.setText(""+dto.getLikeNum());

//        titleTV.setText(dto.getTitle());
//        writerTV.setText(dto.getWriter());
//        dateTV.setText(dto.getDate());
//        likeTV.setText(""+dto.getLikeNum());
//
//        String boardString=MyUtil.getBoardKind(dto.getBoardKind());
//        boardKindTV.setText(boardString);


        return convertView;
    }


    public Filter getFilter(int kind)
    {
        if(kind==0)
        {
            if (likeFilter == null)
            {
                likeFilter = new LikeFilter() ;
            }
            return likeFilter;
        }
        else if(kind==1)
        {
            if (kindFilter == null)
            {
                kindFilter = new kindFilter() ;
            }
            return kindFilter;
        }
        return null ;
    }


    @Override
    public Filter getFilter()
    {
        return null;
    }

    private class LikeFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults() ;

            if (constraint.toString().equals("false")) {
                Collections.sort(filteredBoardList,DataManager.getInstance().descendingBoard);
                results.values = filteredBoardList ;
                results.count = filteredBoardList.size() ;
            } else {
                Collections.sort(filteredBoardList,DataManager.getInstance().likeFilterBoard);
                results.values = filteredBoardList ;
                results.count = filteredBoardList.size() ;

            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            // update listview by filtered data list.
            filteredBoardList = (ArrayList<BoardDTO>) results.values ;

            // notify
            if (results.count > 0) {
                notifyDataSetChanged() ;
            } else {
                notifyDataSetInvalidated() ;
            }
        }
    }


    private class kindFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults() ;

            if (constraint == null || constraint.length() == 0) {
                results.values = boardList ;
                results.count = boardList.size() ;
            } else {
                ArrayList<BoardDTO> itemList = new ArrayList<BoardDTO>() ;


                for (BoardDTO item : boardList) {
                    String boardKind = item.getBoardKind()+"";
                    if (boardKind.trim().equals(constraint.toString().trim()))
                    {
                        itemList.add(item) ;
                    }
                }

                results.values = itemList ;
                results.count = itemList.size() ;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            // update listview by filtered data list.
            filteredBoardList = (ArrayList<BoardDTO>) results.values ;

            // notify
            if (results.count > 0) {
                notifyDataSetChanged() ;
            } else {
                notifyDataSetInvalidated() ;
            }
        }
    }


}