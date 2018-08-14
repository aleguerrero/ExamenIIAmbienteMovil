package com.example.alejandroguerreroa.exameniiambientemovil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProductosFrm extends AppCompatActivity implements View.OnClickListener {

    EditText etNombre;
    EditText etPrecioVenta;
    EditText etId;
    Button agregarProducto;
    Button modificarProducto;
    Button cancelarMod;
    ListView lvProductos;

    //Instancias
    private Util_UI UtilesUI = new Util_UI();
    DatabaseReference mDBProducto = FirebaseDatabase.getInstance().getReference().child("productos");

    //Lista y Adapter
    ArrayList<String> listaProductos = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos_frm);

        //Inicia Pantalla
        incializaPantalla();

        //No abre el teclado automaticamente
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //Cuando se presiona una linea en el LIstView
        ListViewClick();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Para mostrar informacion
        ValueEventListener postListener = new ValueEventListener() {

            //En este muestra info
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Limpia lista
                listaProductos.clear();

                //se toman todos los datos
                for (DataSnapshot productos : dataSnapshot.getChildren()) {

                    //Se guarda en un Producto
                    Producto producto = productos.getValue(Producto.class);

                    //Se agrega a ListaProducto
                    listaProductos.add(productos.getKey() + " - " + producto.nombre + " - " + producto.precioVenta);
                }

                //Se agrega a ListView
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //se incluye el Listener
        mDBProducto.addValueEventListener(postListener);
    }

    //Toma info de lista y la pone en los EditTexts
    private void ListViewClick(){
        lvProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Toma el texto y lo divide
                String elementoSeleccionado = (String) lvProductos.getItemAtPosition(position);
                String[] TextoPartido = elementoSeleccionado.split(" - ");

                //Las divisiones los pone en los EditTexts
                etId.setText(TextoPartido[0]);
                etPrecioVenta.setText(TextoPartido[2]);
                etNombre.setText(TextoPartido[1]);

                //Habilia el boton Modificar y muestra el boton Cancelar
                modificarProducto.setEnabled(true);
                cancelarMod.setVisibility(View.VISIBLE);

                //Deshabilita que se pueda agregar
                agregarProducto.setEnabled(false);

                //Deshabilita EditText de ID
                etId.setEnabled(false);
            }
        });
    }

    private void incializaPantalla() {
        //EditTexts
        etPrecioVenta = (EditText) findViewById(R.id.etPrecioVenta);
        etNombre = (EditText) findViewById(R.id.etNombre);
        etId = (EditText) findViewById(R.id.etId);

        //Buttons
        agregarProducto = (Button) findViewById(R.id.btnAgregar);
        agregarProducto.setOnClickListener(this);
        modificarProducto = (Button) findViewById(R.id.btnModificar);
        modificarProducto.setOnClickListener(this);
        cancelarMod = (Button) findViewById(R.id.btnCancelar);
        cancelarMod.setOnClickListener(this);

        //Lista
        lvProductos = (ListView) findViewById(R.id.listProductos);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaProductos);
        lvProductos.setAdapter(adapter);
    }

    //Limpia campos
    public void limpiaCampos() {
        etId.getText().clear();
        etNombre.getText().clear();
        etPrecioVenta.getText().clear();
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            //Agrega Producto
            case R.id.btnAgregar:
                try {

                    //boolean para verificar que no exista
                    boolean permitirAgregar = true;

                    //Verifica que el cliente no exista
                    for (String producto: listaProductos) {
                        //Toma el id
                        String[] textoProducto = producto.split(" - ");
                        int idProducto = Integer.parseInt(textoProducto[0]);

                        //Toma el ID del EditText
                        int etID = Integer.parseInt(etId.getText().toString());

                        //Inicia veificacion
                        if (idProducto == etID) {
                            permitirAgregar = false;
                        }
                    }

                    //Cierra teclado
                    hideKeyboard(this);

                    //Si es True, agrega un producto, caso contario dice que ya existe
                    if (permitirAgregar) {

                        //Crea un producto
                        Producto producto = new Producto(etNombre.getText().toString(), Integer.parseInt(etPrecioVenta.getText().toString()));

                        //Agrega a FireBase
                        mDBProducto.child(etId.getText().toString()).setValue(producto);

                        //Limpia campos
                        limpiaCampos();

                        //Notifica que se logró
                        UtilesUI.MensajeToast(this, "Producto agregado");

                        break;
                    } else {
                        UtilesUI.MensajeToast(this, "El producto ya existe, agregue uno nuevo");
                        break;
                    }

                } catch (Exception e) {
                    UtilesUI.MensajeToast(this, "Hubo un error");
                    break;
                }

            //Cancela que se modifique un dato
            case R.id.btnCancelar:

                //Limpia campos
                limpiaCampos();

                //Habilita y deshabilita botones
                agregarProducto.setEnabled(true);
                modificarProducto.setEnabled(false);
                cancelarMod.setVisibility(View.INVISIBLE);
                etId.setEnabled(true);

                break;

            //Modifica información
            case R.id.btnModificar:
                try {

                    //Crea Cliente
                    Producto producto =  new Producto(etNombre.getText().toString(), Integer.parseInt(etPrecioVenta.getText().toString()));

                    //Lo modifica
                    mDBProducto.child(etId.getText().toString()).setValue(producto);

                    //Limpia campos
                    limpiaCampos();

                    //Cierra teclado
                    hideKeyboard(this);

                    //Habilita ID
                    etId.setEnabled(true);

                    //Notifica que se logró
                    UtilesUI.MensajeToast(this, "Producto Modificado");

                    break;

                } catch (Exception e) {
                    UtilesUI.MensajeToast(this, "Hubo un error");
                    break;
                }
        }//End switch
    }

    //Oculta Keyboard
    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
