package com.example.saqibameen.app01;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DailyReport extends AppCompatActivity {
    private String courseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_report);
        // Set the title.
        setTitle("Daily Report");
        // Grab course name.
        courseName = getIntent().getStringExtra("courseName");

        // Grab the data from the db.
        // Database Reference.
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference("attendance");
        // Arraylist to store the data.
        final ArrayList<CourseAttendance> attendanceList = new ArrayList<>();
        // Add Listener to get the data.
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot attendance: dataSnapshot.getChildren()) {
                        // Grab the course name.
                        String tempCourseName = attendance.child("courseName").getValue().toString();
                        if (tempCourseName.equals(courseName)) {
                            CourseAttendance temp = attendance.getValue(CourseAttendance.class);
                            attendanceList.add(temp);
                        }
                    }
                    // Set the chart.
                    drawChart(attendanceList);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    /**
     * Sets up the lineChart.
     *
     * @param attendanceList Data to be displayed.
     */
    public void drawChart(ArrayList<CourseAttendance> attendanceList) {
        // For holding multiple plots.
        List<ILineDataSet> lines = new ArrayList<>();
        // Chart.
        LineChart chart = (LineChart) findViewById(R.id.lineChart);


        // Pre-process the data.
        ArrayList<Entry> entryPresent = new ArrayList<>(); // Present Data.
        ArrayList<Entry> entryAbsent = new ArrayList<>(); // Absent Data.
        ArrayList<Entry> entryOnLeave = new ArrayList<>(); // Leave Data.

        final ArrayList<String> dates = new ArrayList<>();


        // Fill data to each.
        for (Integer i = 0; i < attendanceList.size(); i++) {
            entryPresent.add(new Entry(i, attendanceList.get(i).getPresentStudents().size()));
            entryAbsent.add(new Entry(i, attendanceList.get(i).getAbsentStudents().size()));
            entryOnLeave.add(new Entry(i, attendanceList.get(i).getLeaveStudents().size()));
            dates.add(attendanceList.get(i).getCurrentDate());
        }

        // Add data to lines.
        LineDataSet presentData = new LineDataSet(entryPresent, "Present");
        LineDataSet absentData = new LineDataSet(entryAbsent, "Absent");
        LineDataSet leaveData = new LineDataSet(entryOnLeave, "Leaves");
        // Set the colors.
        presentData.setColor(Color.rgb(25,118,210));
        presentData.setCircleColor(Color.rgb(21,101,192));
        absentData.setColor(Color.rgb(123,31,162));
        absentData.setCircleColor(Color.rgb(106,27,154));
        leaveData.setColor(Color.rgb(211,47,47));
        leaveData.setCircleColor(Color.rgb(198,40,40));
        // Finally add.
        lines.add(presentData);
        lines.add(absentData);
        lines.add(leaveData);

        //****
        // Controlling X axis
        XAxis xAxis = chart.getXAxis();
        // Set the xAxis position to bottom. Default is top
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return dates.get((int)value);
            }
        };
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);

        // Controlling right side of y axis
        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false);

        //
        chart.getDescription().setText(" ");


        // Setting Data.
        chart.setData(new LineData(lines));
        chart.animateX(3000, Easing.EasingOption.EaseOutBack);
        //refresh
        chart.invalidate();

    }
}
