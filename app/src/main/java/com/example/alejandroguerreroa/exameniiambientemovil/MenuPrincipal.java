package com.example.alejandroguerreroa.exameniiambientemovil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MenuPrincipal extends AppCompatActivity implements View.OnClickListener {

    Button ProductosBtn;
    Button ClientesBtn;
    Button FacturaBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        inicializaPantalla();
    }

    private void inicializaPantalla() {

        ProductosBtn = (Button) findViewById(R.id.btnProductos);
        ClientesBtn = (Button) findViewById(R.id.btnClientes);
        FacturaBtn = (Button) findViewById(R.id.btnFacturas);

        ProductosBtn.setOnClickListener(this);
        ClientesBtn.setOnClickListener(this);
        FacturaBtn.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnClientes:
                startActivity(new Intent(MenuPrincipal.this, ClientesFrm.class));
                break;
            case R.id.btnProductos:
                startActivity(new Intent(MenuPrincipal.this, ProductosFrm.class));
                break;
            case R.id.btnFacturas:
                startActivity(new Intent(MenuPrincipal.this, FacturaFrm.class));
                break;
        }
    }

}
