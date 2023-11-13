package com.ppl.stumanage.UserManagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ppl.stumanage.R;

import java.util.ArrayList;
import java.util.List;

public class ViewUsersFragment extends Fragment {
    private List<SystemUser> systemUserList = new ArrayList<>();

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

        // Initialize RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        systemUserList = SystemUserGenerator.generateRandomUserList(10);

        // Create and set the adapter
        SystemUserAdapter adapter = new SystemUserAdapter(systemUserList);
        recyclerView.setAdapter(adapter);

        return view;
    }

}