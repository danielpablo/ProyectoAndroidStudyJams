package com.example.pablo.medddddico; //a

import android.app.DatePickerDialog;
import android.widget.DatePicker;

/**
 * Created by pablo on 30/01/16.
 */
public class mDateSetListener implements DatePickerDialog.OnDateSetListener {

    public static boolean status=false;

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {
        int mYear = year;
        int mMonth = monthOfYear+1;         //El mes comeinza en 0
        int mDay = dayOfMonth;
        PrincipalPaciente.anho = year;
        PrincipalPaciente.mes = monthOfYear+1;         //El mes comeinza en 0
        PrincipalPaciente.dia = dayOfMonth;
        PedirCitaPaciente.fecha=Integer.toString(dayOfMonth)+"-"+Integer.toString(monthOfYear+1)+"-"+Integer.toString(year)+"|";
        status=true;
        PedirCitaPaciente.textV.setText("Fecha: " + PrincipalPaciente.dia + " " + PrincipalPaciente.mes + " " + PrincipalPaciente.anho);
    }
}
