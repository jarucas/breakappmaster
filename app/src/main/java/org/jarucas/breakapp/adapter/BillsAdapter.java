package org.jarucas.breakapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jarucas.breakapp.GlideApp;
import org.jarucas.breakapp.R;
import org.jarucas.breakapp.dto.InvoiceModel;
import org.jarucas.breakapp.utils.ItemAnimations;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Javier on 14/09/2018.
 */

public class BillsAdapter extends RecyclerView.Adapter<BillsAdapter.BillsViewHolder> {

    private List<InvoiceModel> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;
    private int animation_type = 0;

    public interface OnItemClickListener {
        void onItemClick(View view, InvoiceModel obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public BillsAdapter(final Context context, final List<InvoiceModel> items, final int animation_type) {
        this.items = items;
        ctx = context;
        this.animation_type = animation_type;
    }

    @Override
    public BillsViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill, parent, false);
        return new BillsViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final BillsViewHolder holder, final int position) {
        InvoiceModel invoice = items.get(position);
        holder.name.setText(position + " | " + invoice.getPlaceName());
        holder.description.setText(new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").format(invoice.getDate()));
        GlideApp.with(ctx).load(invoice.getPhotoUrl()).fitCenter().into(holder.image);

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, items.get(position), position);
                }
            }
        });

        setAnimation(holder.itemView, position);

    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                on_attach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private int lastPosition = -1;
    private boolean on_attach = true;

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimations.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }

    public class BillsViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView description;
        public View lyt_parent;

        public BillsViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            name = (TextView) v.findViewById(R.id.name);
            description = (TextView) v.findViewById(R.id.description);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }
}
