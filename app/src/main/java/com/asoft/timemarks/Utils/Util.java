package com.asoft.timemarks.Utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import com.asoft.timemarks.activities.MainActivity;
import com.asoft.timemarks.models.User;
import com.asoft.timemarks.models.quiz.ItemSubject;
import com.asoft.timemarks.models.response.ResGetBalance;
import com.asoft.timemarks.rest.ApiClient;
import com.asoft.timemarks.rest.ApiInterface;
import com.google.gson.Gson;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Util {
	public static final String KEY_LOGIN_OBJ = "keyloginobj";
	public static final String KEY_SUBJECT_OBJ = "keysubkectobj";
	public static final String KEY_CONSTITUENCY_ID = "keyconstituencyidobj";
	public static final String KEY_FLASH_IMAGE = "keyflashimageobj";

	public static int exit = 0;
	public static String balance = "0";
	public static void saveBoolPreferences(Context context, String key, boolean value) {
		SharedPreferences faves = PreferenceManager.getDefaultSharedPreferences(context);

		SharedPreferences.Editor editor = faves.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static boolean getBoolPreferences(Context context, String key) {
		SharedPreferences myPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		boolean rtn = myPrefs.getBoolean(key, false);

		return rtn;
	}

	public static void saveIntPreferences(Context context, String key,
			Integer value) {
		SharedPreferences faves = context.getSharedPreferences("mypref", Context.MODE_PRIVATE);

		SharedPreferences.Editor editor = faves.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static int getIntPreferences(Context context, String key,
			int defaultValue) {
		SharedPreferences myPrefs = context.getSharedPreferences("mypref", Context.MODE_PRIVATE);
		int rtn = myPrefs.getInt(key, defaultValue);

		return rtn;
	}

	public static void saveStringPreferences(Context context, String key,
			String value) {
		SharedPreferences faves = context.getSharedPreferences("mypref", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = faves.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getStringPreferences(Context context, String key, String defaultstr) {
		SharedPreferences sharedpreferences = context.getSharedPreferences("mypref", Context.MODE_PRIVATE);
		String rtn = sharedpreferences.getString(key, defaultstr);

		return rtn;
	}
	public static boolean saveUserDetailFull(Context context, User user) {
		Gson gson = new Gson();
		String json = gson.toJson(user);
		SharedPreferences sharedpreferences = context.getSharedPreferences("mypref", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedpreferences.edit();
		editor.putString(KEY_LOGIN_OBJ, json);
		editor.apply();
		return true;
	}
	public static User getLoginUser(Context context) {
		SharedPreferences sharedpreferences = context.getSharedPreferences("mypref", Context.MODE_PRIVATE);
		Gson gson = new Gson();
		String json = sharedpreferences.getString(KEY_LOGIN_OBJ, "");
		User obj = gson.fromJson(json, User.class);
		return obj;
	}

	public static boolean saveSubject(Context context, ItemSubject user) {
		Gson gson = new Gson();
		String json = gson.toJson(user);
		SharedPreferences sharedpreferences = context.getSharedPreferences("mypref", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedpreferences.edit();
		editor.putString(KEY_SUBJECT_OBJ, json);
		editor.apply();
		return true;
	}
	public static ItemSubject getSelectedSubject(Context context) {
		SharedPreferences sharedpreferences = context.getSharedPreferences("mypref", Context.MODE_PRIVATE);
		Gson gson = new Gson();
		String json = sharedpreferences.getString(KEY_SUBJECT_OBJ, "");
		ItemSubject obj = gson.fromJson(json, ItemSubject.class);
		return obj;
	}
	public static String getUserId(Context context){
		User user = getLoginUser(context);
		if(user!=null){
			String userId = user.getUserId();
			if(!userId.equals("")){
				return userId;
			}
			return "";
		}
		return "";
	}
	public static Boolean isUserLoggedIn(Context context){
		User user = getLoginUser(context);
		if(user!=null){
			String email = user.getUserId();
			if(!email.equals("")){
				return true;
			}
			return false;
		}
		return false;
	}
	public static void logoutPref(Context context) {
		SharedPreferences sharedpreferences = context.getSharedPreferences("mypref", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedpreferences.edit();
		editor.clear();
		editor.commit();
	}
	public static boolean isConnected(Context ctx) {
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return (netInfo != null && netInfo.isConnected());
	}

	public static String deviceId(Context ctx){
		String android_id = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
		String deviceId = md5(android_id).toUpperCase();
		if(deviceId.equals("0")){
			return UniqueId(ctx);
		}else {
			return deviceId;
		}
	}
	public static String UniqueId(Context ctx){
		String ran_id = UUID.randomUUID().toString();
		long timeId = System.currentTimeMillis()/1000;
		String UniId = ran_id+timeId;
		String savedUniqueId = getStringPreferences(ctx,"unique_id","");
		if(savedUniqueId.equals("")){
			saveStringPreferences(ctx,"unique_id",UniId);
			String newsavedUniqueId = getStringPreferences(ctx,"unique_id","");
			return newsavedUniqueId;
		}else {
			return savedUniqueId;
		}
	}
	public static String md5(final String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
	public static void checkBalance(final Context ctx) {
		ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
		final User mUser = Util.getLoginUser(ctx);
		Call<ResGetBalance> call = apiService.checkBalance(mUser.getUserId());
		call.enqueue(new Callback<ResGetBalance>() {
			@Override
			public void onResponse(Call<ResGetBalance> call, retrofit2.Response<ResGetBalance> response) {
				int statusCode = response.code();
				Log.d("res",statusCode+"");
				if(response.isSuccessful()){
					if(response.body().getStatus()){
						String balance = response.body().getBalance();
						if(balance.trim().length()>0){
							mUser.setBalance(balance);
							Util.saveUserDetailFull(ctx,mUser);
							/*if(MainActivity.textpoints!=null){

								MainActivity.textpoints.setText(balance);
							}*/
						}
					}
				}
			}
			@Override
			public void onFailure(Call<ResGetBalance> call, Throwable t) {
				// Log error here since request failed
				Log.e("Util", t.toString());
			}
		});
	}
}
