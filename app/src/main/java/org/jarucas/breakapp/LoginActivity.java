package org.jarucas.breakapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jarucas.breakapp.dao.User;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Javier on 07/08/2018.
 */

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private static final boolean IS_SMART_LOCK_ENABLED = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        login();
    }

    private void login() {

        final FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        if (usuario != null) {
            checkUserVerification(usuario);
            final String uid = usuario.getUid();
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            loadUserInformation(usuario, uid, db);
            //TODO - loading animation

        } else {
            createSignInIntent();
        }
    }

    private void loadUserInformation(final FirebaseUser usuario, final String uid, final FirebaseFirestore db) {
        db.collection("users").whereEqualTo("guid", uid).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final QuerySnapshot userSnapshot = task.getResult();
                            if (userSnapshot.isEmpty()) {
                                Log.d("LoginActivity", "User does not exist in Database. It will be created");
                                createAuthenticatedUser(uid, usuario, db);
                                Log.d("LoginActivity", "User created into database");
                            } else {
                                final List<DocumentSnapshot> documents = userSnapshot.getDocuments();
                                if (documents.size() > 1) {
                                    Log.d("LoginActivity", "There are more than 1 customer with same uid :(");
                                    finish();
                                }

                                final User user = documents.iterator().next().toObject(User.class);
                                App.setmUser(user);
                                Log.d("LoginActivity", "User information retrieved succesfully");
                            }
                            loginSuccessfull(usuario);
                        } else {
                            Log.d("LoginActivity", "Error getting documents: ", task.getException());
                            finish();
                        }
                    }
                });
    }

    private void createAuthenticatedUser(final String uid, final FirebaseUser usuario, final FirebaseFirestore db) {
        //TODO - Move to a Users class
        final User user = new User(uid, usuario.getDisplayName(), usuario.getEmail(),
                new Date().getTime(), new Date().getTime(), usuario.getPhoneNumber(),
                usuario.getPhotoUrl().toString(), null, null, null,
                usuario.getProviders(), null);
        db.collection("users").document(uid).set(user);
        App.setmUser(user);
    }

    private void createSignInIntent() {
        final List<AuthUI.IdpConfig> availableProviders = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        final Intent i = AuthUI.getInstance()
                .createSignInIntentBuilder().setAvailableProviders(availableProviders).setIsSmartLockEnabled(IS_SMART_LOCK_ENABLED).build();

        startActivityForResult(i, RC_SIGN_IN);
    }

    private void loginSuccessfull(final FirebaseUser usuario) {
        Toast.makeText(this, getString(R.string.Login_welcome) + " " + usuario.getDisplayName(), Toast.LENGTH_LONG).show();
        Intent i = new Intent(this, MapsActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                login();
                finish();
            } else {
                final IdpResponse response = IdpResponse.fromResultIntent(data);

                if (response == null) {
                    Toast.makeText(this, R.string.login_error_cancelled, Toast.LENGTH_LONG).show();
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, R.string.login_error_internet, Toast.LENGTH_LONG).show();
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, R.string.login_error_unknown, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void checkUserVerification(final FirebaseUser usuario) {
        if (!usuario.isEmailVerified()) {
            usuario.sendEmailVerification();
            Toast.makeText(this, R.string.login_verify, Toast.LENGTH_LONG).show();
            finish();
        }
    }
}