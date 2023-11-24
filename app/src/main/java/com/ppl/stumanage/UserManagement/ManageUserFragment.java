package com.ppl.stumanage.UserManagement;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.ppl.stumanage.R;

public class ManageUserFragment extends Fragment {

    private ObjectAnimator scaleUpX;
    private ObjectAnimator scaleUpY;
    private ObjectAnimator scaleDownX;
    private ObjectAnimator scaleDownY;

    public ManageUserFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_user, container, false);

        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Manage Users");
        }

        final TextView tvViewUsers = view.findViewById(R.id.tvViewUsers);
        final TextView tvAddUser = view.findViewById(R.id.tvAddUser);
        final TextView tvViewLoginHistory = view.findViewById(R.id.tvViewLoginHistory);

        // Set up ObjectAnimators for scaling
        scaleUpX = ObjectAnimator.ofFloat(null, "scaleX", 1f, 1.1f);
        scaleUpY = ObjectAnimator.ofFloat(null, "scaleY", 1f, 1.1f);

        scaleDownX = ObjectAnimator.ofFloat(null, "scaleX", 1.1f, 1f);
        scaleDownY = ObjectAnimator.ofFloat(null, "scaleY", 1.1f, 1f);

        // Set the duration for the scaling animation
        scaleUpX.setDuration(100);
        scaleUpY.setDuration(100);
        scaleDownX.setDuration(100);
        scaleDownY.setDuration(100);

        // Set up touch listeners for the TextViews
        setHoverEffect(tvViewUsers);
        setHoverEffect(tvAddUser);
        setHoverEffect(tvViewLoginHistory);

        return view;
    }

    private void setHoverEffect(final TextView textView) {
        final int originalTextColor = textView.getCurrentTextColor();

        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Scale up and change alpha and text color on touch down
                        scaleUpX.setTarget(textView);
                        scaleUpY.setTarget(textView);
                        scaleUpX.start();
                        scaleUpY.start();
                        textView.setAlpha(0.8f); // Adjust alpha as needed
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // Scale down and revert alpha and text color on touch up or cancel
                        scaleDownX.setTarget(textView);
                        scaleDownY.setTarget(textView);
                        scaleDownX.start();
                        scaleDownY.start();
                        textView.setAlpha(1f);
                        textView.setTextColor(originalTextColor);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Now you can set up the click listener
        TextView tvViewUsers = view.findViewById(R.id.tvViewUsers);
        TextView tvAddUsers = view.findViewById(R.id.tvAddUser);
        TextView tvViewLogin =view.findViewById(R.id.tvViewLoginHistory);
        tvViewUsers.setOnClickListener(v -> {
            // Replace the current fragment with ViewUsersFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ViewUsersFragment())
                    .addToBackStack(null)
                    .commit();
        });
        tvAddUsers.setOnClickListener(v -> {
            // Replace the current fragment with ViewUsersFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AddUserFragment())
                    .addToBackStack(null)
                    .commit();
        });
        tvViewLogin.setOnClickListener(v->{
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LoginHistoryFragment())
                    .addToBackStack(null)
                    .commit();
        });


    }

}