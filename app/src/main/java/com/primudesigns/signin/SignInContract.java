package com.primudesigns.signin;

import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;

class SignInContract {

    interface View {

        void showButton();

        void hideButton();

        void showProfile();

        void hideProfile();

        void showProgress();

        void hideProgress();

        void loadProfile(Uri imageURL, String profileName, String profileEmail);

    }

    interface UserAction {

        void signInIntent(GoogleApiClient googleApiClient);

        void firebaseAuthWithGoogle(GoogleSignInAccount account);

        void signedIn();

        void signedOut();

        void loadProfile();

        void resultBack(int requestCode, Intent data);

    }

}
