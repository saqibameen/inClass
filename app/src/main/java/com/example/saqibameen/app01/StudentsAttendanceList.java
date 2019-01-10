package com.example.saqibameen.app01;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StudentsAttendanceList extends ArrayAdapter<Student> {

    private Activity context;
    private List<Student> students;
    private ArrayList<Student> arrayListStudents;

    @NonNull
    @Override
    public Activity getContext() {
        return context;
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    // Constructor.
    public StudentsAttendanceList(Activity context, List<Student> students){
        super(context,R.layout.custom_item,students);
        this.context = context;
        this.students = students;
        this.arrayListStudents = new ArrayList<Student>();
        this.arrayListStudents.addAll(this.students);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Inflator to inflate the custom view.
        LayoutInflater inflater = context.getLayoutInflater();

        //  CustomView to be inflated.
        final View listViewItem = inflater.inflate(R.layout.custom_item, null, true);

        // Grab the data.
        final TextView textName = (TextView) listViewItem.findViewById(R.id.cv_name);
        final TextView textRollNo = (TextView) listViewItem.findViewById(R.id.cv_rollNo);
        final RadioGroup rg = (RadioGroup)listViewItem.findViewById(R.id.rgAttendance);

        // Inflate the view.
        final Student std = students.get(position);
        textName.setText(std.getName());
        textRollNo.setText(std.getRollNo());

        // Get attendance.
        String att = std.getAttendance();
        // Switch and mark attendance.
        switch(att) {
            case "p" :
                RadioButton rbP = rg.findViewById(R.id.rbPresent);
                // Set it true.
                rbP.setChecked(true);
                break;
            case "a" :
                RadioButton rbA = rg.findViewById(R.id.rbAbsent);
                // Set it true.
                rbA.setChecked(true);
                break;
            case "l" :
                RadioButton rbL = rg.findViewById(R.id.rbLeave);
                // Set it true.
                rbL.setChecked(true);
        }

        // Add Listener to radio button change.
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Grab the value of radio button.
                final String value = ((RadioButton)listViewItem.findViewById(rg.getCheckedRadioButtonId()))
                        .getText().toString();
                // When the attendance is changed.
                switch(value) {
                    case "p" :
                        RadioButton rbP = rg.findViewById(R.id.rbPresent);
                        // Set it true.
                        rbP.setChecked(true);
                        // Change the attendance.
                        std.setAttendance("p");
                        break;
                    case "a" :
                        RadioButton rbA = rg.findViewById(R.id.rbAbsent);
                        // Set it true.
                        rbA.setChecked(true);
                        // Change the attendance.
                        std.setAttendance("a");
                        break;
                    case "l" :
                        RadioButton rbL = rg.findViewById(R.id.rbLeave);
                        // Set it true.
                        rbL.setChecked(true);
                        // Change the attendance.
                        std.setAttendance("l");
                }
            }
        });

        return listViewItem;
    }

    /**
     * Filters the list for the given query.
     *
     * @param charText search query — name.
     */
    public void filterByName(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        students.clear();
        if (charText.length() == 0) {
            students.addAll(arrayListStudents);
        } else {
            for (Student std : arrayListStudents) {
                if (std.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    students.add(std);
                }
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Filters the list for given roll no.
     *
     * @param charText search query — roll no.
     */
    public void filterByRollNo(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        students.clear();
        if (charText.length() == 0) {
            students.addAll(arrayListStudents);
        } else {
            for (Student std : arrayListStudents) {
                if (std.getRollNo().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    students.add(std);
                }
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Saves the attendance to db.
     *
     * @param courseName the current course.
     */
    public void saveAttendance(String courseName){
        // Attendance Object.
        CourseAttendance attendance = new CourseAttendance();
        attendance.setCourseName(courseName);
        // Grab Attendance for individual student.
        for (Student temp : arrayListStudents)
        {

            switch (temp.getAttendance()) {
                case "p":
                    attendance.getPresentStudents().add(temp.getRollNo());
                    break;
                case "a":
                    attendance.getAbsentStudents().add(temp.getRollNo());
                    break;
                case "l":
                    attendance.getLeaveStudents().add(temp.getRollNo());
                    break;
            }
        }
        // Save to db.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference("attendance");
        // Generate Unique Key.
        String id = dbRef.push().getKey();
        // Insert the attendance
        dbRef.child(id).setValue(attendance);
        // Show the notification.
        Toast.makeText(context, "Attendance Saved!", Toast.LENGTH_SHORT).show();
    }
}
