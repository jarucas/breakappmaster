package org.jarucas.breakapp.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import org.jarucas.breakapp.App;
import org.jarucas.breakapp.GlideApp;
import org.jarucas.breakapp.R;
import org.jarucas.breakapp.utils.Utils;

public class MyFriendsActivity extends AppCompatActivity {

    private ImageView bgImage;
    private FloatingActionButton fabFriends;
    private RelativeLayout emptyListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);
        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final TextView titleTv = (TextView) findViewById(R.id.title);
        titleTv.setText(getString(R.string.myfriends_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void initComponent() {

        final CircularImageView profileImage = (CircularImageView) findViewById(R.id.imageview);
        final TextView profileName = (TextView) findViewById(R.id.username);
        GlideApp.with(getApplicationContext()).load(App.getmUser().getPhotoUrl()).fitCenter().into(profileImage);
        profileName.setText(App.getmUser().getDisplayName());

        emptyListView = (RelativeLayout) findViewById(R.id.rl_nofriends);
        emptyListView.setVisibility(View.VISIBLE);
        bgImage = (ImageView) findViewById(R.id.bg_image_friends);
        ViewGroup.LayoutParams params = bgImage.getLayoutParams();
        params.height = Utils.getScreenWidth();
        bgImage.setLayoutParams(params);

        fabFriends = (FloatingActionButton) findViewById(R.id.fab_myfriends);
        fabFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.toggleVisibility(emptyListView);
                //startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }
        });
    }
}
