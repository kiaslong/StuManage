package com.ppl.stumanage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ppl.stumanage.StudentManagement.Student;
import com.ppl.stumanage.StudentManagement.StudentDetail;
import com.ppl.stumanage.StudentManagement.StudentList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeFragment extends Fragment {

    public static final String STUDENT_NAME="studentName";
    public static final String STUDENT_ID="studentId";
    public static final String STUDENT_GENDER="studentGender";
    public static final String STUDENT_EMAIL="studentEmail";
    public static final String STUDENT_COURSE="studentCourse";
    private Button buttonAdd;
    ListView listViewStudent;
    List<Student> studentList;
    private StudentList adapter;
    private FirebaseFirestore firestore;
    private CollectionReference studentsCollection;
    private String Role;


    EditText editTextSearch;
    Button buttonSortBy;
    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        studentsCollection = firestore.collection("students");
        studentList = new ArrayList<>();
        adapter = new StudentList(getActivity(), studentList);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Home");
        }
        // Inflate the layout for this fragment



        buttonAdd= view.findViewById(R.id.buttonAddStudent);
        editTextSearch = view.findViewById(R.id.editTextSearch);



        listViewStudent=  view.findViewById(R.id.listViewStudent);
        listViewStudent.setAdapter(adapter);


        buttonSortBy = view.findViewById(R.id.buttonSortBy);
        buttonSortBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSortByDialog();
            }
        });
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                performSearch(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });
        listViewStudent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected student
                Student selectedStudent = studentList.get(position);

                // Create an intent to start the detail activity
                Intent intent = new Intent(getActivity(), StudentDetail.class);

                // Pass the student details to the detail activity
                intent.putExtra(STUDENT_NAME, selectedStudent.getStudentName());
                intent.putExtra(STUDENT_ID, selectedStudent.getStudentId());
                intent.putExtra(STUDENT_GENDER, selectedStudent.getStudentGender());
                intent.putExtra(STUDENT_EMAIL, selectedStudent.getStudentEmail());
                intent.putExtra(STUDENT_COURSE, selectedStudent.getStudentCourse());
                // Start the detail activity
                startActivity(intent);
            }
        });
        listViewStudent.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Student student = studentList.get(position);


                if (Role.equals("Employee")) {

                    return false;
                } else {
                    // For other roles, show the update dialog
                    showUpdateDialog(student.getStudentId(), student.getStudentName(),
                            student.getStudentGender(), student.getStudentEmail(), student.getStudentCourse());
                    return true;
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        fetchUserRole(userRole -> {
            Role=userRole;


            if (userRole.equals("Admin") || userRole.equals("Manager")) {

                enableAdminActions();
            } else if (userRole.equals("Employee")) {

                disableEmployeeActions();
            }

        });


        studentsCollection.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.w("listen", "Listen failed.", e);
                return;
            }

            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                studentList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    if (doc.exists()) {
                        Student student = doc.toObject(Student.class);
                        studentList.add(student);
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                Log.d("listen", "No such document");
            }
        });
    }
    private void showAddDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_student, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = dialogView.findViewById(R.id.editTextName);
        final EditText editTextEmail = dialogView.findViewById(R.id.editTextEmail);
        final EditText editTextCourse = dialogView.findViewById(R.id.editTextCourse);
        final Button buttonSubmit = dialogView.findViewById(R.id.buttonSubmitAdd);
        final RadioGroup radioGroupGender = dialogView.findViewById(R.id.radioGroupGender);
        final RadioButton radioButtonMale = dialogView.findViewById(R.id.radioButtonMale);
        final RadioButton radioButtonFemale = dialogView.findViewById(R.id.radioButtonFemale);

        dialogBuilder.setTitle("Add Student");
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String email = editTextEmail.getText().toString();
                String course = editTextCourse.getText().toString();
                String gender;
                int selectedRadioButtonId = radioGroupGender.getCheckedRadioButtonId();
                if (selectedRadioButtonId == radioButtonMale.getId()) {
                    gender = "Male";
                } else if (selectedRadioButtonId == radioButtonFemale.getId()) {
                    gender = "Female";
                } else {
                    gender = "";
                    Toast.makeText(getActivity(), "Choose gender", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!TextUtils.isEmpty(name)) {
                    Student student = new Student(name, gender, email, course);
                    studentsCollection.add(student)
                            .addOnSuccessListener(documentReference -> {
                                String studentId = documentReference.getId();
                                student.setStudentId(studentId);

                                studentsCollection.document(studentId)
                                        .set(student)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
                                            adapter.notifyDataSetChanged();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getActivity(), "Fail", Toast.LENGTH_LONG).show();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getActivity(), "Fail", Toast.LENGTH_LONG).show();
                            });
                } else {
                    Toast.makeText(getActivity(), "Fail", Toast.LENGTH_LONG).show();
                }
                alertDialog.dismiss();
            }
        });


    }

    //Update Student
    private void showUpdateDialog(final String studentId, final String studentName, final String studentGender, final String studentEmail, final String studentCourse){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_student,null);
        dialogBuilder.setView(dialogView);


        final EditText editTextName = dialogView.findViewById(R.id.editTextName);
        final EditText editTextGender = dialogView.findViewById(R.id.editTextGender);
        final EditText editTextEmail = dialogView.findViewById(R.id.editTextEmail);
        final EditText editTextCourse = dialogView.findViewById(R.id.editTextCourse);
        final Button buttonUpdate  = dialogView.findViewById(R.id.buttonUpdate);
        final Button buttonDelete = dialogView.findViewById(R.id.deleteButton);
        editTextName.setText(studentName);
        editTextGender.setText(studentGender);
        editTextEmail.setText(studentEmail);
        editTextCourse.setText(studentCourse);
        dialogBuilder.setTitle("Update Student : "+studentName);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String gender = editTextGender.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String course = editTextCourse.getText().toString().trim();

                if(TextUtils.isEmpty(name)){
                    editTextName.setError("Please enter name");
                    return;
                }
                updateStudent(studentId,name,gender,email,course);
                alertDialog.dismiss();
            }
        });
        // Delete student
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteStudent(studentId);
                alertDialog.dismiss();
            }
        });


    }

    private void updateStudent(String documentId, String name, String gender, String email, String course) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("students").document(documentId);

        // Update the student's data in Firestore
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("studentName", name);
        updatedData.put("studentGender", gender);
        updatedData.put("studentEmail", email);
        updatedData.put("studentCourse", course);

        docRef.update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Updated Successfully", Toast.LENGTH_LONG).show();
                    // Find the student in the list and update its details
                    for (int i = 0; i < studentList.size(); i++) {
                        Student currentStudent = studentList.get(i);
                        if (currentStudent != null && currentStudent.getStudentId() != null && currentStudent.getStudentId().equals(documentId)) {
                            currentStudent.setStudentName(name);
                            currentStudent.setStudentGender(gender);
                            currentStudent.setStudentEmail(email);
                            currentStudent.setStudentCourse(course);
                            studentList.set(i, currentStudent);
                            break;
                        }
                    }
                    // Notify adapter of the dataset change
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Update failed", Toast.LENGTH_LONG).show();
                });
    }

    private void deleteStudent(String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("students").document(documentId);

        docRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Student is deleted", Toast.LENGTH_LONG).show();
                        for (int i = 0; i < studentList.size(); i++) {
                            if (studentList.get(i).getStudentId().equals(documentId)) {
                                studentList.remove(i);
                                break;
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Deletion failed", Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void performSearch(String searchKeyword) {
        List<Student> searchResults = new ArrayList<>();

        for (Student student : studentList) {
            if (student.getStudentName().toLowerCase().contains(searchKeyword.toLowerCase()) ||
                    student.getStudentGender().toLowerCase().contains(searchKeyword.toLowerCase()) ||
                    student.getStudentCourse().toLowerCase().contains(searchKeyword.toLowerCase())) {
                searchResults.add(student);
            }
        }

        displaySearchResults(searchResults);
    }

    private void displaySearchResults(List<Student> searchResults) {
        StudentList adapter = new StudentList(getActivity(), searchResults);
        listViewStudent.setAdapter(adapter);
    }

    private void showSortByDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Sort By");
        String[] sortByOptions = {"Name", "Gender", "Course"};
        builder.setItems(sortByOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which) {
                    case 0:
                        sortBy("name");
                        break;
                    case 1:
                        sortBy("gender");
                        break;
                    case 2:
                        sortBy("course");
                        break;
                }
            }
        });
        builder.show();
    }

    private void sortBy(String criteria) {
        Collections.sort(studentList, new Comparator<Student>() {
            @Override
            public int compare(Student student1, Student student2) {
                switch (criteria) {
                    case "name":
                        return student1.getStudentName().compareToIgnoreCase(student2.getStudentName());
                    case "gender":
                        return student1.getStudentGender().compareToIgnoreCase(student2.getStudentGender());
                    case "course":
                        return student1.getStudentCourse().compareToIgnoreCase(student2.getStudentCourse());
                    default:
                        return 0;
                }
            }
        });
        StudentList adapter = new StudentList(getActivity(), studentList);
        listViewStudent.setAdapter(adapter);
    }

    private interface UserRoleCallback {
        void onUserRoleFetched(String userRole);
    }

    private void fetchUserRole(UserRoleCallback callback) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Get the user's role
                            String userRole = documentSnapshot.getString("role");
                            if (userRole != null) {
                                callback.onUserRoleFetched(userRole); // Pass the user role via callback
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure to fetch user role
                    });
        }
    }

    private void enableAdminActions() {

        buttonAdd.setVisibility(View.VISIBLE);

    }


    private void disableEmployeeActions() {

        buttonAdd.setVisibility(View.GONE);

    }

}