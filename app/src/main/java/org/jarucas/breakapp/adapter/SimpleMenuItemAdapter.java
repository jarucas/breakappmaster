package org.jarucas.breakapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.jarucas.breakapp.R;
import org.jarucas.breakapp.dto.ItemModel;
import org.jarucas.breakapp.utils.Carts;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Javier on 10/09/2018.
 */

public class SimpleMenuItemAdapter extends RecyclerView.Adapter<SimpleMenuItemAdapter.SimpleMenuItemHolder> {

    private List<ItemModel> items = new ArrayList<>();
    private Context context;
    private Boolean touchable;

    public SimpleMenuItemAdapter(final Context context, final List<ItemModel> items, final Boolean touchable) {
        this.items = items;
        this.context = context;
        this.touchable = touchable;
    }

    @NonNull
    @Override
    public SimpleMenuItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_simple, parent, false);
        return new SimpleMenuItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SimpleMenuItemHolder holder, final int position) {
        final ItemModel itemModel = items.get(position);
        holder.imageButton.setImageResource(getImageResource(itemModel));
        holder.itemTitle.setText(itemModel.getName());
        holder.itemPrice.setText(Float.toString(itemModel.getPrice()));
        if (touchable) {
            holder.itemlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Carts.addSimpleItemToCart(itemModel);
                    Snackbar.make(holder.itemView.getRootView(), "Added " + itemModel.getName() + " to your command", Snackbar.LENGTH_SHORT).show();
                }
            });
        }

    }

    private int getImageResource(final ItemModel itemModel) {
        if (itemModel.getCategory().containsKey("drink")) {
            return R.drawable.ic_local_cafe;
        }

        if (itemModel.getCategory().containsKey("eat")) {
            return R.drawable.ic_restaurant;
        }

        return R.drawable.ic_fitness_center;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class SimpleMenuItemHolder extends RecyclerView.ViewHolder {

        View itemlayout;
        ImageButton imageButton;
        TextView itemTitle;
        TextView itemPrice;

        public SimpleMenuItemHolder(View itemView) {
            super(itemView);
            itemlayout = itemView.findViewById(R.id.itemlayout);
            imageButton = itemView.findViewById(R.id.categoryib);
            itemTitle = itemView.findViewById(R.id.itemtitle);
            itemPrice = itemView.findViewById(R.id.itemprice);
        }
    }

}
