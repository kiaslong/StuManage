package com.ppl.stumanage.UserManagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ppl.stumanage.R;

import java.util.ArrayList;
import java.util.List;

public class SystemUserAdapter extends RecyclerView.Adapter<SystemUserAdapter.ViewHolder> {

    private List<SystemUser> systemUserList = new ArrayList<>();

    // Constructor to initialize the adapter with a list of SystemUser objects
    public SystemUserAdapter(List<SystemUser> systemUserList) {
        this.systemUserList = systemUserList;
    }

    // ViewHolder class to hold references to the views for each item in the RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView userNameTextView;
        private TextView userAgeTextView;
        private TextView userPhoneNumberTextView;
        private TextView userStatusTextView;
        private TextView userEmailTextView;
        private TextView userRoleTextView; // Added TextView for user role

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.textViewUserName);
            userAgeTextView = itemView.findViewById(R.id.textViewUserAge);
            userEmailTextView = itemView.findViewById(R.id.textViewUserEmail);
            userPhoneNumberTextView = itemView.findViewById(R.id.textViewUserPhoneNumber);
            userStatusTextView = itemView.findViewById(R.id.textViewUserStatus);
            userRoleTextView = itemView.findViewById(R.id.textViewUserRole); // Initializing user role TextView
        }
    }


    // onCreateViewHolder: Inflate the item layout and create a ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_system_user, parent, false);
        return new ViewHolder(view);
    }

    // onBindViewHolder: Set the data for a certain position
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SystemUser systemUser = systemUserList.get(position);

        // Set data to the views in the ViewHolder
        holder.userNameTextView.setText(systemUser.getName());
        holder.userEmailTextView.setText(systemUser.getEmail());
        holder.userAgeTextView.setText(String.valueOf(systemUser.getAge()));
        holder.userPhoneNumberTextView.setText(systemUser.getPhoneNumber());
        holder.userRoleTextView.setText(systemUser.getRole());
        holder.userStatusTextView.setText((systemUser.getStatus()));

    }

    // getItemCount: Return the total number of items in the data set
    @Override
    public int getItemCount() {
        return systemUserList.size();
    }
}
