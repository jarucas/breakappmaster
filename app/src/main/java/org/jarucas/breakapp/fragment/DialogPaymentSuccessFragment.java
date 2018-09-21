package org.jarucas.breakapp.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import org.jarucas.breakapp.GlideApp;
import org.jarucas.breakapp.R;
import org.jarucas.breakapp.dto.InvoiceModel;
import org.jarucas.breakapp.listener.CustomListener;

import java.text.SimpleDateFormat;

/**
 * Created by Javier on 09/09/2018.
 */

public class DialogPaymentSuccessFragment extends DialogFragment {

    private View root_view;
    private InvoiceModel invoice;
    private CustomListener customListener;

    public void setCustomListener(final CustomListener customListener) {
        this.customListener = customListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.setCancelable(false);
        root_view = inflater.inflate(R.layout.dialog_payment_success, container, false);
        //TODO - Populate this view

        final TextView nameTv = (TextView) root_view.findViewById(R.id.username);
        final TextView emailTv = (TextView) root_view.findViewById(R.id.email);
        final TextView dateTv = (TextView) root_view.findViewById(R.id.date);
        final TextView timeTv = (TextView) root_view.findViewById(R.id.time);
        final TextView amountTv = (TextView) root_view.findViewById(R.id.amount);

        final TextView paymentTypeTv = (TextView) root_view.findViewById(R.id.paymentcardtype);
        final CircularImageView profileIv = (CircularImageView) root_view.findViewById(R.id.userimage);
        final ImageView cardLogo = (ImageView) root_view.findViewById(R.id.card_logo);
        final FloatingActionButton floatingActionButton = (FloatingActionButton) root_view.findViewById(R.id.fab);

        final SimpleDateFormat dateDateFormat = new SimpleDateFormat("dd, MMM YYYY");
        final SimpleDateFormat timeDateFormat = new SimpleDateFormat("hh:mm a");
        nameTv.setText(invoice.getUserName());
        emailTv.setText(invoice.getEmail());
        dateTv.setText(dateDateFormat.format(invoice.getDate()));
        timeTv.setText(timeDateFormat.format(invoice.getDate()));
        amountTv.setText(Float.toString(invoice.getTotalPrice()) + " €");
        GlideApp.with(getContext()).load(invoice.getPhotoUrl()).fitCenter().into(profileIv);

        //TODO - Fill paymentType info
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCustomListener();
                getActivity().onBackPressed();
                //dismiss();
            }
        });

        return root_view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private void launchCustomListener() {
        if (customListener != null) {
            customListener.onEvent(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    public InvoiceModel getInvoice() {
        return invoice;
    }

    public void setInvoice(InvoiceModel invoice) {
        this.invoice = invoice;
    }
}
