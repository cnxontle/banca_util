package com.example.bbva;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.format.DateTimeFormatter;

public class PagoCredito extends AppCompatActivity {

    private String correo;
    private String cuenta;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagocredito);
        databaseHelper = new DatabaseHelper(this);

        Button btnPagoCredito = findViewById(R.id.pagocredito);
        EditText etMontoPagoCredito = findViewById(R.id.montopagocredito);
        Intent intent = getIntent();
        correo = intent.getStringExtra("correo");
        cuenta = intent.getStringExtra("cuenta");

        btnPagoCredito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String monto = etMontoPagoCredito.getText().toString();
                String fecha = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                int maximoId = databaseHelper.obtenerMaximoIdCredito(correo);
                String estatus = databaseHelper.consultarCredito(correo, maximoId, "estatus");
                String tipocred = databaseHelper.consultarCredito(correo, maximoId, "tipoCred");
                double saldo = databaseHelper.consultarCreditomonto(correo, maximoId, "saldo");
                String descripcion = "";
                double montocorregido = 0;
                double deuda = 0;

                //Comprobar si hay creditos activos
                if(estatus.equals("Activo")){

                    //aviso de campo obligatorio
                    if(monto.equals("")){
                        Toast.makeText(getApplicationContext(), "Favor de Ingresar un monto", Toast.LENGTH_LONG).show();
                    }else{
                        if(tipocred.equals("nómina")){
                            descripcion = "Pago Crédito Nomina";
                        }else{
                            descripcion = "Pago Crédito Hipotecario";
                        }

                        if(saldo-Double.parseDouble(monto)<=0){
                            estatus = "Liquidado";
                            montocorregido = Double.parseDouble(monto)+(saldo-Double.parseDouble(monto));
                        }else{
                            deuda = saldo-Double.parseDouble(monto);
                            montocorregido = Double.parseDouble(monto);
                        }

                        //verificar si el monto no sobrepasa el dinero disponible
                        double balance = databaseHelper.getBalance(cuenta);

                        //verificar el tipo de cuenta
                        String tipo = databaseHelper.getTipo(cuenta,"tipo");

                        if(tipo.equals("crédito")){
                            if(balance + 40000 >= Double.parseDouble(monto)){
                                // modificar los valores de estatus y deuda en la tabla creditos
                                databaseHelper.modificarCredito(correo,"estatus",estatus);
                                databaseHelper.modificarDeuda(correo,"saldo",deuda);
                                databaseHelper.insertTransaction(cuenta, -montocorregido, fecha, descripcion);
                                Intent intent = new Intent(PagoCredito.this, NavigationActivity.class);
                                intent.putExtra("cuenta", cuenta);
                                intent.putExtra("correo", correo);
                                startActivity(intent);
                            }else{
                                Toast.makeText(getApplicationContext(), "No tienes suficientes fondos para este movimiento", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            if(balance >= Double.parseDouble(monto)){
                                // modificar los valores de estatus y deuda en la tabla creditos
                                databaseHelper.modificarCredito(correo,"estatus",estatus);
                                databaseHelper.modificarDeuda(correo,"saldo",deuda);
                                databaseHelper.insertTransaction(cuenta, -montocorregido, fecha, descripcion);
                                Intent intent = new Intent(PagoCredito.this, NavigationActivity.class);
                                intent.putExtra("cuenta", cuenta);
                                intent.putExtra("correo", correo);
                                startActivity(intent);
                            }else{
                                Toast.makeText(getApplicationContext(), "No tienes suficientes fondos para este movimiento", Toast.LENGTH_LONG).show();
                            }
                        }

                    }

                }else{
                    Toast.makeText(getApplicationContext(), "No tienes creditos activos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
