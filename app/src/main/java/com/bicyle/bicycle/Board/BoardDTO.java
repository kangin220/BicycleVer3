package com.bicyle.bicycle.Board;


import java.io.Serializable;

public class BoardDTO implements Serializable {
    private String key; //키
    private String title; //제목
    private String writer; //작성자
    private int boardKind; //게시판 종류
    private String body; //내용
    private String date; //날짜
    private int likeNum; //좋아요 개수

    public BoardDTO(String title, String writer, int boardKind, String body, String date, int likeNum) {
        this.title = title;
        this.writer = writer;
        this.boardKind = boardKind;
        this.body = body;
        this.date = date;
        this.likeNum = likeNum;
    }

    public BoardDTO()
    {

    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public int getBoardKind() {
        return boardKind;
    }

    public void setBoardKind(int boardKind) {
        this.boardKind = boardKind;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}


