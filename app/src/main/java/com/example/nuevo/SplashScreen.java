package com.example.nuevo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.WindowManager;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSION = 123;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Comprueba si los permisos ya están otorgados
        if (checkStoragePermission()) {
            nextActivity();
        }
    }

    private void nextActivity() {
        startActivities(new Intent[]{new Intent(SplashScreen.this, MainActivity.class)});
        finish();
    }

    private boolean checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Los permisos no están otorgados, solicítalos
            ActivityCompat.requestPermissions(SplashScreen.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
            return false;
        } else {
            // Los permisos ya están otorgados
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso otorgado
                Toast.makeText(SplashScreen.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                nextActivity();
            } else {
                // Permiso denegado
                Toast.makeText(SplashScreen.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                // Aquí podrías mostrar un mensaje informativo o proporcionar instrucciones sobre cómo otorgar permisos manualmente desde la configuración de la aplicación
            }
        }
    }
}
