package com.bbva.hackaton.activity;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.bbva.hackaton.R;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bbva.hackaton.activity.ui.gallery.GalleryFragment;
import com.bbva.hackaton.activity.ui.home.HomeFragment;
import com.bbva.hackaton.activity.ui.visorQR.VisorQRFragment;
import com.bbva.hackaton.fragment.UbicacionFragment;
import com.google.android.gms.dynamic.SupportFragmentWrapper;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class MenuActivity extends AppCompatActivity {

    AppBarConfiguration mAppBarConfiguration;
    ImageView btnChangedView;
    FrameLayout frameLayout;
    View itemAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageView btnCambiarVista = findViewById(R.id.btn_changed_view);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        EditText buscarLugar = findViewById(R.id.id_buscar_lugar);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                 R.id.nav_home,R.id.nav_gallery)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.nav_home:
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.nav_host_fragment, new HomeFragment());
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.RED));
                        btnCambiarVista.setVisibility(View.VISIBLE);
                        buscarLugar.setVisibility(View.VISIBLE);
                        toolbar.getBackground().setAlpha(0);
                        drawer.closeDrawers();
                        ft.commit();

                        break;
                    case R.id.nav_gallery:
                        Intent i = new Intent(MenuActivity.this, VisorQRFragment.class);
                        startActivity(i);
                        break;
                }
                return false;
            }
        });


        btnCambiarVista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.nav_host_fragment, new GalleryFragment());
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                btnCambiarVista.setVisibility(View.INVISIBLE);
                buscarLugar.setVisibility(View.INVISIBLE);

            }
        });

    }

    private void onChangedView(){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }



    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}
