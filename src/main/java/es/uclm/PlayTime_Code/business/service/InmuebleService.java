package es.uclm.PlayTime_Code.business.service;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.uclm.PlayTime_Code.business.entity.Inmueble;
import es.uclm.PlayTime_Code.business.entity.Usuario;
import es.uclm.PlayTime_Code.persistence.InmuebleDAO;

import java.util.List;

@Service
public class InmuebleService {

    @Autowired
    private InmuebleDAO inmuebleDAO;


    


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