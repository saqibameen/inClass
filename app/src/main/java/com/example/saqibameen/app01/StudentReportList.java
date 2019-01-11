package com.example.saqibameen.app01;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StudentReportList extends ArrayAdapter<Student> {
    private Activity context;
    private List<Student> students;
    private ArrayList<Student> arrayListStudents;
    private String courseName;

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

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
    public StudentReportList(Activity context, List<Student> students, String courseName){
        super(context,R.layout.custom_item,students);
        this.context = context;
        this.students = students;
        this.arrayListStudents = new ArrayList<Student>();
        this.arrayListStudents.addAll(this.students);
        this.courseName = courseName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Inflator to inflate the custom view.
        LayoutInflater inflater = context.getLayoutInflater();

        //  CustomView to be inflated.
        final View listViewItem = inflater.inflate(R.layout.custom_item_report, null, true);

        // Grab the data.
        final TextView textName = (TextView) listViewItem.findViewById(R.id.cv_name);
        final TextView textRollNo = (TextView) listViewItem.findViewById(R.id.cv_rollNo);
        final ImageView ivViewReport = (ImageView) listViewItem.findViewById(R.id.ivViewReport);

        // Inflate the view.
        final Student std = students.get(position);
        textName.setText(std.getName());
        textRollNo.setText(std.getRollNo());

        // Set Listener on the imageView.
        ivViewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Grab the rollNo.
                String rollNo = textRollNo.getText().toString();
                // Create New Intent.
                Intent nextIntent =  new Intent(context, StudentReportView.class);
                // Put the roll No.
                nextIntent.putExtra("rollNo", rollNo);
                // Put the course name.
                nextIntent.putExtra( "courseName", courseName);
                // start the activity.
                context.startActivity(nextIntent);
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

}
