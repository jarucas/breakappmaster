package org.jarucas.breakapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.jarucas.breakapp.App;
import org.jarucas.breakapp.GlideApp;
import org.jarucas.breakapp.R;
import org.jarucas.breakapp.adapter.PlacesAdapter;
import org.jarucas.breakapp.dto.PlaceModel;
import org.jarucas.breakapp.utils.ItemAnimations;
import org.jarucas.breakapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MyPlacesActivity extends AppCompatActivity {

    private ImageView bgImage;
    private RelativeLayout emptyListView;
    private List<PlaceModel> places;
    private RecyclerView recyclerView;
    private PlacesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_places);
        places = new ArrayList<>();
        downloadPlacesList();

        initToolbar();
        initComponent();
        initRecyclerView();
    }

    private void downloadPlacesList() {
        final List<String> placeVisits = App.getmUser().getPlaceVisits();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        for (String placeCode : placeVisits) {
            downloadPlace(db, placeCode);
        }
        //TODO
    }

    private void downloadPlace(final FirebaseFirestore db, final String code) {
        db.collection("places").whereEqualTo("code", code).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final QuerySnapshot placeSnapshot = task.getResult();
                            if (placeSnapshot.isEmpty()) {
                                //TODO Throw?
                            } else {
                                final List<DocumentSnapshot> documents = placeSnapshot.getDocuments();
                                for (DocumentSnapshot doccument : documents) {
                                    final PlaceModel place = doccument.toObject(PlaceModel.class);
                                    places.add(place);
                                }
                                Log.d("MyPlacesActivity", "Place information retrieved succesfully");
                                emptyListView.setVisibility(View.GONE);
                                mAdapter.notifyDataSetChanged();
                            }

                        } else {
                            Log.d("MyPlacesActivity", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final TextView titleTv = (TextView) findViewById(R.id.title);
        titleTv.setText(getString(R.string.myplaces_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void initComponent() {

        final CircularImageView profileImage = (CircularImageView) findViewById(R.id.imageview);
        final TextView profileName = (TextView) findViewById(R.id.username);
        GlideApp.with(getApplicationContext()).load(App.getmUser().getPhotoUrl()).fitCenter().into(profileImage);
        profileName.setText(App.getmUser().getDisplayName());

        emptyListView = (RelativeLayout) findViewById(R.id.rl_noplaces);
        bgImage = (ImageView) findViewById(R.id.bg_image_places);
        ViewGroup.LayoutParams params = bgImage.getLayoutParams();
        params.height = Utils.getScreenWidth();
        bgImage.setLayoutParams(params);

        if (App.getmUser().getPlaceVisits().isEmpty()) {
            emptyListView.setVisibility(View.VISIBLE);
        } else {
            emptyListView.setVisibility(View.GONE);
        }
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layout);
        recyclerView.setHasFixedSize(true);
        mAdapter = new PlacesAdapter(this, places, ItemAnimations.BOTTOM_UP);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new PlacesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, PlaceModel obj, int position) {
                App.setMplace(obj);
                final Intent i = new Intent(getApplicationContext(), PlaceOverviewActivity.class);
                startActivity(i);
            }
        });

    }


}
