package com.example.pablo.medddddico; //a

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;

public class VerDatosMedico extends PrincipalMedico {

    private int color;

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
                                    ActivityCompat.finishAffinity(VerDatosMedico.this);

                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }else{
            setContentView(R.layout.activity_ver_datos_medico);
            cargarDatos();
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setBackgroundColor(color);
            setSupportActionBar(toolbar);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(color+50);
            }


            EditText DNI = (EditText) findViewById(R.id.MostrarDNI);
            EditText nombr = (EditText) findViewById(R.id.MostrarNombre);
            EditText apell = (EditText) findViewById(R.id.MostrarApellidos);
            final EditText contr = (EditText) findViewById(R.id.MostrarPass);
            final Button boton = (Button) findViewById(R.id.BotonCambioContraseña);


            DNI.setText(getIntent().getStringExtra("DNI"));
            nombr.setText(getIntent().getStringExtra("Nombre"));
            apell.setText(getIntent().getStringExtra("Apellidos"));
            contr.setText(getIntent().getStringExtra("Pass"));

            boton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Cambio de contraseña
                    final AlertDialog.Builder builder = new AlertDialog.Builder(VerDatosMedico.this);
                    builder.setTitle("Cambio de contraseña");
                    builder.setMessage("Introduce tu contraseña actual");
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    final EditText input = new EditText(VerDatosMedico.this);

                    builder.setView(input);
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //Comprobar que la anterior contraseña coincide y que la nueva es valida
                            if (getIntent().getStringExtra("Pass").equals(input.getText().toString())) { //Si las contraseñas coinciden puede cambiar su contraseña


                                final AlertDialog.Builder builder2 = new AlertDialog.Builder(VerDatosMedico.this);
                                builder2.setTitle("Cambio de contraseña");
                                builder2.setMessage("Introduce tu nueva contraseña");
                                final EditText input = new EditText(VerDatosMedico.this);

                                builder2.setView(input);
                                builder2.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                                builder2.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (input.getText().toString().length() < 6) {
                                            boton.setError("La contraseña debe tener al menos 6 caracteres");
                                        } else { //Actualizo la contraseña
                                            contr.setText(input.getText().toString());
                                            Firebase.setAndroidContext(VerDatosMedico.this);


                                            //final Firebase base = new Firebase("https://dazzling-inferno-7049.firebaseio.com/");
                                            final Firebase base = new Firebase("https://incandescent-inferno-4645.firebaseio.com/");
                                            base.child("Medico").child(getIntent().getStringExtra("DNI")).child("Datos").child("Contraseña").setValue(input.getText().toString());

                                            Intent resultData = new Intent();
                                            resultData.putExtra("PassNew", input.getText().toString());
                                            setResult(Activity.RESULT_OK, resultData); //Le envio la nueva contraseña a la clase PrincipalMedico
                                        }
                                    }
                                });
                                builder2.show();
                            } else {
                                boton.setError("Contraseña incorrecta");
                            }
                        }


                    });
                    builder.show();
                }

            });
        }


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void cargarDatos(){
        SharedPreferences mispreferencias = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
        color = mispreferencias.getInt("color",0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatos();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar!=null){
            toolbar.setBackgroundColor(color);
            setSupportActionBar(toolbar);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color+50);
        }
    }
}