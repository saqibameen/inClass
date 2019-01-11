package com.example.saqibameen.app01;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class TeacherDashboard extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);
        // Set Title.
        setTitle("Dashboard");

        // Set onClick Listeners for the CardViews.
        // For Course Report
        ConstraintLayout cvCourseReport = (ConstraintLayout) findViewById(R.id.cvCoursesTeaching);
        cvCourseReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new intent.
                Intent intent = new Intent(TeacherDashboard.this, Courses.class);
                // Grab the extra from previous intent.
                String uid = getIntent().getStringExtra("uid");
                // Attach the string to new intent.
                intent.putExtra("uid", uid);
                // Start the new activity.
                startActivity(intent);
            }
        });
    }

    // Set up the menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Listener for clicks.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Create new intent.
        Intent login = new Intent(TeacherDashboard.this, MainActivity.class);
        startActivity(login);
        // Finish currentActivity.
        finish();
        return super.onOptionsItemSelected(item);
    }

    // Backpress control.
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        TeacherDashboard.super.onBackPressed();
                    }
                }).create().show();
    }
}
