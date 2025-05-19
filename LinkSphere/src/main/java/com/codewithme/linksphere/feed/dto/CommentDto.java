package com.codewithme.linksphere.feed.dto;

public class CommentDto {
    private String content;
    public CommentDto() {

    }
    public CommentDto(String content) {
        this.content = content;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
