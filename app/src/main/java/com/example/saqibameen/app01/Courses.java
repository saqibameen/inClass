package com.example.saqibameen.app01;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Courses extends AppCompatActivity {

    ArrayList<String> courses= new ArrayList<>();
    ArrayList<String> coursesName = new ArrayList<>();
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        // Activity Title.
        setTitle("Courses Teaching");

        String uid = "18ui18zt76aK8GQMwN5pJFIjHqj2";

        // Database Reference.
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query dbQuery = database.getReference("teachers-courses").orderByChild("teacherNo").equalTo(uid);

        // ListView Reference.
        lv = (ListView) findViewById(R.id.listViewCourses);

        // Add Listener to fill the data.
        dbQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot courseSnapshot: dataSnapshot.getChildren()) {
                    String course = courseSnapshot.child("courseNo").getValue().toString();
                    courses.add(course);
                }

                // Resolve query for courses names.
                Query coursesQuery = database.getReference("courses");

                // Event listenere to update.
                coursesQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot courseSnapshot: dataSnapshot.getChildren()) {
                            // Grab the key.
                            String key = courseSnapshot.getKey();
                            // If key is required.
                            if (courses.contains(key)){
                                String courseName = courseSnapshot.child("name").getValue().toString();
                                coursesName.add(courseName);
                            }
                        }

                        // Fill the list.
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                Courses.this,
                                android.R.layout.simple_list_item_1,
                                coursesName );

                        // Set the adapter.
                        lv.setAdapter(arrayAdapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        // Listener for item click.
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                // Create new intent.
                Intent intent = new Intent(Courses.this, Attendance.class);
                // Grab the item clicked.
                String item = (String) parent.getItemAtPosition(position);
                // Attach the string to new intent.
                intent.putExtra("courseName", item);
                // Start the new activity.
                startActivity(intent);
            }
        });
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
                        Courses.super.onBackPressed();
                    }
                }).create().show();
    }
}
