package com.ppl.stumanage.StudentManagement;

public class Student {
    String studentId;
    String studentName;
    String studentEmail;
    String studentGender;
    String studentCourse;


    public Student(){


    }


    public Student(String Id, String Name, String Gender, String Email, String Course) {
        this.studentId=Id;
        this.studentName=Name;
        this.studentGender=Gender;
        this.studentEmail=Email;
        this.studentCourse=Course;
    }

    public Student(String studentName, String studentEmail, String studentGender, String studentCourse) {
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.studentGender = studentGender;
        this.studentCourse = studentCourse;
    }

    public String getStudentCourse() {
        return studentCourse;
    }

    public String getStudentEmail() {
        return studentEmail;
    }
    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentGender() {
        return studentGender;
    }

    public void setStudentId(String studentId) {
        this.studentId=studentId;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public void setStudentGender(String studentGender) {
        this.studentGender = studentGender;
    }

    public void setStudentCourse(String studentCourse) {
        this.studentCourse = studentCourse;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
