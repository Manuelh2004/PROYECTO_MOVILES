package com.example.proyecto_moviles.ui.Movimiento;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto_moviles.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListarMovimientos#newInstance} factory method to
 * create an instance of this fragment.
 */


public class ListarMovimientos extends Fragment implements View.OnClickListener{

    final String servidor = "http://10.0.2.2/proyecto_moviles/controladores/";
    ListView listaMovimientos;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public class MovimientoAdapter extends BaseAdapter {

        private Context context;
        private final List<Movimiento> movimientoList;

        public MovimientoAdapter(Context context, List<Movimiento> movimientoList) {
            this.context = context;
            this.movimientoList = movimientoList;
        }

        @Override
        public int getCount() {
            return movimientoList.size();
        }

        @Override
        public Object getItem(int position) {
            return movimientoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_movimiento, null);
            }

            TextView tvId = convertView.findViewById(R.id.tvIdPV);
            TextView tvUsuario = convertView.findViewById(R.id.tvUsuario);
            TextView tvTipo = convertView.findViewById(R.id.tvTipoMovimiento);
            TextView tvCategoria = convertView.findViewById(R.id.tvCategoria);
            TextView tvMonto = convertView.findViewById(R.id.tvMonto);
            TextView tvFecha = convertView.findViewById(R.id.tvFecha);
            TextView tvDescripcion = convertView.findViewById(R.id.tvDescripcion);
            TextView tvEstado = convertView.findViewById(R.id.tvEstado);

            Movimiento mov = movimientoList.get(position);

            tvId.setText("ID: " + mov.id_movimiento);
            tvUsuario.setText("Usuario: " + mov.usuario);
            tvTipo.setText("Tipo: " + mov.tipo_movimiento);
            tvCategoria.setText("Categoría: " + mov.categoria);
            tvMonto.setText("Monto: " + mov.monto);
            tvFecha.setText("Fecha: " + mov.fecha);
            tvDescripcion.setText("Descripción: " + mov.descripcion);
            tvEstado.setText("Estado: " + (mov.estado.equals("1") ? "Activo" : "Inactivo"));

            return convertView;
        }
    }


    @SuppressLint("NotConstructor")
    private void ListarMovimientos() {
        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);
        String url = servidor + "movimientoController/mostrar_movimiento.php";

        RequestParams params = new RequestParams();
        params.put("id_usuario", idUsuario);

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    List<Movimiento> movimientos = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        String id_movimiento = obj.getString("id_movimiento");
                        String usuario = obj.optString("usuario", "N/A"); // Si devuelves nombre usuario en JSON
                        String tipo_movimiento = obj.getString("nom_tipo_movimiento");
                        String categoria = obj.getString("nom_categoria");
                        String monto = obj.getString("mon_movimiento");
                        String fecha = obj.getString("fech_movimiento");
                        String descripcion = obj.getString("des_movimiento");
                        String estado = obj.getString("est_movimiento");

                        movimientos.add(new Movimiento(id_movimiento, usuario, tipo_movimiento, categoria,
                                monto, fecha, descripcion, estado));
                    }

                    MovimientoAdapter adapter = new MovimientoAdapter(getActivity(), movimientos);
                    listaMovimientos.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error al procesar datos JSON", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String errorMsg = (responseBody != null) ? new String(responseBody) : error.getMessage();
                Toast.makeText(getActivity(), "Error: " + errorMsg, Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListarPrespuestos.
     */
    // TODO: Rename and change types and number of parameters
    public static ListarMovimientos newInstance(String param1, String param2) {
        ListarMovimientos fragment = new ListarMovimientos();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_listar_movimientos, container, false);
        listaMovimientos = rootView.findViewById(R.id.lstMovimientos);

        ListarMovimientos();
        return rootView;
    }

    @Override
    public void onClick(View v) {

    }
}