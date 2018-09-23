package org.jarucas.breakapp.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.util.CollectionUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.jarucas.breakapp.App;
import org.jarucas.breakapp.GlideApp;
import org.jarucas.breakapp.R;
import org.jarucas.breakapp.adapter.BillsAdapter;
import org.jarucas.breakapp.dto.InvoiceModel;
import org.jarucas.breakapp.dto.UserModel;
import org.jarucas.breakapp.fragment.DialogPaymentSuccessFragment;
import org.jarucas.breakapp.listener.CustomListener;
import org.jarucas.breakapp.utils.ItemAnimations;
import org.jarucas.breakapp.utils.Utils;

import java.util.List;

public class MyBillsActivity extends AppCompatActivity {

    private ImageView bgImage;
    private RelativeLayout emptyListView;
    private List<InvoiceModel> bills;
    private RecyclerView recyclerView;
    private BillsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bills);
        bills = App.getmUser().getInvoiceModelList();
        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final TextView titleTv = (TextView) findViewById(R.id.title);
        titleTv.setText(getString(R.string.mybills_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void initComponent() {

        final CircularImageView profileImage = (CircularImageView) findViewById(R.id.imageview);
        final TextView profileName = (TextView) findViewById(R.id.username);
        GlideApp.with(getApplicationContext()).load(App.getmUser().getPhotoUrl()).fitCenter().into(profileImage);
        profileName.setText(App.getmUser().getDisplayName());

        emptyListView = (RelativeLayout) findViewById(R.id.rl_nobills);
        bgImage = (ImageView) findViewById(R.id.bg_image_bills);
        ViewGroup.LayoutParams params = bgImage.getLayoutParams();
        params.height = Utils.getScreenWidth();
        bgImage.setLayoutParams(params);

        if (CollectionUtils.isEmpty(bills)) {
            emptyListView.setVisibility(View.VISIBLE);
        } else {
            emptyListView.setVisibility(View.GONE);
            initRecyclerView();
        }
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layout);
        recyclerView.setHasFixedSize(true);
        mAdapter = new BillsAdapter(this, bills, ItemAnimations.BOTTOM_UP);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new BillsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, InvoiceModel obj, int position) {
                showInvoiceDialog(obj);
            }
        });

        mAdapter.notifyDataSetChanged();

    }

    private void showInvoiceDialog(final InvoiceModel invoice) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final DialogPaymentSuccessFragment newFragment = new DialogPaymentSuccessFragment();
        newFragment.setInvoice(invoice);
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
    }
}
