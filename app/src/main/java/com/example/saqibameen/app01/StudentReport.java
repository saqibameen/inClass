package com.example.saqibameen.app01;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

public class StudentReport extends AppCompatActivity {
    // Data members.
    private String courseName;
    private ListView lv;
    private ArrayList<Student> students= new ArrayList<>();
    private StudentReportList list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_report);
        // Set the title.
        setTitle("Student Report");

        // Grab the course name from intent.
        Intent intent = getIntent();
        // Extract the name.
        courseName = intent.getStringExtra("courseName");
        final ArrayList<String> studentKeys = new ArrayList<>();

        // Grab data from database.
        // Database Reference.
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference("courses");

        // ListView Reference.
        lv = (ListView) findViewById(R.id.lvStudents);

        // Add Listener to fill the data.
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Boolean found = false;
                    for (DataSnapshot courseSnapshot: dataSnapshot.getChildren()) {
                        // Grab the course name.
                        String tempCourseName = courseSnapshot.child("name").getValue().toString();
                        // Grab the course key.
                        if (tempCourseName.equals(courseName)) {
                            for (DataSnapshot subStd: courseSnapshot.child("students").getChildren()) {
                                String key = subStd.child("studentNo").getValue().toString();
                                studentKeys.add(key);
                            }
                            found = true;
                        }
                        // Break when key is found.
                        if (found) break;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


        // Finally grab the students.
        // Build Query for the table.
        final Query stdQuery = database.getReference("students");

        stdQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot courseSnapshot: dataSnapshot.getChildren()) {
                    // Grab the key.
                    String stdKey = courseSnapshot.getKey();
                    // Match the key.
                    if (studentKeys.contains(stdKey)) {
                        // Grab the student.
                        Student std = courseSnapshot.getValue(Student.class);
                        // Add to the list.
                        students.add(std);
                    }
                }
                // Creating object of custom view item.
                list = new StudentReportList(StudentReport.this, students, courseName);
                lv.setAdapter(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());

            }
        });


        // Add Listener to search the data.
        // Grab the edit text view.
        final EditText editTxt = (EditText)findViewById(R.id.etSearchQuery);
        editTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // Grab radio button to identify search type.
                RadioGroup rg = (RadioGroup)findViewById(R.id.rgSearchBox);
                // Grab the value of radio button.
                String value = ((RadioButton)findViewById(rg.getCheckedRadioButtonId()))
                        .getText().toString();
                // Get the Search Query.
                String text = editTxt.getText().toString().toLowerCase(Locale.getDefault());

                // Check the search type.
                if (value.equals("Name")) {
                    list.filterByName(text);
                } else {
                    list.filterByRollNo(text);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
