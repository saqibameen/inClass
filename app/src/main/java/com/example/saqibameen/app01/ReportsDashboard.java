package com.example.saqibameen.app01;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ReportsDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_dashboard);
        // Set the title.
        setTitle("Reports Dashboard");

        // Set onClick Listeners for the CardViews.
        // For Course Report
        ConstraintLayout cvCourseReport = (ConstraintLayout) findViewById(R.id.cvCourseReport);
        cvCourseReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new intent.
                Intent intent = new Intent(ReportsDashboard.this, CourseReport.class);
                // Grab the extra from previous intent.
                String item = getIntent().getStringExtra("courseName");
                // Attach the string to new intent.
                intent.putExtra("courseName", item);
                // Start the new activity.
                startActivity(intent);
            }
        });

        // For Student Report
        ConstraintLayout cvStudentReport = (ConstraintLayout) findViewById(R.id.cvStudentReport);
        cvStudentReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new intent.
                Intent intent = new Intent(ReportsDashboard.this, StudentReport.class);
                // Grab the extra from previous intent.
                String item = getIntent().getStringExtra("courseName");
                // Attach the string to new intent.
                intent.putExtra("courseName", item);
                // Start the new activity.
                startActivity(intent);
            }
        });

        // For Daily Report
        ConstraintLayout cvDailyReport = (ConstraintLayout) findViewById(R.id.cvDailyReport);
        cvDailyReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new intent.
                Intent intent = new Intent(ReportsDashboard.this, DailyReport.class);
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
