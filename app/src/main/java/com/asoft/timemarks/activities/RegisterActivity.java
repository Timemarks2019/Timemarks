package com.asoft.timemarks.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.asoft.timemarks.R;
import com.asoft.timemarks.Utils.ImageUtil;
import com.asoft.timemarks.Utils.Util;
import com.asoft.timemarks.models.ResUploadImage;
import com.asoft.timemarks.models.Result;
import com.asoft.timemarks.models.User;
import com.asoft.timemarks.rest.ApiClient;
import com.asoft.timemarks.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends BaseActivity {
    String name,last_name,email,loginType;
    int CHOOSE_IMAGE = 100;
    int REQUEST_CAMERA = 101;
    ImageView imgProfile;
    String profile_picture = "";
    private String imageFilePath = "";
    private Uri imageOutputUri;
    String vfname,vemail,vmobileNo,vpassword,vimage,vlogin_type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = getIntent().getExtras().getString("name");
        last_name = getIntent().getExtras().getString("last_name");
        email = getIntent().getExtras().getString("email");
        loginType = getIntent().getExtras().getString("loginType");
        profile_picture = getIntent().getExtras().getString("profile_picture");

        imgProfile = (ImageView) findViewById(R.id.imgProfile);
        final ImageView imgChooseImage = (ImageView) findViewById(R.id.imgChooseImage);
        final EditText etFirstName = (EditText) findViewById(R.id.etFirstName);
        final EditText etReferral = (EditText) findViewById(R.id.etReferral);
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etMobile = (EditText) findViewById(R.id.etMobile);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final TextInputLayout tilPassword = (TextInputLayout) findViewById(R.id.tilPassword);

        etFirstName.setText(name);
        etEmail.setText(email);
        if(!profile_picture.equals("")){
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.profile_image_dummy_nw);
            requestOptions.error(R.drawable.profile_image_dummy_nw);
            Glide.with(RegisterActivity.this)
                    .setDefaultRequestOptions(requestOptions)
                    .load(profile_picture).into(imgProfile);
            imgChooseImage.setColorFilter(getResources().getColor(R.color.white));
        }else {
            imgChooseImage.setColorFilter(getResources().getColor(R.color.black));
        }
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(RegisterActivity.this, v);
                popup.getMenuInflater().inflate(R.menu.menu_profile_image, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_choose_gallery:
                                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(i, CHOOSE_IMAGE);
                                return true;
                            case R.id.action_camera:
                                openCameraIntent();
                                return true;
                        }
                        return true;
                    }
                });
                popup.show();//showing popup menu
            }
        });
        if(!email.equalsIgnoreCase("")){
            etEmail.setClickable(false);
            etEmail.setFocusable(false);
        }
        if(loginType.equalsIgnoreCase("facebook") || loginType.equalsIgnoreCase("google")){
            tilPassword.setVisibility(View.GONE);
        }else {
            tilPassword.setVisibility(View.VISIBLE);
        }
        Button btnContinue = (Button) findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fname = etFirstName.getText().toString();
                String refferal = etReferral.getText().toString();
                String mobileNo = etMobile.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                if (Util.isConnected(RegisterActivity.this)) {
                    if (fname.trim().length() == 0) {
                        etFirstName.setError("Please enter First Name");
                        return;
                    }
                    if (email.trim().length() == 0) {
                        etEmail.setError("Please enter a Email Id");
                        return;
                    }
                    if (!isValidEmail(email)) {
                        etEmail.setError("Please enter a valid Email Id");
                        return;
                    }
                    if (mobileNo.trim().length() == 0) {
                        etMobile.setError("Please enter Mobile No");
                        return;
                    }
                    if (mobileNo.trim().length() < 10) {
                        etMobile.setError("Please enter a valid Mobile No");
                        return;
                    }
                    if (mobileNo.trim().length() > 10) {
                        etMobile.setError("Please enter a valid Mobile No");
                        return;
                    }
                    if (!mobileNo.matches("[0-9]*")) {
                        etMobile.setError("Please enter a valid Mobile No");
                        return;
                    }
                    if(!loginType.equalsIgnoreCase("facebook") && !loginType.equalsIgnoreCase("google")){
                        if (password.trim().length() == 0) {
                            etPassword.setError("Please enter a Password");
                            return;
                        }
                    }
                    registerUser(fname,email,mobileNo,password,profile_picture,loginType,refferal);
                } else {
                    Toast.makeText(RegisterActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public static boolean isValidEmail(String target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    private void registerUser(String fname, final String email, final String mobileNo, final String password , final String image, final String login_type, final String refferal) {
        showLoading();
        vfname = fname;
        vemail = email;
        vmobileNo = mobileNo;
        vpassword = password;
        vimage = image;
        vlogin_type = login_type;
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Result> call = apiService.userSignUpWithSocial(fname,email,mobileNo,password,image,login_type,refferal);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {
                hideLoading();
                Log.d("rescode", " "+response.code());
                if(response.isSuccessful()){
                    if(response.body().getStatus()){
                        User user = response.body().getUser();
                        user.setPassword(password);
                        Util.saveUserDetailFull(RegisterActivity.this,user);
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result","SUCCESS");
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
                        startActivity(new Intent(getApplicationContext(), SubjectActivity.class));
                    }else {
                        Toast.makeText(RegisterActivity.this,response.body().getMsg(),Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                // Log error here since request failed
                hideLoading();
                Log.e("ApiCall", t.toString());
            }
        });
    }

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            }
            catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Uri photoUri = FileProvider.getUriForFile(this, getPackageName() +".provider", photoFile);
            imageOutputUri = photoUri;
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(pictureIntent, REQUEST_CAMERA);
        }
    }
    private File createImageFile() throws IOException{

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imageFilePath = image.getAbsolutePath();

        return image;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null) {
            //the image URI
            Uri uriImage = data.getData();
            String sImage = ImageUtil.compressImage(RegisterActivity.this,getRealPathFromURI(uriImage));
            uploadImage(sImage);
        }
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                String sImage = ImageUtil.compressImage(RegisterActivity.this,imageFilePath);
                uploadImage(sImage);
            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "You cancelled the operation", Toast.LENGTH_SHORT).show();
            }
        }
        /*if (requestCode == REQUEST_OTP_VERIFY) {
            if (resultCode == RESULT_OK) {
                String otp = data.getStringExtra("otp");
                Log.d("result", "result : "+otp);
                registerUser(vfname,vlname,vemail,vmobileNo,vpassword,vimage,vlogin_type,otp);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }*/
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private void uploadImage(String sFileUpload) {
        showLoading();
        File fileUpload = new File(sFileUpload);
        RequestBody requestfileUpload = RequestBody.create(MediaType.parse("jpg"), fileUpload);
        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part partImage = MultipartBody.Part.createFormData("image",fileUpload.getName(),requestfileUpload);

        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), "");

        ApiInterface apiService = ApiClient.getClientAdvanced().create(ApiInterface.class);
        //creating a call and calling the upload image method
        Call<ResUploadImage> call = apiService.uploadImage(partImage);

        //finally performing the call
        call.enqueue(new Callback<ResUploadImage>() {
            @Override
            public void onResponse(Call<ResUploadImage> call, Response<ResUploadImage> response) {
                hideLoading();
                //     Toast.makeText(getApplicationContext(), response.body().getMsg(), Toast.LENGTH_LONG).show();
                Log.d("resCode"," : "+response.code());
                if(response.isSuccessful()){
                    if(response.body().getStatus()){
                        profile_picture = response.body().getFull_url();
                        Log.d("msg"," : " +profile_picture);

                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.placeholder(R.drawable.profile_image_dummy_nw);
                        requestOptions.error(R.drawable.profile_image_dummy_nw);
                        Glide.with(RegisterActivity.this)
                                .setDefaultRequestOptions(requestOptions)
                                .load(profile_picture).into(imgProfile);

                    }else {

                    }
                }
            }
            @Override
            public void onFailure(Call<ResUploadImage> call, Throwable t) {
                Log.e("Error",""+t.getMessage());
                hideLoading();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
