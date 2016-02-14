package com.example.pablo.medddddico;

import android.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class BorrarCitaMedicoXPaciente extends BorrarCitaMedico {

    private ListView list;
    private ArrayList<String> citasArrayList = new ArrayList<>();
    private ArrayList<String> citass = new ArrayList<>();
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
                                    ActivityCompat.finishAffinity(BorrarCitaMedicoXPaciente.this);
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }else{
            final ObtenerJson cargar = new ObtenerJson();
            cargar.execute();

            setContentView(R.layout.activity_borrar_cita_medico_xpaciente);
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


            while(cargar.getResult()==null);
            inicializarCitas(cargar.getResult(), getIntent().getStringExtra("DNIMedico"));
            list = (ListView)findViewById(R.id.listViewBorrarCitaMedicoXPaciente);
            String[] citas = new String[citasArrayList.size()];
            citas = citasArrayList.toArray(citas);
            ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,citas);
            list.setAdapter(adaptador);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BorrarCitaMedicoXPaciente.this);
                    builder.setMessage("Seguro que quiere eliminar esa cita?")
                            .setTitle("Advertencia")
                            .setCancelable(false)
                            .setNegativeButton("Cancelar",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    })
                            .setPositiveButton("Continuar",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            BorrarC borrar = new BorrarC();
                                            borrar.execute(position, cargar.getResult());
                                            finish();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
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

    public void inicializarCitas(String resultado, String dni){
        int i=0;
        citasArrayList = new ArrayList<>();
        try{
            JSONObject object = new JSONObject(resultado);
            JSONObject a = object.getJSONObject("Medico");
            JSONObject b = a.getJSONObject(getIntent().getStringExtra("DNIMedico"));
            JSONObject c = b.getJSONObject("Pacientes");
            JSONObject datos = c.getJSONObject(getIntent().getStringExtra("DNIPaciente"));
            //JSONObject datos = object.getJSONObject("Medico").getJSONObject(dni).getJSONObject("Pacientes").getJSONObject(getIntent().getStringExtra("DNIPaciente"));
            Iterator<String> claves = datos.keys();
            while(claves.hasNext()){
                String clave = claves.next();
                if(!clave.equals(" ")) {
                    citasArrayList.add("Fecha | Hora: "+clave);
                    citass.add(clave);
                }
            }
        }catch (JSONException e){
        }
    }

    public class BorrarC extends AsyncTask<Object, Void, Void> {

        private ArrayList<String> citasAntiguas = new ArrayList<>();

        @Override
        protected Void doInBackground(Object... params) {

            //Firebase ref = new Firebase("https://dazzling-inferno-7049.firebaseio.com");
            Firebase.setAndroidContext(BorrarCitaMedicoXPaciente.this);
            Firebase base = new Firebase("https://incandescent-inferno-4645.firebaseio.com");


            JSONObject datos=null;
            try {
                JSONObject object = new JSONObject((String) params[1]);
                datos = object.getJSONObject("Paciente").getJSONObject(getIntent().getStringExtra("DNIPaciente")).getJSONObject("Citas");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String citaEliminar = citass.get(((int) params[0]));
            base.child("Paciente").child(getIntent().getStringExtra("DNIPaciente")).child("Citas").child(citaEliminar).removeValue();

            if(citass.size()>0){
                base.child("Medico").child(getIntent().getStringExtra("DNIMedico")).child("Pacientes").child(getIntent().getStringExtra("DNIPaciente")).child(citaEliminar).removeValue();
            }


            finish();


            return null;
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
