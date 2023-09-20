package com.example.bbva;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class NavigationActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    private String correo;
    private String cuenta;

    private Menu mMenu;

    private SharedViewModel viewModel;
    FirstFragment firstFragment = new FirstFragment();
    SecondFragment secondFragment = new SecondFragment();
    ThirdFragment thirdFragment = new ThirdFragment();
    FourthFragment fourthFragment = new FourthFragment();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        databaseHelper = new DatabaseHelper(this);

        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        Intent intent = getIntent();
        correo = intent.getStringExtra("correo");
        cuenta = intent.getStringExtra("cuenta");

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadFragment(firstFragment);
    }

    public String getCorreo() {
        return correo;
    }
    public String getCuenta() {
        return cuenta;
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.first_fragment:
                    loadFragment(firstFragment);
                    return true;

                case R.id.second_fragment:
                    loadFragment(secondFragment);
                    return  true;

                case R.id.third_fragment:
                    loadFragment(thirdFragment);
                    return true;
                case R.id.fourth_fragment:
                    loadFragment(fourthFragment);
                    return true;
            }
            return false;
        }
    };

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu) {
            Intent next = new Intent(this, MainActivity.class);
            startActivity(next);
            return true;
        }
        if (id == R.id.menu2) {
            Intent next2 = new Intent(this, MapsActivity.class);
            startActivity(next2);
            return true;
        }
        if (id == R.id.menuAgregarCuenta) {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_agregar_cuenta);

            // Obtener los elementos de la vista
            EditText editTextNumCuenta = dialog.findViewById(R.id.editTextNumCuenta);
            EditText editTextAlias = dialog.findViewById(R.id.editTextAlias);
            RadioGroup radioGroupTipoCuenta = dialog.findViewById(R.id.radioGroupTipoCuenta);
            Button buttonGuardar = dialog.findViewById(R.id.buttonGuardar);

            // Configurar el botón Guardar
            buttonGuardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Obtener los valores ingresados por el usuario
                    try {
                        String numCuenta = editTextNumCuenta.getText().toString();
                        String alias = editTextAlias.getText().toString();
                        String tipoCuenta = ((RadioButton) dialog.findViewById(radioGroupTipoCuenta.getCheckedRadioButtonId())).getText().toString();

                        //verificar si es tarjeta de credito
                        if(tipoCuenta.equals("crédito")&&databaseHelper.existeCuentaCredito(correo)){
                            Toast toast = Toast.makeText(NavigationActivity.this, "¡No puedes agregar más de una tarjeta de crédito!", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }else{
                            // Guardar la nueva cuenta en la base de datos
                            databaseHelper.insertDataCuentas(numCuenta, alias, tipoCuenta, correo);
                            Toast.makeText(NavigationActivity.this, "¡Cuenta agregada!", Toast.LENGTH_SHORT).show();
                            viewModel.setSharedValue(numCuenta);
                            dialog.dismiss();
                        }

                    } catch (Exception e) {
                        Toast.makeText(NavigationActivity.this, "Para agregar una nueva cuenta se deben llenar todos los campos", Toast.LENGTH_SHORT).show();}
                    finally {
                        dialog.dismiss();
                    }
                }
            });

            dialog.show();
            return true;

        }
        if (id == R.id.menuSelCuenta) {

           Cursor cursor = databaseHelper.getCuentas(correo);
            // Crear un submenú desplegable
            SubMenu submenu = item.getSubMenu();
            submenu.clear(); // Limpiar el submenú por si ya contenía elementos anteriores

            // Agregar cada cuenta como un elemento del submenú
            if (cursor.moveToFirst()) {
                do {
                    String cuenta = cursor.getString(1);
                    MenuItem iteme = submenu.add(0, 0, 0, cuenta);
                    final String finalCuenta = cuenta; // Crear una variable final para almacenar el valor actual de cuenta
                    iteme.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            // Mostrar la cuenta seleccionada en un Toast
                            String cta = databaseHelper.obtenerCuenta(correo,finalCuenta);
                            viewModel.setSharedValue(cta);

                            return true;
                        }
                    });
                } while (cursor.moveToNext());
            }
            return true;

        }


        return super.onOptionsItemSelected(item);
    }
}