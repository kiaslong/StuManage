package com.ppl.stumanage.UserManagement;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class LoginHistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private LoginHistoryAdapter adapter;
     ProgressBar progressBar;
    private List<LoginHistoryModel> loginHistoryList;

    public LoginHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        loginHistoryList = new ArrayList<>();


    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        getActivity().getMenuInflater().inflate(R.menu.filter_menu, menu);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_history, container, false);
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Login History");
        }
        progressBar = view.findViewById(R.id.progress_bar);
        return view;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            showUserSelectionDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view_login_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new LoginHistoryAdapter(loginHistoryList);
        recyclerView.setAdapter(adapter);
        fetchAllUsersForLoginHistory();
    }

    private void fetchAllUsersForLoginHistory() {

        progressBar.setVisibility(View.VISIBLE);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");

        usersRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            loginHistoryList.clear();

            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                // Retrieve data for each user document
                String userName = documentSnapshot.getString("name");
                String userEmail = documentSnapshot.getString("email");
                List<Long> loginTime = (List<Long>) documentSnapshot.get("loginTimeList");

                if (loginTime != null && !loginTime.isEmpty()) {
                    for (Long time : loginTime) {
                        String loginTimeString = convertLongToDate(time);
                        LoginHistoryModel loginHistory = new LoginHistoryModel(userName, userEmail, loginTimeString);
                        loginHistoryList.add(loginHistory);
                    }
                }
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            progressBar.setVisibility(View.GONE);
        }).addOnFailureListener(e -> {
            Log.e("LoginHistoryFragment", "Error fetching users for login history: " + e.getMessage());
            progressBar.setVisibility(View.GONE);
        });
    }

    private String convertLongToDate(Long time) {
        // Assuming 'time' represents milliseconds since epoch
        Date date = new Date(time);
        // Define your desired date format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(date); // Returns the formatted date string
    }

    private List<String> getUserNamesFromList(List<LoginHistoryModel> loginHistoryList) {
        List<String> userNames = new ArrayList<>();
        HashSet<String> uniqueNames = new HashSet<>();

        for (LoginHistoryModel user : loginHistoryList) {
            if (uniqueNames.add(user.getUsername())) {
                userNames.add(user.getUsername());
            }
        }
        return userNames;
    }


    private void filterByUserName(String selectedUserName) {
        List<LoginHistoryModel> filteredList = new ArrayList<>();

        for (LoginHistoryModel user : loginHistoryList) {
            if (user.getUsername().equals(selectedUserName)) {
                filteredList.add(user);
            }
        }

        // Update the adapter with the filtered list
        adapter.filterList(filteredList);
    }

    private void showUserSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select User");


        // Get a list of unique user names to display in the dialog
        List<String> userNames = getUserNamesFromList(loginHistoryList);

        // Add a reset icon at the beginning of the dialog list
        userNames.add(0, "Reset Filter \uD83D\uDD04");

        builder.setItems(userNames.toArray(new String[0]), (dialog, which) -> {
            if (which == 0) {
                // Reset filter icon clicked, restore the original list
                adapter.filterList(loginHistoryList);
            } else {
                String selectedUserName = userNames.get(which);
                filterByUserName(selectedUserName);
            }
        });

        builder.show();
    }

}
