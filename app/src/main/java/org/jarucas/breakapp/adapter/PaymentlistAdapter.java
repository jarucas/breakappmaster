package org.jarucas.breakapp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jarucas.breakapp.R;
import org.jarucas.breakapp.dto.PaymentModel;

import java.util.List;

/**
 * Created by Javier on 26/08/2018.
 */

public class PaymentlistAdapter extends RecyclerView.Adapter<PaymentlistAdapter.PaymentViewHolder>{

    private List<PaymentModel> payments;

    public PaymentlistAdapter(List<PaymentModel> payments) {
        this.payments = payments;
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_payment, parent, false);
        PaymentViewHolder pvh = new PaymentViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        holder.getCardnumber().setText(payments.get(position).getCardNumber());
        holder.getDate().setText(payments.get(position).getExpireDate());
        holder.getNickname().setText(payments.get(position).getNickName());
        holder.getCardlogo().setImageResource(payments.get(position).getType().equals("visa")? R.drawable.ic_visa : R.drawable.ic_master_card);
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class PaymentViewHolder extends RecyclerView.ViewHolder {
        private CardView cv;
        private TextView nickname;
        private TextView date;
        private TextView cvv;
        private TextView cardnumber;
        private ImageView cardlogo;

        public PaymentViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.payment_card);
            nickname = (TextView)itemView.findViewById(R.id.nick_name);
            date = (TextView)itemView.findViewById(R.id.expire);
            cvv = (TextView)itemView.findViewById(R.id.cvv_number);
            cardnumber = (TextView)itemView.findViewById(R.id.card_number);
            cardlogo = (ImageView)itemView.findViewById(R.id.card_logo);
        }

        public CardView getCv() {
            return cv;
        }

        public void setCv(CardView cv) {
            this.cv = cv;
        }

        public TextView getNickname() {
            return nickname;
        }

        public void setNickname(TextView nickname) {
            this.nickname = nickname;
        }

        public TextView getDate() {
            return date;
        }

        public void setDate(TextView date) {
            this.date = date;
        }

        public TextView getCvv() {
            return cvv;
        }

        public void setCvv(TextView cvv) {
            this.cvv = cvv;
        }

        public TextView getCardnumber() {
            return cardnumber;
        }

        public void setCardnumber(TextView cardnumber) {
            this.cardnumber = cardnumber;
        }

        public ImageView getCardlogo() {
            return cardlogo;
        }

        public void setCardlogo(ImageView cardlogo) {
            this.cardlogo = cardlogo;
        }
    }



}
