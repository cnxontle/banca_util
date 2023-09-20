package com.example.bbva;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FirstFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match

    private int cuentaEjecuciones = 0;
    private static final String ARG_PARAM1 = "param1";
    private SharedViewModel viewModel;

    private RecyclerView recyclerView;
    private MovimientoAdapter adapter;


    // TODO: Rename and change types of parameters
    private String mParam1;
    public FirstFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.

     * @return A new instance of fragment FirstFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FirstFragment newInstance(String param1, String param2) {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        String correo = ((NavigationActivity) getActivity()).getCorreo();

        if (cuentaEjecuciones == 0) {
            String intentcuenta = ((NavigationActivity) getActivity()).getCuenta();
            // Incrementar el contador
            cuentaEjecuciones++;

            //si hay in intent establece el valor de cuenta desde el intent si no, lo busca en base de datos
            if (intentcuenta == null) {
                String consultaCuenta = dbHelper.getCuenta(correo, "cuenta");
                viewModel.setSharedValue(consultaCuenta);
            }else{
                viewModel.setSharedValue(intentcuenta);
            }
        }
        recyclerView = view.findViewById(R.id.movimientosRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        viewModel.getSharedValue().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String value) {
                final String cuenta = value;

                String alias = dbHelper.getTipo(cuenta, "alias");
                String tipo = dbHelper.getTipo(cuenta, "tipo");
                //String nombre = dbHelper.getParameter(correo, "nombre");

                double balance = dbHelper.getBalance(cuenta);

                TextView balanceTextView = view.findViewById(R.id.firstfragmentBalance);
                NumberFormat format = NumberFormat.getCurrencyInstance();
                String balanceFormatted = format.format(balance);
                balanceTextView.setText(balanceFormatted);

                TextView cuentaTextView = view.findViewById(R.id.cuentaFirstActivity);
                cuentaTextView.setText("Cuenta: " + alias+" ("+tipo+")");

                TextView limiteTextView = view.findViewById(R.id.firsrfragmentlimit);
                if(tipo.equals("crédito")){
                    limiteTextView.setText("Limite -40mil");
                }else{
                    limiteTextView.setText("Saldo disponible");
                }

                TextView nodecuentaTextView = view.findViewById(R.id.nodecuentaFirstActivity);
                nodecuentaTextView.setText("No. de Cuenta: " + cuenta);

                List<Movimiento> movimientos = new ArrayList<>();
                List<Movimiento> movimientosDeCuenta = dbHelper.getMovimientosPorCuenta(cuenta);
                movimientos.addAll(movimientosDeCuenta);

                adapter = new MovimientoAdapter(movimientos);
                recyclerView.setAdapter(adapter);
            }
        });
        return view;
    }
}