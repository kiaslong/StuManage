package com.ppl.stumanage;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.protobuf.StringValue;
import com.ppl.stumanage.UserManagement.LoginHistoryFragment;
import com.ppl.stumanage.UserManagement.SystemUser;


public class SettingsFragment extends Fragment {



    public SettingsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        getActivity().getMenuInflater().inflate(R.menu.settings_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_change_password) {
            showChangePasswordDialog();

        } else if (item.getItemId() == R.id.action_delete_account) {
            showDeleteAccountDialog();
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Settings");
        }



        // Find all EditText fields
        EditText editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextEmail.setEnabled(false);

        EditText editTextName = view.findViewById(R.id.editTextName);
        EditText editTextAge = view.findViewById(R.id.editTextAge);
        EditText editTextPhoneNumber = view.findViewById(R.id.editTextPhoneNumber);
        TextView tvStatus= view.findViewById(R.id.textViewStatus);


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String userUid = currentUser.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userUid);
        if (currentUser != null) {




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
            String newName = editTextName.getText().toString();
            int newAge = Integer.parseInt(editTextAge.getText().toString());
            String newPhoneNumber = editTextPhoneNumber.getText().toString();

            // Update the Firestore document
            userRef.update("name", newName,
                            "age", newAge,
                            "phoneNumber", newPhoneNumber)
                    .addOnSuccessListener(aVoid -> {
                        // Successfully updated data
                        Log.d("SettingsFragment", "User data updated successfully");
                        // You can show a toast or perform any additional actions upon success
                        Toast.makeText(requireContext(), "Changes saved", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new HomeFragment())
                                .addToBackStack(null)
                                .commit();
                    })
                    .addOnFailureListener(e -> {
                        // Failed to update data
                        Log.e("SettingsFragment", "Error updating user data: " + e.getMessage());
                        // Handle the failure, display an error message, etc.
                        Toast.makeText(requireContext(), "Failed to save changes", Toast.LENGTH_SHORT).show();
                    });
        });

        return view;
    }


    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.diaglog_changepassword, null);

        EditText editTextNewPassword = dialogView.findViewById(R.id.editPasswordBox);
        EditText editConfirmNewPassword = dialogView.findViewById(R.id.editConfirmBox);
        Button btnCancel = dialogView.findViewById(R.id.btnForgotCancel);
        Button btnChange = dialogView.findViewById(R.id.btnChangePass);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnChange.setOnClickListener(v -> {
            String newPassword = editTextNewPassword.getText().toString();
            String confirmPassword = editConfirmNewPassword.getText().toString();

            if (!newPassword.isEmpty() && newPassword.equals(confirmPassword)) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    user.updatePassword(newPassword)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Password updated successfully
                                    Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    // Failed to update password
                                    Toast.makeText(requireContext(), "Failed to change password", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } else {
                // Show error message if passwords don't match or empty
                Toast.makeText(requireContext(), "Passwords don't match or empty", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showDeleteAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.diaglog_delete_account, null);

        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnDelete = dialogView.findViewById(R.id.btnDelete);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnDelete.setOnClickListener(v -> {
            // Firebase authentication delete user
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                user.delete()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Successfully deleted user
                                Toast.makeText(requireContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show();

                                FirebaseFirestore db = FirebaseFirestore.getInstance();

                                db.collection("users").document(user.getUid())
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            // DocumentSnapshot successfully deleted
                                            Log.d("Delete User", "DocumentSnapshot successfully deleted!");
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle any errors
                                            Log.w("Delete User", "Error deleting document", e);
                                        });


                                startActivity(new Intent(requireContext(), LoginActivity.class));
                                requireActivity().finish(); // Close the current activity
                            } else {
                                // Failed to delete account
                                Toast.makeText(requireContext(), "Failed to delete account", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        dialog.show();
    }



}