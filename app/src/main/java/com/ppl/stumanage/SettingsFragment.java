package com.ppl.stumanage;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class SettingsFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST =111;
    private boolean isAdmin ;
    private ActivityResultLauncher<String> galleryLauncher;

    public SettingsFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    // Handle the returned URI here
                    if (uri != null) {
                        // Upload the image to Firebase Storage
                        uploadImageToFirebaseStorage(uri);
                    }
                }
        );
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
    public void onPrepareOptionsMenu(Menu menu) {
       isAdmin = checkIfAdmin();
        MenuItem deleteAccountItem = menu.findItem(R.id.action_delete_account);

        if (isAdmin) {
            // User is an admin, hide the delete account item
            deleteAccountItem.setVisible(false);
        } else {
            // User is not an admin, show the delete account item
            deleteAccountItem.setVisible(true);
        }

        super.onPrepareOptionsMenu(menu);
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
        ImageView imageView = view.findViewById(R.id.imageView);


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

                String imageUrl = queryDocumentSnapshot.getString("profileImageURL");

                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.default_logo_user) // Placeholder image while loading
                            .error(R.drawable.default_logo_user) // Image to show if loading fails
                            .into(imageView);
                }
                editTextName.setText(userName);
                editTextEmail.setText(userEmail);
                editTextAge.setText(String.valueOf(userAge));
                editTextPhoneNumber.setText(userPhoneNumber);
                tvStatus.setText(userStatus);

                if (userStatus.equals("Status: Locked")) {
                    tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
                } else if (userStatus.equals("Status: Normal")) {
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

        Button btnEditProfilePic=view.findViewById(R.id.btnEditProfilePic);
        btnEditProfilePic.setOnClickListener(v->{

            galleryLauncher.launch("image/*");

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

    private boolean checkIfAdmin() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(!currentUser.getEmail().equals("admin@gmail.com"))
            return false;
        return true;
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        // Get reference to Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("profile_pictures/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

        // Upload the file to Firebase Storage
        UploadTask uploadTask = storageRef.putFile(imageUri);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
            Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
        }).addOnSuccessListener(taskSnapshot -> {
            // Task completed successfully, get the download URL
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();

                // Update the image URL in Firestore under the user's document
                updateImageUrlInFirestore(imageUrl);
            });
        });
    }

    private void updateImageUrlInFirestore(String imageUrl) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(currentUser.getUid());

            userRef.update("profileImageURL", imageUrl)
                    .addOnSuccessListener(aVoid -> {
                        updateProfilePictureFromUrl(imageUrl);
                        Toast.makeText(requireContext(), "Profile image updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Failed to update profile image URL
                        Toast.makeText(requireContext(), "Failed to update profile image", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateProfilePictureFromUrl(String imageUrl) {
        ImageView imageView = requireView().findViewById(R.id.imageView);

        // Load image using Glide library
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.default_logo_user) // Placeholder image while loading
                .error(R.drawable.default_logo_user) // Image to show if loading fails
                .into(imageView);
    }

}