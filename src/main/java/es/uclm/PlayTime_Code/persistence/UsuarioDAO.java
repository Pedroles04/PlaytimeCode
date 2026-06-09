package es.uclm.PlayTime_Code.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import es.uclm.PlayTime_Code.business.entity.Usuario;

@Repository
public class UsuarioDAO {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void guardar(Usuario usuario) {
        em.merge(usuario);
    }

    public Usuario buscarPorLogin(String login) {
        try {
            return em.createQuery("SELECT u FROM Usuario u WHERE u.login = :login", Usuario.class)
                    .setParameter("login", login)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean existeLogin(String login) {
        Long count = em.createQuery("SELECT COUNT(u) FROM Usuario u WHERE u.login = :login", Long.class)
                .setParameter("login", login)
                .getSingleResult();
        return count > 0;
    }
}

