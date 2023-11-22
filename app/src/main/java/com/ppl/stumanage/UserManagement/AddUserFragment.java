package com.ppl.stumanage.UserManagement;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ppl.stumanage.R;

import java.util.HashMap;
import java.util.Map;

public class AddUserFragment extends Fragment {

    private EditText editTextEmail;

    private EditText editTextName;
    private EditText editTextAge;
    private EditText editTextPhoneNumber;
    private RadioGroup radioGroupRole;
    private RadioButton radioButtonManager;
    private RadioButton radioButtonEmployee;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;


    public AddUserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_user, container, false);

        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize EditText fields
        editTextEmail = view.findViewById(R.id.editTextEmail);
        radioGroupRole = view.findViewById(R.id.radioGroupRole);
        radioButtonManager = view.findViewById(R.id.radioButtonManager);
        radioButtonEmployee = view.findViewById(R.id.radioButtonEmployee);
        editTextName = view.findViewById(R.id.editTextName);
        editTextAge = view.findViewById(R.id.editTextAge);
        editTextPhoneNumber = view.findViewById(R.id.editTextPhoneNumber);

        Button createUserButton = view.findViewById(R.id.btnCreateUser);
        
        createUserButton.setOnClickListener(v -> createUserAccount());

        return view;
    }

    private void createUserAccount() {
        String email = editTextEmail.getText().toString().trim();
        int selectedRoleId = radioGroupRole.getCheckedRadioButtonId();
        String selectedRole;
        if (selectedRoleId == radioButtonManager.getId()) {
            selectedRole = "Manager";
        } else if (selectedRoleId == radioButtonEmployee.getId()) {
            selectedRole = "Employee";
        } else {
            selectedRole = "";
        }
        String name = editTextName.getText().toString().trim();
        String ageStr = editTextAge.getText().toString().trim();
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        
        if (TextUtils.isEmpty(selectedRole)) {
            // Show an error message if no role is selected
            Toast.makeText(requireContext(), "Please select a role", Toast.LENGTH_SHORT).show();
            return;
        }

        // Perform error checking
        if (TextUtils.isEmpty(email)  || TextUtils.isEmpty(name) ||
                TextUtils.isEmpty(ageStr) || TextUtils.isEmpty(phoneNumber)) {
            // Show an error message if any field is empty
            Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            // Show an error message for invalid age
            Toast.makeText(requireContext(), "Invalid age format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proceed with creating the user account and storing data in Firestore
        // Use Firebase Authentication createUserWithEmailAndPassword method
        firebaseAuth.createUserWithEmailAndPassword(email, "123456")
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // User account created successfully
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // User authentication successful, now store additional user data in Firestore
                            storeUserData(user.getUid(), email, selectedRole, name, age, phoneNumber);
                        }
                    } else {
                        // Handle account creation failure
                        Toast.makeText(requireContext(), "Failed to create user account", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void storeUserData(String userId, String email, String role, String name, int age, String phoneNumber) {
        DocumentReference userRef = firestore.collection("users").document(userId);

        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("role", role);
        userData.put("name", name);
        userData.put("age", age);
        userData.put("phoneNumber", phoneNumber);
        userData.put("status", "locked"); // Adding the "status" field with default value

        userRef.set(userData)
                .addOnSuccessListener(aVoid -> {
                    // User data added successfully
                    Toast.makeText(requireContext(), "User account created successfully", Toast.LENGTH_SHORT).show();
                    // Clear the form or navigate to another fragment/activity
                })
                .addOnFailureListener(e -> {
                    // Handle errors while adding user data to Firestore
                    Toast.makeText(requireContext(), "Failed to store user data", Toast.LENGTH_SHORT).show();
                });
    }
}
