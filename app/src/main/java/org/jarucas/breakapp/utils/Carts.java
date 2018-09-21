package org.jarucas.breakapp.utils;

import org.jarucas.breakapp.App;
import org.jarucas.breakapp.dto.CartItemModel;
import org.jarucas.breakapp.dto.ItemModel;

import java.util.List;

/**
 * Created by Javier on 12/09/2018.
 */

public class Carts {

    public static void addSimpleItemToCart(final ItemModel item) {

        final List<CartItemModel> cart = App.getCart();
        for (final CartItemModel cartItem : cart) {
            if (cartItem.getCode().equals(item.getCode())) {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                App.setCart(cart);
                return;
            }
        }

        final CartItemModel cartItemModel = new CartItemModel(item);
        cart.add(cartItemModel);
        App.setCart(cart);
    }

    public static void addItemToCart(final CartItemModel item) {

        final List<CartItemModel> cart = App.getCart();
        for (final CartItemModel cartItem : cart) {
            if (cartItem.getCode().equals(item.getCode())) {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                App.setCart(cart);
                return;
            }
        }

        item.setQuantity(1);
        cart.add(item);
        App.setCart(cart);
    }

    public static void removeItemFromCart(final CartItemModel item) {

        final List<CartItemModel> cart = App.getCart();
        for (final CartItemModel cartItem : cart) {
            if (cartItem.getCode().equals(item.getCode())) {
                final int quantity = cartItem.getQuantity();
                if (quantity <= 1) {
                    cart.remove(cartItem);
                } else {
                    cartItem.setQuantity(quantity + 1);
                }
                App.setCart(cart);
            }
        }
    }
}
