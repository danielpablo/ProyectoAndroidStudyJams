package com.example.pablo.medddddico;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import yuku.ambilwarna.AmbilWarnaDialog;

public class Settings extends AppCompatActivity {

    private ListView list;
    private String[] opciones = {"Color"};
    private int colore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!isNetworkAvailable()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Conexión a Internet no disponible")
                    .setTitle("Advertencia")
                    .setCancelable(false)

                    .setPositiveButton("Salir de la aplicación",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ActivityCompat.finishAffinity(Settings.this);
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }else{
            setContentView(R.layout.activity_settings);
            cargarDatos();
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setBackgroundColor(colore);
            setSupportActionBar(toolbar);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(colore+50);
            }

            list = (ListView)findViewById(R.id.listViewSettings);
            ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,opciones);
            list.setAdapter(adaptador);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (opciones[position]) {
                        case "Color":
                            AmbilWarnaDialog dialog = new AmbilWarnaDialog(Settings.this, toolbar.getSolidColor(), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                                @Override
                                public void onCancel(AmbilWarnaDialog dialog) {

                                }

                                @Override
                                public void onOk(AmbilWarnaDialog dialog, int color) {
                                    colore = color;
                                    toolbar.setBackgroundColor(colore);
                                    guardarPreferencias();
                                }
                            });
                            dialog.show();
                            break;
                    }
                }
            });

            guardarPreferencias();
            cargarDatos();
        }


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void guardarPreferencias(){
        SharedPreferences mispreferencias = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mispreferencias.edit();
        editor.putInt("color", colore);
        editor.commit();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(colore+50);
        }
    }

    public void cargarDatos(){
        SharedPreferences mispreferencias = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
        colore = mispreferencias.getInt("color",0);
    }
}
