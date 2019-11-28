package com.asoft.timemarks.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.asoft.timemarks.R;
import com.asoft.timemarks.fragments.FragmentQuizChapters;

public class ActivityQuizChapters extends AppCompatActivity {
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_chapters);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intename = getIntent();
        String subject_id = (String) intename.getSerializableExtra("subject");
        String subject_name = (String) intename.getSerializableExtra("subject_name");
        String title = (String) intename.getSerializableExtra("title");
        getSupportActionBar().setTitle(title);

        Bundle bundle = new Bundle();
        bundle.putString("subject", subject_id);
        bundle.putString("subject_name", subject_name);
        Fragment home = new FragmentQuizChapters();
        home.setArguments(bundle);
        fragmentTransaction.replace(R.id.container,home).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

}
