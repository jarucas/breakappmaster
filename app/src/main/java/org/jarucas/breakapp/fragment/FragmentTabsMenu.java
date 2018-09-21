package org.jarucas.breakapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jarucas.breakapp.App;
import org.jarucas.breakapp.R;
import org.jarucas.breakapp.adapter.SimpleMenuItemAdapter;
import org.jarucas.breakapp.dto.ItemModel;

import java.util.List;

/**
 * Created by Javier on 10/09/2018.
 */

public class FragmentTabsMenu extends Fragment {

    private String title;
    private List<ItemModel> items;
    private Boolean canDemand;

    public FragmentTabsMenu() {
    }

    public static FragmentTabsMenu newInstance(final String title, final List<ItemModel> items, final Boolean canDemand) {
        final FragmentTabsMenu fragmentTabsMenu = new FragmentTabsMenu();
        fragmentTabsMenu.setItems(items);
        fragmentTabsMenu.setTitle(title);
        fragmentTabsMenu.setCanDemand(canDemand);
        return fragmentTabsMenu;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tabs_menu, container, false);

        ((TextView) root.findViewById(R.id.titlemenudialog)).setText(title);
        final RecyclerView recyclerView = root.findViewById(R.id.fragment_menu_recycler);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL, false);
        final SimpleMenuItemAdapter simpleMenuItemAdapter = new SimpleMenuItemAdapter(App.getContext(), items, canDemand);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(simpleMenuItemAdapter);
        //simpleMenuItemAdapter.notifyDataSetChanged();

        return root;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ItemModel> getItems() {
        return items;
    }

    public void setItems(List<ItemModel> items) {
        this.items = items;
    }

    public Boolean getCanDemand() {
        return canDemand;
    }

    public void setCanDemand(Boolean canDemand) {
        this.canDemand = canDemand;
    }
}
