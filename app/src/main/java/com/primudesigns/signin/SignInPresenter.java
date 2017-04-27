package com.primudesigns.signin;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import de.hdodenhof.circleimageview.CircleImageView;


public class SignInPresenter implements SignInContract.UserAction {

    @NonNull
    private final SignInContract.View mSignInView;
    @NonNull
    private final FirebaseAuth mAuth;

    @NonNull
    private final Activity mActivity;

    private static final String TAG = "SignInActivity";

    public SignInPresenter(@NonNull SignInContract.View signInView, @NonNull Activity activity, @NonNull FirebaseAuth auth) {
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
    public void firebaseAuthWithGoogle(GoogleSignInAccount account, final ProgressBar mProgress, final CircleImageView profileImage, final TextView profileName, final TextView profileEmail) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(mActivity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            mProgress.setVisibility(View.GONE);
                            loadProfile(profileImage, profileName, profileEmail);
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
    public void loadProfile(CircleImageView profileImage, TextView profileName, TextView profileEmail) {

        if (mAuth.getCurrentUser() != null) {

            Glide.with(mActivity)
                    .load(mAuth.getCurrentUser().getPhotoUrl())
                    .into(profileImage);

            profileName.setText(mAuth.getCurrentUser().getDisplayName());
            profileEmail.setText(mAuth.getCurrentUser().getEmail());

        }
    }
}
