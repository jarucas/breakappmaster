package org.jarucas.breakapp.dto;

/**
 * Created by Javier on 07/09/2018.
 */

public class TableModel {

    private String code;
    private String placeid;

    public TableModel() {
        //EMPTY CONSTRUTOR
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPlaceid() {
        return placeid;
    }

    public void setPlaceid(String placeid) {
        this.placeid = placeid;
    }
}
