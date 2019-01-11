package com.example.saqibameen.app01;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

public class CourseDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_dashboard);
        // Set the activity title.
        setTitle("Course Dashboard");

        // Set Listeners for the cardview clicks.
        // For Reports
        ConstraintLayout cvReports = (ConstraintLayout) findViewById(R.id.cvReports);
        cvReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new intent.
                Intent intent = new Intent(CourseDashboard.this, ReportsDashboard.class);
                // Grab the extra from previous intent.
                String item = getIntent().getStringExtra("courseName");
                // Attach the string to new intent.
                intent.putExtra("courseName", item);
                // Start the new activity.
                startActivity(intent);
            }
        });

        // For Attendance.
        ConstraintLayout cvAttendance = (ConstraintLayout) findViewById(R.id.cvAttendance);
        cvAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new intent.
                Intent intent = new Intent(CourseDashboard.this, Attendance.class);
                // Grab the extra from previous intent.
                String item = getIntent().getStringExtra("courseName");
                // Attach the string to new intent.
                intent.putExtra("courseName", item);
                // Start the new activity.
                startActivity(intent);
            }
        });

    }
}
