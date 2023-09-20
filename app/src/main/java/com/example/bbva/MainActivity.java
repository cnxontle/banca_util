package com.example.bbva;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.bbva.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        // Agregar un Listener al botón 1
        binding.buttonAcceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correo = binding.accederCuenta.getText().toString();
                String pasword = binding.editTextPassword.getText().toString();

                if(correo.equals("") || pasword.equals(""))
                    Toast.makeText(getApplicationContext(), "Todos los campos son obligatorios", Toast.LENGTH_LONG).show();
                else {
                    Boolean checkCredentials = databaseHelper.checkUserPassword(correo,pasword);
                    if (checkCredentials == true){
                        String nombre = databaseHelper.getParameter(correo, "nombre");
                        Toast toast = Toast.makeText(getApplicationContext(), "Bienvenid@ "+nombre, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0); // setGravity(int gravity, int xOffset, int yOffset)
                        toast.show();
                        Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
                        intent.putExtra("correo", correo);
                        startActivity(intent);

                    } else {
                        Toast.makeText(getApplicationContext(), "Usuario o contraseña equivocados", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


        // Agregar un Listener al botón 2
        Button boton2 = findViewById(R.id.button2);
        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegistroActivity.class);
                startActivity(intent);
            }
        });

    }
}