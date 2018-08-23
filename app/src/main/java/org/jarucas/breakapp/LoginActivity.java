package org.jarucas.breakapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

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

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        if (usuario != null) {

            if (!usuario.isEmailVerified()) {
                usuario.sendEmailVerification();
                Toast.makeText(this, R.string.login_verify, Toast.LENGTH_LONG).show();
                finish();
            }

            Toast.makeText(this, getString(R.string.Login_welcome) + " " + usuario.getDisplayName(), Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, MapsActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);

        } else {

            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build(),
                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                    .setIsSmartLockEnabled(IS_SMART_LOCK_ENABLED)
                    .build(), RC_SIGN_IN);
        }
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
}