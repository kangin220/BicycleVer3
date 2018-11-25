package com.bicyle.bicycle.util;


import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MyUtil {


    //boardKind int -> String
    public static String getBoardKind(int kind) {
        switch (kind) {
            case 0:
                return "자유게시판";
            case 1:
                return "동호회홍보";
            case 2:
                return "자전거분실";
            case 3:
                return "사고팔기";
        }
        return "ERROR";

    }

    //날짜구하기
    public static String getDate() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        //SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss ");
        return dateFm.format(date);
    }

    public static String getChannelId(List<String> userIds) {
        String channelID = "";

        Collections.sort(userIds);

        for (String id : userIds) {
            channelID += id;
        }
        return channelID;
    }

}
