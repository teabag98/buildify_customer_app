package com.qinsley.mbcustomer.DTO;

import java.io.Serializable;

public class AppliedJobDTO implements Serializable {
    String aj_id = "";
    String user_id = "";
    String artist_id = "";
    String job_id = "";
    String price = "";
    String description = "";
    String status = "";
    String created_at = "";
    String updated_at = "";
    String artist_image = "";
    String artist_name = "";
    String category_name = "";
    String category_price = "";
    String artist_address = "";
    String artist_mobile = "";
    String ava_rating = "";
    String artist_email = "";
    String currency_symbol = "";
    String job_date = "";
    String time = "";
    String job_timestamp = "";

    public String getJob_date() {
        return job_date;
    }

    public void setJob_date(String job_date) {
        this.job_date = job_date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getJob_timestamp() {
        return job_timestamp;
    }

    public void setJob_timestamp(String job_timestamp) {
        this.job_timestamp = job_timestamp;
    }

    public String getAj_id() {
        return aj_id;
    }

    public void setAj_id(String aj_id) {
        this.aj_id = aj_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getArtist_id() {
        return artist_id;
    }

    public void setArtist_id(String artist_id) {
        this.artist_id = artist_id;
    }

    public String getJob_id() {
        return job_id;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getArtist_image() {
        return artist_image;
    }

    public void setArtist_image(String artist_image) {
        this.artist_image = artist_image;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_price() {
        return category_price;
    }

    public void setCategory_price(String category_price) {
        this.category_price = category_price;
    }

    public String getArtist_address() {
        return artist_address;
    }

    public void setArtist_address(String artist_address) {
        this.artist_address = artist_address;
    }

    public String getArtist_mobile() {
        return artist_mobile;
    }

    public void setArtist_mobile(String artist_mobile) {
        this.artist_mobile = artist_mobile;
    }

    public String getAva_rating() {
        return ava_rating;
    }

    public void setAva_rating(String ava_rating) {
        this.ava_rating = ava_rating;
    }

    public String getArtist_email() {
        return artist_email;
    }

    public void setArtist_email(String artist_email) {
        this.artist_email = artist_email;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCurrency_symbol() {
        return currency_symbol;
    }

    public void setCurrency_symbol(String currency_symbol) {
        this.currency_symbol = currency_symbol;
    }
}
