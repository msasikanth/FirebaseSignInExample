package com.primudesigns.signin;

import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignInContract {

    interface View {

        void showButton();

        void hideButton();

        void showProfile();

        void hideProfile();

    }

    interface UserAction {

        void signInIntent(GoogleApiClient googleApiClient);

        void firebaseAuthWithGoogle(GoogleSignInAccount account, ProgressBar mProgress, CircleImageView profileImage, TextView profileName, TextView profileEmail);

        void signedIn();

        void signedOut();

        void loadProfile(CircleImageView profileImage, TextView profileName, TextView profileEmail);

    }

}
