package org.jarucas.breakapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.jarucas.breakapp.App;
import org.jarucas.breakapp.R;
import org.jarucas.breakapp.dto.CartItemModel;
import org.jarucas.breakapp.dto.ItemModel;
import org.jarucas.breakapp.listener.CustomListener;
import org.jarucas.breakapp.services.SwipeItemTouchHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Javier on 10/09/2018.
 */

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartMenuItemHolder> implements SwipeItemTouchHelper.SwipeHelperAdapter {

    private List<CartItemModel> items = new ArrayList<>();
    private List<CartItemModel> items_swiped = new ArrayList<>();
    private Context context;
    private CustomListener customCartItemAdapterListener;

    public void setCustomCartItemAdapterListener(CustomListener customCartItemAdapterListener) {
        this.customCartItemAdapterListener = customCartItemAdapterListener;
    }

    public CartItemAdapter(final Context context, final List<CartItemModel> items) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public CartMenuItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_cart, parent, false);
        return new CartMenuItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartMenuItemHolder holder, final int position) {
        final CartItemModel itemModel = items.get(position);
        holder.imageButton.setImageResource(getImageResource(itemModel));
        holder.itemTitle.setText(itemModel.getName() + ((itemModel.getQuantity() > 1) ? "  (x" + itemModel.getQuantity() + ")" : ""));
        holder.itemPrice.setText(Float.toString(itemModel.getPrice() * itemModel.getQuantity()) + " €");

        holder.itemlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemModel.setQuantity(itemModel.getQuantity() + 1);
                notifyItemChanged(position);
                App.setCart(items);
                updateCartPrice();
            }
        });

        holder.bt_undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.get(position).setSwipped(false);
                items_swiped.remove(items.get(position));
                notifyItemChanged(position);
            }
        });


        if (itemModel.isSwipped()) {
            holder.goneitemlayout.setVisibility(View.VISIBLE);
        } else {
            holder.goneitemlayout.setVisibility(View.GONE);
        }
    }

    private void updateCartPrice() {
        if (customCartItemAdapterListener != null) {
            customCartItemAdapterListener.onEvent(this);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                for (CartItemModel item : items_swiped) {
                    int index_removed = items.indexOf(item);
                    if (index_removed != -1) {
                        items.remove(index_removed);
                        notifyItemRemoved(index_removed);
                    }
                }
                items_swiped.clear();
                App.setCart(items);
                updateCartPrice();
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        super.onAttachedToRecyclerView(recyclerView);
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

    @Override
    public void onItemDismiss(int position) {
        // handle when double swipe
        final CartItemModel cartItemModel = items.get(position);
        if (cartItemModel.isSwipped()) {
            items_swiped.remove(cartItemModel);
            items.remove(position);
            notifyItemRemoved(position);
            App.setCart(items);
            updateCartPrice();
            return;
        }

        if (cartItemModel.getQuantity() > 1) {
            cartItemModel.setQuantity(cartItemModel.getQuantity() - 1);
            notifyDataSetChanged();
        } else {
            cartItemModel.setSwipped(true);
            items_swiped.add(cartItemModel);
            notifyItemChanged(position);
        }
        App.setCart(items);
        updateCartPrice();

    }

    public class CartMenuItemHolder extends RecyclerView.ViewHolder implements SwipeItemTouchHelper.TouchViewHolder {

        View itemlayout;
        View goneitemlayout;
        ImageButton imageButton;
        TextView itemTitle;
        TextView itemPrice;
        Button bt_undo;

        public CartMenuItemHolder(View itemView) {
            super(itemView);
            itemlayout = itemView.findViewById(R.id.itemlayout);
            goneitemlayout = itemView.findViewById(R.id.swipeundo);
            imageButton = itemView.findViewById(R.id.categoryib);
            itemTitle = itemView.findViewById(R.id.itemtitle);
            itemPrice = itemView.findViewById(R.id.itemprice);
            bt_undo = (Button) itemView.findViewById(R.id.bt_undo);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(context.getResources().getColor(R.color.grey_5));
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

}
