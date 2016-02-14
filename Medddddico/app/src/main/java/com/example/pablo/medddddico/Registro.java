package com.example.pablo.medddddico; //A

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
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Iterator;

/**
 * Created by pablo on 26/01/16.
 */
public class Registro extends Inicio {

    private String nombreMedico;
    private String DNIMedico;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if(!isNetworkAvailable()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Conexión a Internet no disponible")
                    .setTitle("Advertencia")
                    .setCancelable(false)

                    .setPositiveButton("Salir de la aplicación",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ActivityCompat.finishAffinity(Registro.this);
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }else{
            setContentView(R.layout.activity_registro);

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

            Firebase.setAndroidContext(this);
            //final Firebase base = new Firebase("https://dazzling-inferno-7049.firebaseio.com/");
            final Firebase base = new Firebase("https://incandescent-inferno-4645.firebaseio.com/");

            final CheckBox check = (CheckBox) findViewById(R.id.checkBoxRegistro);


            Button boton = (Button) findViewById(R.id.BotonRegistro);

            final Button botonAux = boton;


            boton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText nombre = (EditText) findViewById(R.id.nombreRegistro);
                    EditText apellidos = (EditText) findViewById(R.id.apellidosRegistro);
                    EditText dni = (EditText) findViewById(R.id.DNIRegistro);
                    EditText pass = (EditText) findViewById(R.id.passRegistro);

                    boolean comprobacion = true;
                    if(nombre.getText().toString().length() == 0){
                        comprobacion = false;
                        nombre.setError("Este campo no puede estar vacío");
                    }

                    if(apellidos.getText().length() == 0){
                        comprobacion = false;
                        apellidos.setError("Este campo no puede estar vacío");
                    }

                    if(dni.getText().toString().length() != 9){
                        comprobacion = false;
                        dni.setError("Formato de DNI inválido");
                    }else{
                        String[] aux = dni.getText().toString().split("");
                        for (int i = 1; i < aux.length - 1; i++){ //Compruebo que los primeros 8 caracteres sean numeros y el ultimo una letra mayuscula
                            if(!Character.isDigit(aux[i].charAt(0))){
                                comprobacion = false;
                                dni.setError("Formato de DNI inválido");
                            }
                        }
                        if(!Character.isUpperCase(aux[9].charAt(0))){
                            comprobacion = false;
                            dni.setError("Formato de DNI inválido");
                        }
                    }

                    if(pass.getText().toString().length() < 6){
                        comprobacion = false;
                        pass.setError("La contraseña debe tener mínimo 6 caracteres");
                    }

                    if(comprobacion) {

                        String opcion;
                        if (check.isChecked()) {
                            opcion = "Medico";
                        } else {
                            opcion = "Paciente";
                        }
                /*INTRODUCIR DATOS EN BD*/
                        boolean var = true;
                        try {
                            if (!comprobarSiExiste(getIntent().getStringExtra("Resultado"), dni.getText().toString(), opcion)) {
                                registroUsuario reg = new registroUsuario();
                                reg.execute(base, opcion, dni.getText().toString(), pass.getText().toString(), nombre.getText().toString(), apellidos.getText().toString(), getIntent().getStringExtra("Resultado"));


                                if (opcion.equals("Medico")) {
                                    Intent intent = new Intent(Registro.this, PrincipalMedico.class);
                                    intent.putExtra("DNI", dni.getText().toString());
                                    intent.putExtra("Pass", pass.getText().toString());
                                    intent.putExtra("Nombre", nombre.getText().toString());
                                    intent.putExtra("Apellidos", apellidos.getText().toString());

                                    while (reg.getResult() == null);
                                    intent.putExtra("Resultado", reg.getResult());
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(Registro.this, PrincipalPaciente.class);
                                    intent.putExtra("Pass", pass.getText().toString());
                                    intent.putExtra("Nombre", nombre.getText().toString());
                                    intent.putExtra("Apellidos", apellidos.getText().toString());
                                    intent.putExtra("DNI", dni.getText().toString());

                                    while (reg.getResult() == null) ;
                                    intent.putExtra("NombreMedico", nombreMedico);
                                    intent.putExtra("DNIMedico",DNIMedico);
                                    intent.putExtra("Resultado", reg.getResult());   //Esto non estaba
                                    startActivity(intent);
                                }
                                var = false;
                                finish(); //Acabamos la actividad
                            } else {
                                botonAux.setError("Error: DNI ya existe");
                                dni.setError("Error: DNI ya existe");
                                pass.setText("");
                            }
                        } catch (JSONException e) {
                        }
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

    public boolean comprobarSiExiste(String resultado, String dni, String opcion) throws JSONException {
        try{
            JSONObject object = new JSONObject(resultado);
            JSONObject medico = object.getJSONObject(opcion);
            Iterator<String> claves = medico.keys();
            while(claves.hasNext()){
                String clave = claves.next();
                if(clave.equals(dni)){
                    return true;
                }
            }
            return false;

        }catch (JSONException e){
            return false;
        }
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            final Context a = this;
            finish();
        }
        return super.onKeyDown(keyCode, event);

    }


    public class registroUsuario extends AsyncTask<Object, Void, Void> {

        private String result;

        @Override
        protected Void doInBackground(Object... params) {


            ((Firebase) params[0]).child((String) params[1]).child((String) params[2]).child("Datos").child("Contraseña").setValue(params[3]);
            ((Firebase) params[0]).child((String) params[1]).child((String) params[2]).child("Datos").child("Nombre").setValue(params[4]);
            ((Firebase) params[0]).child((String) params[1]).child((String) params[2]).child("Datos").child("Apellidos").setValue(params[5]);

            if(params[1].equals("Paciente")){
                try{
                    JSONObject object = new JSONObject((String)params[6]);
                    JSONObject medicos = object.getJSONObject("Medico");
                    Iterator<String> claves = medicos.keys();
                    int min=0;
                    int i=0;
                    String dni=null;
                    while(claves.hasNext()){
                        String clave = claves.next();
                        JSONObject pacientes = medicos.getJSONObject(clave).getJSONObject("Pacientes");
                        if(i==0){ min=pacientes.length(); dni = clave;}        //Se inicializa el valor min al del primer medico
                        i++;
                        if(pacientes.length() < min){
                            dni = clave;
                        }
                    }

                    ((Firebase) params[0]).child("Medico").child(dni).child("Pacientes").child((String)params[2]).child(" ").setValue(" ");
                    nombreMedico = medicos.getJSONObject(dni).getJSONObject("Datos").get("Nombre") +" "+medicos.getJSONObject(dni).getJSONObject("Datos").get("Apellidos");
                    DNIMedico = dni;
                    ((Firebase) params[0]).child((String) params[1]).child((String) params[2]).child("Datos").child("Doctor").setValue(dni);

                    ((Firebase) params[0]).child((String) params[1]).child((String) params[2]).child("Citas").child(" ").setValue(" ");



                }catch (JSONException e){
                }
            }else{
                ((Firebase) params[0]).child("Medico").child((String) params[2]).child("Pacientes").child(" ").setValue(" ");

            }

            //Se actualiza la variable resultado = json de la firebase

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
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "/n");
                }


                String resultado = sb.toString();
                if (resultado != null) {
                    setResult(resultado);
                }

                return null;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        public String getResult() {
            return result;
        }

        protected void setResult(String a) {
            if (a != null) {
                result = a;
            }
        }

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

    public void cargarDatos(){
        SharedPreferences mispreferencias = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
        color = mispreferencias.getInt("color",0);
    }

}
