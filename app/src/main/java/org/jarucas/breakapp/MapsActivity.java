package org.jarucas.breakapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Collections;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final int ZOOM = 20;
    private GoogleMap mMap;
    private UserLockBottomSheetBehavior bottomSheetBehavior;
    private FloatingActionButton floatingActionButton;
    private List<Object> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        places = downloadPlacesList();
        initNavigationMenu();
        initMapsFragment();
        initComponents();
    }

    //TODO- Connect with firebase and
    private List<Object> downloadPlacesList() {
        return Collections.EMPTY_LIST;
    }

    private void initNavigationMenu() {
        final NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ImageButton imageButton = (ImageButton) findViewById(R.id.bt_menu);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem item) {
                //TODO - Complete the menu behavior and remove this shity toast
                Intent i;
                switch (item.getItemId()) {

                    case R.id.nav_profile:
                        i = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(i);
                        break;
                    case R.id.nav_close:
                        closeSession();
                        break;
                    case R.id.nav_tutorial:
                        i = new Intent(getApplicationContext(), TutorialActivity.class);
                        startActivity(i);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }


    private void initComponents() {

        LinearLayout llBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = (UserLockBottomSheetBehavior) BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }


        });

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_directions);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                   //TODO

                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    floatingActionButton.setImageResource(R.drawable.ic_location);
                    ((TextView) findViewById(R.id.placeName)).setText("Pick a place");
                    //TODO
                }
            }
        });
    }

    private void closeSession() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });
    }


    private void initMapsFragment() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        putPlacesIntoMap();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                configureSheetInformation(marker.getPosition());
                floatingActionButton.setImageResource(R.drawable.ic_close);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                try {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), ZOOM));
                } catch (Exception e) {
                }
                return true;
            }
        });
    }

    private void putPlacesIntoMap() {
        final LatLng ccAqua = new LatLng(39.4564876, -0.3484014);
        MarkerOptions marker = new MarkerOptions().position(ccAqua).title("Marker in cc. Aqua");
        mMap.addMarker(marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ccAqua, 14));
    }

    private void configureSheetInformation(LatLng position) {
        ((TextView) findViewById(R.id.placeName)).setText("Jamaica Lunch");
        ((TextView) findViewById(R.id.placeRatingNumber)).setText("4.5 (51)");
        ((TextView) findViewById(R.id.placeDescription1)).setText("El mejor café");
        ((TextView) findViewById(R.id.placePhone)).setText("+34 639 246 123");
        ((TextView) findViewById(R.id.placeAddress)).setText("Calle Menorca 16, CC.Aqua");
        ((TextView) findViewById(R.id.placeSchedule)).setText("L-D: 9AM - 20PM");
        ((AppCompatRatingBar) findViewById(R.id.placeRating)).setRating(4.5f);
        //TODO
    }
}
