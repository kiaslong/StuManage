package com.ppl.stumanage;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private EditText loginEmail, loginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);

        Button loginButton = findViewById(R.id.login_button);
        TextView forgotPassword = findViewById(R.id.forgot_password);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPasswordDialog();
            }
        });
    }

    private void loginUser() {
        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();

        if (!email.isEmpty() && !password.isEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Login success, handle accordingly
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                // Get the current timestamp
                                long currentTime = System.currentTimeMillis();

                                // Check if loginTimeList field exists
                                firestore.collection("users").document(user.getUid())
                                        .get()
                                        .addOnSuccessListener(documentSnapshot -> {
                                            if (documentSnapshot.exists()) {
                                                if(!user.getEmail().equals("admin@gmail.com")) {
                                                    if (documentSnapshot.contains("loginTimeList")) {
                                                        // The field exists, update the array with the new timestamp
                                                        firestore.collection("users").document(user.getUid())
                                                                .update("loginTimeList", FieldValue.arrayUnion(currentTime))
                                                                .addOnSuccessListener(aVoid -> {
                                                                    // Successfully updated login time
                                                                    Log.d("LoginActivity", "Login time updated in Firestore");
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    // Failed to update login time
                                                                    Log.e("LoginActivity", "Failed to update login time in Firestore: " + e.getMessage());
                                                                });
                                                    } else {
                                                        // The field doesn't exist, create the field with the first login time
                                                        firestore.collection("users").document(user.getUid())
                                                                .update("loginTimeList", FieldValue.arrayUnion(currentTime))
                                                                .addOnSuccessListener(aVoid -> {
                                                                    // Successfully created loginTimeList field with login time
                                                                    Log.d("LoginActivity", "loginTimeList field created in Firestore");
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    // Failed to create loginTimeList field
                                                                    Log.e("LoginActivity", "Failed to create loginTimeList field in Firestore: " + e.getMessage());
                                                                });
                                                    }
                                                }
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            // Failed to get document
                                            Log.e("LoginActivity", "Failed to get document from Firestore: " + e.getMessage());
                                        });
                            }
                        } else {
                            // Login failed, display error message
                            Toast.makeText(LoginActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }


    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.diaglog_forgot, null);
        EditText userEmail = view.findViewById(R.id.editBox);

        builder.setView(view);
        AlertDialog dialog = builder.create();

        view.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compareEmail(userEmail);
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialog.show();
    }

    private void compareEmail(EditText email) {
        if (email.getText().toString().isEmpty()) {
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            return;
        }
        firebaseAuth.sendPasswordResetEmail(email.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Check your email", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
