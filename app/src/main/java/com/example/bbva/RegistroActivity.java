package com.example.bbva;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bbva.databinding.ActivityRegistroBinding;

import java.time.format.DateTimeFormatter;

public class RegistroActivity extends AppCompatActivity {

    ActivityRegistroBinding binding;
    DatabaseHelper databaseHelper;

    private EditText RegApellidos, RegNombre, RegCorreo, RegCuenta, RegTarjeta, RegPass1, RegPass2;
    private RadioGroup radioGroup;
    private RadioButton radioButtonDeb, radioButtonCred;
    private Button register_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        databaseHelper = new DatabaseHelper(this);


        // Obtener instancias de los elementos de la vista
        RegApellidos = findViewById(R.id.RegApellidos);
        RegNombre = findViewById(R.id.RegNombre);
        RegCorreo = findViewById(R.id.RegCorreo);
        RegCuenta = findViewById(R.id.RegCuenta);
        RegPass1 = findViewById(R.id.RegPass1);
        RegPass2 = findViewById(R.id.RegPass2);
        radioGroup = findViewById(R.id.radioGroup);
        radioButtonDeb = findViewById(R.id.radioButtonDeb);
        radioButtonCred = findViewById(R.id.radioButtonCred);
        register_button = findViewById(R.id.register_button);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radioButtonDeb) {
                    final String tipo = "débito";
                } else if (i == R.id.radioButtonCred) {
                    final String tipo = "crédito";
                }
            }
        });


        // Configurar el botón para que muestre un mensaje con los datos ingresados
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre = binding.RegNombre.getText().toString();
                String cuenta = binding.RegCuenta.getText().toString();
                String password = binding.RegPass1.getText().toString();
                String correo = binding.RegCorreo.getText().toString();
                String tipo = "";
                String alias = "Principal";
                String confirmPassword = binding.RegPass2.getText().toString();

                int radioButtonId = radioGroup.getCheckedRadioButtonId();
                if (radioButtonId == R.id.radioButtonDeb) {
                    tipo = "débito";
                } else if (radioButtonId == R.id.radioButtonCred) {
                    tipo = "crédito";
                }

                if (cuenta.equals("") || password.equals("") || confirmPassword.equals("") || correo.equals(""))
                    Toast.makeText(getApplicationContext(), "Todos los campos son obligatorios", Toast.LENGTH_LONG).show();
                else{
                    if(password.equals(confirmPassword)){
                        Boolean checkUserPassword = databaseHelper.checkUser(correo);

                        if (checkUserPassword == false){
                            Boolean insert = databaseHelper.insertDataUsers(password, nombre, correo);
                            databaseHelper.insertDataCuentas(cuenta, alias, tipo, correo);

                            if (insert == true){
                                Toast.makeText(getApplicationContext(),"¡Registro exitoso!", Toast.LENGTH_LONG).show();

                                double monto = 500.0;
                                String fecha = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                                String descripcion = "Saldo Inicial";
                                boolean result = databaseHelper.insertTransaction(cuenta, monto, fecha, descripcion);

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(),"No fue posible Registrarse", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),"La cuenta ya existe, inicie sesión", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),"Contraseña Incorrecta", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        // Configurar el comportamiento de los RadioButton
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButtonDeb) {
                    radioButtonDeb.setChecked(true);
                    radioButtonCred.setChecked(false);
                } else if (checkedId == R.id.radioButtonCred) {
                    radioButtonDeb.setChecked(false);
                    radioButtonCred.setChecked(true);
                }
            }
        });
    }
}