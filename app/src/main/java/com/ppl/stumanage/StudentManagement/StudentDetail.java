package com.ppl.stumanage.StudentManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ppl.stumanage.HomeFragment;
import com.ppl.stumanage.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentDetail extends AppCompatActivity {

    TextView TextViewName, TextViewGender, TextViewEmail, TextViewCourse;
    Button btnAddCertificate;
    CollectionReference certificateCollection;
    ListView listViewCertificate;
    List<Certificate> certificateList;
    CertificateList adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Student Detail");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        certificateCollection = db.collection("certificates");

        TextViewName = findViewById(R.id.textViewName);
        TextViewGender = findViewById(R.id.textViewGender);
        TextViewEmail = findViewById(R.id.textViewEmail);
        TextViewCourse = findViewById(R.id.textViewCourse);
        btnAddCertificate = findViewById(R.id.btnAddCertificate);
        listViewCertificate = findViewById(R.id.listViewCertificate);
        certificateList = new ArrayList<>();

        Intent intent = getIntent();
        String studentName = intent.getStringExtra(HomeFragment.STUDENT_NAME);
        String studentGender = intent.getStringExtra(HomeFragment.STUDENT_GENDER);
        String studentEmail = intent.getStringExtra(HomeFragment.STUDENT_EMAIL);
        String studentCourse = intent.getStringExtra(HomeFragment.STUDENT_COURSE);


        TextViewName.setText(studentName);
        TextViewGender.setText(studentGender);
        TextViewEmail.setText(studentEmail);
        TextViewCourse.setText(studentCourse);

        btnAddCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });
        listViewCertificate.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Certificate certificate = certificateList.get(position);
                showUpdateDialog(certificate.getcId(), certificate.getcName(), certificate.getcDate(), certificate.getcStudentId());
                return false;
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onStart() {
        super.onStart();
       fetchData();
    }

    private void showAddDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_certificate, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextCname = dialogView.findViewById(R.id.editTextCname);
        final EditText editTextDate = dialogView.findViewById(R.id.editTextDate);
        final Button buttonAddCertificate = dialogView.findViewById(R.id.buttonAddCertificate);

        dialogBuilder.setTitle("Add Certificate");
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        buttonAddCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextCname.getText().toString().trim();
                String date = editTextDate.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    editTextCname.setError("Please enter name");
                    return;
                }

                // Get the Firestore reference and add the certificate
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference certificateCollection = db.collection("certificates");
                Intent intent = getIntent();
                String studentId= intent.getStringExtra(HomeFragment.STUDENT_ID);

                Certificate certificate = new Certificate(name, date, studentId);

                certificateCollection.add(certificate)
                        .addOnSuccessListener(documentReference -> {

                            String generatedId = documentReference.getId();
                            certificate.setcId(generatedId);


                            certificateCollection.document(generatedId).set(certificate)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(StudentDetail.this, "Success", Toast.LENGTH_LONG).show();
                                        alertDialog.dismiss();
                                        fetchData();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(StudentDetail.this, "Failed to update certificate ID", Toast.LENGTH_LONG).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(StudentDetail.this, "Failed to add certificate", Toast.LENGTH_LONG).show();
                        });
            }
        });
    }


    private void showUpdateDialog(final String cId, final String cName, final String cDate, final String cStudentId){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_certificate,null);
        dialogBuilder.setView(dialogView);


        final EditText editTextCname = dialogView.findViewById(R.id.editTextCname);
        final EditText editTextDate = dialogView.findViewById(R.id.editTextDate);
        final Button buttonUpdate  = dialogView.findViewById(R.id.buttonUpdateC);
        final Button buttonDelete = dialogView.findViewById(R.id.buttonDeleteC);
        editTextCname.setText(cName);
        editTextDate.setText(cDate);
        dialogBuilder.setTitle("Update certificate : "+cName);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextCname.getText().toString().trim();
                String date = editTextDate.getText().toString().trim();

                if(TextUtils.isEmpty(name)){
                    editTextCname.setError("Please enter name");
                    return;
                }
                updateCertificate(cId,name,date,cStudentId);
                alertDialog.dismiss();
            }
        });
        // Delete student
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCertificate(cId);
                alertDialog.dismiss();
            }
        });


    }

    private void updateCertificate(String id, String name, String date, String sid){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference certificateRef = db.collection("certificates").document(id);

        Map<String, Object> updates = new HashMap<>();
        updates.put("cName", name);
        updates.put("cDate", date);

        certificateRef.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(StudentDetail.this, "Updated Successfully", Toast.LENGTH_LONG).show();
                        fetchData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StudentDetail.this, "Failed to update certificate", Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void deleteCertificate(String cId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference certificateRef = db.collection("certificates").document(cId);

        certificateRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(StudentDetail.this, "Certificate is deleted", Toast.LENGTH_LONG).show();
                        fetchData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StudentDetail.this, "Failed to delete certificate", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void fetchData(){
        String studentId = getIntent().getStringExtra(HomeFragment.STUDENT_ID);
        // Retrieving data from Firestore
        certificateCollection.whereEqualTo("cStudentId", studentId).addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Toast.makeText(StudentDetail.this, "Failed to fetch certificates", Toast.LENGTH_LONG).show();
                return;
            }

            certificateList.clear();
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Certificate certificate = documentSnapshot.toObject(Certificate.class);
                certificateList.add(certificate);
            }
            CertificateList adapter = new CertificateList(StudentDetail.this, certificateList);
            listViewCertificate.setAdapter(adapter);
        });
    }

}