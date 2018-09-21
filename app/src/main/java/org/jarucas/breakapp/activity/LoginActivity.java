package org.jarucas.breakapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

import org.jarucas.breakapp.App;
import org.jarucas.breakapp.R;
import org.jarucas.breakapp.dto.UserModel;
import org.jarucas.breakapp.utils.Utils;

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
        //getFacebookPackageHash();
        login();
    }


    private void loadUserInformation(final FirebaseUser usuario, final String uid, final FirebaseFirestore db) {
        db.collection("users").whereEqualTo("guid", uid).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final QuerySnapshot userSnapshot = task.getResult();
                            if (userSnapshot.isEmpty()) {
                                Log.d("LoginActivity", "UserModel does not exist in Database. It will be created");
                                createAuthenticatedUser(uid, usuario, db);
                                Log.d("LoginActivity", "UserModel created into database");
                            } else {
                                final List<DocumentSnapshot> documents = userSnapshot.getDocuments();
                                if (documents.size() > 1) {
                                    Log.d("LoginActivity", "There are more than 1 customer with same uid :(");
                                    finish();
                                }

                                final UserModel user = documents.iterator().next().toObject(UserModel.class);
                                App.setmUser(user);
                                Log.d("LoginActivity", "UserModel information retrieved succesfully");
                            }
                            loginSuccessfull(usuario);
                        } else {
                            Log.d("LoginActivity", "Error getting documents: ", task.getException());
                            finish();
                        }
                    }
                });
    }

    public static void createAuthenticatedUser(final String uid, final FirebaseUser usuario, final FirebaseFirestore db) {
        //TODO - Move to a Users class
        final UserModel user = new UserModel(uid, usuario.getDisplayName(), usuario.getEmail(),
                new Date().getTime(), new Date().getTime(), usuario.getPhoneNumber(),
                usuario.getPhotoUrl().toString(), null, null, null,
                usuario.getProviders(), null);
        db.collection("users").document(uid).set(user);
        App.setmUser(user);
    }

    private void createSignInIntent() {
        final List<AuthUI.IdpConfig> availableProviders = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        final Intent i = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setLogo(R.mipmap.ic_launcher_round)
                .setTheme(R.style.AppTheme_NoActionBar)
                .setAvailableProviders(availableProviders)
                .setIsSmartLockEnabled(IS_SMART_LOCK_ENABLED).build();

        startActivityForResult(i, RC_SIGN_IN);
    }

    private void loginSuccessfull(final FirebaseUser usuario) {
        Utils.showCustomToast(this, getString(R.string.Login_welcome) + " " + usuario.getDisplayName());
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
                    Utils.showCustomToast(this, getString(R.string.login_error_cancelled));
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Utils.showCustomToast(this, getString(R.string.login_error_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Utils.showCustomToast(this, getString(R.string.login_error_unknown));
                }
            }
        }
    }

    private void checkUserVerification(final FirebaseUser usuario) {
        if (!usuario.isEmailVerified()) {
            usuario.sendEmailVerification();
            Utils.showCustomToast(this, getString(R.string.login_verify));
            //TODO Verification Activity
            finish();
        }
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

    //    public static void getFacebookPackageHash() {
//        try {
//            PackageInfo info = App.getContext().getPackageManager().getPackageInfo("org.jarucas.breakapp", PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("LoginActivity", "KeyHash:" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (final PackageManager.NameNotFoundException e) {
//        } catch (final NoSuchAlgorithmException e) {
//        }
//    }
}