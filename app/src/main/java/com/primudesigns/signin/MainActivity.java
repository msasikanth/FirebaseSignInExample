package com.primudesigns.signin;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.primudesigns.signin.databinding.ActivityMainBinding;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements
        SignInContract.View,
        GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public static final int RC_SIGN_IN = 9001;

    private SignInContract.UserAction mAction;

    private SignInButton mButton;
    private ProgressBar mProgress;
    private RelativeLayout mProfile;
    private CircleImageView mProfileImage;
    private TextView mProfileName;
    private TextView mProfileEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Views
        mButton = mainBinding.sbSignIn;
        mProgress = mainBinding.pbSignIn;
        mProfile = mainBinding.rlProfile;
        mProfileImage = mainBinding.ciProfileImage;
        mProfileName = mainBinding.tvProfileName;
        mProfileEmail = mainBinding.ivProfileEmail;

        //Configure Sign-In
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();

        mAuth = FirebaseAuth.getInstance();
        mAction = new SignInPresenter(this, this, mAuth);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    /* User Already Signed-In */
                    mAction.signedIn();
                    mAction.loadProfile(mProfileImage, mProfileName, mProfileEmail);
                } else {
                    /* User not Signed-In */
                    mAction.signedOut();
                }

            }
        };

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAction.signedIn();
                mAction.signInIntent(mGoogleApiClient);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                mAction.firebaseAuthWithGoogle(account, mProgress, mProfileImage, mProfileName, mProfileEmail);
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sign_out:
                mAction.signedOut();
                mAuth.signOut();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showButton() {
        mButton.setVisibility(View.VISIBLE);

        if (mAuth.getCurrentUser() != null) {
            mProgress.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideButton() {
        mButton.setVisibility(View.GONE);

        if (mAuth.getCurrentUser() == null) {
            mProgress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showProfile() {
        mProfile.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProfile() {
        mProfile.setVisibility(View.GONE);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection failed : " + connectionResult.getErrorMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }
}
