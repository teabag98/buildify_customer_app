package com.qinsley.mbcustomer.DTO;

import java.io.Serializable;

public class UserBooking implements Serializable {

    String id = "";
    String user_id = "";
    String artist_id = "";
    String booking_time = "";
    String description = "";
    String duration = "";
    String service_id = "";
    String start_time = "";
    String category_price = "";
    String booking_date = "";
    String end_time = "";
    String price = "";
    String status = "";
    String booking_flag = "";
    String decline_by = "";
    String time_zone = "";
    String decline_reason = "";
    String booking_timestamp = "";
    String commission_type = "";
    String booking_type = "";
    String flat_type = "";
    String created_at = "";
    String updated_at = "";
    String job_id = "";
    String latitude = "";
    String longitude = "";
    String address = "";
    String discount_amount = "";
    String artistImage = "";
    String category_name = "";
    String artistName = "";
    String artist_commission_type = "";
    String artistMobile = "";
    String artistEmail = "";
    String artistLocation = "";
    String userName = "";
    String jobDone = "";
    String completePercentages = "";
    String ava_rating = "";
    String working_min = "";
    String userImage = "";
    String currency_type = "";
    String total_amount = "";
    String invoice_id = "";
    HistoryDTO invoice;
    boolean isSection = false;
    String section_name = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getBooking_time() {
        return booking_time;
    }

    public void setBooking_time(String booking_time) {
        this.booking_time = booking_time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getCategory_price() {
        return category_price;
    }

    public void setCategory_price(String category_price) {
        this.category_price = category_price;
    }

    public String getBooking_date() {
        return booking_date;
    }

    public void setBooking_date(String booking_date) {
        this.booking_date = booking_date;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBooking_flag() {
        return booking_flag;
    }

    public void setBooking_flag(String booking_flag) {
        this.booking_flag = booking_flag;
    }

    public String getDecline_by() {
        return decline_by;
    }

    public void setDecline_by(String decline_by) {
        this.decline_by = decline_by;
    }

    public String getTime_zone() {
        return time_zone;
    }

    public void setTime_zone(String time_zone) {
        this.time_zone = time_zone;
    }

    public String getDecline_reason() {
        return decline_reason;
    }

    public void setDecline_reason(String decline_reason) {
        this.decline_reason = decline_reason;
    }

    public String getBooking_timestamp() {
        return booking_timestamp;
    }

    public void setBooking_timestamp(String booking_timestamp) {
        this.booking_timestamp = booking_timestamp;
    }

    public String getCommission_type() {
        return commission_type;
    }

    public void setCommission_type(String commission_type) {
        this.commission_type = commission_type;
    }

    public String getBooking_type() {
        return booking_type;
    }

    public void setBooking_type(String booking_type) {
        this.booking_type = booking_type;
    }

    public String getFlat_type() {
        return flat_type;
    }

    public void setFlat_type(String flat_type) {
        this.flat_type = flat_type;
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

    public String getJob_id() {
        return job_id;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(String discount_amount) {
        this.discount_amount = discount_amount;
    }

    public String getArtistImage() {
        return artistImage;
    }

    public void setArtistImage(String artistImage) {
        this.artistImage = artistImage;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtist_commission_type() {
        return artist_commission_type;
    }

    public void setArtist_commission_type(String artist_commission_type) {
        this.artist_commission_type = artist_commission_type;
    }

    public String getArtistMobile() {
        return artistMobile;
    }

    public void setArtistMobile(String artistMobile) {
        this.artistMobile = artistMobile;
    }

    public String getArtistEmail() {
        return artistEmail;
    }

    public void setArtistEmail(String artistEmail) {
        this.artistEmail = artistEmail;
    }

    public String getArtistLocation() {
        return artistLocation;
    }

    public void setArtistLocation(String artistLocation) {
        this.artistLocation = artistLocation;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getJobDone() {
        return jobDone;
    }

    public void setJobDone(String jobDone) {
        this.jobDone = jobDone;
    }

    public String getCompletePercentages() {
        return completePercentages;
    }

    public void setCompletePercentages(String completePercentages) {
        this.completePercentages = completePercentages;
    }

    public String getAva_rating() {
        return ava_rating;
    }

    public void setAva_rating(String ava_rating) {
        this.ava_rating = ava_rating;
    }

    public String getWorking_min() {
        return working_min;
    }

    public void setWorking_min(String working_min) {
        this.working_min = working_min;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getCurrency_type() {
        return currency_type;
    }

    public void setCurrency_type(String currency_type) {
        this.currency_type = currency_type;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getInvoice_id() {
        return invoice_id;
    }

    public void setInvoice_id(String invoice_id) {
        this.invoice_id = invoice_id;
    }

    public HistoryDTO getInvoice() {
        return invoice;
    }

    public void setInvoice(HistoryDTO invoice) {
        this.invoice = invoice;
    }

    public boolean isSection() {
        return isSection;
    }

    public void setSection(boolean section) {
        isSection = section;
    }

    public String getSection_name() {
        return section_name;
    }

    public void setSection_name(String section_name) {
        this.section_name = section_name;
    }
}
