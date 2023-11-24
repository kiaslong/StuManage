package com.ppl.stumanage;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.protobuf.StringValue;
import com.ppl.stumanage.UserManagement.SystemUser;


public class SettingsFragment extends Fragment {



    public SettingsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Find all EditText fields
        EditText editTextEmail = view.findViewById(R.id.editTextEmail);
        EditText editTextName = view.findViewById(R.id.editTextName);
        EditText editTextAge = view.findViewById(R.id.editTextAge);
        EditText editTextPhoneNumber = view.findViewById(R.id.editTextPhoneNumber);
        TextView tvStatus= view.findViewById(R.id.textViewStatus);


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userUid = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(userUid);


            userRef.get().addOnSuccessListener(queryDocumentSnapshot -> {
                // Clear the list before adding new data




                    String userName = queryDocumentSnapshot.getString("name");
                    int userAge = queryDocumentSnapshot.getLong("age").intValue();
                    String userEmail = queryDocumentSnapshot.getString("email");
                    String userPhoneNumber = queryDocumentSnapshot.getString("phoneNumber");
                    String userStatus = queryDocumentSnapshot.getString("status");

                    editTextName.setText(userName);
                    editTextEmail.setText(userEmail);
                    editTextAge.setText(String.valueOf(userAge));
                    editTextPhoneNumber.setText(userPhoneNumber);
                    tvStatus.setText(userStatus);

                if (userStatus.equals("Locked")) {
                    tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
                } else if (userStatus.equals("Normal")) {
                    tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.green));
                }


            }).addOnFailureListener(e -> {
                // Handle any errors that may occur during the fetch
                Log.e("ViewUsersFragment", "Error fetching users: " + e.getMessage());

            });

        }



        // Handle Save Changes button click
        Button btnSaveChanges = view.findViewById(R.id.btnSaveChanges);
        btnSaveChanges.setOnClickListener(v -> {

        });

        return view;
    }
}