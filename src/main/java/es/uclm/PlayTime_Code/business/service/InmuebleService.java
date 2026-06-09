package es.uclm.PlayTime_Code.business.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.uclm.PlayTime_Code.business.entity.Direccion; // NUEVO IMPORT
import es.uclm.PlayTime_Code.business.entity.Inmueble;
import es.uclm.PlayTime_Code.business.entity.PoliticaCancelacion;
import es.uclm.PlayTime_Code.business.entity.Usuario;
import es.uclm.PlayTime_Code.persistence.InmuebleDAO;

import java.util.List;

@Service
public class InmuebleService {

    @Autowired
    private InmuebleDAO inmuebleDAO;

    // Firma modificada: Cambiado String direccion por Direccion direccion, y eliminado String ciudad
    public boolean registrarInmueble(Usuario propietario, String titulo, Direccion direccion, String descripcion,
            double precio, boolean reservaDirecta,
            int numHabitaciones, int numBanos,
            PoliticaCancelacion politicaCancelacion) {
			try {
				Inmueble inmueble = new Inmueble();
				inmueble.setPropietario(propietario);
				inmueble.setTitulo(titulo);
				inmueble.setDireccion(direccion); // Setea el objeto embebido directamente
				// inmueble.setCiudad(ciudad); <- ELIMINADO porque la localidad va dentro de direccion
				inmueble.setDescripcion(descripcion);
				inmueble.setPrecioPorNoche(precio);
				inmueble.setReservaInmediata(reservaDirecta);
				inmueble.setNumHabitaciones(numHabitaciones);
				inmueble.setNumBanos(numBanos);
				inmueble.setPoliticaCancelacion(politicaCancelacion); 
				inmuebleDAO.guardar(inmueble);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
    }

    // Sobrecarga modificada para adaptarse al nuevo objeto Direccion
    public boolean registrarInmueble(Usuario propietario,
                                     String titulo,
                                     Direccion direccion,
                                     String descripcion,
                                     double precioPorNoche,
                                     boolean reservaDirecta) {
        return registrarInmueble(propietario, titulo, direccion, descripcion,
                precioPorNoche, reservaDirecta, 0, 0, PoliticaCancelacion.REEMBOLSABLE);
    }
    
    public void eliminar(Long id) {
        Inmueble inmueble = buscarPorId(id);
        if (inmueble != null) {
            inmuebleDAO.eliminar(inmueble);
        }
    }

    public List<Inmueble> listarTodos() {
        return inmuebleDAO.listarTodos();
    }

    public List<Inmueble> buscarInmuebles(String texto) {
        return inmuebleDAO.buscarPorTexto(texto);
    }

    public Inmueble buscarPorId(Long id) {
        return inmuebleDAO.buscarPorId(id);
    }
}