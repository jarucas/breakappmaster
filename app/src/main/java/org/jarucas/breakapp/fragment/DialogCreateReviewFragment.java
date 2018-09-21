package org.jarucas.breakapp.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.jarucas.breakapp.App;
import org.jarucas.breakapp.GlideApp;
import org.jarucas.breakapp.R;
import org.jarucas.breakapp.dto.PlaceModel;
import org.jarucas.breakapp.dto.ReviewModel;
import org.jarucas.breakapp.listener.CustomListener;

import java.util.Date;

/**
 * Created by Javier on 09/09/2018.
 */

public class DialogCreateReviewFragment extends DialogFragment {

    private View root_view;
    private PlaceModel place;
    private CustomListener customListener;
    private ReviewModel reviewModel;

    public void setCustomListener(final CustomListener customListener) {
        this.customListener = customListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.dialog_create_review, container, false);

        this.setCancelable(false);
        //TODO - Populate this view

        final TextView placeTv = (TextView) root_view.findViewById(R.id.placetitle);
        final TextView userTv = (TextView) root_view.findViewById(R.id.username);

        final EditText titleEt = (EditText) root_view.findViewById(R.id.et_title);
        final EditText contentEt = (EditText) root_view.findViewById(R.id.et_post);
        final AppCompatRatingBar ratingBar = (AppCompatRatingBar) root_view.findViewById(R.id.rating_bar);

        final CircularImageView profileIv = (CircularImageView) root_view.findViewById(R.id.imageview);
        final FloatingActionButton floatingActionButton = (FloatingActionButton) root_view.findViewById(R.id.fab);

        userTv.setText(App.getmUser().getDisplayName());
        placeTv.setText(place.getName());
        GlideApp.with(getContext()).load(place.getImageURL1()).fitCenter().into(profileIv);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Strings.isNullOrEmpty(titleEt.getText().toString()) && !Strings.isNullOrEmpty(contentEt.getText().toString())) {
                    reviewModel = new ReviewModel();
                    reviewModel.setCode(place.getCode() + new Date());
                    reviewModel.setPlaceCode(place.getCode());
                    reviewModel.setPlaceName(place.getName());
                    reviewModel.setPlaceImageUrl(place.getImageURL1());
                    reviewModel.setUserName(App.getmUser().getDisplayName());
                    reviewModel.setUserImageUrl(App.getmUser().getPhotoUrl());
                    reviewModel.setRating(ratingBar.getRating());
                    reviewModel.setTitle(titleEt.getText().toString());
                    reviewModel.setContent(contentEt.getText().toString());
                    reviewModel.setTimestamp(new Date());
                    launchCustomListener();
                    getActivity().onBackPressed();
                }
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

    public PlaceModel getPlace() {
        return place;
    }

    public void setPlace(PlaceModel place) {
        this.place = place;
    }

    public ReviewModel getReviewModel() {
        return reviewModel;
    }

    public void setReviewModel(ReviewModel reviewModel) {
        this.reviewModel = reviewModel;
    }
}
