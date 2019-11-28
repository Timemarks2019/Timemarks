package com.asoft.timemarks.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyProfile extends BaseActivity {
    String name,email,loginType;
    int CHOOSE_IMAGE = 100;
    int REQUEST_CAMERA = 101;
    ImageView imgProfile;
    String profile_picture = "";

    public static EditText etDob;
    User user;
    private Uri outputFileUri;
    private String imageFilePath = "";
    private Uri imageOutputUri;

    String sImage = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = Util.getLoginUser(MyProfile.this);

        imgProfile = (ImageView) findViewById(R.id.imgProfile);
        final ImageView imgChooseImage = (ImageView) findViewById(R.id.imgChooseImage);
        final EditText etFirstName = (EditText) findViewById(R.id.etFirstName);

        etFirstName.setText(user.getName());

        profile_picture = user.getImage();
        if(profile_picture!=null){
            if(!profile_picture.equals("")){
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.profile_image_dummy_nw);
                requestOptions.error(R.drawable.profile_image_dummy_nw);
                Glide.with(MyProfile.this)
                        .setDefaultRequestOptions(requestOptions)
                        .load(profile_picture).into(imgProfile);
                imgChooseImage.setColorFilter(getResources().getColor(R.color.white));
            }else {
                imgChooseImage.setColorFilter(getResources().getColor(R.color.black));
            }
        }
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MyProfile.this, v);
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

        Button btnUpdate = (Button) findViewById(R.id.btnContinue);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etFirstName.getText().toString();

                if (Util.isConnected(MyProfile.this)) {
                    if (name.trim().length() == 0) {
                        etFirstName.setError("Please enter First Name");
                        return;
                    }
                    if (!name.matches("[a-zA-Z ]*")) {
                        etFirstName.setError("Special Characters or Numbers not allowed in First Name");
                        return;
                    }

                    updateProfile(sImage,name);
                } else {
                    Toast.makeText(MyProfile.this, "No Internet Connection!!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    /*private void updateProfile(final String name,final String lastname,final String mobile,final String dob,final String pincode,final String image) {
        showLoading();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        String user_id = user.getUserId();
        Log.d("PostData",""+name+","+lastname+","+mobile+","+user_id);
        Call<Result> call = apiService.updateProfile(user_id,name,lastname,mobile,dob,pincode,image);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                hideLoading();
                int statusCode = response.code();
                String res = response.message();
                Log.d("res",statusCode+" , "+res);
                Toast.makeText(MyProfile.this,response.body().getMsg(),Toast.LENGTH_LONG).show();
                if(response.isSuccessful()){
                    if(response.body().getStatus()){
                        User newuser = response.body().getUser();
                        String resd = new Gson().toJson(response.body());
                        Log.d("resd",": "+resd);
                        newuser.setPassword(user.getPassword());
                        Util.saveUserDetailFull(MyProfile.this,newuser);
                        finish();
                    }
                }
            }
            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                // Log error here since request failed
                hideLoading();
                Log.e("CallApi", t.toString());
            }
        });
    }
   */
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
            sImage = ImageUtil.compressImage(MyProfile.this,getRealPathFromURI(uriImage));

            final File fileUpload = new File(sImage);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.profile_image_dummy_nw);
            requestOptions.error(R.drawable.profile_image_dummy_nw);
            Glide.with(MyProfile.this)
                    .setDefaultRequestOptions(requestOptions)
                    .load(Uri.fromFile(fileUpload)).into(imgProfile);
        //    uploadImage(sImage);
        }
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                sImage = ImageUtil.compressImage(MyProfile.this,imageFilePath);
         //       uploadImage(sImage);
                final File fileUpload = new File(sImage);
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.profile_image_dummy_nw);
                requestOptions.error(R.drawable.profile_image_dummy_nw);
                Glide.with(MyProfile.this)
                        .setDefaultRequestOptions(requestOptions)
                        .load(Uri.fromFile(fileUpload)).into(imgProfile);
            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "You cancelled the operation", Toast.LENGTH_SHORT).show();
            }
        }
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

    private void updateProfile(String sFileUpload,String sName) {
        showLoading();
        final File fileUpload = new File(sFileUpload);
        RequestBody requestfileUpload = RequestBody.create(MediaType.parse("jpg"), fileUpload);
        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part partImage = MultipartBody.Part.createFormData("image",fileUpload.getName(),requestfileUpload);

        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), Util.getUserId(MyProfile.this));
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), sName);

        ApiInterface apiService = ApiClient.getClientAdvanced().create(ApiInterface.class);
        //creating a call and calling the upload image method
        Call<ResUploadImage> call = apiService.uploadProfileImage(partImage,uid,name);

        //finally performing the call
        call.enqueue(new Callback<ResUploadImage>() {
            @Override
            public void onResponse(Call<ResUploadImage> call, Response<ResUploadImage> response) {
                hideLoading();
                //     Toast.makeText(getApplicationContext(), response.body().getMsg(), Toast.LENGTH_LONG).show();
                Log.d("resCode"," : "+response.code()+" "+new Gson().toJson(response.body()));
                if(response.isSuccessful()){
                    if(response.body().getStatus()){
                        profile_picture = response.body().getFull_url();
                        Log.d("msg"," : " +profile_picture);

                        User mUser = Util.getLoginUser(MyProfile.this);
                        mUser.setImage(profile_picture);
                        Util.saveUserDetailFull(MyProfile.this,mUser);

                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.placeholder(R.drawable.profile_image_dummy_nw);
                        requestOptions.error(R.drawable.profile_image_dummy_nw);
                        Glide.with(MyProfile.this)
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
