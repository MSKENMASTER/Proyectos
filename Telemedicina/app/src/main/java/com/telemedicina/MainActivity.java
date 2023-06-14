package com.telemedicina;


import static android.content.ContentValues.TAG;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    TextView nombrePerfil;
    static CircleImageView fotoperfil;

    int REQUEST_CODE = 200;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button cerrarSesion;
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        getFragment(menuItem);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getFragment(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                Fragment fragment = HomeFragment.newInstance("", "");
                getSupportFragmentManager()
                        .beginTransaction()
                        //.setCustomAnimations(R.anim.nav_enter, R.anim.nav_exit)
                        .replace(R.id.containerView, fragment)
                        .commit();

                setTitle(getString(R.string.menu_home));
                break;
            case R.id.nav_profile:
                Fragment fragment2 = profileFragment.newInstance("", "");
                getSupportFragmentManager()
                        .beginTransaction()
                        //.setCustomAnimations(R.anim.nav_enter, R.anim.nav_exit)
                        .replace(R.id.containerView, fragment2)
                        .commit();


                setTitle(getString(R.string.menu_profile));
                break;
            case R.id.nav_Citas:
                Fragment fragment4 = CitasFragment.newInstance("", "");
                getSupportFragmentManager()
                        .beginTransaction()
                        //.setCustomAnimations(R.anim.nav_enter, R.anim.nav_exit)
                        .replace(R.id.containerView, fragment4)
                        .commit();

                setTitle(getString(R.string.menu_Citas));
                break;
            case R.id.nav_llamadas:
                Fragment fragment5 = LlamadasFragment.newInstance("", "");
                getSupportFragmentManager()
                        .beginTransaction()
                        //.setCustomAnimations(R.anim.nav_enter, R.anim.nav_exit)
                        .replace(R.id.containerView, fragment5)
                        .commit();

                setTitle(getString(R.string.menu_llamadas));
                break;
        }
    }

    @RequiresApi (api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(findViewById(R.id.toolbar));

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        cerrarSesion = navigationView.findViewById(R.id.logout);
        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoPersonalizado();
            }
        });
        nombrePerfil = navigationView.getHeaderView(0).findViewById(R.id.header_title);
        navigationView.setNavigationItemSelectedListener(this);
        nombrePerfil.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        fotoperfil = navigationView.getHeaderView(0).findViewById(R.id.ImagenPerfil);
        Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(fotoperfil);



        MenuItem menuItem = navigationView.getMenu().getItem(0);
        onNavigationItemSelected(menuItem);
        menuItem.setChecked(true);

        ObtenerDatosLlamadas();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean verficarPermisos2(){

        int PermisosLlamada2 = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        if(PermisosLlamada2 == PackageManager.PERMISSION_GRANTED){
        }else{
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE);
        }

        return true;
    }

    private void mostrarDialogoPersonalizado() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_cerrar_sesion, null);

        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();

        Button btnSI = view.findViewById(R.id.aceptarButton);
        btnSI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, StartActivity.class);
                startActivity(intent);
            }
        });

        Button btnNO = view.findViewById(R.id.nobutton);
        btnNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }



    protected void ObtenerDatosLlamadas() {

        Uri uri;

        /*
        content://media/internal/images
        content://media/external/video
        content://media/internal/audio
        */

        //         content://media/*/images
        //         content://settings/system/ringtones

        uri = Uri.parse("content://call_log/calls");

        String[] projeccion = new String[]{CallLog.Calls.TYPE, CallLog.Calls.NUMBER, CallLog.Calls.DATE};



        Cursor c = getContentResolver().query(
                uri,
                projeccion,
                null,
                null,
                null);

        String tipo="", tlf="", fecha="";
        if(c.getCount()>0) {

            while (c.moveToNext()) {
                tipo = c.getString(0);
                tlf = c.getString(1);
                fecha = c.getString(2);
            }

            if(tipo.equals("1")) {
                tipo = "https://firebasestorage.googleapis.com/v0/b/proyecto-dam-ded5c.appspot.com/o/llamada_recibida.png?alt=media&token=99844ef3-a8d0-42d4-ad4b-93d6643b025a";
            }else if(tipo.equals("2")){
                tipo = "https://firebasestorage.googleapis.com/v0/b/proyecto-dam-ded5c.appspot.com/o/llamada_enviada.png?alt=media&token=b4002798-407a-476f-910c-1d8dc5e21a70";
            }
            final String tipofinal = tipo;
            final String tlffinal = tlf;

            Date date = new Date();
            date.setTime(Long.parseLong(fecha));
            final String fechafinal = new SimpleDateFormat().format(date);
            DocumentReference docRef = db.collection("doctors").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> doctors = new HashMap<>();
                            doctors.put("tipo", tipofinal);
                            doctors.put("tlf", tlffinal);
                            doctors.put("fecha", fechafinal.replaceAll("/", "-"));
                            db.collection("doctors").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("calls").document(fechafinal.replaceAll("/", "-")).set(doctors);
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

            DocumentReference docRef2 = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> users = new HashMap<>();
                            users.put("tipo", tipofinal);
                            users.put("tlf", tlffinal);
                            users.put("fecha", fechafinal.replaceAll("/", "-"));
                            db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("calls").document(fechafinal.replaceAll("/", "-")).set(users);
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }

}
    public static void ActualizarImagenPerfil(String uri, Context context){
        Glide.with(context).load(uri).into(fotoperfil);
    }

    }