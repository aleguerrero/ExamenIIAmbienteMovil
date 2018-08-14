package com.example.alejandroguerreroa.exameniiambientemovil;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FacturaFrm extends AppCompatActivity implements View.OnClickListener {

    //Spinner
    Spinner spCliente;
    Spinner spProducto;

    //TextViews
    TextView tvTotal;

    //EditText
    EditText etCantidad;

    //ListView
    ListView lvLista;

    //Buttons
    Button btnAgregar;
    Button btnPagar;

    //Arrays para llenar Spinners de Cliente y Producto
    ArrayList<String> listaClientes = new ArrayList<>();
    ArrayList<String> listaProductos = new ArrayList<>();

    //Instancias
    DatabaseReference mDB = FirebaseDatabase.getInstance().getReference();
    Util_UI util_ui =  new Util_UI();

    //Para Lista
    ArrayList<String> carrito = new ArrayList<>();
    ArrayAdapter<String> adapter;

    //Adapters para Clientes y Productos
    ArrayAdapter<String> adapterCliente;
    ArrayAdapter<String> adapterProducto;

    //Total
    int total;

    //Cantidades por producto
    ArrayList<Integer> cantidadesXProducto = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factura_frm);

        iniciaPantalla();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Crea Listener
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Limpia lista
                listaClientes.clear();
                listaProductos.clear();

                //se toman todos los datos de cliente
                for (DataSnapshot clientes : dataSnapshot.child("clientes").getChildren()) {

                    //Se guarda en un cliente
                    Cliente cliente =  clientes.getValue(Cliente.class);

                    //Se agrega a ListaClientes
                    listaClientes.add(clientes.getKey() + " - " + cliente.nombre + " - " + cliente.telefono);
                }

                //se toman todos los datos de productos
                for (DataSnapshot productos : dataSnapshot.child("productos").getChildren()) {

                    //Se guarda en un Producto
                    Producto producto = productos.getValue(Producto.class);

                    //Se agrega a ListaProducto
                    listaProductos.add(productos.getKey() + " - " + producto.nombre + " - " + producto.precioVenta);
                }

                //Se agrega a Spinners
                adapterCliente.notifyDataSetChanged();
                adapterProducto.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        //Agrega el Listener
        mDB.addValueEventListener(valueEventListener);
    }

    private void iniciaPantalla() {
        //Spinners
        spCliente = findViewById(R.id.spCliente);
        spProducto = findViewById(R.id.spProducto);

        //TextViews
        tvTotal = findViewById(R.id.tvTotal);

        //EditText
        etCantidad = findViewById(R.id.etCantidad);

        //ListView
        lvLista = findViewById(R.id.lvLista);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, carrito);
        lvLista.setAdapter(adapter);
        //Para Clientes
        adapterCliente = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaClientes);
        adapterCliente.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCliente.setAdapter(adapterCliente);
        //Para Productos
        adapterProducto = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaProductos);
        adapterProducto.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProducto  .setAdapter(adapterProducto);

        //Buttons
        btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(this);
        btnPagar = findViewById(R.id.btnPagar);
        btnPagar.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAgregar:
                try {
                    //Separa texto
                    String[] caracProducto = spProducto.getSelectedItem().toString().split(" - ");

                    //Toma precio y lo multiplica
                    int precioXCantidad = Integer.parseInt(caracProducto[2]) * Integer.parseInt(etCantidad.getText().toString());
                    cantidadesXProducto.add(Integer.parseInt(etCantidad.getText().toString()));

                    //Agrega a la lista
                    carrito.add(caracProducto[0] + " - " + caracProducto[1] + " - Cantidad: " + etCantidad.getText().toString() + " - Precio: " + precioXCantidad);
                    adapter.notifyDataSetChanged();

                    //Calcula total
                    total += precioXCantidad;
                    tvTotal.setText(String.valueOf(total));

                    //Limpia campos
                    spProducto.setSelection(-1);
                    etCantidad.getText().clear();

                    break;
                } catch (Exception e) {
                    util_ui.MensajeToast(this, "Hubo un error");
                    break;
                }
            case R.id.btnPagar:
                try {
                    //ArrayList para cada fila
                     ArrayList<Factura> facturas = new ArrayList<>();

                    //Busca ID
                    String[] caracCliente = spCliente.getSelectedItem().toString().split(" - ");

                    //Por cada producto en carrito, lo va a agregar en factura
                    for (int i = 0; i < adapter.getCount(); i++) {

                        //Divide texto por fila de carrito
                        String[] caracFila = adapter.getItem(i).toString().split(" - ");

                        //Se crea una factura
                        Factura factura = new Factura();
                        factura.cantidad = cantidadesXProducto.get(i);
                        factura.cliente = caracCliente[0];
                        factura.producto = caracFila[0];

                        //Se agrega a facturasPorComprar
                        facturas.add(factura);
                    }

                    //Se crea un boolean que agregue las facturas a la tabla


                    //Si funciona, muestra mensaje de éxito y cierra
                    //si no, muestra que hubo un error

                    break;
                } catch (Exception e) {
                    util_ui.MensajeToast(this, "Hubo un error");
                    break;
                }
        }
    }
}

