package org.jarucas.breakapp.dto;

import java.util.Map;

/**
 * Created by Javier on 10/09/2018.
 */

public class ItemModel {

    private String code;
    private String name;
    private String menuCode;
    private float price;
    private Map<String, Boolean> category;
    private String imageUrl;
    private String description;

    public ItemModel() {
        //EMPTY CONSTRUCTOR
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

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Map<String, Boolean> getCategory() {
        return category;
    }

    public void setCategory(Map<String, Boolean> category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
