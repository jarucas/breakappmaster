package org.jarucas.breakapp.dto;

import java.util.List;

/**
 * Created by Javier on 10/09/2018.
 */

public class MenuModel {

    private String code;
    private String placeCode;
    private List<ItemModel> items;

    public MenuModel() {
        //EMPTY CONSTRUCTOR
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPlaceCode() {
        return placeCode;
    }

    public void setPlaceCode(String placeCode) {
        this.placeCode = placeCode;
    }

    public List<ItemModel> getItems() {
        return items;
    }

    public void setItems(List<ItemModel> items) {
        this.items = items;
    }
}
