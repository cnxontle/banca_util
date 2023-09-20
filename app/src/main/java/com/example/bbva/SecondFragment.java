package com.example.bbva;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.TextView;

import java.text.NumberFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SecondFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SecondFragment extends Fragment {

    private SharedViewModel viewModel;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SecondFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SecondFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SecondFragment newInstance(String param1, String param2) {
        SecondFragment fragment = new SecondFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        NavigationActivity activity = (NavigationActivity) getActivity();
        String correo = activity.getCorreo();

        viewModel.getSharedValue().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String value) {
                final String cuenta = value;

                TextView cuentapagoTextView = view.findViewById(R.id.pagoCuenta);
                cuentapagoTextView.setText("Cuenta: "+cuenta);
                DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                double balance = dbHelper.getBalance(cuenta);
                TextView balanceTextView = view.findViewById(R.id.pagooBalance);

                NumberFormat format = NumberFormat.getCurrencyInstance();
                String balanceFormatted = format.format(balance);
                balanceTextView.setText(balanceFormatted);


                ImageButton buttonChangeActivity = view.findViewById(R.id.imageButtonSIAPA);
                buttonChangeActivity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), PagoServicios.class);
                        String prefijo = "Pago SIAPA ***";
                        intent.putExtra("prefijo", prefijo);
                        intent.putExtra("cuenta", cuenta);
                        intent.putExtra("correo", correo);
                        startActivity(intent);
                    }
                });
                ImageButton buttonChangeActivity2 = view.findViewById(R.id.imageButtoncfe);
                buttonChangeActivity2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), PagoServicios.class);
                        String prefijo = "Pago CFE ***";
                        intent.putExtra("prefijo", prefijo);
                        intent.putExtra("cuenta", cuenta);
                        intent.putExtra("correo", correo);
                        startActivity(intent);
                    }
                });
                ImageButton buttonChangeActivity3 = view.findViewById(R.id.imageButtonrecarga);
                buttonChangeActivity3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), RecargaActivity.class);
                        intent.putExtra("cuenta", cuenta);
                        intent.putExtra("correo", correo);
                        startActivity(intent);
                    }
                });
                ImageButton buttonChangeActivity4 = view.findViewById(R.id.imageButtonOtros);
                buttonChangeActivity4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), PagoServicios.class);
                        String prefijo = "Pago Servicio ***";
                        intent.putExtra("prefijo", prefijo);
                        intent.putExtra("cuenta", cuenta);
                        intent.putExtra("correo", correo);
                        startActivity(intent);
                    }
                });
            }
        });




        return view;
    }


}