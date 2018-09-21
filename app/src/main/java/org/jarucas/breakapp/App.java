package org.jarucas.breakapp;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import org.jarucas.breakapp.dto.CartItemModel;
import org.jarucas.breakapp.dto.InvoiceModel;
import org.jarucas.breakapp.dto.PlaceModel;
import org.jarucas.breakapp.dto.UserModel;

import java.util.List;

/**
 * Created by Javier on 23/08/2018.
 */

public class App extends MultiDexApplication {

    private static Context mContext;
    private static UserModel mUser;
    private static PlaceModel mplace;
    private static List<CartItemModel> cart;
    private static InvoiceModel invoice;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mUser = null;
        mplace = null;
        cart = null;
        invoice = null;

    }

    public static Context getContext() {
        return mContext;
    }

    public static UserModel getmUser() {
        return mUser;
    }

    public static void setmUser(UserModel mUser) {
        App.mUser = mUser;
    }

    public static PlaceModel getMplace() {
        return mplace;
    }

    public static void setMplace(PlaceModel mplace) {
        App.mplace = mplace;
    }

    public static List<CartItemModel> getCart() {
        return cart;
    }

    public static void setCart(List<CartItemModel> cart) {
        App.cart = cart;
    }

    public static InvoiceModel getInvoice() {
        return invoice;
    }

    public static void setInvoice(InvoiceModel invoice) {
        App.invoice = invoice;
    }
}
