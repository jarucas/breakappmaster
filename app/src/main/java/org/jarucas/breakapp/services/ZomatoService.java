package org.jarucas.breakapp.services;

import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import org.jarucas.breakapp.App;
import org.jarucas.breakapp.R;
import org.jarucas.breakapp.dto.PlaceModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Javier on 31/08/2018.
 */

public class ZomatoService extends AsyncTask<Void, Void, Void> {

    private String mZomatoString;
    private List<PlaceModel> restaurants;
    private static final double LAT = (double) 39.4697500;
    private static final double LON = (double) -0.37739;

    @Override
    protected Void doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        restaurants = new ArrayList<>();
        final Uri builtUri = Uri.parse(App.getContext().getString(R.string.zomato_api));
        URL url;
        try {
            url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("user-key", App.getContext().getString(R.string.zomato_key));
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                //Nothing to do
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            mZomatoString = buffer.toString();
            final JSONObject jsonObject = new JSONObject(mZomatoString);
            Log.v("Response", jsonObject.toString());
            JSONArray restaurantsArray = jsonObject.getJSONArray("restaurants");
            //list = new ArrayList<>();
            for (int i = 0; i < restaurantsArray.length(); i++) {

                Log.v("BRAD_", i + "");

                JSONObject jRestaurant = (JSONObject) restaurantsArray.get(i);
                jRestaurant = jRestaurant.getJSONObject("restaurant");
                JSONObject jLocattion = jRestaurant.getJSONObject("location");
                JSONObject jRating = jRestaurant.getJSONObject("user_rating");

                final String code = jRestaurant.getString("id");
                final String name = jRestaurant.getString("name");
                //final String street = jLocattion.getString("address");
                //final String city = jLocattion.getString("city");
                //final String locality = jLocattion.getString("locality");
                //final String zipcode = jLocattion.getString("zipcode");
                final double lat = jLocattion.getDouble("latitude");
                final double lon = jLocattion.getDouble("longitude");
                final String currency = jRestaurant.getString("currency");
                final int cost = jRestaurant.getInt("average_cost_for_two");
                final String imageUrlThumbs = jRestaurant.getString("thumb");
                final String imageUrl = jRestaurant.getString("featured_image");
                final float rating = (float) jRating.getDouble("aggregate_rating");
                final int ratings = jRating.getInt("votes");
                final String[] categories = jRestaurant.getString("cuisines").split(",");

                final Map<String, String> schedule = new HashMap<>();
                schedule.put("Monday", "8:00 - 21:00");
                schedule.put("Tuesday", "8:00 - 21:00");
                schedule.put("Wednesday", "8:00 - 21:00");
                schedule.put("Thursday", "8:00 - 21:00");
                schedule.put("Friday", "8:00 - 21:00");
                schedule.put("Saturday", "8:00 - 21:00");

                //TODO acotate in Spain
                final Map<String, Double> location = new HashMap<>();
                final double latSp = lat + 73.4;
                final double lonSp = lon - 18.81;
                location.put("latitude", latSp);
                location.put("longitude", lonSp);

                final int maxResults = 1;
                Geocoder gc = new Geocoder(App.getContext(), Locale.getDefault());
                final List<Address> addresses = gc.getFromLocation(latSp, lonSp, maxResults);
                final String country = addresses.iterator().next().getCountryName();
                final String locality = addresses.iterator().next().getLocality();
                final String zipcode = addresses.iterator().next().getPostalCode();
                final String street = addresses.iterator().next().getAddressLine(0);
                final Map<String, String> address = new HashMap<>();
                address.put("city", locality);
                address.put("street", street);
                address.put("zipcode", zipcode);
                address.put("location", locality);
                address.put("country", country);

                final Map<String, Boolean> cat = new HashMap<>();
                for (String category : categories) {
                    cat.put(category, true);
                }

                //TODO
                final String phone = "+34963292827";
                final String description = "This is a placeholder for a nice description";

                PlaceModel restaurant = new PlaceModel();
                restaurant.setCode(code);
                restaurant.setName(name);
                //restaurant.setLocation(new MyLatLng(latSp, lonSp));
                restaurant.setLocation(location);
                restaurant.setPhone(phone);
                restaurant.setDescription(description);

                restaurant.setImageURL2(imageUrl);
                restaurant.setImageURL1(imageUrlThumbs);
                restaurant.setValuation(rating);
                restaurant.setValuations(ratings);

                restaurant.setAddress(address);
                restaurant.setSchedule(schedule);
                restaurant.setCategories(cat);

                restaurants.add(restaurant);
            }
        } catch (final MalformedURLException e) {
            Log.e("ZomatoService", e.getMessage());
        } catch (final IOException e) {
            Log.e("ZomatoService", e.getMessage());
        } catch (final JSONException e) {
            Log.e("ZomatoService", e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("ZomatoService", "Error closing stream", e);
                }
            }
        }

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        for (PlaceModel place : restaurants) {
            db.collection("places").document(place.getCode()).set(place);
        }
        Log.d("ZomatoService", "Places updated");

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

    }
}

