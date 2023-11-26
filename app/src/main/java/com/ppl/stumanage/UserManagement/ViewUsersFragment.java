package com.ppl.stumanage.UserManagement;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ppl.stumanage.MainActivity;
import com.ppl.stumanage.R;

import java.util.ArrayList;
import java.util.List;

public class ViewUsersFragment extends Fragment {
    private List<SystemUser> systemUserList = new ArrayList<>();
    private ProgressBar loadingIndicator;

    private SystemUserAdapter adapter = new SystemUserAdapter(systemUserList, new SystemUserAdapter.UserItemClickListener() {
        @Override
        public void onEditClicked(SystemUser user) {

            EditUserFragment editUserFragment = new EditUserFragment();
            // Pass any necessary data to the fragment using Bundle
            Bundle bundle = new Bundle();
            bundle.putString("userId", user.getUserId());
            editUserFragment.setArguments(bundle);

            replaceFragment(editUserFragment);
        }



        @Override
        public void onLockClicked(SystemUser user) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(user.getUserId());

            // Get the current status
            String currentStatus = user.getStatus();

            // Determine the new status
            String newStatus = (currentStatus.equalsIgnoreCase("Locked")) ? "Normal" : "Locked";

            // Update the user status
            userRef.update("status", newStatus)
                    .addOnSuccessListener(aVoid -> {
                        // Update successful
                        Log.d("Firestore", "User status updated to " + newStatus);
                        // You might want to update the UI or perform additional actions upon success
                    })
                    .addOnFailureListener(e -> {
                        // Handle errors
                        Log.e("Firestore", "Error updating user status: " + e.getMessage());
                    });
            fetchUpdatedUserData();
        }


    });


    public ViewUsersFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_users, container, false);

        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("View Users");
        }
        loadingIndicator = view.findViewById(R.id.loadingIndicator);


        // Initialize RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        fetchUpdatedUserData();



        recyclerView.setAdapter(adapter);

        return view;
    }

    private void fetchUpdatedUserData() {


        // Initially, set it visible to show loading
        loadingIndicator.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");

        usersRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            // Clear the list before adding new data
            systemUserList.clear();

            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                String userId = documentSnapshot.getId();
                String userName = documentSnapshot.getString("name");
                int userAge = documentSnapshot.getLong("age").intValue();
                String userEmail = documentSnapshot.getString("email");
                String userPhoneNumber = documentSnapshot.getString("phoneNumber");
                String userStatus = documentSnapshot.getString("status");
                String userRole = documentSnapshot.getString("role");
                String imageUrl=documentSnapshot.getString("profileImageURL");


                if(!userEmail.equals("admin@gmail.com")){
                // Create a SystemUser object and add it to the list
                SystemUser user = new SystemUser(userId, userEmail, userRole, userName, userAge, userPhoneNumber, userStatus,imageUrl);
                systemUserList.add(user);

                }
            }

            // Notify the adapter that the data set has changed
            adapter.notifyDataSetChanged();
            loadingIndicator.setVisibility(View.GONE);
        }).addOnFailureListener(e -> {
            // Handle any errors that may occur during the fetch
            Log.e("ViewUsersFragment", "Error fetching users: " + e.getMessage());
            loadingIndicator.setVisibility(View.GONE);
        });
    }
    public void replaceFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }


}