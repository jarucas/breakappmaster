package org.jarucas.breakapp.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.terminator.ChipTerminatorHandler;

import org.jarucas.breakapp.App;
import org.jarucas.breakapp.GlideApp;
import org.jarucas.breakapp.R;
import org.jarucas.breakapp.adapter.ImagesAdapter;
import org.jarucas.breakapp.adapter.SimpleReviewsAdapter;
import org.jarucas.breakapp.dto.ItemModel;
import org.jarucas.breakapp.dto.MenuModel;
import org.jarucas.breakapp.dto.PlaceModel;
import org.jarucas.breakapp.dto.ReviewModel;
import org.jarucas.breakapp.fragment.DialogMenuFragment;
import org.jarucas.breakapp.utils.Utils;
import org.jarucas.breakapp.utils.ViewAnimations;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * TODO Make this page prettier:
 * -Put the propper description
 * TODO KILL THE DIALOG
 */

public class PlaceOverviewActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int ZOOM = 12;

    private GoogleMap mMap;
    private PlaceModel mplace;
    private View parent_view;
    private View lyt_expand_reviews, lyt_expand_warranty, lyt_expand_description, lyt_expand_location, lyt_expand_images;
    private NestedScrollView nested_scroll_view;
    private List<ReviewModel> reviews;
    private SimpleReviewsAdapter reviewsAdapter;
    private MenuModel mMenu;
    private List<ItemModel> menuItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_place_overview);
        parent_view = findViewById(R.id.parent_view);
        mplace = App.getMplace();

        initMapsFragment();
        initComponent();
    }

    private void initMapsFragment() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MapView mapView = (MapView) findViewById(R.id.cardmap);
        mapView.onCreate(null);
        mapView.getMapAsync(this);
    }

    private void initComponent() {
        // nested scrollview
        nested_scroll_view = (NestedScrollView) findViewById(R.id.nested_scroll_view);

        final TextView titleTv = ((TextView) findViewById(R.id.title));
        final TextView ratingTv = ((TextView) findViewById(R.id.ratingtv));
        final TextView locationTv = ((TextView) findViewById(R.id.locationtext));
        final ImageView imageView = (ImageView) findViewById(R.id.image);
        final AppCompatRatingBar ratingBar = (AppCompatRatingBar) findViewById(R.id.ratingbar);

        titleTv.setText(mplace.getName());
        locationTv.setText(mplace.getAddress().get("street"));
        GlideApp.with(getApplicationContext()).load(mplace.getImageURL2()).fitCenter().into(imageView);
        ratingBar.setRating(mplace.getValuation());
        ratingTv.setText(mplace.getValuation() + " (" + mplace.getValuations() + ")");

        initReviewsComponent();
        initImagesComponent();
        initCategoriesComponent();
        initToggleSectionComponents();
        initSheduleComponent();
        initFloatingActionButton();
        initialStatus();
    }

    private void initReviewsComponent() {
        reviews = downloadReviews();
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.reviewsrecycler);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        reviewsAdapter = new SimpleReviewsAdapter(this, reviews);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(reviewsAdapter);
    }

    private List<ReviewModel> downloadReviews() {
        //TODO download reviews for current place only
        reviews = new LinkedList<>();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("reviews").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final QuerySnapshot reviewsSnapshot = task.getResult();
                            if (reviewsSnapshot.isEmpty()) {
                                Log.d("PlaceOverviewActivity", "There is no reviews in database");
                            } else {
                                final List<DocumentSnapshot> documents = reviewsSnapshot.getDocuments();
                                for (DocumentSnapshot doccument : documents) {
                                    final ReviewModel review = doccument.toObject(ReviewModel.class);
                                    reviews.add(review);
                                }
                                Log.d("PlaceOverviewActivity", "Reviews information retrieved succesfully");
                                reviewsAdapter.notifyDataSetChanged();
                            }

                        } else {
                            Log.d("MapsActivity", "Error getting documents: ", task.getException());
                            finish();
                        }
                    }
                });

        return reviews;
    }

    private void downloadMenu() {
        //TODO download reviews for current place only
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("menus")
                //.whereEqualTo("placeCode", mplace.getCode())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final QuerySnapshot menuSnapshot = task.getResult();
                            if (menuSnapshot.isEmpty()) {
                                Log.d("PlaceSelectedActivity", "There is no menus in database");
                            } else {
                                final List<DocumentSnapshot> documents = menuSnapshot.getDocuments();
                                for (DocumentSnapshot doccument : documents) {
                                    final MenuModel menu = doccument.toObject(MenuModel.class);
                                    mMenu = menu;
                                }
                                Log.d("PlaceSelectedActivity", "Menu information retrieved succesfully");
                                menuItems = mMenu.getItems();
                                //menuAdapter.notifyDataSetChanged();
                            }

                        } else {
                            Log.d("PlaceSelectedActivity", "Error getting documents: ", task.getException());
                            finish();
                        }
                    }
                });
    }

    private void showMenuDialog() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final DialogMenuFragment newFragment = new DialogMenuFragment();
        newFragment.setPlace(mplace);
        newFragment.setItemModelList(menuItems);
        newFragment.setCanDemand(false);
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
    }

    private void initImagesComponent() {
        final List<String> imagesUrls = mplace.getImageUrls() != null ? mplace.getImageUrls() : mockImageUrls();
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.imagesrecycler);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        final ImagesAdapter imagesAdapter = new ImagesAdapter(this, imagesUrls);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(imagesAdapter);
    }

    //TODO Utils
    public static List<String> mockImageUrls() {
        final List<String> urls = new ArrayList<>();
        urls.add("https://www.businessesforsale.co.za/southafrican/static/articleimage?articleId=12980&name=image2.jpg");
        urls.add("https://muchosnegociosrentables.com/wp-content/uploads/2015/09/C%C3%B3mo-abrir-una-cafeter%C3%ADa-o-coffee-shop.jpg");
        urls.add("https://cdn.londonandpartners.com/asset/this-is-a-coffee-shop_this-is-a-coffee-shop-image-courtesy-of-this-is-a-coffee-shop_55286b7ded38f0ec3b323e0220c11c4b.jpg");
        urls.add("https://res.cloudinary.com/cdt-76/frenchcoffeeshop3.jpg");
        urls.add("https://images.pexels.com/photos/683039/pexels-photo-683039.jpeg?auto=compress&cs=tinysrgb&h=350");
        urls.add("https://www.liverpool-one.com/wp-content/uploads/2016/10/coffee-shops.jpg");
        return urls;
    }

    private void initCategoriesComponent() {
        final NachoTextView et_tag = (NachoTextView) findViewById(R.id.et_tag);
        final List<String> items = new ArrayList<>();
        items.addAll(mplace.getCategories().keySet());
        et_tag.setText(items);
        et_tag.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL);
    }

    private void initialStatus() {
        // expand first description
        // toggleArrow(bt_toggle_description);
        // lyt_expand_description.setVisibility(View.VISIBLE);
    }

    private void initToggleSectionComponents() {
        // section reviews
        final ImageButton bt_toggle_reviews = (ImageButton) findViewById(R.id.bt_toggle_reviews);
        lyt_expand_reviews = (View) findViewById(R.id.lyt_expand_reviews);
        bt_toggle_reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSection(view, lyt_expand_reviews);
            }
        });

        // section schedul
        final ImageButton bt_toggle_schedule = (ImageButton) findViewById(R.id.bt_toggle_warranty);
        lyt_expand_warranty = (View) findViewById(R.id.lyt_expand_warranty);
        bt_toggle_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSection(view, lyt_expand_warranty);
            }
        });

        // section description
        final ImageButton bt_toggle_description = (ImageButton) findViewById(R.id.bt_toggle_description);
        lyt_expand_description = (View) findViewById(R.id.lyt_expand_description);
        bt_toggle_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSection(view, lyt_expand_description);
            }
        });

        // section location
        final ImageButton bt_toggle_location = (ImageButton) findViewById(R.id.bt_toggle_location);
        lyt_expand_location = (View) findViewById(R.id.lyt_expand_location);
        bt_toggle_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSection(view, lyt_expand_location);
            }
        });

        // section location
        final ImageButton bt_toggle_images = (ImageButton) findViewById(R.id.bt_toggle_images);
        lyt_expand_images = (View) findViewById(R.id.lyt_expand_images);
        bt_toggle_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSection(view, lyt_expand_images);
            }
        });
    }

    private void initFloatingActionButton() {
        downloadMenu();
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuDialog();
            }
        });
    }

    private void initSheduleComponent() {
        final Map<String, String> schedule = mplace.getSchedule();
        final TextView ro1Tv = ((TextView) findViewById(R.id.row1));
        final TextView ro2Tv = ((TextView) findViewById(R.id.row2));
        final TextView ro3Tv = ((TextView) findViewById(R.id.row3));
        final TextView ro4Tv = ((TextView) findViewById(R.id.row4));
        final TextView ro5Tv = ((TextView) findViewById(R.id.row5));
        final TextView ro6Tv = ((TextView) findViewById(R.id.row6));
        final TextView ro7Tv = ((TextView) findViewById(R.id.row7));
        ro1Tv.setText(getScheduleDay(schedule, "Monday"));
        ro2Tv.setText(getScheduleDay(schedule, "Tuesday"));
        ro3Tv.setText(getScheduleDay(schedule, "Wednesday"));
        ro4Tv.setText(getScheduleDay(schedule, "Thursday"));
        ro5Tv.setText(getScheduleDay(schedule, "Friday"));
        ro6Tv.setText(getScheduleDay(schedule, "Saturday"));
        ro7Tv.setText(getScheduleDay(schedule, "Sunday"));
    }

    //TODO utils
    public static String getScheduleDay(final Map<String, String> schedule, final String day) {
        final String sch = schedule.get(day);
        return sch != null ? sch : "CLOSED";
    }

    private void toggleSection(View bt, final View lyt) {
        boolean show = toggleArrow(bt);
        if (show) {
            ViewAnimations.expand(lyt, new ViewAnimations.AnimListener() {
                @Override
                public void onFinish() {
                    Utils.nestedScrollTo(nested_scroll_view, lyt);
                }
            });
        } else {
            ViewAnimations.collapse(lyt);
        }
    }

    public boolean toggleArrow(View view) {
        if (view.getRotation() == 0) {
            view.animate().setDuration(200).rotation(180);
            return true;
        } else {
            view.animate().setDuration(200).rotation(0);
            return false;
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        final MarkerOptions marker = new MarkerOptions().position(mplace.getLocation()).title(mplace.getName()).icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mMap.addMarker(marker);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mplace.getLocation(), ZOOM));
    }
}
