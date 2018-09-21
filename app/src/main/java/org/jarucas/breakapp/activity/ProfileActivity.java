package org.jarucas.breakapp.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.jarucas.breakapp.App;
import org.jarucas.breakapp.GlideApp;
import org.jarucas.breakapp.R;
import org.jarucas.breakapp.dto.UserModel;
import org.jarucas.breakapp.utils.Utils;

import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initToolbar();
        initComponents();
        loadUserInformation();
    }

    private void loadUserInformation() {
        final UserModel user = App.getmUser();
        displayUserInformation(user);
    }

    private void displayUserInformation(final UserModel user) {
        final String displayName = user.getDisplayName();
        final String email = user.getEmail();
        final Uri photoURL = Uri.parse(user.getPhotoUrl());

        final TextView tvEmail = (TextView) findViewById(R.id.profile_email);
        final TextView tvName = (TextView) findViewById(R.id.profile_name);
        final ImageView ivImage = (ImageView) findViewById(R.id.profile_image);

        tvEmail.setText(email);
        tvName.setText(displayName);
        GlideApp.with(getApplicationContext()).load(photoURL).fitCenter().into(ivImage);
    }

    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(R.string.profile_title);
        //Utils.setSystemBarColor(this, R.color.colorPrimary);
    }

    private void showEditProfileDialog() {
        //TODO - Fix dialog profile
        final UserModel user = App.getmUser();
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_profile);
        dialog.setCancelable(true);

        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText etName = (EditText) dialog.findViewById(R.id.et_name);
        final EditText etSurname = (EditText) dialog.findViewById(R.id.et_surname);
        final EditText etPhone = (EditText) dialog.findViewById(R.id.et_phone);
        final EditText etEmail = (EditText) dialog.findViewById(R.id.et_email);
        final Button spnBirthdate = (Button) dialog.findViewById(R.id.spn_birth_date);
        final AppCompatCheckBox cb_allday = (AppCompatCheckBox) dialog.findViewById(R.id.cb_spam);
        final ImageButton btClose = (ImageButton) dialog.findViewById(R.id.bt_close);
        final Button btSave = (Button) dialog.findViewById(R.id.bt_save);

        final String[] displayNames = user.getDisplayName().split(" ");
        etName.setText(displayNames[0]);
        etSurname.setText(displayNames[1] + " " + displayNames[2]);
        etPhone.setText(user.getPhone());
        etEmail.setText(user.getEmail());

        spnBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDatePickerDark(spnBirthdate);
            }
        });

        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO - Update user info and upload to Firestore
                user.setEmail(etEmail.getText().toString());
                user.setPhone(etPhone.getText().toString());
                user.setEmail(etEmail.getText().toString());
                displayUserInformation(user);
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void dialogDatePickerDark(final Button bt) {
        //TODO fix this date
        final Calendar cur_calender = Calendar.getInstance();
        final DatePickerDialog datePicker = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        long date_ship_millis = calendar.getTimeInMillis();
                        bt.setText(Utils.getFormattedDateSimple(date_ship_millis));
                    }
                },
                cur_calender.get(Calendar.YEAR),
                cur_calender.get(Calendar.MONTH),
                cur_calender.get(Calendar.DAY_OF_MONTH)
        );
        //set dark theme
        datePicker.setCancelText("Cancel");
        datePicker.setOkText("Save");
        datePicker.setThemeDark(true);
        datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
        datePicker.setMinDate(cur_calender);
        datePicker.show(getFragmentManager(), "Datepickerdialog");
    }

    private void initComponents() {

        ((ImageView) findViewById(R.id.profile_image)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO - Change profile picture
            }
        });

        ((FloatingActionButton) findViewById(R.id.fab_settings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditProfileDialog();
            }
        });

        ((FloatingActionButton) findViewById(R.id.fab_payment)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MyPaymentActivity.class));
            }
        });

        ((FloatingActionButton) findViewById(R.id.fab_bills)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MyBillsActivity.class));
            }
        });

        ((FloatingActionButton) findViewById(R.id.fab_friends)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MyFriendsActivity.class));
            }
        });

        ((FloatingActionButton) findViewById(R.id.fab_places)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MyPlacesActivity.class));
            }
        });

        ((FloatingActionButton) findViewById(R.id.fab_reviews)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MyReviewsActivity.class));
            }
        });
    }
}
