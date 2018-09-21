package org.jarucas.breakapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jarucas.breakapp.R;
import org.jarucas.breakapp.dto.ReviewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Javier on 09/09/2018.
 */

public class SimpleReviewsAdapter extends RecyclerView.Adapter<SimpleReviewsAdapter.SimpleReviewHolder> {

    private List<ReviewModel> reviews = new ArrayList<>();
    private Context context;

    public SimpleReviewsAdapter(final Context context, final List<ReviewModel> reviews) {
        this.reviews = reviews;
        this.context = context;
    }

    @NonNull
    @Override
    public SimpleReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_review_simple, parent, false);
        return new SimpleReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleReviewHolder holder, final int position) {
        final ReviewModel review = reviews.get(position);
        holder.titleTv.setText(review.getTitle());
        holder.contentTv.setText(review.getContent());
        holder.userTv.setText(review.getUserName());
        holder.ratingTv.setText(Float.toString(review.getRating()));
        holder.ratingCompat.setRating(review.getRating());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class SimpleReviewHolder extends RecyclerView.ViewHolder {

        TextView titleTv;
        TextView contentTv;
        TextView userTv;
        TextView ratingTv;
        AppCompatRatingBar ratingCompat;

        public SimpleReviewHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.title);
            contentTv = itemView.findViewById(R.id.content);
            userTv = itemView.findViewById(R.id.placedby);
            ratingTv = itemView.findViewById(R.id.placeRatingNumber);
            ratingCompat = itemView.findViewById(R.id.placeRating);
        }
    }

}
