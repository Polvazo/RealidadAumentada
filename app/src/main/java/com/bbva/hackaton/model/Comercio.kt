package com.corebuild.arlocation.demo.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Comercio : Serializable{

        @SerializedName("_id")
        var idComercio: String? = null
        @SerializedName("convenio")
        var convenio: String? = null
        @SerializedName("RUC")
        var ruc: String? = null
        @SerializedName("NombreComercialMarca")
        var nombreComercialMarca: String? = null
        @SerializedName("Categoria")
        var categoria: String? = null
        @SerializedName("Promocion")
        var promocion: String? = null
        @SerializedName("Desde")
        var fechaDesde: String? = null
        @SerializedName("Hasta")
        var fechaHasta: String? = null
        @SerializedName("Campana")
        var campana: String? = null
        @SerializedName("TC")
        var validoTC: String? = null
        @SerializedName("TD")
        var validoTD: String? = null
        @SerializedName("PSI")
        var validoPSI: String? = null
        @SerializedName("PV")
        var validoPV: String? = null
        @SerializedName("Direcciones")
        var direccion: String? = null
        @SerializedName("latitude")
        var latitud: String? = null
        @SerializedName("longitude")
        var longitud: String? = null
        @SerializedName("TerminosyCondiciones")
        var terminosCondiciones: String? = null
        @SerializedName("url")
        var urlImage: String? = null
        var posicionView: String? = null


}