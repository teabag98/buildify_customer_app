package com.qinsley.mbcustomer.DTO;

import java.io.Serializable;

public class MarkerData implements Serializable {

    String title = "";
    String id = "";
    String snippet = "";
    String img = "";

    public MarkerData(String title, String id, String snippet, String img) {
        this.title = title;
        this.id = id;
        this.snippet = snippet;
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
