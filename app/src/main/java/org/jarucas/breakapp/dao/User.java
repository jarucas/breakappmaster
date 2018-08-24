package org.jarucas.breakapp.dao;

import java.util.List;

/**
 * Created by Javier on 24/08/2018.
 */

public class User {

    private String guid;

    private String displayName;

    private String email;

    private Long registrationDate;

    private Long lastLogin;

    private String phone;

    private String photoUrl;

    private List<String> bills;

    private List<String> placeVisits;

    private List<String> places;

    private List<String> providers;

    private List<String> reviews;

    public User() {
        // Empty Constructor
    }

    public User(String guid, String displayName, String email, Long registrationDate,
                Long lastLogin, String phone, String photoUrl, List<String> bills, List<String> placeVisits,
                List<String> places, List<String> providers, List<String> reviews) {
        this.guid = guid;
        this.displayName = displayName;
        this.email = email;
        this.registrationDate = registrationDate;
        this.lastLogin = lastLogin;
        this.phone = phone;
        this.photoUrl = photoUrl;
        this.bills = bills;
        this.placeVisits = placeVisits;
        this.places = places;
        this.providers = providers;
        this.reviews = reviews;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Long registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public List<String> getBills() {
        return bills;
    }

    public void setBills(List<String> bills) {
        this.bills = bills;
    }

    public List<String> getPlaceVisits() {
        return placeVisits;
    }

    public void setPlaceVisits(List<String> placeVisits) {
        this.placeVisits = placeVisits;
    }

    public List<String> getPlaces() {
        return places;
    }

    public void setPlaces(List<String> places) {
        this.places = places;
    }

    public List<String> getProviders() {
        return providers;
    }

    public void setProviders(List<String> providers) {
        this.providers = providers;
    }

    public List<String> getReviews() {
        return reviews;
    }

    public void setReviews(List<String> reviews) {
        this.reviews = reviews;
    }
}
