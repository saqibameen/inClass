package com.example.saqibameen.app01;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
        Intent login = new Intent(Courses.this, MainActivity.class);
        startActivity(login);
        // Finish currentActivity.
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                Intent intent = new Intent(Courses.this, CourseDashboard.class);
                // Grab the item clicked.
                String item = (String) parent.getItemAtPosition(position);
                // Attach the string to new intent.
                intent.putExtra("courseName", item);
                // Start the new activity.
                startActivity(intent);
            }
        });
    }
}
