package org.jarucas.breakapp;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jarucas.breakapp.dao.User;
import org.jarucas.breakapp.utils.Utils;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initToolbar();
        loadUserInformation();
    }

    private void loadUserInformation() {
        final User user = App.getmUser();
        final String displayName = user.getDisplayName();
        final String email = user.getEmail();
        final Uri photoURL = Uri.parse(user.getPhotoUrl());

        ((TextView) findViewById(R.id.profile_email)).setText(email);
        ((TextView) findViewById(R.id.profile_name)).setText(displayName);
        GlideApp.with(getApplicationContext()).load(photoURL)
                .fitCenter() // scale to fit entire image within ImageView
                .into((ImageView) findViewById(R.id.profile_image));
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(R.string.profile_title);
        Utils.setSystemBarColor(this, R.color.colorPrimary);
    }
}
