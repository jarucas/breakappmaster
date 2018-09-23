package org.jarucas.breakapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.jarucas.breakapp.App;
import org.jarucas.breakapp.GlideApp;
import org.jarucas.breakapp.R;
import org.jarucas.breakapp.adapter.PlacePagerAdapter;
import org.jarucas.breakapp.dto.PlaceModel;
import org.jarucas.breakapp.dto.TableModel;
import org.jarucas.breakapp.dto.UserModel;
import org.jarucas.breakapp.utils.Utils;
import org.jarucas.breakapp.utils.ViewAnimations;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int ZOOM = 15;
    private static final int BARCODE_READER_REQUEST_CODE = 1;
    private static final int RC_HANDLE_LOCATION_PERM = 3;

    private GoogleMap mMap;
    private LatLng myLocation;
    private NavigationView navView;
    private List<PlaceModel> places;
    private List<MarkerOptions> markers;
    private ViewPager viewPager;
    private PlacePagerAdapter placePagerAdapter;
    private PlaceAutocompleteFragment placeAutoComplete;
    private boolean rotate = false;
    private View layoutSsat;
    private View layoutScan;
    private View layoutGo;
    private View layoutCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        places = downloadPlacesList();
        markers = new LinkedList<>();
        initPlaceAutocomplete();
        initNavigationMenu();
        loadUserInformation();
        initMapsFragment();
        initViewPager();
        initFabButtons();
    }

    private void initPlaceAutocomplete() {
        placeAutoComplete = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete);
        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                //Utils.showCustomToast(getParent(), "Place selected: " + place.getName());
                Log.d("Maps", "Place selected: " + place.getName());
                final TextView searchTv = (TextView) findViewById(R.id.search_text);
                searchTv.setText(place.getName());
                //TODO Update Places
            }

            @Override
            public void onError(Status status) {
                //Utils.showCustomToast(getParent(), "An error occurred: " + status);
                Log.d("Maps", "An error occurred: " + status);
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (BARCODE_READER_REQUEST_CODE == requestCode) {
            if (CommonStatusCodes.SUCCESS == resultCode) {
                if (data != null) {
                    afterScanTableCode(data);
                } else {
                    showWarningDialog(getString(R.string.ERROR_UNKNOWN), getString(R.string.error_unknown_desc));
                }
            } else {
                showWarningDialog(getString(R.string.ERROR_UNKNOWN), getString(R.string.error_unknown_desc));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initFabButtons() {
        layoutSsat = findViewById(R.id.lyt_sat);
        layoutScan = findViewById(R.id.lyt_scan);
        layoutGo = findViewById(R.id.lyt_go);
        layoutCenter = findViewById(R.id.lyt_center);

        final FloatingActionButton fabSat = (FloatingActionButton) findViewById(R.id.fab_sat);
        final FloatingActionButton fabScan = (FloatingActionButton) findViewById(R.id.fab_scan);
        final FloatingActionButton fabGo = (FloatingActionButton) findViewById(R.id.fab_go);
        final FloatingActionButton fabCenter = (FloatingActionButton) findViewById(R.id.fab_center);
        final FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);

        ViewAnimations.initShowOut(layoutSsat);
        ViewAnimations.initShowOut(layoutScan);
        ViewAnimations.initShowOut(layoutGo);
        ViewAnimations.initShowOut(layoutCenter);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFabMode(v);
            }
        });

        fabSat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMapMode();
            }
        });

        fabScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
                startActivityForResult(i, BARCODE_READER_REQUEST_CODE);
                toggleFabMode(fabAdd);
            }
        });

        fabGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PlaceOverviewActivity.class);
                startActivity(i);
                toggleFabMode(fabAdd);
            }
        });

        fabCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerMapOnMyLocation();
                toggleFabMode(fabAdd);
            }
        });
    }

    private void toggleFabMode(final View v) {
        rotate = ViewAnimations.rotateFab(v, !rotate);
        if (rotate) {
            //((FloatingActionButton) v).setImageResource(R.drawable.ic_remove);
            ViewAnimations.showIn(layoutCenter);
            ViewAnimations.showIn(layoutGo);
            ViewAnimations.showIn(layoutScan);
            ViewAnimations.showIn(layoutSsat);
            //back_drop.setVisibility(View.VISIBLE);
        } else {
            //((FloatingActionButton) v).setImageResource(R.drawable.ic_add);
            ViewAnimations.showOut(layoutSsat);
            ViewAnimations.showOut(layoutScan);
            ViewAnimations.showOut(layoutGo);
            ViewAnimations.showOut(layoutCenter);
            // back_drop.setVisibility(View.GONE);
        }

    }

    private void initNavigationMenu() {
        navView = (NavigationView) findViewById(R.id.nav_view);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ImageButton imageButton = (ImageButton) findViewById(R.id.bt_menu);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem item) {
                Intent i;
                switch (item.getItemId()) {

                    case R.id.nav_profile:
                        i = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(i);
                        break;
                    case R.id.nav_finder:
                         drawer.closeDrawers();
                        break;
                    case R.id.nav_bills:
                        i = new Intent(getApplicationContext(), MyBillsActivity.class);
                        startActivity(i);
                        break;
                    case R.id.nav_places:
                        i = new Intent(getApplicationContext(), MyPlacesActivity.class);
                        startActivity(i);
                        break;
                    case R.id.nav_scan:
                        i = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
                        startActivityForResult(i, BARCODE_READER_REQUEST_CODE);
                        break;
                    case R.id.nav_reviews:
                        i = new Intent(getApplicationContext(), MyReviewsActivity.class);
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

    private void initViewPager() {
        places = downloadPlacesList();
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        placePagerAdapter = new PlacePagerAdapter(this, places);
        viewPager.setAdapter(placePagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int pos) {
                zoomInPlace(markers.get(pos));
                final PlaceModel selectedPlace = places.get(pos);
                App.setMplace(selectedPlace);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void afterScanTableCode(final Intent data) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
        final String code = barcode.displayValue;
        db.collection("tables").whereEqualTo("code", code).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final QuerySnapshot tableSnapshot = task.getResult();
                            if (tableSnapshot.isEmpty()) {
                                showWarningDialog(getString(R.string.ERROR_UNKNOWN), getString(R.string.error_unknown_desc));
                            } else {
                                final List<DocumentSnapshot> documents = tableSnapshot.getDocuments();
                                if (documents.size() > 1) {
                                    Log.d("MapsActivity", "There are more than 1 table with same code");
                                    showWarningDialog(getString(R.string.ERROR_UNKNOWN), getString(R.string.error_unknown_desc));
                                }

                                final TableModel table = documents.iterator().next().toObject(TableModel.class);
                                int placeIndex = findPlaceIndexByCode(table.getPlaceid());
                                if (placeIndex < 0) {
                                    showWarningDialog(getString(R.string.ERROR_UNKNOWN), getString(R.string.error_unknown_desc));
                                } else {
                                    final PlaceModel myPlace = placePagerAdapter.getItem(placeIndex);
                                    viewPager.setCurrentItem(placeIndex, true);
                                    zoomInPlace(markers.get(placeIndex));
                                    showAfterScanDialog(code, myPlace);
                                }
                            }
                        } else {
                            showWarningDialog(getString(R.string.ERROR_UNKNOWN), getString(R.string.error_unknown_desc));
                        }
                    }
                });
    }

    private void showAfterScanDialog(final String code, final PlaceModel myPlace) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_light);
        dialog.setCancelable(true);

        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextView titleTv = (TextView) dialog.findViewById(R.id.title);
        final TextView contentTv = (TextView) dialog.findViewById(R.id.content);
        final CircularImageView imageView = ((CircularImageView) dialog.findViewById(R.id.image));
        final ImageButton closeBt = (ImageButton) dialog.findViewById(R.id.bt_close);
        final AppCompatButton goBt = (AppCompatButton) dialog.findViewById(R.id.bt_follow);

        titleTv.setText(myPlace.getName());
        contentTv.setText(code);
        GlideApp.with(getApplicationContext()).load(myPlace.getImageURL1()).fitCenter().into(imageView);
        closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        goBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.setMplace(myPlace);
                Intent intent = new Intent(getApplicationContext(), PlaceSelectedActivity.class);
                startActivity(intent);
                finish();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void showWarningDialog(final String title, final String description) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_warning);
        dialog.setCancelable(true);

        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextView titleTv = (TextView) dialog.findViewById(R.id.title);
        final TextView contentTv = (TextView) dialog.findViewById(R.id.content);
        final AppCompatButton closeBt = (AppCompatButton) dialog.findViewById(R.id.bt_close);

        titleTv.setText(title);
        contentTv.setText(description);
        closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void initMapsFragment() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            requestLocationPermission();
        }

        putPlacesIntoMap();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                viewPager.setCurrentItem(findPlaceIndexByName(marker.getTitle()), true);
                return true;
            }
        });
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //centerMapOnMyLocation();
    }

    private void requestLocationPermission() {
        Log.w("MapsActivity", "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_LOCATION_PERM);
        }
    }

    private void changeMapMode() {
        final int mapType = mMap.getMapType();
        if (4 == mapType) {
            mMap.setMapType(2);
        } else {
            mMap.setMapType(mapType + 1);
        }
    }

    private void zoomInPlace(final MarkerOptions marker) {
        try {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), ZOOM));
        } catch (Exception e) {
            //TODO
        }
    }

    @SuppressLint("MissingPermission")
    private void centerMapOnMyLocation() {
        //TODO ask to set GPS on
        mMap.setMyLocationEnabled(true);
        final Location location = mMap.getMyLocation();
        if (location != null) {
            myLocation = new LatLng(location.getLatitude(),
                    location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,
                    ZOOM));
        }
    }

    private int findPlaceIndexByName(final String title) {
        for (PlaceModel place : places) {
            if (place.getName().equals(title)) {
                return places.indexOf(place);
            }
        }
        return -1;
    }

    private int findPlaceIndexByCode(final String code) {
        for (PlaceModel place : places) {
            if (place.getCode().equals(code)) {
                return places.indexOf(place);
            }
        }
        return -1;
    }

    private void putPlacesIntoMap() {
        for (PlaceModel place : places) {
            final MarkerOptions marker = new MarkerOptions().position(place.getLocation()).title(place.getName()).icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            mMap.addMarker(marker);
            markers.add(marker);
        }
    }

    private List<PlaceModel> downloadPlacesList() {
        //new ZomatoService().execute();

        //TODO
        final List<PlaceModel> placeList = new LinkedList<>();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("MapsActivity", "Retrieving places information");
        db.collection("places").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final QuerySnapshot placeSnapshot = task.getResult();
                            if (placeSnapshot.isEmpty()) {
                                Log.d("MapsActivity", "There is no places in database");
                                finish();
                                //TODO Throw?
                            } else {
                                final List<DocumentSnapshot> documents = placeSnapshot.getDocuments();
                                for (DocumentSnapshot doccument : documents) {
                                    final PlaceModel place = doccument.toObject(PlaceModel.class);
                                    placeList.add(place);
                                }
                                Log.d("MapsActivity", "Places information retrieved succesfully");
                                putPlacesIntoMap();
                                placePagerAdapter.notifyDataSetChanged();
                            }

                        } else {
                            Log.d("MapsActivity", "Error getting documents: ", task.getException());
                            finish();
                        }
                    }
                });
        return placeList;
    }

    //TODO - Move to Utils class
    public static PlaceModel mockPlace(final int pos) {
        final PlaceModel mplace = new PlaceModel();
        mplace.setCode("Jamaica001");
        mplace.setName("Jamaica Lunch " + pos);
        mplace.setDescription("El mejor café");
        mplace.setPhone("+34 639 246 123");
        mplace.setValuation((long) 4.5);
        mplace.setValuations(51);
        //mplace.setLocation(new MyLatLng(39.4564876 + (pos / 10000d), -0.3484014 + (pos / 10000d)));

        final Map<String, String> schedule = new HashMap<>();
        schedule.put("Monday", "8:00 - 21:00");
        mplace.setSchedule(schedule);

        final Map<String, String> address = new HashMap<>();
        address.put("city", "Valencia");
        address.put("street", "C/ Menorca 16");
        address.put("zipcode", "46023");
        mplace.setAddress(address);

        return mplace;
    }

    private void loadUserInformation() {
        final UserModel user = App.getmUser();
        final String displayName = user.getDisplayName();
        final String email = user.getEmail();
        final Uri photoURL = Uri.parse(user.getPhotoUrl());
        final View headerView = navView.getHeaderView(0);

        final TextView tvEmail = (TextView) headerView.findViewById(R.id.drawer_email);
        final TextView tvName = (TextView) headerView.findViewById(R.id.drawer_name);
        final ImageView ivimageDrawer = (ImageView) headerView.findViewById(R.id.drawer_img);

        tvEmail.setText(email);
        tvName.setText(displayName);
        GlideApp.with(getApplicationContext()).load(photoURL).fitCenter().into(ivimageDrawer);
    }

    //TODO - Move to Utils class
    private void closeSession() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                App.setmUser(null);
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });
    }
}
