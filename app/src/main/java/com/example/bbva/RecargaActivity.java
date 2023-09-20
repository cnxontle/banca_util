package com.example.bbva;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.format.DateTimeFormatter;

public class RecargaActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    private EditText NumeroCel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recarga);
        String cuenta = getIntent().getStringExtra("cuenta");
        String correo = getIntent().getStringExtra("correo");
        databaseHelper = new DatabaseHelper(this);

        NumeroCel = findViewById(R.id.editTextNumber3);

        Spinner spinnercomp = findViewById(R.id.spinner);
        String[] elementos = {"","Telcel", "Movistar", "Unefón", "AT&T"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, elementos);
        spinnercomp.setAdapter(adapter);

        Spinner spinnermonto = findViewById(R.id.spinnermonto);
        String[] elementosmonto = {"","$20", "$50", "$100", "$200", "$500"};
        ArrayAdapter<String> adaptermonto = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, elementosmonto);
        spinnermonto.setAdapter(adaptermonto);


        Button buttonGoToMain = findViewById(R.id.recbutton);
        buttonGoToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String numeroCel = NumeroCel.getText().toString();
                if (numeroCel.length() >= 3) {
                    numeroCel = numeroCel.substring(numeroCel.length() - 3);
                }
                String compañia = spinnercomp.getSelectedItem().toString();
                String montoString = spinnermonto.getSelectedItem().toString();
                String monto = montoString.replaceAll("\\$", "");


                if(monto.equals("") || numeroCel.equals("") ||compañia.equals("")){
                    Toast.makeText(getApplicationContext(), "Todos los campos son obligatorios", Toast.LENGTH_LONG).show();
                } else {

                    String descripcion = "Recarga " + compañia + " ***" + numeroCel;
                    String fecha = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                    //verificar si el monto no sobrepasa el dinero disponible
                    double balance = databaseHelper.getBalance(cuenta);

                    //verificar el tipo de cuenta
                    String tipo = databaseHelper.getTipo(cuenta,"tipo");

                    if(tipo.equals("crédito")){
                        if(balance + 40000 >= Double.parseDouble(monto)){
                            databaseHelper.insertTransaction(cuenta, -Double.parseDouble(monto), fecha, descripcion);
                            Intent intent = new Intent(RecargaActivity.this, NavigationActivity.class);
                            intent.putExtra("cuenta", cuenta);
                            intent.putExtra("correo", correo);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(), "No tienes suficientes fondos para este movimiento", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        if(balance >= Double.parseDouble(monto)){
                            databaseHelper.insertTransaction(cuenta, -Double.parseDouble(monto), fecha, descripcion);
                            Intent intent = new Intent(RecargaActivity.this, NavigationActivity.class);
                            intent.putExtra("cuenta", cuenta);
                            intent.putExtra("correo", correo);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(), "No tienes suficientes fondos para este movimiento", Toast.LENGTH_LONG).show();
                        }
                    }

                }
            }

        });

    }
}