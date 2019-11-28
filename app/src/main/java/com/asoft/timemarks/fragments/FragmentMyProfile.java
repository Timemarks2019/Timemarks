package com.asoft.timemarks.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.asoft.timemarks.R;
import com.asoft.timemarks.Utils.ImageUtil;
import com.asoft.timemarks.Utils.Util;
import com.asoft.timemarks.activities.ChangePasswordActivity;
import com.asoft.timemarks.activities.MyProfile;
import com.asoft.timemarks.models.ResUploadImage;
import com.asoft.timemarks.models.User;
import com.asoft.timemarks.rest.ApiClient;
import com.asoft.timemarks.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentMyProfile extends BaseFragment {
    TextView tvName,tvEmail,tvMobile,tvDob,tvPinCode,tvPrimaryMobile;
    TextView tvFacebookAccount,tvTwitterAccount,tvInstaAccount;
    ImageView imgProfile,imgChooseImage;
    User user;
    int CHOOSE_IMAGE = 100;
    int REQUEST_CAMERA = 101;
    View viewDob;
    View viewArePinCode;
    View viewPrimaryMobile;
    View viewFullProfile;
    TextView tvShowHideProfile;
    private Uri outputFileUri;
    private String imageFilePath = "";
    private Uri imageOutputUri;
    String profile_picture = "";
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_my_profile, container, false);

        user = Util.getLoginUser(getActivity());

        imgProfile = (ImageView) fragmentView.findViewById(R.id.imgProfile);
        imgChooseImage = (ImageView) fragmentView.findViewById(R.id.imgChooseImage);
        tvName = (TextView) fragmentView.findViewById(R.id.tvName);
        tvEmail = (TextView) fragmentView.findViewById(R.id.tvEmail);
        tvMobile = (TextView) fragmentView.findViewById(R.id.tvMobile);
        tvDob = (TextView) fragmentView.findViewById(R.id.tvDob);
        tvPinCode = (TextView) fragmentView.findViewById(R.id.tvPinCode);
        tvPrimaryMobile = (TextView) fragmentView.findViewById(R.id.tvPrimaryMobile);


        View viewEditProfile = (View) fragmentView.findViewById(R.id.viewEditProfile);
        viewEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyProfile.class);
                startActivity(intent);
            }
        });


        viewDob = (View) fragmentView.findViewById(R.id.viewDob);
        viewArePinCode = (View) fragmentView.findViewById(R.id.viewArePinCode);
        viewPrimaryMobile = (View) fragmentView.findViewById(R.id.viewPrimaryMobile);

        viewFullProfile = (View) fragmentView.findViewById(R.id.viewFullProfile);
        tvShowHideProfile = (TextView) fragmentView.findViewById(R.id.tvShowHideProfile);
        View viewShowHideProfile = (View) fragmentView.findViewById(R.id.viewShowHideProfile);
        viewShowHideProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewFullProfile.getVisibility() == View.VISIBLE) {
                    viewFullProfile.setVisibility(View.GONE);
                    tvShowHideProfile.setText("Show Full Profile");
                } else {
                    viewFullProfile.setVisibility(View.VISIBLE);
                    tvShowHideProfile.setText("Hide Full Profile");
                }
            }
        });

        Button btnChangePassword = (Button) fragmentView.findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
        User user = Util.getLoginUser(getActivity());
        /*if(user.getLogin_type().equalsIgnoreCase("facebook") || user.getLogin_type().equalsIgnoreCase("google")){
            btnChangePassword.setVisibility(View.INVISIBLE);
        }*/
        Button btnLogout = (Button) fragmentView.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logout();
            }
        });
        return fragmentView;
    }
    public void onResume() {
        super.onResume();
        user = Util.getLoginUser(getActivity());
        if(user!=null) {
            setData();
        }
    }
    private void setData(){
        tvName.setText(user.getName());
        tvEmail.setText(user.getEmail());
        tvMobile.setText(user.getMobile());

        String profile_picture = user.getImage();
        if(profile_picture!=null){
            if(!profile_picture.equals("")){
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.profile_image_dummy_nw);
                requestOptions.error(R.drawable.profile_image_dummy_nw);
                Glide.with(getActivity())
                        .setDefaultRequestOptions(requestOptions)
                        .load(profile_picture).into(imgProfile);
                imgChooseImage.setColorFilter(getResources().getColor(R.color.white));
            }else {
                imgChooseImage.setColorFilter(getResources().getColor(R.color.black));
            }
        }
        if(user.getMobile().equalsIgnoreCase("")){
            viewPrimaryMobile.setVisibility(View.GONE);
        }else {
            viewPrimaryMobile.setVisibility(View.VISIBLE);
        }
    }
    private void Logout() {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.dialog_logout, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setView(promptView);
        alert.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Util.logoutPref(getActivity());
                getActivity().finish();
            }
        });
        alert.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
        // create an alert dialog
        AlertDialog alert1 = alert.create();
        alert1.show();
    }
    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            }
            catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Uri photoUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() +".provider", photoFile);
            imageOutputUri = photoUri;
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(pictureIntent, REQUEST_CAMERA);
        }
    }
    private File createImageFile() throws IOException{

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imageFilePath = image.getAbsolutePath();

        return image;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == getActivity().RESULT_OK && data != null) {
            //the image URI
            Uri uriImage = data.getData();
            String sImage = ImageUtil.compressImage(getActivity(),getRealPathFromURI(uriImage));
            uploadImage(sImage);
        }
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == getActivity().RESULT_OK) {
                String sImage = ImageUtil.compressImage(getActivity(),imageFilePath);
                uploadImage(sImage);
            }
            else if (resultCode == getActivity().RESULT_CANCELED) {
                Toast.makeText(getActivity(), "You cancelled the operation", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getActivity(), contentUri, proj, null, null, null);
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
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), user.getName());

        ApiInterface apiService = ApiClient.getClientAdvanced().create(ApiInterface.class);
        //creating a call and calling the upload image method
        Call<ResUploadImage> call = apiService.uploadProfileImage(partImage,uid,name);

        //finally performing the call
        call.enqueue(new Callback<ResUploadImage>() {
            @Override
            public void onResponse(Call<ResUploadImage> call, Response<ResUploadImage> response) {
                hideLoading();
                //     Toast.makeText(getApplicationContext(), response.body().getMsg(), Toast.LENGTH_LONG).show();
                Log.d("resCode"," : "+response.code());
                if(response.isSuccessful()){
                    if(response.body().getStatus()){
                        profile_picture = response.body().getImage();
                        Log.d("msg"," : " +profile_picture);
                        User user = Util.getLoginUser(getActivity());
                        user.setImage(profile_picture);
                        Util.saveUserDetailFull(getActivity(),user);
                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.placeholder(R.drawable.profile_image_dummy_nw);
                        requestOptions.error(R.drawable.profile_image_dummy_nw);
                        Glide.with(getActivity())
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
}
