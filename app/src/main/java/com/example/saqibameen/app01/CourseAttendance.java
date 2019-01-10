package com.example.saqibameen.app01;

import java.util.ArrayList;
import java.util.Date;

public class CourseAttendance {

    private String courseName;
    private ArrayList<String> presentStudents = new ArrayList<String>();
    private ArrayList<String> absentStudents = new ArrayList<String>();
    private ArrayList<String> leaveStudents = new ArrayList<String>();
    private Date currentDate;

    public ArrayList<String> getLeaveStudents() {
        return leaveStudents;
    }

    public void setLeaveStudents(ArrayList<String> leaveStudents) {
        this.leaveStudents = leaveStudents;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    //Empty Constructor

    public CourseAttendance(String courseName, ArrayList<String> presentStudents, ArrayList<String> absentStudents, ArrayList<String> leaveStudents, Date currentDate) {
        this.courseName = courseName;
        this.presentStudents = presentStudents;
        this.absentStudents = absentStudents;
        this.leaveStudents = leaveStudents;
        this.currentDate = currentDate;
    }

    public CourseAttendance(){
        this.currentDate = new Date();
    }

    //Getters and Setters
    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public ArrayList<String> getPresentStudents() {
        return presentStudents;
    }

    public void setPresentStudents(ArrayList<String> presentStudents) {
        this.presentStudents = presentStudents;
    }

    public ArrayList<String> getAbsentStudents() {
        return absentStudents;
    }

    public void setAbsentStudents(ArrayList<String> absentStudents) {
        this.absentStudents = absentStudents;
    }
}
