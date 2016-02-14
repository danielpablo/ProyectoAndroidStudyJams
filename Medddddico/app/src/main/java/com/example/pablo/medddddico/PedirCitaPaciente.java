package com.example.pablo.medddddico; //a

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

public class PedirCitaPaciente extends PrincipalPaciente {

    public static TextView textV;
    public static String fecha=new String();
    public static String hora=new String();
    private String doc = new String();
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
                                    ActivityCompat.finishAffinity(PedirCitaPaciente.this);
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }else{
            final ObtenerJson obtenerJson = new ObtenerJson();
            obtenerJson.execute();
            Firebase.setAndroidContext(this);
            //final Firebase base = new Firebase("https://dazzling-inferno-7049.firebaseio.com/");
            final Firebase base = new Firebase("https://incandescent-inferno-4645.firebaseio.com/");

            setContentView(R.layout.activity_pedir_cita_paciente);
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
            TextView textViu = (TextView) findViewById(R.id.textViewConcertarCita);
            textV=textViu;

            final Button botonConcertarCita = (Button) findViewById(R.id.buttonConcertarCita);
            Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);

            TimePickerDialog Tdialog = new TimePickerDialog(PedirCitaPaciente.this,
                    new mTimeSetListener(), mHour, mMinute, true);
            Tdialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            Tdialog.show();
            textViu.setText(textV.getText());
            DatePickerDialog dialog = new DatePickerDialog(PedirCitaPaciente.this,
                    new mDateSetListener(), mYear, mMonth, mDay);
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dialog.show();

            botonConcertarCita.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    while(obtenerJson.getResult()==null);
                    if(comprobarFecha(obtenerJson.getResult())){
                        registroCita registroUsuario = new registroCita();
                        registroUsuario.execute(base, getIntent().getStringExtra("DNI"));   //Registrar cita
                        finish();
                    }else{
                        botonConcertarCita.setError("No se pudo concertar cita. Intente otra hora/día");
                        textV.setError("No se pudo concertar cita. Intente otra hora/día");
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

    public boolean comprobarFecha(String resultado){
        int contador=0;
        Calendar prev=null;
        Calendar next=null;
        Calendar analizar=null;
        Calendar esta=null;
        final long MILLSECS_PER_DAY = 24*60*60*1000;
        int ano,mes,dia,hora,minuto;
        //Date prev=null;
        Calendar hoy = Calendar.getInstance();
        Calendar DataCita = new GregorianCalendar(PrincipalPaciente.anho,PrincipalPaciente.mes-1,PrincipalPaciente.dia,PrincipalPaciente.hora,PrincipalPaciente.minuto);
        if(hoy.getTimeInMillis()<DataCita.getTimeInMillis()) {      //No se puede pedir cita para un día del pasado
            try {
                JSONObject object = new JSONObject(resultado);
                JSONObject a = object.getJSONObject("Medico");
                doc = object.getJSONObject("Paciente").getJSONObject(getIntent().getStringExtra("DNI")).getJSONObject("Datos").getString("Doctor");

                JSONObject b = a.getJSONObject(doc);
                JSONObject datos = b.getJSONObject("Pacientes");
                Iterator<String> claves = datos.keys();
                int conta = 0;
                if(datos.length()==1) return true;

                while (claves.hasNext()) {
                    String clave = claves.next();
                    if(!clave.equals("")&&!clave.equals(" ")) {
                        JSONObject citas = datos.getJSONObject(clave);
                        Iterator<String> cits = citas.keys();
                        if (citas.length() != 1) {
                            while (cits.hasNext()) {
                                String clav = cits.next();
                                if (!clav.equals("") && !(clav.equals(" "))) {
                                    String[] aa = clav.split("-");
                                    dia = Integer.parseInt(aa[0]);
                                    mes = Integer.parseInt(aa[1]);
                                    //ano = Integer.parseInt(aa[4]) * 1000 + Integer.parseInt(aa[5]) * 100 + Integer.parseInt(aa[6]) * 10 + Integer.parseInt(aa[7]);
                                    String[] bb = clav.split("|");
                                    for (int i = 0; i < aa.length; i++) {
                                    }
                                    ano = Integer.parseInt(bb[clav.indexOf('|')]) + Integer.parseInt(bb[clav.indexOf('|') - 1]) * 10 + Integer.parseInt(bb[clav.indexOf('|') - 2]) * 100 + Integer.parseInt(bb[clav.indexOf('|') - 3]) * 1000;

                                    for (int i = 0; i < bb.length; i++) {
                                    }
                                    String[] cc = clav.split(":");
                                    for (int i = 0; i < cc.length; i++) {
                                    }
                                    minuto = Integer.parseInt(cc[1]);

                                    char c;
                                    int i = 0;
                                    hora = 0;
                                    while ((c = clav.charAt(cc[0].length() - 1 - i)) != '|') {
                                        hora += Integer.parseInt(Character.toString(c)) * (Math.pow(10, i));

                                        i++;
                                    }
                                    if (conta == 0) {
                                        prev = new GregorianCalendar(0, 0, 0, 0, 0);
                                        next = new GregorianCalendar(ano, mes - 1, dia, hora, minuto);
                                    }
                                    analizar = new GregorianCalendar(ano, mes - 1, dia, hora, minuto);
                                    if (analizar.getTimeInMillis() == DataCita.getTimeInMillis()) {
                                        return false;
                                    }
                                    conta++;
                                }
                            }
                        }
                    }
                }
                return true;
            } catch (JSONException e) {
                return false;
            } /*catch (ParseException e) {
            e.printStackTrace();
        }*/
        }else{
            return false;
        }
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
                return null;
            }catch (IOException e){
                return null;
            }
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

    public class registroCita extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... params) {

                ((Firebase) params[0]).child("Paciente").child((String) params[1]).child("Citas").child(fecha).setValue(hora);

            try{
                JSONObject object = new JSONObject(getIntent().getStringExtra("Resultado"));
                JSONObject medicos = object.getJSONObject("Medico");
                Iterator<String> claves = medicos.keys();

                while (claves.hasNext()) {    //Iteramos sobre los medicos
                    String clave = claves.next();
                    JSONObject pacientes = medicos.getJSONObject(clave).getJSONObject("Pacientes");
                    Iterator<String> claves2 = pacientes.keys();
                    while (claves2.hasNext()) {       //Iteramos sobre los pacientes de cada medico
                        String cl = claves2.next();
                        if (cl.equals((String) params[1])) {
                            ((Firebase) params[0]).child("Medico").child(clave).child("Pacientes").child((String) params[1]).child(fecha).setValue(" ");
                        }
                    }
                }

                ((Firebase)params[0]).child("Medico").child(doc).child("Pacientes").child((String) params[1]).child(fecha).setValue(" ");



                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
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
