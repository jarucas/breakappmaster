package org.jarucas.breakapp.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatRatingBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jarucas.breakapp.App;
import org.jarucas.breakapp.GlideApp;
import org.jarucas.breakapp.R;
import org.jarucas.breakapp.dto.PlaceModel;

import java.util.List;
import java.util.Map;

/**
 * Created by Javier on 28/08/2018.
 */

public class PlacePagerAdapter extends PagerAdapter {

    private Context context;
    private List<PlaceModel> items;

    private PlacePagerAdapter.OnItemClickListener onItemClickListener;

    private interface OnItemClickListener {
        void onItemClick(View view, PlaceModel obj);
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public PlacePagerAdapter(final Context context, final List<PlaceModel> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    public PlaceModel getItem(final int pos) {
        return items.get(pos);
    }

    public void setItems(final List<PlaceModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        try {
            return view == ((LinearLayout) object);
        } catch (final ClassCastException e) {
            Log.e("MapsActivity", e.getMessage());
            return false;
        }
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final PlaceModel item = items.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_card_place, container, false);

        ((TextView) view.findViewById(R.id.placeName)).setText(item.getName());
        ((TextView) view.findViewById(R.id.placeRatingNumber)).setText(new StringBuilder().append("(").append(item.getValuations()).append(")").toString());
        ((TextView) view.findViewById(R.id.placeAddress)).setText(extractAddressLine(item));
        ((TextView) view.findViewById(R.id.placeSchedule)).setText(extractScheduleLine(item));
        GlideApp.with(App.getContext()).load(item.getImageURL1()).fitCenter().into((ImageView) view.findViewById(R.id.image));
        ((AppCompatRatingBar) view.findViewById(R.id.placeRating)).setRating(item.getValuation());

        ((ViewPager) container).addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((LinearLayout) object);

    }

    //TODO
    private String extractScheduleLine(final PlaceModel item) {
        return item.getSchedule().get("Monday");
        //TODO
    }

    //TODO Utils
    public static String extractAddressLine(final PlaceModel item) {
        final Map<String, String> address = item.getAddress();
        //return address.get("street") + ", " + address.get("zipcode") + " " + address.get("city");
        return address.get("street");
    }
}
