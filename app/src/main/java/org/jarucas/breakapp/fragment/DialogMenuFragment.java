package org.jarucas.breakapp.fragment;

import android.app.Dialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import org.jarucas.breakapp.R;
import org.jarucas.breakapp.adapter.CustomMenuAdapter;
import org.jarucas.breakapp.dto.ItemModel;
import org.jarucas.breakapp.dto.PlaceModel;
import org.jarucas.breakapp.listener.CustomListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Javier on 09/09/2018.
 */

public class DialogMenuFragment extends DialogFragment {

    private View root_view;
    private PlaceModel place;
    private List<ItemModel> itemModelList;
    private Boolean canDemand;
    private CustomListener customListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.dialog_menu, container, false);
        ((TextView) root_view.findViewById(R.id.placetitle)).setText(place.getName());
        final ViewPager viewPager = root_view.findViewById(R.id.viewpager_menu);
        final TabLayout tabLayout = (TabLayout) root_view.findViewById(R.id.tab_layout);

        final List<ItemModel> drinkList = new ArrayList<>();
        final List<ItemModel> eatList = new ArrayList<>();
        for (ItemModel item : itemModelList) {
            if (item.getCategory().containsKey("drink")) {
                drinkList.add(item);
            }

            if (item.getCategory().containsKey("eat")) {
                eatList.add(item);
            }
        }

        final CustomMenuAdapter customMenuAdapter = new CustomMenuAdapter(getChildFragmentManager());
        customMenuAdapter.addFragment(FragmentTabsMenu.newInstance("All the menu:", itemModelList, canDemand));
        customMenuAdapter.addFragment(FragmentTabsMenu.newInstance("To Drink:", drinkList, canDemand));
        customMenuAdapter.addFragment(FragmentTabsMenu.newInstance("To Eat:", eatList, canDemand));
        viewPager.setAdapter(customMenuAdapter);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_format_list_bulleted);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_local_cafe);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_restaurant);

        tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(final TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        final FloatingActionButton floatingActionButton = (FloatingActionButton) root_view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCustomListener();
                dismiss();
            }
        });

        return root_view;
    }

    private void launchCustomListener() {
        if (customListener != null) {
            customListener.onEvent(this);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public View getRoot_view() {
        return root_view;
    }

    public void setRoot_view(View root_view) {
        this.root_view = root_view;
    }

    public PlaceModel getPlace() {
        return place;
    }

    public void setPlace(PlaceModel place) {
        this.place = place;
    }

    public List<ItemModel> getItemModelList() {
        return itemModelList;
    }

    public void setItemModelList(List<ItemModel> itemModelList) {
        this.itemModelList = itemModelList;
    }

    public Boolean getCanDemand() {
        return canDemand;
    }

    public void setCanDemand(Boolean canDemand) {
        this.canDemand = canDemand;
    }

    public void setCustomListener(CustomListener customListener) {
        this.customListener = customListener;
    }
}
