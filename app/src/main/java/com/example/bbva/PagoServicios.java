package com.example.bbva;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.format.DateTimeFormatter;

public class PagoServicios extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    private EditText PagoRef, PagoMonto;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagoservicios);
        String correo = getIntent().getStringExtra("correo");
        String cuenta = getIntent().getStringExtra("cuenta");
        String prefijo = getIntent().getStringExtra("prefijo");

        if (prefijo.equals("Pago SIAPA ***")){
            TextView textView5 = findViewById(R.id.textView5);
            textView5.setText("Pago del servicio SIAPA");

        } else if (prefijo.equals("Pago CFE ***")) {
            TextView textView5 = findViewById(R.id.textView5);
            textView5.setText("Pago del servicio CFE");

        } else if (prefijo.equals("Pago Servicio ***")){
            TextView textView5 = findViewById(R.id.textView5);
            textView5.setText("Pago de servicios");

            EditText referenciaServ = findViewById(R.id.editTextTextPersonName);
            referenciaServ.setHint("Ingresa la referencia a tu servicio");
        }

        databaseHelper = new DatabaseHelper(this);

        PagoRef = findViewById(R.id.editTextTextPersonName);
        PagoMonto = findViewById(R.id.editTextTextPersonName2);


        Button buttonGoToMain = findViewById(R.id.pagoser);
        buttonGoToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String monto = PagoMonto.getText().toString();
                String referencia = PagoRef.getText().toString();
                if (referencia.length() >= 3) {
                    referencia = referencia.substring(referencia.length() - 3);
                }

                if (monto.equals("") || referencia.equals("") ) {
                    Toast.makeText(getApplicationContext(), "Todos los campos son obligatorios", Toast.LENGTH_LONG).show();
                }
                else{
                    String descripcion = (prefijo+referencia);
                    String fecha = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));


                    //verificar si el monto no sobrepasa el dinero disponible
                    double balance = databaseHelper.getBalance(cuenta);

                    //verificar el tipo de cuenta

                    String tipo = databaseHelper.getTipo(cuenta,"tipo");

                    if(tipo.equals("crédito")){
                        if(balance + 40000 >= Double.parseDouble(monto)){
                            databaseHelper.insertTransaction(cuenta, -Double.parseDouble(monto), fecha, descripcion);
                            Intent intent = new Intent(PagoServicios.this, NavigationActivity.class);
                            intent.putExtra("cuenta", cuenta);
                            intent.putExtra("correo", correo);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(), "No tienes suficientes fondos para este movimiento", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        if(balance >= Double.parseDouble(monto)){
                            databaseHelper.insertTransaction(cuenta, -Double.parseDouble(monto), fecha, descripcion);
                            Intent intent = new Intent(PagoServicios.this, NavigationActivity.class);
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