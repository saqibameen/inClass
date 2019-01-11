package com.example.saqibameen.app01;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentReportView extends AppCompatActivity {
    // Arraylist to store the data.
    final ArrayList<CourseAttendance> attendanceList = new ArrayList<>();
    // PieChart.
    private PieChart pieChart;
    // Roll No.
    private String rollNo;

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
        // If the back button is pressed.
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        // Create new intent.
        Intent login = new Intent(StudentReportView.this, MainActivity.class);
        startActivity(login);
        // Finish currentActivity.
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_report_view);
        // Set Title.
        setTitle("Report");
        // Grab the values from the intent.
        // Roll No and Course Name.
        rollNo = getIntent().getStringExtra("rollNo");
        String courseName = getIntent().getStringExtra("courseName");

        // Grab the data from the db.
        // Database Reference.
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query dbRef = database.getReference("attendance").orderByChild("courseName").equalTo(courseName);

        // Add Listener to get the data.
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot attendance: dataSnapshot.getChildren()) {
                        CourseAttendance temp = attendance.getValue(CourseAttendance.class);
                        attendanceList.add(temp);
                    }
                    // Set the chart.
                    setPieChart(attendanceList);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    /**
     * Sets up the pieChart.
     *
     * @param attendanceList Data to be displayed.
     */
    public void setPieChart(ArrayList<CourseAttendance> attendanceList) {
        // Grab the chart view.
        this.pieChart = findViewById(R.id.pieChart);

        // Chart Settings.
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(true);
        pieChart.setExtraOffsets(5,10,5,5);
        pieChart.setDragDecelerationFrictionCoef(0.9f);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic);

        // Pre-process the data for display.
        Float totalPresent = 0f;
        Float totalAbsent = 0f;
        Float totalLeave = 0f;
        // Count each of these.
        for (CourseAttendance temp : attendanceList) {
            if (temp.getPresentStudents().contains(rollNo)) { // If student was present.
                ++totalPresent;
            } else if (temp.getAbsentStudents().contains(rollNo)) { // If absent.
                ++totalAbsent;
            } else { // Otherwise was on leave.
                ++totalLeave;
            }
        }
        // Total Attendances.
        Float totalAttendances = (float) attendanceList.size();
        // Percentage of each.
        Float present = totalPresent/totalAttendances;
        Float absent = totalAbsent/totalAttendances;
        Float leave = totalLeave/totalAttendances;


        // Add the data.
        ArrayList<PieEntry> yValues = new ArrayList<>();
        yValues.add(new PieEntry(present,"Present"));
        yValues.add(new PieEntry(absent,"Absent"));
        yValues.add(new PieEntry(leave,"Leaves"));

        // Description
        PieDataSet dataSet = new PieDataSet(yValues, "  (Course Attendance Report)");
        this.pieChart.getDescription().setText("Values are in %");


        // Styling
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        // Set Data for PieChart.
        PieData pieData = new PieData((dataSet));
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.YELLOW);

        // Input the data.
        pieChart.setData(pieData);

        // Set Legend Alignment.
        Legend legend = pieChart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
    }
}
