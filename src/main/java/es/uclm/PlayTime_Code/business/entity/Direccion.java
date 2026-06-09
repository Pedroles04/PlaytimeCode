package es.uclm.PlayTime_Code.business.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class Direccion {
    
    private String tipoVia; // Calle, Avenida, Plaza, etc.
    private String nombreVia;
    private String numero;
    private String pisoPortal; // Piso, Puerta o Portal (opcional)
    private String codigoPostal;
    private String localidad;
    private String provincia;

    public Direccion() {}

    // Métodos de ayuda opcionales para pintar la dirección bonita de golpe
    public String getDireccionCompleta() {
        String base = tipoVia + " " + nombreVia + ", Nº " + numero;
        if (pisoPortal != null && !pisoPortal.isBlank()) {
            base += ", " + pisoPortal;
        }
        return base + " - " + codigoPostal + ", " + localidad + " (" + provincia + ")";
    }

    // Getters y Setters
    public String getTipoVia() { return tipoVia; }
    public void setTipoVia(String tipoVia) { this.tipoVia = tipoVia; }
    public String getNombreVia() { return nombreVia; }
    public void setNombreVia(String nombreVia) { this.nombreVia = nombreVia; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getPisoPortal() { return pisoPortal; }
    public void setPisoPortal(String pisoPortal) { this.pisoPortal = pisoPortal; }
    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }
    public String getLocalidad() { return localidad; }
    public void setLocalidad(String localidad) { this.localidad = localidad; }
    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }
}