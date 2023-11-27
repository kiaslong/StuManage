package com.ppl.stumanage.StudentManagement;

public class Certificate {
    String cId;
    String cName;
    String cDate;
    String cStudentId;
    public Certificate() {
    }
    public Certificate(String id,String name,String date,String sId){
        this.cId=id;
        this.cName=name;
        this.cDate=date;
        this.cStudentId=sId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public Certificate(String cName, String cDate, String cStudentId) {
        this.cName = cName;
        this.cDate = cDate;
        this.cStudentId = cStudentId;
    }

    public String getcDate() {
        return cDate;
    }

    public String getcId() {
        return cId;
    }

    public String getcName() {
        return cName;
    }

    public String getcStudentId() {
        return cStudentId;
    }
}
