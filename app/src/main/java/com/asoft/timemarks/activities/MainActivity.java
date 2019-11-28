package com.asoft.timemarks.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.asoft.timemarks.R;
import com.asoft.timemarks.Utils.Util;
import com.asoft.timemarks.fragments.FragAddMoney;
import com.asoft.timemarks.fragments.FragmenHome;
import com.asoft.timemarks.fragments.FragmentClosed;
import com.asoft.timemarks.fragments.FragmentLive;
import com.asoft.timemarks.fragments.FragmentMyProfile;
import com.asoft.timemarks.fragments.FragmentQuizTabs;
import com.asoft.timemarks.fragments.FragmentResult;
import com.asoft.timemarks.models.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.neonankiti.android.support.design.widget.FlexibleBottomNavigationView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    View header;
    private FlexibleBottomNavigationView mBottomNavigationView;
    TextView tvWallet;
    TextView tvSubject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        header = LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);
        navigationView.addHeaderView(header);

        tvWallet = header.findViewById(R.id.tvWallet);
        View rlWallet = (View) header.findViewById(R.id.rlWallet);
        rlWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new FragAddMoney(),true);
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        tvSubject = header.findViewById(R.id.tvSubject);
        tvSubject.setText(Util.getSelectedSubject(MainActivity.this).getSubject_name());
        View viewSubject = (View) header.findViewById(R.id.viewSubject);
        viewSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(MainActivity.this,SubjectActivity.class));
            }
        });
        setupBottomNavigation();

    }
    private void setupBottomNavigation() {
        mBottomNavigationView = (FlexibleBottomNavigationView) findViewById(R.id.bottom_navigation);
        mBottomNavigationView.enableShiftMode(false);

        final TextView textView = (TextView) mBottomNavigationView.findViewById(R.id.action_home).findViewById(R.id.largeLabel);
        final TextView textViewSmall = (TextView) mBottomNavigationView.findViewById(R.id.action_home).findViewById(R.id.smallLabel);
        final TextView textView2 = (TextView) mBottomNavigationView.findViewById(R.id.action_live).findViewById(R.id.largeLabel);
        final TextView textView2Small = (TextView) mBottomNavigationView.findViewById(R.id.action_live).findViewById(R.id.smallLabel);
        final TextView textView3 = (TextView) mBottomNavigationView.findViewById(R.id.action_my_tests).findViewById(R.id.largeLabel);
        final TextView textView3Small = (TextView) mBottomNavigationView.findViewById(R.id.action_my_tests).findViewById(R.id.smallLabel);

        final TextView textView5 = (TextView) mBottomNavigationView.findViewById(R.id.action_profile).findViewById(R.id.largeLabel);
        final TextView textView5Small = (TextView) mBottomNavigationView.findViewById(R.id.action_profile).findViewById(R.id.smallLabel);

        textView.setTextSize(11);
        textView2.setTextSize(11);
        textView3.setTextSize(11);

        textView5.setTextSize(11);
        textViewSmall.setTextSize(11);
        textView2Small.setTextSize(11);
        textView3Small.setTextSize(11);

        textView5Small.setTextSize(11);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new FlexibleBottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        loadFragment(new FragmenHome(),false);
                        return true;
                    case R.id.action_live:
                        loadFragment(new FragmentLive(),false);
                        return true;
                    case R.id.action_my_tests:
                        loadFragment(new FragmentClosed(),false);
                        return true;
                    case R.id.action_profile:
                        loadFragment(new FragmentMyProfile(),false);
                        return true;
                }
                return false;
            }
        });
        mBottomNavigationView.setSelectedItemId(R.id.action_home);
    }
    private void loadFragment(Fragment fragment,Boolean isBackstack) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        if(isBackstack){
            transaction.addToBackStack("tag");
        }
        transaction.commit();
    }

    protected void onResume() {
        super.onResume();
        tvWallet.setText("\u20B9 "+ Util.getLoginUser(MainActivity.this).getBalance()+"");
        User user = Util.getLoginUser(MainActivity.this);
        if(user!=null){
            TextView textView1 = (TextView)header.findViewById(R.id.textView1);
            textView1.setText(user.getName());
            String profileImage = user.getImage();
            Log.d("profileImage","Img : "+profileImage);
            ImageView imgProfile = (ImageView)header.findViewById(R.id.imageView);
            if(profileImage!=null){
                if(!profileImage.equals("")){
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.profile_image_dummy_nw);
                    requestOptions.error(R.drawable.profile_image_dummy_nw);
                    Glide.with(MainActivity.this)
                            .setDefaultRequestOptions(requestOptions)
                            .load(profileImage).into(imgProfile);
                }
            }
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            mBottomNavigationView.setSelectedItemId(R.id.action_home);
        } else if (id == R.id.nav_wallet) {
            loadFragment(new FragAddMoney(),true);
        } else if (id == R.id.nav_slideshow) {
            mBottomNavigationView.setSelectedItemId(R.id.action_live);
        } else if (id == R.id.nav_manage) {
            mBottomNavigationView.setSelectedItemId(R.id.action_my_tests);
        } else if (id == R.id.nav_share) {
            /*Intent sendInt = new Intent(Intent.ACTION_SEND);
            sendInt.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            sendInt.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + "\nhttps://play.google.com/store/apps/details?id=" + getPackageName());
            sendInt.setType("text/plain");
            startActivity(Intent.createChooser(sendInt, "Share"));*/
            startActivity(new Intent(MainActivity.this, RefferAndEarn.class));
        } else if (id == R.id.nav_send) {
            Util.logoutPref(MainActivity.this);
            startActivity(new Intent(MainActivity.this,FrontLogin.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if(frag instanceof FragAddMoney){
            frag.onActivityResult(requestCode, resultCode, data);
        }
    }
}
