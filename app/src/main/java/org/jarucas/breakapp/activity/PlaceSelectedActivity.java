package org.jarucas.breakapp.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jarucas.breakapp.App;
import org.jarucas.breakapp.GlideApp;
import org.jarucas.breakapp.R;
import org.jarucas.breakapp.adapter.CartItemAdapter;
import org.jarucas.breakapp.dto.CartItemModel;
import org.jarucas.breakapp.dto.InvoiceModel;
import org.jarucas.breakapp.dto.MenuModel;
import org.jarucas.breakapp.dto.PlaceModel;
import org.jarucas.breakapp.dto.UserModel;
import org.jarucas.breakapp.fragment.DialogMenuFragment;
import org.jarucas.breakapp.fragment.DialogPaymentSuccessFragment;
import org.jarucas.breakapp.listener.CustomListener;
import org.jarucas.breakapp.services.SwipeItemTouchHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//TODO - Put order on this shit
public class PlaceSelectedActivity extends AppCompatActivity {

    private PlaceModel mplace;
    private ProgressBar progress_bar;
    private AppCompatButton paymentBt;
    private MenuModel mMenu;
    private RecyclerView comandRecyclerView;
    private List<CartItemModel> cart;
    private TextView priceTv;
    private View emptylayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_selected);

        mplace = App.getMplace();
        cart = new ArrayList<>();
        App.setCart(cart);

        downloadMenu();
        initComponent();
    }

    private void initComponent() {
        final TextView titleTv = ((TextView) findViewById(R.id.placetitle));
        priceTv = ((TextView) findViewById(R.id.pricetotaltv));
        final ImageView imageView = (ImageView) findViewById(R.id.placeimage);
        final ImageView menuIv = (ImageView) findViewById(R.id.placemenubutton);

        emptylayout = findViewById(R.id.emptylayout);
        emptylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenuDialog();
            }
        });

        priceTv.setText("0 €");

        paymentBt = (AppCompatButton) findViewById(R.id.bt_payment);
        paymentBt.setClickable(false);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);

        titleTv.setText(mplace.getName());
        GlideApp.with(getApplicationContext()).load(mplace.getImageURL2()).fitCenter().into(imageView);
        menuIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenuDialog();
            }
        });

        paymentBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitPaymentAction();
            }
        });

        initComandRecyclerView();
    }

    private void updateCart() {
        comandRecyclerView.getAdapter().notifyDataSetChanged();
        cart = App.getCart();
        emptyCartBehaviour();
        updatePrice();
    }

    private void emptyCartBehaviour() {
        if (cart.isEmpty()) {
            paymentBt.setClickable(false);
            emptylayout.setVisibility(View.VISIBLE);
        } else {
            paymentBt.setClickable(true);
            emptylayout.setVisibility(View.GONE);
        }
    }

    private void updatePrice() {
        float totalPrice = getTotalPrice();
        priceTv.setText(Float.toString(totalPrice) + " €");
    }

    private float getTotalPrice() {
        float totalPrice = 0;
        for (CartItemModel cartItem : cart) {
            totalPrice = totalPrice + cartItem.getPrice() * cartItem.getQuantity();
        }
        return totalPrice;
    }

    private void initComandRecyclerView() {
        comandRecyclerView = (RecyclerView) findViewById(R.id.recyclercomand);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(App.getContext(), LinearLayoutManager.VERTICAL, false);
        final CartItemAdapter cartItemAdapter = new CartItemAdapter(App.getContext(), cart);
        cartItemAdapter.setCustomCartItemAdapterListener(new CustomListener<CartItemAdapter>() {
            @Override
            public void onEvent(CartItemAdapter customListener) {
                emptyCartBehaviour();
                updatePrice();
            }
        });
        comandRecyclerView.setLayoutManager(layoutManager);
        comandRecyclerView.setItemAnimator(new DefaultItemAnimator());
        comandRecyclerView.setAdapter(cartItemAdapter);

        ItemTouchHelper.Callback callback = new SwipeItemTouchHelper(cartItemAdapter);
        final ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(comandRecyclerView);
    }

    private void submitPaymentAction() {
        progress_bar.setVisibility(View.VISIBLE);
        paymentBt.setAlpha(0f);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showDialogPaymentSuccess();
                progress_bar.setVisibility(View.GONE);
                paymentBt.setAlpha(1f);
            }
        }, 1000);
    }

    private void showDialogPaymentSuccess() {
        final InvoiceModel invoice = generateInvoce();
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final DialogPaymentSuccessFragment newFragment = new DialogPaymentSuccessFragment();
        newFragment.setInvoice(invoice);
        newFragment.setCustomListener(new CustomListener<DialogPaymentSuccessFragment>() {
            @Override
            public void onEvent(final DialogPaymentSuccessFragment customListener) {
                final UserModel userModel = App.getmUser();
                userModel.addInvoice(invoice);
                userModel.addBill(invoice.getCode());
                userModel.addPlaceVisit(invoice.getPlaceCode());
                App.setmUser(userModel);
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(userModel.getGuid()).set(userModel);
                finish();
            }
        });
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
    }

    private void showMenuDialog() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final DialogMenuFragment newFragment = new DialogMenuFragment();
        newFragment.setPlace(mplace);
        newFragment.setItemModelList(mMenu.getItems());
        newFragment.setCanDemand(true);
        newFragment.setCustomListener(new CustomListener<DialogMenuFragment>() {
            @Override
            public void onEvent(final DialogMenuFragment customListener) {
                emptyCartBehaviour();
                updateCart();
            }
        });
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
        fragmentManager.executePendingTransactions();
    }

    private InvoiceModel generateInvoce() {
        final InvoiceModel invoiceModel = new InvoiceModel();

        invoiceModel.setCode(mplace.getCode() + new Date().getTime());
        invoiceModel.setUserCode(App.getmUser().getGuid());
        invoiceModel.setUserName(App.getmUser().getDisplayName());
        invoiceModel.setEmail(App.getmUser().getEmail());
        invoiceModel.setPlaceCode(mplace.getCode());
        invoiceModel.setPlaceName(mplace.getName());
        invoiceModel.setItems(cart);
        invoiceModel.setTotalPrice(getTotalPrice());
        invoiceModel.setDate(new Date());
        invoiceModel.setPaymentModel(null);
        invoiceModel.setPhotoUrl(mplace.getImageURL1());

        return invoiceModel;
    }

    private void downloadMenu() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("menus")
                //.whereEqualTo("placeCode", mplace.getCode())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final QuerySnapshot menuSnapshot = task.getResult();
                            if (menuSnapshot.isEmpty()) {
                                Log.d("PlaceSelectedActivity", "There is no menus in database");
                            } else {
                                final List<DocumentSnapshot> documents = menuSnapshot.getDocuments();
                                for (DocumentSnapshot doccument : documents) {
                                    final MenuModel menu = doccument.toObject(MenuModel.class);
                                    mMenu = menu;
                                }
                                Log.d("PlaceSelectedActivity", "Menu information retrieved succesfully");
                                //menuAdapter.notifyDataSetChanged();
                            }

                        } else {
                            Log.d("PlaceSelectedActivity", "Error getting documents: ", task.getException());
                            finish();
                        }
                    }
                });
    }

}
