package org.jarucas.breakapp.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import org.jarucas.breakapp.App;
import org.jarucas.breakapp.GlideApp;
import org.jarucas.breakapp.R;
import org.jarucas.breakapp.adapter.PaymentlistAdapter;
import org.jarucas.breakapp.dto.PaymentModel;
import org.jarucas.breakapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MyPaymentActivity extends AppCompatActivity {

    private ImageView bgImage;
    private FloatingActionButton fabPayment;
    private RelativeLayout emptyListView;

    private RecyclerView recyclerView;
    private PaymentlistAdapter paymentlistAdapter;
    private List<PaymentModel> paymentList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_payment);

        initToolbar();
        initComponent();
        //TODO - REMOVE CARDS
    }

    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final TextView titleTv = (TextView) findViewById(R.id.title);
        titleTv.setText(getString(R.string.mypayment_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void initComponent() {

        final CircularImageView profileImage = (CircularImageView) findViewById(R.id.imageview);
        final TextView profileName = (TextView) findViewById(R.id.username);
        GlideApp.with(getApplicationContext()).load(App.getmUser().getPhotoUrl()).fitCenter().into(profileImage);
        profileName.setText(App.getmUser().getDisplayName());

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //TODO - obtain paymentlist
        paymentList = new ArrayList<>();

        paymentlistAdapter = new PaymentlistAdapter(paymentList);
        recyclerView.setAdapter(paymentlistAdapter);

        emptyListView = (RelativeLayout) findViewById(R.id.rl_noplaces);
        emptyListView.setVisibility(View.VISIBLE);
        bgImage = (ImageView) findViewById(R.id.bg_image_payments);
        ViewGroup.LayoutParams params = bgImage.getLayoutParams();
        params.height = Utils.getScreenWidth();
        bgImage.setLayoutParams(params);

        fabPayment = (FloatingActionButton) findViewById(R.id.fab_mypayments);
        fabPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyListView.setVisibility(View.GONE);
                paymentList.add(createPayment());
                paymentlistAdapter.notifyDataSetChanged();
            }
        });
    }

    private PaymentModel createPayment() {
        return new PaymentModel("**** **** **** 9867", "John Mamon", "05 / 24", "981", "mastercard");
    }


}
