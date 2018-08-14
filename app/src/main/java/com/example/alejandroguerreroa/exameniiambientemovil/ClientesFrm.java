package com.example.alejandroguerreroa.exameniiambientemovil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ClientesFrm extends AppCompatActivity implements View.OnClickListener {

    EditText etNombre;
    EditText etTelefono;
    EditText etId;
    Button agregarCliente;
    Button modificarCliente;
    Button cancelarMod;
    ListView lvClientes;

    //Instancias
    private Util_UI UtilesUI = new Util_UI();
    DatabaseReference mDBCliente = FirebaseDatabase.getInstance().getReference().child("clientes");

    //Lista y Adapter
    ArrayList<String> listaClientes = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes_frm);

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
                listaClientes.clear();

                //se toman todos los datos
                for (DataSnapshot clientes : dataSnapshot.getChildren()) {

                    //Se guarda en un cliente
                    Cliente cliente =  clientes.getValue(Cliente.class);

                    //Se agrega a ListaClientes
                    listaClientes.add(clientes.getKey() + " - " + cliente.nombre + " - " + cliente.telefono);
                }

                //Se agrega a ListView
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //se incluye el Listener
        mDBCliente.addValueEventListener(postListener);
    }

    //Toma info de lista y la pone en los EditTexts
    private void ListViewClick(){
        lvClientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Toma el texto y lo divide
                String elementoSeleccionado = (String) lvClientes.getItemAtPosition(position);
                String[] TextoPartido = elementoSeleccionado.split(" - ");

                //Las divisiones los pone en los EditTexts
                etId.setText(TextoPartido[0]);
                etTelefono.setText(TextoPartido[2]);
                etNombre.setText(TextoPartido[1]);

                //Habilia el boton Modificar y muestra el boton Cancelar
                modificarCliente.setEnabled(true);
                cancelarMod.setVisibility(View.VISIBLE);

                //Deshabilita que se pueda agregar
                agregarCliente.setEnabled(false);

                //Deshabilita EditText de ID
                etId.setEnabled(false);
            }
        });
    }

    private void incializaPantalla() {
        //EditTexts
        etTelefono = (EditText) findViewById(R.id.etPrecioVenta);
        etNombre = (EditText) findViewById(R.id.etNombre);
        etId = (EditText) findViewById(R.id.etId);

        //Buttons
        agregarCliente = (Button) findViewById(R.id.btnAgregar);
        agregarCliente.setOnClickListener(this);
        modificarCliente = (Button) findViewById(R.id.btnModificar);
        modificarCliente.setOnClickListener(this);
        cancelarMod = (Button) findViewById(R.id.btnCancelar);
        cancelarMod.setOnClickListener(this);

        //Lista
        lvClientes = (ListView) findViewById(R.id.listProductos);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaClientes);
        lvClientes.setAdapter(adapter);
    }

    //Limpia campos
    public void limpiaCampos() {
        etId.getText().clear();
        etNombre.getText().clear();
        etTelefono.getText().clear();
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            //Agrega Cliente
            case R.id.btnAgregar:
                try {

                    //boolean para verificar que no exista
                    boolean permitirAgregar = true;

                    //Verifica que el cliente no exista
                    for (String cliente : listaClientes) {
                        //Toma el id
                        String[] textoCliente = cliente.split(" - ");
                        int idCliente = Integer.parseInt(textoCliente[0]);

                        //Toma el ID del EditText
                        int etID = Integer.parseInt(etId.getText().toString());

                        //Inicia veificacion
                        if (idCliente == etID) {
                            permitirAgregar = false;
                        }
                    }

                    //Cierra teclado
                    hideKeyboard(this);

                    //Si es True, agrega un cliente, caso contario dice que ya existe
                    if (permitirAgregar) {

                        //Crea un cliente
                        Cliente cliente = new Cliente(etNombre.getText().toString(), etTelefono.getText().toString());

                        //Agrega a FireBase
                        mDBCliente.child(etId.getText().toString()).setValue(cliente);

                        //Limpia campos
                        limpiaCampos();

                        //Notifica que se logró
                        UtilesUI.MensajeToast(this, "Cliente Agregado");

                        break;
                    } else {
                        UtilesUI.MensajeToast(this, "El cliente ya existe, agregue uno nuevo");
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
                agregarCliente.setEnabled(true);
                modificarCliente.setEnabled(false);
                cancelarMod.setVisibility(View.INVISIBLE);
                etId.setEnabled(true);

                break;

            //Modifica información
            case R.id.btnModificar:
                try {

                    //Crea Cliente
                    Cliente cliente =  new Cliente(etNombre.getText().toString(), etTelefono.getText().toString());

                    //Lo modifica
                    mDBCliente.child(etId.getText().toString()).setValue(cliente);

                    //Limpia campos
                    limpiaCampos();

                    //Cierra teclado
                    hideKeyboard(this);

                    //Habilita ID
                    etId.setEnabled(true);

                    //Notifica que se logró
                    UtilesUI.MensajeToast(this, "Cliente Modificado");

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
