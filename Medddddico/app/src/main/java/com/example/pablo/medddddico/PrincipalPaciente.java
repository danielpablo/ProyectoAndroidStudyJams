package com.example.pablo.medddddico; //a

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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PrincipalPaciente extends AppCompatActivity {

    private int color=0;

    private String dni;
    private String nombre;
    private String apellidos;
    private String pass;
    private String nombreMedico;
    private String DNIMedico;

    private ListView list;
    private String[] opciones = {"Pedir Cita","Borrar Cita","Ver Citas", "Ver Datos Personales"};
    public static int anho;
    public static int mes;
    public static int dia;
    public static int hora;
    public static int minuto;

    public void setAnho(int ano){anho=ano; }
    public void setMes(int mes){ this.mes = mes; }
    public void setDia(int dia){ this.dia = dia; }


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
                                    ActivityCompat.finishAffinity(PrincipalPaciente.this);
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }else{
            setContentView(R.layout.activity_principal_paciente);
            cargarDatos();
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarPaciente);
            toolbar.setBackgroundColor(color);
            setSupportActionBar(toolbar);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(color+50);
            }

            setDNI(getIntent().getStringExtra("DNI"));
            setNombre(getIntent().getStringExtra("Nombre"));
            setApellidos(getIntent().getStringExtra("Apellidos"));
            setPass(getIntent().getStringExtra("Pass"));
            setNombreMedico(getIntent().getStringExtra("NombreMedico"));
            setDNIMedico(getIntent().getStringExtra("DNIMedico"));



            list = (ListView)findViewById(R.id.listViewPacientes);
            ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,opciones);
            list.setAdapter(adaptador);



            final String resultado = getIntent().getStringExtra("Resultado");

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (opciones[position]){
                        case "Pedir Cita":
                            Intent intentCrearCita = new Intent(PrincipalPaciente.this, PedirCitaPaciente.class);
                            intentCrearCita.putExtra("Resultado",resultado);
                            intentCrearCita.putExtra("DNI",dni);
                            intentCrearCita.putExtra("DNIMedico",DNIMedico);
                            startActivity(intentCrearCita);
                            //startActivityForResult(intentCrearCita,3);
                            break;
                        case "Borrar Cita":
                            Intent intentBorrarCitas = new Intent(PrincipalPaciente.this, BorrarCitaPaciente.class);
                            intentBorrarCitas.putExtra("DNI", dni);
                            intentBorrarCitas.putExtra("Resultado",resultado);
                            startActivity(intentBorrarCitas);
                            break;
                        case "Ver Citas":
                            Intent intentVerCitas = new Intent(PrincipalPaciente.this, VerCitasPaciente.class);
                            intentVerCitas.putExtra("DNI",dni);
                            startActivity(intentVerCitas);
                            break;
                        case "Ver Datos Personales":
                            Intent intentVerDatos = new Intent(PrincipalPaciente.this, VerDatosPaciente.class);
                            intentVerDatos.putExtra("DNI", dni);
                            intentVerDatos.putExtra("Apellidos", apellidos);
                            intentVerDatos.putExtra("Nombre", nombre);
                            intentVerDatos.putExtra("Pass", pass);
                            if(DNIMedico==null){
                                intentVerDatos.putExtra("DNIMedico",nombreMedico);
                            }else {
                                intentVerDatos.putExtra("DNIMedico", DNIMedico);
                            }
                            intentVerDatos.putExtra("Resultado",resultado);
                            startActivityForResult(intentVerDatos, 0);
                            break;
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

    public void cargarDatos(){
        SharedPreferences mispreferencias = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
        color = mispreferencias.getInt("color",0);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_paciente, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.cerrarSesion) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿Desea cerrar sesión?")
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
                                    SharedPreferences mispreferencias = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = mispreferencias.edit();
                                    editor.putString("dni", "");
                                    editor.putString("pass","");
                                    editor.putString("nombre","");
                                    editor.putString("apellidos", "");
                                    editor.putString("nombreMedico", "");
                                    editor.commit();
                                    Intent intent = new Intent(PrincipalPaciente.this, Inicio.class);
                                    startActivity(intent);
                                    ActivityCompat.finishAffinity(PrincipalPaciente.this);
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }else if(id == R.id.lanzarAjustes){
            //startActivity(new Intent(PrincipalPaciente.this, Ajustes.class));
            startActivity(new Intent(PrincipalPaciente.this, Settings.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setDNI(String a){
        if(a != null && dni == null){
            dni = a;
        }
    }

    private void setNombre(String a){
        if(a != null && nombre ==null){
            nombre = a;
        }
    }

    private void setApellidos(String a){
        if(a != null && apellidos == null){
            apellidos = a;
        }
    }

    private void setPass(String a){
        if(a != null  && pass == null){
            pass = a;
        }
    }

    private void setNombreMedico(String a){
        if(a != null && nombreMedico==null){
            nombreMedico=a;
        }
    }

    private void setDNIMedico(String a){
        if(a != null && DNIMedico==null){
            DNIMedico=a;
        }
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

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                pass = data.getStringExtra("PassNew");
            }
        }
    }
}