package com.asoft.timemarks.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.asoft.timemarks.BuildConfig;
import com.asoft.timemarks.R;
import com.asoft.timemarks.Utils.Util;
import com.asoft.timemarks.models.Result;
import com.asoft.timemarks.models.User;
import com.asoft.timemarks.rest.ApiClient;
import com.asoft.timemarks.rest.ApiInterface;
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

import org.json.JSONObject;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;

import static com.asoft.timemarks.Utils.Constants.REQ_CODE_FINISH;

public class FrontLogin extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_SMS = 2;
    private static final int  REQUEST_READ_EXTRNAL = 3;
    private static final int  REQUEST_WRITE_EXTRNAL =4;

    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String Name = "nameKey";
    public static final String Email = "emailKey";
    private static final int REQUEST_READ_CONTACTS = 0;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    ProgressDialog prgDialog;

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
        FacebookSdk.sdkInitialize(FrontLogin.this.getApplicationContext());
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_front_login);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        User user = Util.getLoginUser(FrontLogin.this);
        if(user!=null){
            String email = user.getMobile();
            String password = user.getPassword();
            if(!email.equals("") && !password.equals("")){
                if(Util.isConnected(FrontLogin.this)){
                    mEmailView.setText(email);
                    mPasswordView.setText(password);
                    loginUser(email,password,"");
                }else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection!!", Toast.LENGTH_LONG).show();
                }
            }
        }
        final Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                String email = mEmailView.getText().toString();
                String password = mPasswordView.getText().toString();

                if (email.trim().length() > 0 && password.trim().length() > 0) {
                    if (networkInfo != null && networkInfo.isConnected()) {
                        loginUser(email,password,"");
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection!!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the Login credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);
        Button btn_signup = (Button) findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(FrontLogin.this, SocialSignUpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                FrontLogin.this.startActivity(intent);
            }
        });

        View txtForgot = (View) findViewById(R.id.txtForgot);
        txtForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FrontLogin.this, ForgotPasswordActivity.class);
                FrontLogin.this.startActivity(intent);
            }
        });

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
                LoginManager.getInstance().logInWithReadPermissions(FrontLogin.this, (Arrays.asList("public_profile", "email")));
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
    }
    private void loginUser(String mobile, final String password, final String login_type) {
        final ProgressDialog loading = ProgressDialog.show(FrontLogin.this,"","Please wait...",false,false);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Result> call = apiService.userLogin(mobile,password,login_type);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {
                loading.dismiss();
                if(response.body() == null){
                    Log.d("Res","Body is Null");
                    return;
                }
                int statusCode = response.code();
                String res = response.message();
                Log.d("res",statusCode+" , "+res+" , "+response.body().getStatus());
                if(response.body().getStatus()){
                    User user = response.body().getUser();
                    user.setPassword(password);
                    Util.saveUserDetailFull(FrontLogin.this,user);
                    finish();
                    startActivity(new Intent(getApplicationContext(), SubjectActivity.class));
                }else {
                    Toast.makeText(FrontLogin.this,response.body().getMsg(),Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                // Log error here since request failed
                loading.dismiss();
                Log.e(TAG, t.toString());
            }
        });
    }
    /*private void resetPass(String rollno) {
        final ProgressDialog loading = ProgressDialog.show(FrontLogin.this,"","Please wait...",false,false);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Result> call = apiService.resetPassword(rollno);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {
                loading.dismiss();
                int statusCode = response.code();
                String res = response.message();
                Log.d("res",statusCode+" , "+res+" , "+response.body().getStatus());
                Toast.makeText(FrontLogin.this,response.body().getMsg(),Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                // Log error here since request failed
                loading.dismiss();
                Log.e(TAG, t.toString());
            }
        });
    }*/
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.length() > 2;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 2;
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
                              //  openRegisterActivity(first_name,last_name,email,pictureUri.toString(),"facebook");
                                loginUser(email,"","facebook");
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
        Intent intent = new Intent(FrontLogin.this, RegisterActivity.class);
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
         //   openRegisterActivity(first_name,last_name,email, String.valueOf(personPhoto),"google");
            loginUser(email,"","google");
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
