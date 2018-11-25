package com.bicyle.bicycle.Board;


import java.io.Serializable;

public class ReplyDTO implements Serializable//리플
{
    private String key; //키
    private String date;//날짜
    private String body; //내용
    private String writer; //작성자

    public ReplyDTO()
    {

    }

    public ReplyDTO(String date, String body, String writer) {
        this.date = date;
        this.body = body;
        this.writer = writer;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }
}

