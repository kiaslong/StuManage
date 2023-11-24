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

public class LoginHistoryAdapter extends RecyclerView.Adapter<LoginHistoryAdapter.ViewHolder> {

    private List<LoginHistoryModel> loginHistoryList;


    public LoginHistoryAdapter(List<LoginHistoryModel> loginHistoryList) {
        this.loginHistoryList = loginHistoryList;
    }
    public void filterList(List<LoginHistoryModel> filteredList) {
        this.loginHistoryList = new ArrayList<>(filteredList);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.login_history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LoginHistoryModel loginHistory = loginHistoryList.get(position);

        holder.usernameTextView.setText(loginHistory.getUsername());
        holder.emailTextView.setText(loginHistory.getEmail());
        holder.loginTimeTextView.setText(loginHistory.getLoginTime());
    }

    @Override
    public int getItemCount() {
        return loginHistoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView emailTextView;
        TextView loginTimeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.text_view_username);
            emailTextView = itemView.findViewById(R.id.text_view_email);
            loginTimeTextView = itemView.findViewById(R.id.text_view_login_time);
        }
    }
}
