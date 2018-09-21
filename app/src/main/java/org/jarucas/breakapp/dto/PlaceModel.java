package org.jarucas.breakapp.dto;


import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Javier on 28/08/2018.
 */

public class PlaceModel {

    private String code;
    private String name;
    private String description;
    private String phone;
    private Map<String, Double> location;

    private String imageURL1;
    private String ImageURL2;

    private int valuations;
    private float valuation;

    private Map<String, Integer> userVisits;

    private Map<String, String> address;
    private Map<String, String> schedule;
    private Map<String, Boolean> categories;
    private List<String> imageUrls;
    private List<String> reviews;

    public PlaceModel() {
        //Empty Constructor
    }

    public void addReviews(final String reviewCode) {
        if (reviews == null) {
            reviews = new ArrayList<>();
        }

        reviews.add(reviewCode);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LatLng getLocation() {
        return new LatLng(this.location.get("latitude"), this.location.get("longitude"));
    }

    public void setLocation(Map<String, Double> location) {
        this.location = location;
    }

    public int getValuations() {
        return valuations;
    }

    public void setValuations(int valuations) {
        this.valuations = valuations;
    }

    public float getValuation() {
        return valuation;
    }

    public void setValuation(float valuation) {
        this.valuation = valuation;
    }

    public Map<String, Integer> getUserVisits() {
        return userVisits;
    }

    public void setUserVisits(Map<String, Integer> userVisits) {
        this.userVisits = userVisits;
    }

    public Map<String, String> getAddress() {
        return address;
    }

    public void setAddress(Map<String, String> address) {
        this.address = address;
    }

    public Map<String, String> getSchedule() {
        return schedule;
    }

    public void setSchedule(Map<String, String> schedule) {
        this.schedule = schedule;
    }

    public Map<String, Boolean> getCategories() {
        return categories;
    }

    public void setCategories(Map<String, Boolean> categories) {
        this.categories = categories;
    }

    public String getImageURL1() {
        return imageURL1;
    }

    public void setImageURL1(String imageURL1) {
        this.imageURL1 = imageURL1;
    }

    public String getImageURL2() {
        return ImageURL2;
    }

    public void setImageURL2(String imageURL2) {
        ImageURL2 = imageURL2;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public List<String> getReviews() {
        return reviews;
    }

    public void setReviews(List<String> reviews) {
        this.reviews = reviews;
    }
}
