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

public class CertificateList extends ArrayAdapter<Certificate> implements Filterable {
    private Activity context;
    private List<Certificate> CertificateList;

    public CertificateList(Activity context, List<Certificate> CertificateList){
        super(context, R.layout.certificate_list_layout, CertificateList);
        this.context = context;
        this.CertificateList = CertificateList;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View listViewItem = inflater.inflate(R.layout.certificate_list_layout,null,true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        TextView textViewDate = (TextView) listViewItem.findViewById(R.id.textViewDate);

        Certificate certificate = CertificateList.get(position);

        textViewName.setText(certificate.getcName());
        textViewDate.setText(certificate.getcDate());


        return listViewItem;
    }

    @Override
    public Filter getFilter() {
        return super.getFilter();
    }
}
