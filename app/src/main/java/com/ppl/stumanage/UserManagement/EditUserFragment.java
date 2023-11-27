package com.ppl.stumanage.UserManagement;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;


import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ppl.stumanage.R;


public class EditUserFragment extends Fragment {


    public EditUserFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_user, container, false);

        EditText editTextEmail = view.findViewById(R.id.editEmail);
        EditText editTextName = view.findViewById(R.id.editName);
        EditText editTextAge = view.findViewById(R.id.editAge);
        EditText editTextPhoneNumber = view.findViewById(R.id.editPhone);
        RadioButton radioButtonManager = view.findViewById(R.id.radBtnManager);
        RadioButton radioButtonEmployee = view.findViewById(R.id.radBtnEmployee);

        Bundle bundle = getArguments();
        String userId = bundle.getString("userId");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        if (bundle != null) {


            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {

                    String userEmail = documentSnapshot.getString("email");
                    String userName = documentSnapshot.getString("name");
                    int userAge = documentSnapshot.getLong("age") != null ? documentSnapshot.getLong("age").intValue() : 0;
                    String userPhoneNumber = documentSnapshot.getString("phoneNumber");
                    String userRole = documentSnapshot.getString("role");


                    editTextEmail.setText(userEmail);
                    editTextEmail.setEnabled(false);
                    editTextName.setText(userName);
                    editTextAge.setText(String.valueOf(userAge));
                    editTextPhoneNumber.setText(userPhoneNumber);


                    if (userRole != null) {
                        if (userRole.equalsIgnoreCase("Manager")) {
                            radioButtonManager.setChecked(true);
                        } else if (userRole.equalsIgnoreCase("Employee")) {
                            radioButtonEmployee.setChecked(true);
                        }
                    }
                }
            }).addOnFailureListener(e -> {
                // Handle any errors that may occur during the fetch
                Log.e("EditUserFragment", "Error fetching user data: " + e.getMessage());
            });
        }

        Button saveChangesButton = view.findViewById(R.id.editBtnSave);
        saveChangesButton.setOnClickListener(v -> {

            String updatedName = editTextName.getText().toString();
            int updatedAge = Integer.parseInt(editTextAge.getText().toString());
            String updatedPhoneNumber = editTextPhoneNumber.getText().toString();
            String updatedRole = radioButtonManager.isChecked() ? "Manager" : "Employee";

            // Update user details in Firestore
            userRef.update(
                            "name", updatedName,
                            "age", updatedAge,
                            "phoneNumber", updatedPhoneNumber,
                            "role", updatedRole)
                    .addOnSuccessListener(aVoid -> {
                        // Update successful
                        Log.d("EditUserFragment", "User details updated successfully");
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new ViewUsersFragment())
                                .addToBackStack(null)
                                .commit();

                    })
                    .addOnFailureListener(e -> {
                        // Handle any errors
                        Log.e("EditUserFragment", "Error updating user details: " + e.getMessage());
                    });
        });


        return view;
    }


}