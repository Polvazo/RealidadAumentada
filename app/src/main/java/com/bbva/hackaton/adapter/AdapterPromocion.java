package com.bbva.hackaton.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bbva.hackaton.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.corebuild.arlocation.demo.model.Comercio;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class AdapterPromocion extends RecyclerView.Adapter<AdapterPromocion.MyViewHolder> {

    private GoogleMap mMap;
    protected LatLng mMapLocation;
    private FragmentManager fragmentmanager;
    private GoogleMap map;
    private ArrayList<Comercio> lugar;
    private OnNoteListener mOnNoteListener;
    private Context activity;
    String idComercio;


    public AdapterPromocion(Context activity, ArrayList<Comercio> mData, OnNoteListener onNoteListener, FragmentManager fragmentmanager) {
        this.activity = activity;
        this.lugar = mData;
        this.mOnNoteListener = onNoteListener;
        this.fragmentmanager = fragmentmanager;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nombreNegocio;
        TextView promocion;
        TextView descripcionPromocion;
        TextView categoria;
        TextView idConvenio;
        ImageView imgPromocionPortada;
        OnNoteListener mOnNoteListener;
        TextView positionView;
        Double latitud;
        Double longitud;

        FrameLayout mapLayout;
        private SupportMapFragment mapFragment;


        public MyViewHolder(View  view, OnNoteListener onNoteListener) {
            super(view);
            nombreNegocio = view.findViewById(R.id.txt_nombre_lugar);
            promocion = view.findViewById(R.id.txt_promocion);
            imgPromocionPortada = view.findViewById(R.id.img_promocion);
            descripcionPromocion = view.findViewById(R.id.txt_descripcion_promocion);
            categoria = view.findViewById(R.id.txt_categoria);
            idConvenio = view.findViewById(R.id.txt_id_convenio);


            mapLayout = (FrameLayout) itemView.findViewById(R.id.map);
            mOnNoteListener = onNoteListener;

            view.setOnClickListener(this);



        }

        public SupportMapFragment getMapFragmentAndCallback(OnMapReadyCallback callback) {
            if (mapFragment == null) {

                mapFragment = (SupportMapFragment) fragmentmanager.findFragmentById(R.id.map);
                mapFragment.getMapAsync(callback);


            }

            return mapFragment;
        }

        public void removeMapFragment( MyViewHolder holder ) {
            if (mapFragment != null) {
                LatLng latLng = new LatLng(holder.latitud,holder.longitud);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));

            }
        }

        @Override
        public void onClick(View view) {

            Log.d("Latitud: por aca paso", "" );

            mOnNoteListener.onNoteClick(getItemViewType());
        }
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("Latitud: por aca paso", "" );

        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_promociones, parent, false);

        return new MyViewHolder(inflatedView, mOnNoteListener);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        LatLng latLng = new LatLng(holder.latitud,holder.longitud);

           holder.getMapFragmentAndCallback(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    Log.d("Paso por aca ", "de verdad");
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));


                }
            });

    }

    @Override
    public void onViewDetachedFromWindow(@NonNull MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        if (holder.latitud != null && holder.longitud != null) {
            // If error still occur unpredictably, it's best to remove fragment here
            holder.removeMapFragment(holder);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        idComercio = lugar.get(position).getIdComercio();

        holder.latitud = Double.valueOf(lugar.get(position).getLatitud());

        holder.longitud = Double.valueOf(lugar.get(position).getLongitud());

        String giroComercial = lugar.get(position).getNombreComercialMarca();
        holder.nombreNegocio.setText(giroComercial);

        String idConvenio = lugar.get(position).getIdComercio();
        holder.idConvenio.setText(idConvenio);

        String categoria = lugar.get(position).getCategoria();
        holder.categoria.setText(categoria);

        String descripcionPromocion = lugar.get(position).getPromocion();
        holder.descripcionPromocion.setText(descripcionPromocion);

        String promocion = lugar.get(position).getFechaDesde() + " valido hasta " + lugar
                .get(position).getFechaHasta();
        holder.promocion.setText(promocion);

        String imgPromocion = lugar.get(position).getUrlImage();

        Glide.with(activity)
                .load(imgPromocion)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.imgPromocionPortada);

    }

    @Override
    public int getItemCount() {
        return lugar.size();
    }

    public interface OnNoteListener{
        void onNoteClick(int position);
    }

    @Override
    public int getItemViewType(int position) {
        return Integer.parseInt(lugar.get(position).getConvenio()==null?"0":lugar.get(position).getConvenio());
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(lugar.get(position).getConvenio());
    }



}
