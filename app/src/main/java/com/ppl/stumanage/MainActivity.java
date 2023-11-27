package com.ppl.stumanage;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ppl.stumanage.UserManagement.ManageUserFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private OnBackPressedDispatcher mOnBackPressedDispatcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        // Determine whether the user is an admin
        boolean isAdmin = checkIfAdmin();

        // Get the menu and remove the "User Manage" item if not an admin
        Menu navMenu = navigationView.getMenu();
        if (!isAdmin) {
            navMenu.removeItem(R.id.nav_UserManage);
        }
        ImageView imageHeader=  headerView.findViewById(R.id.imageHeader);
        TextView tvHeaderName =  headerView.findViewById(R.id.headerName);
        TextView tvHeaderContact =  headerView.findViewById(R.id.headerContact);




        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        mOnBackPressedDispatcher = getOnBackPressedDispatcher();


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(currentUser.getUid());

            userRef.addSnapshotListener((documentSnapshot, e) -> {
                if (e != null) {

                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String userName = documentSnapshot.getString("name");
                    String userContact = documentSnapshot.getString("email");
                    String imageUrl = documentSnapshot.getString("profileImageURL");

                    // Set the name and contact in the navigation header TextViews
                    tvHeaderName.setText(userName);
                    String contact = "Contact: " + userContact;
                    tvHeaderContact.setText(contact);

                    // Load image using Glide library into the ImageView
                    Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.default_logo_user) // Placeholder image while loading
                            .error(R.drawable.default_logo_user) // Image to show if loading fails
                            .into(imageHeader);
                } else {

                }
            });
        }



    }



    private boolean checkIfAdmin() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if(!currentUser.getEmail().equals("admin@gmail.com"))
                return false;
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        }else if (itemId == R.id.nav_UserManage) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ManageUserFragment()).commit();
        } else if (itemId == R.id.nav_settings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
        } else if (itemId == R.id.nav_about) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutFragment()).commit();
        } else if (itemId == R.id.nav_logout) {
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    // Check if there are fragments in the back stack
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack();
                    } else {

                        finish();
                    }
                }
            }
        };
        mOnBackPressedDispatcher.addCallback(this, callback);
    }

}