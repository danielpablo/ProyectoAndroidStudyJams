package com.example.pablo.medddddico; //a

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Inicio extends AppCompatActivity {
    private static String nombre;
    private static String apellidos;
    private static String nombreMedico;
    protected int color;
    private static String DNIusuario;     //Para guardar
    private static String passusuario;     //Para guardar

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
                                    finish();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }else{

            final ObtenerJson cargar = new ObtenerJson();
            final Object resultado = cargar.execute();

            setContentView(R.layout.activity_inicio);
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

            cargarDatos();
            if(nombre==null){
                finish();
            }


            if(!nombre.equals("") && !apellidos.equals("") && !passusuario.equals("") && !DNIusuario.equals("")){
                if(nombreMedico.equals("")){     //Es un medico
                    Intent intent = new Intent(Inicio.this, PrincipalMedico.class);
                    intent.putExtra("DNI", DNIusuario);
                    intent.putExtra("Pass", passusuario);
                    intent.putExtra("Nombre", nombre);
                    intent.putExtra("Apellidos", apellidos);
                    while(cargar.getResult()==null);
                    intent.putExtra("Resultado", cargar.getResult());
                    startActivity(intent);
                }else{      //Es un paciente
                    Intent intent = new Intent(Inicio.this, PrincipalPaciente.class);
                    intent.putExtra("DNI", DNIusuario);
                    intent.putExtra("Pass", passusuario);
                    intent.putExtra("Nombre", nombre);
                    intent.putExtra("Apellidos", apellidos);
                    intent.putExtra("NombreMedico", nombreMedico);
                    while (cargar.getResult()==null);
                    intent.putExtra("Resultado", cargar.getResult());
                    startActivity(intent);
                }
                finish();
            }

            final Context a = this;

            Button botton1 = (Button) findViewById(R.id.button1);
            Button botton2 = (Button) findViewById(R.id.button2);


            botton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    while (cargar.getResult() == null) {
                    }
                    Intent intent = new Intent(a, Registro.class);
                    intent.putExtra("Resultado", cargar.getResult());
                    startActivity(intent);

                }
            });

            final Button botonAux = botton2;
            botton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    EditText dni = ((EditText) findViewById(R.id.dni));
                    EditText contrasena = ((EditText) findViewById(R.id.pass));

                    CheckBox check = (CheckBox) findViewById(R.id.checkMedico);

                    String opcion = "Paciente";
                    if(check.isChecked()){ //Si esta checked es un medico, si no un cliente
                        opcion = "Medico";
                    }

                    while(cargar.getResult() == null){
                    }
                    try {
                        if(comprobarSiExiste(cargar.getResult(), dni.getText().toString(), contrasena.getText().toString(), opcion)){
                            Toast.makeText(Inicio.this, "Login correcto", Toast.LENGTH_SHORT).show();
                            if(opcion.equals("Medico")){

                                SharedPreferences mispreferencias = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = mispreferencias.edit();
                                editor.putString("dni", dni.getText().toString());
                                editor.putString("pass",contrasena.getText().toString());
                                editor.putString("nombre",nombre);
                                editor.putString("apellidos",apellidos);
                                editor.putString("nombreMedico", "");
                                editor.commit();

                                Intent intent = new Intent(Inicio.this, PrincipalMedico.class);
                                intent.putExtra("DNI", dni.getText().toString());
                                intent.putExtra("Pass", contrasena.getText().toString());
                                intent.putExtra("Nombre", nombre);
                                intent.putExtra("Apellidos", apellidos);
                                editor.commit();

                                intent.putExtra("Resultado", cargar.getResult());
                                startActivity(intent);
                            }else{

                                SharedPreferences mispreferencias = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = mispreferencias.edit();
                                editor.putString("dni", dni.getText().toString());
                                editor.putString("pass",contrasena.getText().toString());
                                editor.putString("nombre",nombre);
                                editor.putString("apellidos",apellidos);
                                editor.putString("nombreMedico", nombreMedico);
                                editor.commit();

                                Intent intent = new Intent(Inicio.this, PrincipalPaciente.class);
                                intent.putExtra("DNI", dni.getText().toString());
                                intent.putExtra("Pass", contrasena.getText().toString());
                                intent.putExtra("Nombre", nombre);
                                intent.putExtra("Apellidos", apellidos);
                                intent.putExtra("NombreMedico", nombreMedico);

                                intent.putExtra("Resultado", cargar.getResult());
                                startActivity(intent);
                            }

                            finish();
                        }else{
                            botonAux.setError("Error: Login Incorrecto");
                            contrasena.setText("");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

    public boolean comprobarSiExiste(String resultado, String dni, String pass, String opcion) throws JSONException {
        try{
            JSONObject object = new JSONObject(resultado);
            JSONObject datos = object.getJSONObject(opcion).getJSONObject(dni).getJSONObject("Datos");

            if(datos.get("Contraseña").equals(pass)){
                nombre = datos.getString("Nombre");
                apellidos = datos.getString("Apellidos"); //Cargo los datos que faltan para pasarlos a posteriores actividades

                if(opcion.equals("Paciente")){
                    nombreMedico = datos.getString("Doctor");
                }

                return true; //Si la clave coincide con alguna retornamos true
            }
            return false;

        }catch (JSONException e){
            return false;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    public class ObtenerJson extends AsyncTask<String, Void, Void> {

        String result= null;
        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {

                //URL url = new URL("https://dazzling-inferno-7049.firebaseio.com/.json");
                URL url = new URL("https://incandescent-inferno-4645.firebaseio.com/.json");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null){
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "/n");
                }

                String resultado = sb.toString();
                if(resultado != null){
                    setResult(resultado);
                }

                inputStream.close();
                reader.close();
                urlConnection.disconnect();
                return null;

            }catch (IOException e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }

        public String getResult(){
            return result;
        }
        protected void setResult(String a){
            if(a != null){
                result = a;
            }
        }

    }

    public void cargarDatos(){
        SharedPreferences mispreferencias = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
        color = mispreferencias.getInt("color", 0);
        DNIusuario = mispreferencias.getString("dni","");
        passusuario = mispreferencias.getString("pass","");
        nombre = mispreferencias.getString("nombre","");
        apellidos = mispreferencias.getString("apellidos","");
        nombreMedico = mispreferencias.getString("nombreMedico","");

    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatos();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarPaciente);
        if(toolbar!=null) {
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


