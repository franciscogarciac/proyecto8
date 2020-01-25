package com.example.taxis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class Taxiv2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public NavigationView navView;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxiv2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navView = (NavigationView)findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        switch (menuItem.getItemId()) {
            case R.id.menu_actividad:
                fragment = new Registro_Actividad();

                Toast.makeText(this.getApplicationContext(),"Click Opcion 1", Toast.LENGTH_LONG).show();
                break;
            case R.id.menu_distancia:
                fragment = new Registro_Actividad();
                Toast.makeText(this.getApplicationContext(),"Click Opcion 2", Toast.LENGTH_LONG).show();
                break;
            case R.id.menu_ruta:
                fragment = new Registro_Actividad();
                Toast.makeText(this.getApplicationContext(),"Click Opcion 3", Toast.LENGTH_LONG).show();
                break;

        }
        if(fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
            menuItem.setChecked(true);
            getSupportActionBar().setTitle(menuItem.getTitle());
        }

        getSupportActionBar().setTitle(menuItem.getTitle());
        drawerLayout.closeDrawers();
        return false;
    }
}
