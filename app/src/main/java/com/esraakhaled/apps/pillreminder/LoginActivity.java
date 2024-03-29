package com.esraakhaled.apps.pillreminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.esraakhaled.apps.pillreminder.database.AppExecutors;
import com.esraakhaled.apps.pillreminder.database.MedicationDatabase;
import com.esraakhaled.apps.pillreminder.model.Medicine;
import com.esraakhaled.apps.pillreminder.utils.SharedPrefrencesUtil;
import com.esraakhaled.apps.pillreminder.utils.WidgetUpdateNotifierUtil;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    private static final String EMAIL = "email";
    private final String TAG = "Social_Login";
    private final int RC_SIGN_IN = 123;

    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;

    @BindView(R.id.fb_login_button)
    LoginButton fbLoginButton;

    @BindView(R.id.twitter_login_button)
    TwitterLoginButton twitterLoginButton;

    @BindView(R.id.google_login_button)
    SignInButton googleLoginButton;

    @BindView(R.id.facebook_login)
    ImageView facebookLogin;

    @BindView(R.id.twitter_login)
    ImageView twitterLogin;

    @BindView(R.id.google_login)
    ImageView googleLogin;

    private FirebaseAuth.AuthStateListener authListener;
    private GoogleSignInClient mGoogleSignInClient;

    private MedicationDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //handle twitter login
        initializeTwitterLogin();

        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        if (SharedPrefrencesUtil.getUserId(this) != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        mAuth = FirebaseAuth.getInstance();
/*
        mAuth.signOut();
        LoginManager.getInstance().logOut();
*/

        setTwitterButtonCallbacks();

        addFirebaseAuthListener();

        setGoogleLogin();
        //handle fb login
        initializeFacebookLogin();

        database = MedicationDatabase.getInstance(this);
    }

    @OnClick(R.id.facebook_login)
    void loginWithFacebook() {
        fbLoginButton.performClick();
    }

    @OnClick(R.id.twitter_login)
    void loginWithTwitter() {
        twitterLoginButton.performClick();
    }

    @OnClick({R.id.google_login, R.id.google_login_button})
    void loginWithGoogle() {
        signIn();
    }

    private void setGoogleLogin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void setTwitterButtonCallbacks() {
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {

            @Override
            public void success(Result<TwitterSession> result) {
                Log.d(TAG, "loginButton Callback: Success");
                exchangeTwitterToken(result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d(TAG, "loginButton Callback: Failure " +
                        exception.getLocalizedMessage());
            }
        });
    }

    private void addFirebaseAuthListener() {
        authListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            updateUI(user);
        };
    }

    private void initializeTwitterLogin() {
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.twitter_consumer_key),
                        getString(R.string.twitter_consumer_secret)))
                .debug(true)
                .build();
        Twitter.initialize(config);
    }

    private void initializeFacebookLogin() {
        mCallbackManager = CallbackManager.Factory.create();
        fbLoginButton.setReadPermissions(EMAIL);
        fbLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            mAuth.removeAuthStateListener(authListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }

    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }

                    // ...
                });
    }

    private void exchangeTwitterToken(TwitterSession session) {

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if (!task.isSuccessful()) {
                        Log.w(TAG, "signInWithCredential",
                                task.getException());
                        Toast.makeText(LoginActivity.this,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Toast.makeText(this, user.getUid() + "", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, getString(R.string.login_successful), Toast.LENGTH_SHORT).show();
            saveUserToken(user.getUid());
            checkIfUserDataFound(user.getUid());
        }
    }

    private void checkIfUserDataFound(String userId) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference medicine = firebaseDatabase.getReference("medicine");

        medicine.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean hasUser = dataSnapshot.hasChild(userId);
                if (hasUser) {
                    String medicinesString =
                            dataSnapshot.child(userId).getValue(String.class);

                    Log.e("MSG", medicinesString);
                    Gson gson = new Gson();
                    TypeToken<ArrayList<Medicine>> token = new TypeToken<ArrayList<Medicine>>() {
                    };
                    List<Medicine> medicines = gson.fromJson(medicinesString, token.getType());

                    insertIntoDataBase(medicines);
                } else {
                    startMainActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DB", databaseError.getMessage() + " " + databaseError.getDetails());
            }
        });
    }

    private void insertIntoDataBase(List<Medicine> medicines) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            database.medicationDao().addListOfMedicines(medicines);
            runOnUiThread(() -> {
                WidgetUpdateNotifierUtil.notifyWidget(this);
                startMainActivity();
            });
        });
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void saveUserToken(String uid) {
        SharedPrefrencesUtil.saveUserId(this, uid);
    }
}
