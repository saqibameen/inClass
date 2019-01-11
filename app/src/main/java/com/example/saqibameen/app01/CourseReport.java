package com.example.saqibameen.app01;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CourseReport extends AppCompatActivity {
    // Data members.
    private PieChart pieChart;
    private String courseName;
    private Float present;
    private Float absent;
    private Float leave;

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
        Intent login = new Intent(CourseReport.this, MainActivity.class);
        startActivity(login);
        // Finish currentActivity.
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_report);
        // Set the title.
        setTitle("Course Report");
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
            totalPresent += temp.getPresentStudents().size();
            totalAbsent += temp.getAbsentStudents().size();
            totalLeave += temp.getLeaveStudents().size();
        }
        // Total Attendances.
        Float totalAttendances = totalPresent + totalAbsent + totalLeave;
        // Percentage of each.
        present = totalPresent/totalAttendances;
        absent = totalAbsent/totalAttendances;
        leave = totalLeave/totalAttendances;


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

    /**
     * Generate the PDF.
     *
     * @param view current view.
     */
    public void generatePDF(View view) {
        // Create new document.
        Document doc = new Document();
        // Path to the course report.
        String path = Environment.getExternalStorageDirectory() + "/CourseReport.pdf";
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
            // Creating Chunk
            Chunk titleChunk = new Chunk(courseName + " Attendance Report", font);
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

            // Present Data.
            // Creating Chunk
            Float pp = present *100f;
            Chunk presentChunk = new Chunk("Present Percentage = " + pp.toString() + "%", detailsFont);
            // Creating Paragraph to add...
            Paragraph presentParagraph = new Paragraph(presentChunk);
            // Add to doc.
            doc.add(presentParagraph);

            // Absent Data.
            // Creating Chunk
            Float aa = absent *100f;
            Chunk absentChunk = new Chunk("Absent Percentage = " + aa.toString() + "%", detailsFont);
            // Creating Paragraph to add...
            Paragraph absentPara = new Paragraph(absentChunk);
            // Add to doc.
            doc.add(absentPara);

            // Leave Data.
            // Creating Chunk
            Float ll = leave *100f;
            Chunk leaveChunk = new Chunk("Leave Percentage = " + ll.toString() + "%", detailsFont);
            // Creating Paragraph to add...
            Paragraph leavePara = new Paragraph(leaveChunk);
            // Add to doc.
            doc.add(leavePara);

            // Close the document.
            doc.close();

            // Toast Message.
            Toast.makeText(CourseReport.this, "PDF Saved!", Toast.LENGTH_SHORT).show();

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
