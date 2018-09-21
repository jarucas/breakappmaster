package org.jarucas.breakapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import org.jarucas.breakapp.adapter.ReviewsTitleAdapter;
import org.jarucas.breakapp.dto.PlaceModel;
import org.jarucas.breakapp.dto.ReviewModel;
import org.jarucas.breakapp.dto.UserModel;
import org.jarucas.breakapp.fragment.DialogCreateReviewFragment;
import org.jarucas.breakapp.listener.CustomListener;
import org.jarucas.breakapp.utils.ItemAnimations;
import org.jarucas.breakapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MyReviewsActivity extends AppCompatActivity {

    private ImageView bgImage;
    private FloatingActionButton fabReviews;
    private RelativeLayout emptyListView;
    private List<ReviewModel> reviews;
    private RecyclerView recyclerView;
    private RecyclerView placesRecyclerView;
    private ReviewsTitleAdapter mAdapter;
    private PlacesAdapter placesAdapter;
    private List<PlaceModel> pendingReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reviews);
        reviews = new ArrayList<>();
        pendingReviews = new ArrayList<>();

        downloadReviews();

        initToolbar();
        initComponent();
        initRecyclerView();
        initPlacesRecyclerView();
    }

    private void downloadReviews() {
        final List<String> reviewCodes = App.getmUser().getReviews();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (reviewCodes != null) {
            for (String reviewCode : reviewCodes) {
                downloadReview(db, reviewCode);
            }
        } else {
            populatePendingReviews();
        }

    }

    private void downloadReview(final FirebaseFirestore db, final String code) {
        db.collection("reviews").whereEqualTo("code", code).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final QuerySnapshot reviewSnapshot = task.getResult();
                            if (reviewSnapshot.isEmpty()) {
                                //TODO Throw?
                            } else {
                                final List<DocumentSnapshot> documents = reviewSnapshot.getDocuments();
                                for (DocumentSnapshot doccument : documents) {
                                    final ReviewModel review = doccument.toObject(ReviewModel.class);
                                    reviews.add(review);
                                }
                                Log.d("MyReviewsActivity", "Place information retrieved succesfully");
                                emptyListView.setVisibility(View.GONE);
                                mAdapter.notifyDataSetChanged();
                                if (reviews.size() == App.getmUser().getReviews().size()) {
                                    populatePendingReviews();
                                }
                            }

                        } else {
                            Log.d("MyReviewsActivity", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void populatePendingReviews() {
        //TODO if reviews empty
        final UserModel user = App.getmUser();
        final List<String> placesToDownload = new ArrayList<>();
        final List<String> placeVisits = user.getPlaceVisits();//nullcheck
        placesToDownload.addAll(placeVisits);
        if (!reviews.isEmpty() && !placeVisits.isEmpty()) {
            for (final String placeCode : placeVisits) {
                for (final ReviewModel reviewModel : reviews) {
                    if (placeCode.equals(reviewModel.getPlaceCode())) {
                        placesToDownload.remove(placeCode);
                        break;
                    }
                }
            }
        }

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        for (final String placeCode : placesToDownload) {
            downloadPlace(db, placeCode);
        }
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
                                    pendingReviews.add(place);
                                    placesAdapter.notifyDataSetChanged();
                                }
                                Log.d("MyReviewsActivity", "Place information retrieved succesfully");
                            }

                        } else {
                            Log.d("MyReviewsActivity", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final TextView titleTv = (TextView) findViewById(R.id.title);
        titleTv.setText(getString(R.string.myreviews_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void initComponent() {

        final View layoutPlaces = findViewById(R.id.lyt_center);
        final CircularImageView profileImage = (CircularImageView) findViewById(R.id.imageview);
        final TextView profileName = (TextView) findViewById(R.id.username);
        GlideApp.with(getApplicationContext()).load(App.getmUser().getPhotoUrl()).fitCenter().into(profileImage);
        profileName.setText(App.getmUser().getDisplayName());

        emptyListView = (RelativeLayout) findViewById(R.id.rl_noreviews);
        if (reviews.isEmpty()) {
            emptyListView.setVisibility(View.VISIBLE);
        } else {
            emptyListView.setVisibility(View.GONE);
        }

        layoutPlaces.setVisibility(View.GONE);
        bgImage = (ImageView) findViewById(R.id.bg_image_reviews);
        ViewGroup.LayoutParams params = bgImage.getLayoutParams();
        params.height = Utils.getScreenWidth();
        bgImage.setLayoutParams(params);

        fabReviews = (FloatingActionButton) findViewById(R.id.fab_myreviews);
        fabReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.toggleVisibility(layoutPlaces);
            }
        });
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layout);
        recyclerView.setHasFixedSize(true);
        if (reviews == null) {
            reviews = new ArrayList<>();
        }
        mAdapter = new ReviewsTitleAdapter(this, reviews, ItemAnimations.BOTTOM_UP);
        recyclerView.setAdapter(mAdapter);
    }

    private void initPlacesRecyclerView() {
        placesRecyclerView = (RecyclerView) findViewById(R.id.placesRecyclerView);
        final LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        placesRecyclerView.setLayoutManager(layout);
        placesRecyclerView.setHasFixedSize(true);
        placesAdapter = new PlacesAdapter(this, pendingReviews, ItemAnimations.BOTTOM_UP);
        placesRecyclerView.setAdapter(placesAdapter);

        // on item list clicked
        placesAdapter.setOnItemClickListener(new PlacesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, PlaceModel obj, int position) {
                createReview(obj);
            }
        });

    }

    private void createReview(final PlaceModel placeModel) {
        //TODO
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final DialogCreateReviewFragment newFragment = new DialogCreateReviewFragment();
        newFragment.setPlace(placeModel);
        newFragment.setCustomListener(new CustomListener<DialogCreateReviewFragment>() {
            @Override
            public void onEvent(final DialogCreateReviewFragment customListener) {
                final UserModel userModel = App.getmUser();
                final ReviewModel reviewModel = customListener.getReviewModel();

                userModel.addReview(reviewModel.getCode());
                App.setmUser(userModel);
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(userModel.getGuid()).set(userModel);
                db.collection("reviews").document(reviewModel.getCode()).set(reviewModel);
                placeModel.addReviews(reviewModel.getCode());
                updatePlaceRating(db, placeModel, reviewModel.getRating());
                reviews.add(reviewModel);
                mAdapter.notifyDataSetChanged();
                pendingReviews.remove(placeModel);
                placesAdapter.notifyDataSetChanged();
                if (pendingReviews.isEmpty()) {
                    fabReviews.setClickable(false);
                    fabReviews.setBackgroundColor(Color.GRAY);
                }
            }
        });
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
    }

    private void updatePlaceRating(FirebaseFirestore db, PlaceModel placeModel, float rating) {
        final int valuations = placeModel.getValuations();
        final int newValuations = valuations + 1;
        placeModel.setValuation((placeModel.getValuation() * valuations + rating) / newValuations);
        placeModel.setValuations(newValuations);
        db.collection("places").document(placeModel.getCode()).set(placeModel);

        //TODO Update place rating
    }
}
