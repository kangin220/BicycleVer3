package com.bicyle.bicycle.Data;

import com.bicyle.bicycle.Board.BoardDTO;
import com.bicyle.bicycle.Board.ReplyDTO;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Comparator;


public class DataManager {



    //임시 userName
    //public String userName = "user" + new Random().nextInt(10000);  // 랜덤한 유저 이름 설정 ex) user1234
    public String userName="user5839";
    private static DataManager dataManager = new DataManager();
    public static DataManager getInstance(){
        return dataManager;
    }

    public FirebaseDatabase firebaseDatabase ;
    public DatabaseReference databaseReference;

    public DescendingBoard descendingBoard = new DescendingBoard();
    public DescendingReply descendingReply = new DescendingReply();
    public LikeFilterBoard likeFilterBoard = new LikeFilterBoard();


    class DescendingBoard implements Comparator<BoardDTO>
    {
        @Override
        public int compare(BoardDTO o1, BoardDTO o2) {
            return o2.getDate().compareTo(o1.getDate());
        }
    }

    class DescendingReply implements Comparator<ReplyDTO>
    {
        @Override
        public int compare(ReplyDTO o1, ReplyDTO o2) {
            return o2.getDate().compareTo(o1.getDate());
        }
    }

    class LikeFilterBoard implements Comparator<BoardDTO>
    {
        @Override
        public int compare(BoardDTO o1, BoardDTO o2) {
            if (o2.getLikeNum() < o1.getLikeNum()) {
                return -1;
            } else if (o2.getLikeNum() > o1.getLikeNum()) {
                return 1;
            }
            return 0;

        }
    }

}
