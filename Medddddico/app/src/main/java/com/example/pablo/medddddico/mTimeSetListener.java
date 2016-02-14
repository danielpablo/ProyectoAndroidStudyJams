package com.example.pablo.medddddico; //a

import android.app.TimePickerDialog;
import android.widget.TimePicker;

/**
 * Created by pablo on 30/01/16.
 */
public class mTimeSetListener implements TimePickerDialog.OnTimeSetListener {

    public static boolean status=false;

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        int mHour = hourOfDay;
        int mMinute = minute;
        PrincipalPaciente.hora = hourOfDay;
        PrincipalPaciente.minuto = minute;
        status=true;
        PedirCitaPaciente.textV.setText(PedirCitaPaciente.textV.getText()+"-Hora: "+ PrincipalPaciente.hora + ":" + PrincipalPaciente.minuto);
        PedirCitaPaciente.fecha+=Integer.toString(hourOfDay)+":"+Integer.toString(minute);
    }
}
