package org.jarucas.breakapp.dto;

/**
 * Created by Javier on 10/09/2018.
 */

public class CartItemModel extends ItemModel {

    private int quantity;
    private boolean swipped;

    public CartItemModel() {
        //EMPTY CONSTRUCTOR
    }

    public CartItemModel(ItemModel item) {
        this.setQuantity(1);
        this.setCategory(item.getCategory());
        this.setCode(item.getCode());
        this.setName(item.getName());
        this.setDescription(item.getDescription());
        this.setImageUrl(item.getImageUrl());
        this.setMenuCode(item.getMenuCode());
        this.setPrice(item.getPrice());
        this.setSwipped(false);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isSwipped() {
        return swipped;
    }

    public void setSwipped(boolean swipped) {
        this.swipped = swipped;
    }
}
