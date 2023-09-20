package com.example.bbva;

import android.app.Dialog;
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
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FourthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FourthFragment extends Fragment {

    DecimalFormat df = new DecimalFormat("0.00");

    private SharedViewModel viewModel;

    private String selectedOption = "";
    private String soltipo = "";
    private String soltiempo = "";


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FourthFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FourthFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FourthFragment newInstance(String param1, String param2) {
        FourthFragment fragment = new FourthFragment();
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
        View view = inflater.inflate(R.layout.fragment_fourth, container, false);

        NavigationActivity activity = (NavigationActivity) getActivity();
        String correo = activity.getCorreo();


        viewModel.getSharedValue().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String value) {
                final String cuenta = value;

                TextView cuentapagoTextView = view.findViewById(R.id.crediCuenta);
                cuentapagoTextView.setText("Cuenta: "+cuenta);
                DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                double balance = dbHelper.getBalance(cuenta);
                TextView balanceTextView = view.findViewById(R.id.creditoBalance);

                NumberFormat format = NumberFormat.getCurrencyInstance();
                String balanceFormatted = format.format(balance);
                balanceTextView.setText(balanceFormatted);

                TextView preguntaTextView = view.findViewById(R.id.creditosPregunta);
                RadioButton periodo1 = view.findViewById(R.id.periodo1);
                RadioButton periodo2 = view.findViewById(R.id.periodo2);
                RadioButton periodo3 = view.findViewById(R.id.periodo3);
                RadioButton periodo4 = view.findViewById(R.id.periodo4);
                if(selectedOption.equals("Crédito Nómina")){
                    preguntaTextView.setText("¿Cuánto dinero necesitas?");
                    periodo1.setText("6 meses");
                    periodo2.setText("12 meses");
                    periodo3.setText("24 meses");
                    periodo4.setText("48 meses");

                } else if(selectedOption.equals("Crédito Hipotecario")){
                    preguntaTextView.setText("¿Cuánto vale tu casa?");
                    periodo1.setText("5 años");
                    periodo2.setText("10 años");
                    periodo3.setText("15 años");
                    periodo4.setText("20 años");

                }

                EditText montosolicitud = view.findViewById(R.id.creditocasa);

                int maximoId = dbHelper.obtenerMaximoIdCredito(correo);
                String estatus = dbHelper.consultarCredito(correo, maximoId, "estatus");
                String tipocred = dbHelper.consultarCredito(correo, maximoId, "tipoCred");
                String iniciocred = dbHelper.consultarCredito(correo, maximoId, "fechainicio");
                String tiempocred = dbHelper.consultarCredito(correo, maximoId, "plazo");
                double saldo = dbHelper.consultarCreditomonto(correo, maximoId, "saldo");
                double monto = dbHelper.consultarCreditomonto(correo, maximoId, "monto");
                String interescred = "Interes: 12% anual";
                String restante = String.valueOf(saldo);

                //BOTON SOLICITAR CREDITO
                Button buttoncred1 = view.findViewById(R.id.buttoncred1);
                buttoncred1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(estatus.equals("Activo")){

                            Toast.makeText(activity.getApplicationContext(), "Credito No Aprobado, ya tienes un crédito activo", Toast.LENGTH_LONG).show();

                        }else{
                            String monto = montosolicitud.getText().toString();
                            String desctipcion = "";
                            if(soltipo.equals("nómina")){
                                desctipcion = "Crédito nómina APROBADO";
                            } else if (soltipo.equals("hipotecario")) {
                                desctipcion = "Crédito hipotecario APROBADO";
                            }
                            String fecha = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                            if (monto.equals("")||desctipcion.equals("")||soltiempo.equals("")){

                                Toast.makeText(activity.getApplicationContext(), "Todos los campos son obligatorios", Toast.LENGTH_LONG).show();
                            }else{
                                String estatus = "Activo";
                                dbHelper.insertTransaction(cuenta, Double.parseDouble(monto), fecha, desctipcion);

                                dbHelper.insertDataCredits(soltipo,Double.parseDouble(monto), fecha, soltiempo, Double.parseDouble(monto), estatus, correo);
                                Intent intent = new Intent(getActivity(), NavigationActivity.class);
                                intent.putExtra("cuenta", cuenta);
                                intent.putExtra("correo", correo);
                                startActivity(intent);
                            }
                        }


                    }
                });

                Button buttoncred2 = view.findViewById(R.id.buttoncred2);
                buttoncred2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.credito_status);


                        //calcular el plazo de pago en años
                        double tiempoEnAnios = 0;

                        switch (tiempocred) {
                            case "6 meses":
                                tiempoEnAnios = 0.5;
                                break;
                            case "12 meses":
                                tiempoEnAnios = 1;
                                break;
                            case "24 meses":
                                tiempoEnAnios = 2;
                                break;
                            case "48 meses":
                                tiempoEnAnios = 4;
                                break;
                            case "5 años":
                                tiempoEnAnios = 5;
                                break;
                            case "10 años":
                                tiempoEnAnios = 10;
                                break;
                            case "15 años":
                                tiempoEnAnios = 15;
                                break;
                            case "20 años":
                                tiempoEnAnios = 20;
                                break;
                            default:

                                tiempoEnAnios = -1;
                                break;
                        }

                        //Calcular el total a pagar
                        double montoinicial = monto;
                        double interes = 0.12;
                        double interesMensual = interes / 12.0;
                        double numPagos = tiempoEnAnios * 12;
                        double pagoMensual = (interesMensual * montoinicial) / (1 - Math.pow(1 + interesMensual, -numPagos));
                        double totalPagar = pagoMensual * numPagos;
                        String valorFormateado = df.format(totalPagar);

                        TextView statusTitulo = dialog.findViewById(R.id.statusEstatus);
                        TextView Tipocred = dialog.findViewById(R.id.textView9);
                        TextView inicioc = dialog.findViewById(R.id.textView10);
                        TextView statusTiempo = dialog.findViewById(R.id.textView11);
                        TextView statusMonto = dialog.findViewById(R.id.textView12);
                        TextView interesCred = dialog.findViewById(R.id.textView13);
                        TextView statusTotal = dialog.findViewById(R.id.textView14);
                        TextView statusDeuda = dialog.findViewById(R.id.textView16);


                        if(estatus.equals("Activo")){
                            statusTitulo.setText(estatus);
                            Tipocred.setText("Tipo: "+tipocred);
                            inicioc.setText("Fecha inicio: "+iniciocred);
                            statusTiempo.setText("Plazo: "+tiempocred);
                            statusMonto.setText("Monto inicial: $"+String.valueOf(monto));
                            interesCred.setText(interescred);
                            statusTotal.setText("Total: $"+valorFormateado);
                            statusDeuda.setText("Deuda: $"+String.valueOf(restante));

                        }
                        dialog.show();

                    }
                });

                RadioGroup radioGrouptipo = view.findViewById(R.id.radiogruptipocredito);
                radioGrouptipo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                        RadioButton radioButton = view.findViewById(checkedId);
                        selectedOption = radioButton.getText().toString();
                        if(selectedOption.equals("Crédito Nómina")){
                            preguntaTextView.setText("¿Cuánto dinero necesitas?");
                            periodo1.setText("6 meses");
                            periodo2.setText("12 meses");
                            periodo3.setText("24 meses");
                            periodo4.setText("48 meses");
                            soltipo = "nómina";


                        } else if(selectedOption.equals("Crédito Hipotecario")){
                            preguntaTextView.setText("¿Cuánto vale tu casa?");
                            periodo1.setText("5 años");
                            periodo2.setText("10 años");
                            periodo3.setText("15 años");
                            periodo4.setText("20 años");
                            soltipo = "hipotecario";
                        }
                    }
                });
                RadioGroup radioGrouptiempo = view.findViewById(R.id.radiogrouptiempo);
                radioGrouptiempo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                        RadioButton radioButton = view.findViewById(checkedId);
                        soltiempo = radioButton.getText().toString();
                    }
                });

                Button buttoncredpagar = view.findViewById(R.id.buttoncredpagar);
                buttoncredpagar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), PagoCredito.class);
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