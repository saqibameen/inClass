package com.example.saqibameen.app01;

import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DailyReport extends AppCompatActivity {
    // Course Name.
    private String courseName;
    // Attendance for report.
    private ArrayList<CourseAttendance> attendanceList;

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
        Intent login = new Intent(DailyReport.this, MainActivity.class);
        startActivity(login);
        // Finish currentActivity.
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        attendanceList = new ArrayList<>();
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

    /**
     * Generate the PDF.
     *
     * @param view current view.
     */
    public void generatePDF(View view) {
        // Create new document.
        Document doc = new Document();
        // Path to the course report.
        String path = Environment.getExternalStorageDirectory() + "/DailyReport.pdf";
        // Check for path and write.
        try {
            // Get instance.
            PdfWriter.getInstance(doc, new FileOutputStream(path));
            // Open the document.
            doc.open();

            // LINE SEPARATOR
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

            // Adding Title
            Font font = new Font(Font.FontFamily.HELVETICA, 20); // Heading Font
            Font detailsFont = new Font(Font.FontFamily.HELVETICA, 14); // Details Font
            Font headingFont = new Font(Font.FontFamily.HELVETICA, 17); // Details Font
            // Creating Chunk
            Chunk titleChunk = new Chunk(courseName +" Daily Attendance Report", font);
            // Creating Paragraph to add...
            Paragraph titleParagraph = new Paragraph(titleChunk);
            // Setting Alignment for Heading
            titleParagraph.setAlignment(Element.ALIGN_CENTER);
            // Finally Adding that Chunk
            doc.add(titleParagraph);

            // Add Date.
            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            String strDate = dateFormat.format(date);
            Chunk dateChunk = new Chunk("Timestamp: " + strDate, detailsFont);
            // Creating Paragraph to add...
            Paragraph datePara = new Paragraph(dateChunk);
            // Setting Alignment for Heading
            datePara.setAlignment(Element.ALIGN_RIGHT);
            // Add to doc.
            doc.add(datePara);

            // Add the line separator.
            doc.add(new Paragraph(""));
            doc.add(new Chunk(lineSeparator));
            doc.add(new Paragraph(""));


            // Iterate over the attendance data.
            for (CourseAttendance temp : attendanceList) {
                // Add Heading.
                Chunk tempChunk = new Chunk("Timestamp: " + temp.getCurrentDate(), headingFont);
                // Creating Paragraph to add...
                Paragraph tempPara = new Paragraph(tempChunk);
                // Add to doc.
                doc.add(tempPara);
                doc.add(new Paragraph(""));

                // Add Details.
                // Total Data.
                tempChunk = new Chunk("Total Students= " + (temp.getPresentStudents().size() + temp.getAbsentStudents().size() + temp.getLeaveStudents().size()), detailsFont);
                // Creating Paragraph to add...
                tempPara = new Paragraph(tempChunk);
                // Add to doc.
                doc.add(tempPara);

                // Present Data.
                tempChunk = new Chunk("Present Students= " + temp.getPresentStudents().size(), detailsFont);
                // Creating Paragraph to add...
                tempPara = new Paragraph(tempChunk);
                // Add to doc.
                doc.add(tempPara);

                // Absent Data.
                tempChunk = new Chunk("Absent Students= " + temp.getAbsentStudents().size(), detailsFont);
                // Creating Paragraph to add...
                tempPara = new Paragraph(tempChunk);
                // Add to doc.
                doc.add(tempPara);

                // Leave Data.
                tempChunk = new Chunk("On Leave Students= " + temp.getLeaveStudents().size(), detailsFont);
                // Creating Paragraph to add...
                tempPara = new Paragraph(tempChunk);
                // Add to doc.
                doc.add(tempPara);
                doc.add(new Paragraph(""));
                doc.add(new Paragraph(""));
                doc.add(new Paragraph(""));
                doc.add(new Paragraph(""));
                doc.add(new Paragraph(""));
            }

            // Close the document.
            doc.close();

            // Toast Message.
            Toast.makeText(DailyReport.this, "PDF Saved!", Toast.LENGTH_SHORT).show();

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
