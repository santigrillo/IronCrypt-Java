 package com.ironcrypt.modelo.sesiones;
  
import com.ironcrypt.modelo.entidades.Boveda;
import com.ironcrypt.modelo.entidades.MiembroEquipo;                                                             
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;                                                                        
import jakarta.persistence.NoResultException;                                                                    
import jakarta.persistence.PersistenceContext;                                                                   
import java.util.List;                                                                                           

@Stateless                                                                                                       
public class BovedaFacade extends AbstractFacade<Boveda> {                                                       

    @PersistenceContext(unitName = "IronCryptPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {                                                                 
        return em;
    }

    public BovedaFacade() {
        super(Boveda.class);
    }

    /**
     * Devuelve todas las bóvedas personales del usuario.
     */
    public List<Boveda> findByUsuario(Long usuarioId) {                                                          
        return em.createNamedQuery("Boveda.findByUsuario", Boveda.class)                                         
                 .setParameter("usuarioId", usuarioId)
                 .getResultList();
    }

    /**
     * Devuelve todas las bóvedas a las que el usuario tiene acceso:
     * 1) Sus bóvedas personales (equipo IS NULL).
     * 2) Las bóvedas de los equipos a los que pertenece.
     */
    public List<Boveda> findDisponiblesParaUsuario(Long usuarioId) {
        return em.createQuery(
            "SELECT b FROM Boveda b LEFT JOIN b.equipo eq WHERE (b.usuario.id = :usuarioId AND eq IS NULL) " +
            "OR (eq.id IN (SELECT m.equipo.id FROM MiembroEquipo m WHERE m.usuario.id = :usuarioId)) " +
            "ORDER BY b.fechaCreacion DESC", Boveda.class)
            .setParameter("usuarioId", usuarioId)
            .getResultList();
    }

    /**
     * Comprueba si un usuario tiene permisos de lectura/descifrado sobre una bóveda:
     * - Si es personal: debe ser el dueño.
     * - Si es de equipo: debe pertenecer al equipo (ADMIN o MEMBER).
     */
    public boolean canUserRead(Boveda boveda, Long usuarioId) {                                                  
        if (boveda == null || usuarioId == null) {                                                               
            return false;                                                                                        
        }                                                                                                        
        if (boveda.isPersonal()) {                                                                               
            return boveda.getUsuario().getId().equals(usuarioId);                                                
        }                                                                                                        
        // Consulta directa para verificar membresía                                                             
        Long count = em.createQuery(                                                                             
            "SELECT COUNT(m) FROM MiembroEquipo m WHERE m.equipo.id = :equipoId AND m.usuario.id = :usuarioId",  
            Long.class)
            .setParameter("equipoId", boveda.getEquipo().getId())
            .setParameter("usuarioId", usuarioId)
            .getSingleResult();
        return count != null && count > 0;
    }

    /**
     * Comprueba si un usuario tiene permisos de escritura (editar/eliminar bóveda o crear/editar ítems):        
     * - Si es personal: debe ser el dueño.
     * - Si es de equipo: debe tener rol ADMIN en dicho equipo.
     */
    public boolean canUserWrite(Boveda boveda, Long usuarioId) {
        if (boveda == null || usuarioId == null) {
            return false;
        }
        if (boveda.isPersonal()) {
            return boveda.getUsuario().getId().equals(usuarioId);
        }
        // Consulta directa para verificar rol ADMIN
        try {
            String rol = em.createQuery(
                "SELECT m.rol FROM MiembroEquipo m WHERE m.equipo.id = :equipoId AND m.usuario.id = :usuarioId", 
                String.class)
                .setParameter("equipoId", boveda.getEquipo().getId())
                .setParameter("usuarioId", usuarioId)
                .getSingleResult();
            return MiembroEquipo.ROL_ADMIN.equalsIgnoreCase(rol);
        } catch (NoResultException e) {
            return false;
        }
    }
}
