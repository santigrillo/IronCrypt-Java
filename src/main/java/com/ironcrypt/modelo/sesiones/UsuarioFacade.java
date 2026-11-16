/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author santiagogrillo
 */
 package com.ironcrypt.modelo.sesiones;                                                 
                                                                                           
    import com.ironcrypt.modelo.entidades.Usuario;                                         
    import jakarta.ejb.Stateless;                                                          
    import jakarta.persistence.EntityManager;                                              
    import jakarta.persistence.NoResultException;                                          
    import jakarta.persistence.PersistenceContext;                                         
                                                                                           
    @Stateless                                                                             
    public class UsuarioFacade extends AbstractFacade<Usuario> {                           
                                                                                           
        @PersistenceContext(unitName = "IronCryptPU")                                      
        private EntityManager em;                                                          
                                                                                           
        @Override                                                                          
        protected EntityManager getEntityManager() {                                       
            return em;                                                                     
        }                                                                                  
                                                                                           
        public UsuarioFacade() {                                                           
            super(Usuario.class);                                                          
        }                                                                                  
                                                                                           
        /**                                                                                
         * Busca un usuario por su correo electrónico único.                               
         * Retorna null si no existe.                                                      
         */                                                                                
        public Usuario findByEmail(String email) {                                         
            try {                                                                          
                return em.createNamedQuery("Usuario.findByEmail", Usuario.class)           
                         .setParameter("email", email.trim().toLowerCase())                
                         .getSingleResult();                                               
            } catch (NoResultException e) {                                                
                return null;                                                               
            }                                                                              
        }                                                                                  
    }  
