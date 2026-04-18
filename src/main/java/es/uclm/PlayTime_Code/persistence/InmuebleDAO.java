package es.uclm.PlayTime_Code.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import es.uclm.PlayTime_Code.business.entity.Inmueble;

import java.util.List;

@Repository
@Transactional
public class InmuebleDAO {

    @PersistenceContext
    private EntityManager em;

    public void guardar(Inmueble inmueble) {
        em.persist(inmueble);
    }
    
    @Transactional
    public void eliminar(Inmueble inmueble) {
        em.remove(em.contains(inmueble) ? inmueble : em.merge(inmueble));
    }


    public List<Inmueble> listarTodos() {
        return em.createQuery("SELECT i FROM Inmueble i", Inmueble.class).getResultList();
    }

    public List<Inmueble> buscarPorTexto(String texto) {
        return em.createQuery("""
            SELECT i FROM Inmueble i
            WHERE LOWER(i.titulo) LIKE LOWER(CONCAT('%', :t, '%'))
               OR LOWER(i.direccion) LIKE LOWER(CONCAT('%', :t, '%'))
               OR LOWER(i.descripcion) LIKE LOWER(CONCAT('%', :t, '%'))
        """, Inmueble.class)
        .setParameter("t", texto)
        .getResultList();
    }
    
    public Inmueble buscarPorId(Long id) {
        return em.find(Inmueble.class, id);
    }
}
