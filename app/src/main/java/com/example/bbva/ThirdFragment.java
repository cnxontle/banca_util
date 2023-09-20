package com.example.bbva;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ThirdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThirdFragment extends Fragment {

    private SharedViewModel viewModel;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ThirdFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThirdFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThirdFragment newInstance(String param1, String param2) {
        ThirdFragment fragment = new ThirdFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_third, container, false);

        NavigationActivity activity = (NavigationActivity) getActivity();
        String correo = activity.getCorreo();

        viewModel.getSharedValue().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String value) {
                final String cuenta = value;
                TextView cuentatransTextView = view.findViewById(R.id.tranferCuenta);
                EditText tmonto = view.findViewById(R.id.transferMonto);
                EditText tdestino = view.findViewById(R.id.noCtaDest);
                EditText tconcepto = view.findViewById(R.id.transferConcepto);
                cuentatransTextView.setText("Cuenta: "+cuenta);

                DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                double balance = dbHelper.getBalance(cuenta);
                TextView balanceTextView = view.findViewById(R.id.transferBalance);

                NumberFormat format = NumberFormat.getCurrencyInstance();
                String balanceFormatted = format.format(balance);
                balanceTextView.setText(balanceFormatted);

                Button transferbutton = view.findViewById(R.id.transferbutton);
                transferbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String transmonto = tmonto.getText().toString();
                        String transref = tdestino.getText().toString();
                        String descripcion = "";
                        String descripcion2 = "";

                        if (transmonto.equals("") || transref.equals("") ) {
                            Toast.makeText(activity.getApplicationContext(), "Los campos No. Cuenta y monto son obligatorios", Toast.LENGTH_LONG).show();
                        }else{
                            if (tdestino.length() >= 3) {
                                descripcion = "Transferencia ***"+transref.substring(transref.length() - 3);
                            }
                            if (cuenta.length() >= 3) {
                                descripcion2 = "Transferencia ***"+transref.substring(transref.length() - 3);
                            }
                            String fecha = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                            //verificar si el monto no sobrepasa el dinero disponible
                            double balance = dbHelper.getBalance(cuenta);

                            //verificar el tipo de cuenta
                            String tipo = dbHelper.getTipo(cuenta,"tipo");

                            if(tipo.equals("crédito")){
                                if(balance + 40000 >= Double.parseDouble(transmonto)){
                                    dbHelper.insertTransaction(cuenta, -Double.parseDouble(transmonto), fecha, descripcion);
                                    dbHelper.insertTransaction(transref, Double.parseDouble(transmonto), fecha, descripcion2);
                                    balance = dbHelper.getBalance(cuenta);

                                    NumberFormat format = NumberFormat.getCurrencyInstance();
                                    String balanceFormatted = format.format(balance);
                                    balanceTextView.setText(balanceFormatted);

                                    tmonto.setText("");
                                    tdestino.setText("");
                                    tconcepto.setText("");
                                }else{
                                    Toast.makeText(getContext(), "No tienes suficientes fondos para este movimiento", Toast.LENGTH_LONG).show();
                                }
                            }else{
                                if(balance >= Double.parseDouble(transmonto)){
                                    dbHelper.insertTransaction(cuenta, -Double.parseDouble(transmonto), fecha, descripcion);
                                    dbHelper.insertTransaction(transref, Double.parseDouble(transmonto), fecha, descripcion2);
                                    balance = dbHelper.getBalance(cuenta);

                                    NumberFormat format = NumberFormat.getCurrencyInstance();
                                    String balanceFormatted = format.format(balance);
                                    balanceTextView.setText(balanceFormatted);

                                    tmonto.setText("");
                                    tdestino.setText("");
                                    tconcepto.setText("");
                                }else{
                                    Toast.makeText(getContext(), "No tienes suficientes fondos para este movimiento", Toast.LENGTH_LONG).show();
                                }
                            }

                        }
                    }
                });

            }
        });

        return view;

    }
}