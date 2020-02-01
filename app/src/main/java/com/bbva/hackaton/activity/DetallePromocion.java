package com.bbva.hackaton.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.corebuild.arlocation.demo.model.Comercio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import com.bbva.hackaton.R;

public class DetallePromocion extends AppCompatActivity {

    TextView descripcionPromocion;
    TextView terminosCondiciones;
    TextView isPuntosVida;
    TextView isPagoSinIntereses;
    TextView isTarjetaCredito;
    TextView isTarjetaDebido;
    TextView nombreComercial;
    ImageView imgPromocion;
    ImageView imgPagarLukita;

    Toolbar appBarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_promocion);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        appBarLayout = findViewById(R.id.toolbar); //here toolbar is your id in xml
        setSupportActionBar(toolbar);

        nombreComercial = findViewById(R.id.txt_detalle_nombre_comercial);
        imgPagarLukita = findViewById(R.id.img_pago_lukita);

        imgPromocion = findViewById(R.id.img_promocion_comercio);
        descripcionPromocion = findViewById(R.id.txt_detalle_descripcion_promocion);
        terminosCondiciones = findViewById(R.id.txt_detalle_terminos_condiciones);
        isPuntosVida = findViewById(R.id.flag_pago_sin_intereses);
        isPagoSinIntereses = findViewById(R.id.flag_puntos_vida);
        isTarjetaCredito = findViewById(R.id.flag_tarjeta_credito);
        isTarjetaDebido = findViewById(R.id.flag_tarjeta_debito);

        Comercio mListComercio = (Comercio) getIntent().getExtras().get("mListaComercio");


        Glide.with(getApplicationContext())
                .load(mListComercio.getUrlImage())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(imgPromocion);

        getSupportActionBar().setTitle(mListComercio.getCampana());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_back_pressed);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        nombreComercial.setText(mListComercio.getNombreComercialMarca() == null ? "" : mListComercio.getNombreComercialMarca());
        descripcionPromocion.setText(mListComercio.getPromocion() == null ? "" : mListComercio.getPromocion());
        terminosCondiciones.setText(mListComercio.getTerminosCondiciones() == null ? "" : mListComercio.getTerminosCondiciones());
        isPuntosVida.setText(mListComercio.getValidoPV() == null ? "" : mListComercio.getValidoPV());
        isPagoSinIntereses.setText(mListComercio.getValidoPSI() == null ? "" : mListComercio.getValidoPSI());
        isTarjetaCredito.setText(mListComercio.getValidoTC() == null ? "" : mListComercio.getValidoTC());
        isTarjetaDebido.setText(mListComercio.getValidoTD() == null ? "" : mListComercio.getValidoTD());


        imgPagarLukita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarPagoLiquita();
            }
        });

    }


    private void registrarPagoLiquita() {
        ImageView closeDialgo;

        AlertDialog.Builder builder = new AlertDialog.Builder(DetallePromocion.this);
        LayoutInflater inflater = this.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_pagar_mini_prestamo, null);
        builder.setView(view)
                .setCancelable(true);
        AlertDialog alert11 = builder.create();
        closeDialgo = view.findViewById(R.id.id_close_banner);
        closeDialgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert11.dismiss();
            }
        });


        alert11.show();
    }
}
