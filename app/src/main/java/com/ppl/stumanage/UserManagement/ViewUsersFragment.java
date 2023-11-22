package com.ppl.stumanage.UserManagement;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ppl.stumanage.R;

import java.util.ArrayList;
import java.util.List;

public class ViewUsersFragment extends Fragment {
    private List<SystemUser> systemUserList = new ArrayList<>();
    private SystemUserAdapter adapter= new SystemUserAdapter(systemUserList);

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
        ProgressBar loadingIndicator = view.findViewById(R.id.loadingIndicator);

        // Initially, set it visible to show loading
        loadingIndicator.setVisibility(View.VISIBLE);

        // Initialize RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");

        usersRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            // Clear the list before adding new data
            systemUserList.clear();

            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                String userId = documentSnapshot.getId();
                // Retrieve data for each user document
                String userName = documentSnapshot.getString("name");
                int userAge = documentSnapshot.getLong("age").intValue();
                String userEmail = documentSnapshot.getString("email");
                String userPhoneNumber = documentSnapshot.getString("phoneNumber");
                String userStatus = documentSnapshot.getString("status");
                String userRole = documentSnapshot.getString("role");

                // Create a SystemUser object and add it to the list
                SystemUser user = new SystemUser(userId, userEmail, userRole, userName, userAge, userPhoneNumber,userStatus);
                systemUserList.add(user);
                Log.d("User", user.toString());
            }

            // Notify the adapter that the data set has changed
            adapter.notifyDataSetChanged();
            loadingIndicator.setVisibility(View.GONE);
        }).addOnFailureListener(e -> {
            // Handle any errors that may occur during the fetch
            Log.e("ViewUsersFragment", "Error fetching users: " + e.getMessage());
            loadingIndicator.setVisibility(View.GONE);
        });


        recyclerView.setAdapter(adapter);

        return view;
    }

}