package org.jarucas.breakapp.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jarucas.breakapp.GlideApp;
import org.jarucas.breakapp.R;
import org.jarucas.breakapp.dto.PlaceModel;
import org.jarucas.breakapp.dto.ReviewModel;
import org.jarucas.breakapp.utils.ItemAnimations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Javier on 14/09/2018.
 */

public class ReviewsTitleAdapter extends RecyclerView.Adapter<ReviewsTitleAdapter.ReviewsViewHolder> {

    private List<ReviewModel> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;
    private int animation_type = 0;

    public interface OnItemClickListener {
        void onItemClick(View view, ReviewModel obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public ReviewsTitleAdapter(final Context context, final List<ReviewModel> items, final int animation_type) {
        this.items = items;
        ctx = context;
        this.animation_type = animation_type;
    }

    @Override
    public ReviewsViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_review, parent, false);
        return new ReviewsViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ReviewsViewHolder holder, final int position) {
        ReviewModel reviewModel = items.get(position);
        holder.placeTv.setText(reviewModel.getPlaceName());
        holder.titleTv.setText(reviewModel.getTitle());
        holder.contentTv.setText(reviewModel.getContent());
        holder.ratingTv.setText(Float.toString(reviewModel.getRating()));
        holder.ratingCompat.setRating(reviewModel.getRating());

        GlideApp.with(ctx).load(reviewModel.getPlaceImageUrl()).fitCenter().into(holder.image);

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

    public class ReviewsViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        TextView placeTv;
        TextView contentTv;
        TextView titleTv;
        TextView ratingTv;
        AppCompatRatingBar ratingCompat;

        public ReviewsViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            placeTv = itemView.findViewById(R.id.placetitle);
            contentTv = itemView.findViewById(R.id.content);
            titleTv = itemView.findViewById(R.id.title);
            ratingTv = itemView.findViewById(R.id.placeRatingNumber);
            ratingCompat = itemView.findViewById(R.id.placeRating);
        }
    }
}
