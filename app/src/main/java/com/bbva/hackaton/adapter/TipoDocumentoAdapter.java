package com.bbva.hackaton.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bbva.hackaton.R;

import java.util.ArrayList;

public class TipoDocumentoAdapter  extends BaseAdapter {
    private Activity activity;
    private ArrayList<String> documentos;
    private LayoutInflater inflater;

    public TipoDocumentoAdapter(Activity activity, ArrayList<String> doctors){
        this.activity=activity;
        this.documentos=doctors;
        inflater=(LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){
        return documentos.size();
    }

    @Override
    public long getItemId(int i){
        return 0;
    }

    @Override
    public String getItem(int i) {
        return documentos.get(i);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = inflater.inflate(R.layout.item_tipo_documentos, null);


        TextView spinnerItem=(TextView) view.findViewById(R.id.txt_tipo_documento);
        spinnerItem.setText(documentos.get(position).toString());
        return view;
    }
}
