package com.telemedicina;



import static android.app.PendingIntent.getActivity;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity {

    private ImageSwitcher imageSwitcher;

    private int[] gallery = { R.drawable.uno, R.drawable.dos, R.drawable.tres,
            R.drawable.cuatro};

    private int position;

    private static final Integer DURATION = 20000;
    private Timer timer = null;
    Button login, register;

    int REQUEST_CODE = 200;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = new Intent(StartActivity.this, SplashScreen.class);
        startActivity(intent);

        verficarPermisos();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        login = (Button) findViewById(R.id.buttonLogin);
        register = (Button) findViewById(R.id.buttonCrearCuenta);

        register.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(StartActivity.this, CrearCuenta.class);
                        startActivity(intent);
                    }
                }
        );

        login.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(StartActivity.this, iniciar_sesion.class);
                        startActivity(intent);
                    }
                }

        );

        imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                return new ImageView(StartActivity.this);
            }
        });

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        imageSwitcher.setInAnimation(fadeIn);
        imageSwitcher.setOutAnimation(fadeOut);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                // avoid exception:
                // "Only the original thread that created a view hierarchy can touch its views"
                runOnUiThread(new Runnable() {
                    public void run() {
                        imageSwitcher.setImageResource(gallery[position]);
                        position++;
                        if (position == gallery.length) {
                            position = 0;
                        }
                    }
                });
            }

        }, 10, DURATION);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean verficarPermisos(){
        int PermisosLlamada = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG);

        if(PermisosLlamada == PackageManager.PERMISSION_GRANTED){
        }else{
            requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG}, REQUEST_CODE);
        }

        return true;
    }


}

