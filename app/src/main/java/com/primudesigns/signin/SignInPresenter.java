package com.primudesigns.signin;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


class SignInPresenter implements SignInContract.UserAction {

    private static final String TAG = "SignInActivity";
    @NonNull
    private final SignInContract.View mSignInView;
    @NonNull
    private final FirebaseAuth mAuth;
    @NonNull
    private final Activity mActivity;

    SignInPresenter(@NonNull SignInContract.View signInView, @NonNull Activity activity, @NonNull FirebaseAuth auth) {
        mSignInView = signInView;
        mActivity = activity;
        mAuth = auth;
    }

    @Override
    public void signInIntent(GoogleApiClient googleApiClient) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        mActivity.startActivityForResult(signInIntent, MainActivity.RC_SIGN_IN);
    }

    @Override
    public void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        mSignInView.hideProgress();
                        if (!task.isSuccessful()) {
                            signedOut();
                            Toast.makeText(mActivity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            loadProfile();
                            Log.d(TAG, "Sign In Successful");
                        }
                    }
                });
    }

    @Override
    public void signedIn() {

        mSignInView.hideButton();

        if (mAuth.getCurrentUser() != null) {
            mSignInView.showProfile();
        }
    }

    @Override
    public void signedOut() {

        if (mAuth.getCurrentUser() == null) {
            mSignInView.hideProfile();
        }

        mSignInView.showButton();
    }

    @Override
    public void loadProfile() {

        if (mAuth.getCurrentUser() != null) {

            FirebaseUser currentUser = mAuth.getCurrentUser();
            mSignInView.loadProfile(currentUser.getPhotoUrl(), currentUser.getDisplayName(), currentUser.getEmail());

        }
    }

    @Override
    public void resultBack(int requestCode, Intent data) {
        if (requestCode == MainActivity.RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }

        }
    }
}
