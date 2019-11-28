package com.asoft.timemarks.activities;

import android.accounts.Account;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.asoft.timemarks.BuildConfig;
import com.asoft.timemarks.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import kotlin.jvm.internal.Intrinsics;

public class SocialSignUpActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = MainActivity.class.getSimpleName();
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    private static final int RC_SIGN_IN = 101;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private SignInButton btnSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(SocialSignUpActivity.this.getApplicationContext());
        setContentView(R.layout.activity_social_sign_up);

        //Facebook
        callbackManager = CallbackManager.Factory.create();

        if(AccessToken.getCurrentAccessToken() != null){
            RequestData();

        }
        accessTokenTracker= new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                displayMessage(newProfile);
            }
        };
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);

        //    loginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends", "email"));
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday"));
        loginButton.registerCallback(callbackManager, callback);

        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        Button btn_facebook_login = (Button) findViewById(R.id.btn_facebook_login);
        btn_facebook_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   login_type = "fb";
                LoginManager.getInstance().logInWithReadPermissions(SocialSignUpActivity.this, (Arrays.asList("public_profile", "email")));
            }
        });
    //    generateHashkey();

        //Google
        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
        //  btnSignIn.setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Customizing G+ button
        //    btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnSignIn.setScopes(gso.getScopeArray());
        Button btn_google_login = (Button) findViewById(R.id.btn_google_login);
        btn_google_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  login_type = "gplus";
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                mGoogleApiClient.clearDefaultAccountAndReconnect();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        Button btn_email_login = (Button) findViewById(R.id.btn_email_login);
        btn_email_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegisterActivity("","","","","emaillogin");
            }
        });
        TextView tvTerms = (TextView)findViewById(R.id.tvTerms);
        tvTerms.setMovementMethod(LinkMovementMethod.getInstance());
    }
    public void generateHashkey(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("Info",info.packageName);
                Log.d("HashkEY", Base64.encodeToString(md.digest(), Base64.NO_WRAP));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG, e.getMessage(), e);
        }
    }

    public void RequestData(){
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                JSONObject json = response.getJSONObject();
                System.out.println("Json data :"+json);
                if(json != null){
                    String text = "<b>Name :</b> "+json.optString("name")+"<br><br><b>Email :</b> "+json.optString("email")+"<br><br><b>Profile link :</b> "+json.optString("link");
                    //    details_txt.setText(Html.fromHtml(text));
                    Log.d("loginData","data : "+text);
                }

            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }
    FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(final LoginResult loginResult) {
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            if (BuildConfig.DEBUG) {
                                FacebookSdk.setIsDebugEnabled(true);
                                FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

                                String uid = object.optString("id");
                                String email = object.optString("email");
                                String name = object.optString("name");
                            //    String first_name = object.optString("first_name");
                            //    String last_name = object.optString("last_name");
                                String gender = object.optString("gender");
                                Log.d("obj",object.toString());
                                Uri.Builder builder = new Uri.Builder();
                                builder.scheme("https")
                                        .authority("graph.facebook.com")
                                        .appendPath(uid)
                                        .appendPath("picture")
                                        .appendQueryParameter("width", "100")
                                        .appendQueryParameter("height", "100");
                                Uri pictureUri = builder.build();
                                String[] splited = name.split("\\s+");
                                String first_name = splited[0];
                                String last_name = "";
                                if(splited.length>1){
                                    last_name = splited[1];
                                }
                                Log.d("Details",uid+" -- "+email+" -- "+name+" -- "+pictureUri.toString());
                                String profileUri = "https://www.facebook.com/"+uid+"";
                               // reqSignup(email,first_name,"facebook");
                                openRegisterActivity(first_name,last_name,email,pictureUri.toString(),"facebook");
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, name, email, first_name, last_name, gender");
            request.setParameters(parameters);
            request.executeAsync();
        }
        @Override
        public void onCancel() {
        }
        @Override
        public void onError(FacebookException e) {
            Toast.makeText(getApplication(), "Something went wrong, please try again later", Toast.LENGTH_LONG).show();
        }
    };
    private void openRegisterActivity(String name,String last_name, String email, String profile_picture, String loginType){
        Intent intent = new Intent(SocialSignUpActivity.this, RegisterActivity.class);
        intent.putExtra("name",name);
        intent.putExtra("last_name",last_name);
        intent.putExtra("email",email);
        intent.putExtra("profile_picture",profile_picture);
        intent.putExtra("loginType",loginType);
        startActivityForResult(intent,REQ_CODE_FINISH);
    }
    private void displayMessage(Profile profile){
        if(profile != null){
            Log.d("Profile",profile.getFirstName()+" - "+profile.getLastName()+" -- "+profile.getLinkUri()+" - "+profile.getProfilePictureUri(400, 400).toString());
            //      reqSignup(profile.getProfilePictureUri(400, 400).toString(),profile.getFirstName(),"facebook");
        }else if(profile == null){
            Log.d("Profile","Profile is Null");
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Results",requestCode+" - "+resultCode+" - "+data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);

        }
        if (requestCode == REQ_CODE_FINISH) {
            if (resultCode == RESULT_OK) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result","SUCCESS");
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.e(TAG, "display name: " + acct.getDisplayName());

            String personName = acct.getDisplayName();
            //        String personPhotoUrl = acct.getPhotoUrl().toString();
            String email = acct.getEmail();
            String id = acct.getIdToken();

            Log.e(TAG, "Name: " + personName + ", email: " + email + ", Image: " );
            Log.e(TAG, "Id: " +acct.getId());
            String[] splited = personName.split("\\s+");
            String first_name = splited[0];
            String last_name = "";
            if(splited.length>1){
                last_name = splited[1];
            }
            Uri personPhoto = acct.getPhotoUrl();
            Log.e(TAG, "personPhoto: " +personPhoto);
            openRegisterActivity(first_name,last_name,email, String.valueOf(personPhoto),"google");
        } else {

        }
    }
    /**
     * Callback received when a permissions request has been completed.
     */

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
}
