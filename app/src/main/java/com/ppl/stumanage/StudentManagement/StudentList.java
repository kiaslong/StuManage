package com.ppl.stumanage.StudentManagement;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.ppl.stumanage.R;

import java.util.List;

public class StudentList extends ArrayAdapter<Student> implements Filterable {
    private Activity context;
    private List<Student> StudentList;

    public StudentList(Activity context, List<Student> StudentList){
        super(context, R.layout.list_layout, StudentList);
        this.context = context;
        this.StudentList = StudentList;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View listViewItem = inflater.inflate(R.layout.list_layout,null,true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        TextView textViewGender = (TextView) listViewItem.findViewById(R.id.textViewGender);
        TextView textViewCourse = (TextView) listViewItem.findViewById(R.id.textViewCourse);

        Student student = StudentList.get(position);

        textViewName.setText(student.getStudentName());
        textViewGender.setText(student.getStudentGender());
        textViewCourse.setText(student.getStudentCourse());

        return listViewItem;
    }

    @Override
    public Filter getFilter() {
        return super.getFilter();
    }
}
