/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ironcrypt.modelo.sesiones;

/**
 *
 * @author santiagogrillo
 */
 import com.ironcrypt.modelo.entidades.Equipo;                                                                    
import com.ironcrypt.modelo.entidades.MiembroEquipo;                                                             
import com.ironcrypt.modelo.entidades.Usuario;                                                                   
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;                                                                                           

@Stateless
public class EquipoFacade extends AbstractFacade<Equipo> {                                                       

    @PersistenceContext(unitName = "IronCryptPU")
    private EntityManager em;                                                                                    

    @Override                                                                                                    
    protected EntityManager getEntityManager() {                                                                 
        return em;
    }

    public EquipoFacade() {                                                                                      
        super(Equipo.class);
    }

    /**
     * Crea un nuevo equipo y automáticamente asocia al creador con rol ADMIN.
     */                                                                                                          
    public Equipo crearEquipo(String nombre, Usuario creador) {                                                  
        Equipo equipo = new Equipo(nombre.trim(), creador);                                                      
        em.persist(equipo);                                                                                      
        em.flush();

        MiembroEquipo admin = new MiembroEquipo(equipo, creador, MiembroEquipo.ROL_ADMIN);                       
        em.persist(admin);
        em.flush();

        return equipo;
    }

    /**
     * Retorna todos los equipos a los que pertenece el usuario (como ADMIN o MEMBER).
     */                                                                                                          
    public List<Equipo> findEquiposPorUsuario(Long usuarioId) {                                                  
        return em.createQuery(                                                                                   
            "SELECT m.equipo FROM MiembroEquipo m WHERE m.usuario.id = :usuarioId ORDER BY m.equipo.nombre ASC", 
            Equipo.class)
            .setParameter("usuarioId", usuarioId)
            .getResultList();
    }

    /**
     * Retorna los equipos donde el usuario es ADMIN (para saber en cuáles puede crear bóvedas).
     */
    public List<Equipo> findEquiposAdminPorUsuario(Long usuarioId) {
        return em.createQuery(
            "SELECT m.equipo FROM MiembroEquipo m WHERE m.usuario.id = :usuarioId AND m.rol = :rolAdmin ORDER BY m.equipo.nombre ASC", 
            Equipo.class)         
            .setParameter("usuarioId", usuarioId)
            .setParameter("rolAdmin", MiembroEquipo.ROL_ADMIN)
            .getResultList();
    }


    /**
     * Retorna todos los miembros de un equipo ordenados por rol y nombre.
     */                                                                                                          
    public List<MiembroEquipo> findMiembrosPorEquipo(Long equipoId) {                                            
        return em.createNamedQuery("MiembroEquipo.findByEquipo", MiembroEquipo.class)                            
                 .setParameter("equipoId", equipoId)
                 .getResultList();
    }

    /**
     * Busca la membresía de un usuario en un equipo. Retorna null si no pertenece.
     */
    public MiembroEquipo findMembresia(Long equipoId, Long usuarioId) {
        try {                                                                                                    
            return em.createNamedQuery("MiembroEquipo.findByEquipoYUsuario", MiembroEquipo.class)
                     .setParameter("equipoId", equipoId)
                     .setParameter("usuarioId", usuarioId)
                     .getSingleResult();
        } catch (NoResultException e) {                                                                          
            return null;
        }
    }

    /**
     * Valida si un usuario pertenece a un equipo.
     */
    public boolean esMiembro(Long equipoId, Long usuarioId) {
        return findMembresia(equipoId, usuarioId) != null;                                                       
    }

    /**
     * Valida si un usuario es ADMIN del equipo.
     */                                                                                                          
    public boolean esAdmin(Long equipoId, Long usuarioId) {                                                      
        MiembroEquipo membresia = findMembresia(equipoId, usuarioId);                                            
        return membresia != null && membresia.isAdmin();
    }

    /**
     * Agrega un nuevo miembro a un equipo si aún no pertenece.
     */
    public boolean agregarMiembro(Long equipoId, Usuario usuarioNuevo, String rol) {
        if (esMiembro(equipoId, usuarioNuevo.getId())) {
            return false;                                                                                        
        }

        Equipo equipo = find(equipoId);                                                                          
        if (equipo == null) {                                                                                    
            return false;                                                                                        
        }

        MiembroEquipo nuevo = new MiembroEquipo(equipo, usuarioNuevo, rol);
        em.persist(nuevo);
        em.flush();                                                                                              
        return true;                                                                                             
    }

    /**
     * Actualiza el rol de un integrante existente.
     */
    public boolean cambiarRol(Long equipoId, Long usuarioId, String nuevoRol) {
        MiembroEquipo membresia = findMembresia(equipoId, usuarioId);                                            
        if (membresia == null) {                                                                                 
            return false;                                                                                        
        }                                                                                                        
        membresia.setRol(nuevoRol);
        em.merge(membresia);                                                                                     
        return true;                                                                                             
    }

    /**
     * Remueve a un usuario de un equipo.
     */
    public boolean eliminarMiembro(Long equipoId, Long usuarioId) {
        MiembroEquipo membresia = findMembresia(equipoId, usuarioId);
        if (membresia == null) {
            return false;
        }
        em.remove(membresia);                                                                                    
        return true;                                                                                             
    }                                                                                                            
}
