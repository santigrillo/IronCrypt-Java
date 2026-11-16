/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

/**
 *
 * @author santiagogrillo
 */
package com.ironcrypt.controlador;                                                         
                                                                                               
import com.ironcrypt.modelo.entidades.Equipo;                                              
import com.ironcrypt.modelo.entidades.MiembroEquipo;                                       
import com.ironcrypt.modelo.entidades.Usuario;                                             
import com.ironcrypt.modelo.sesiones.EquipoFacade;                                         
import com.ironcrypt.modelo.sesiones.UsuarioFacade;                                        
import jakarta.ejb.EJB;                                                                    
import jakarta.servlet.ServletException;                                                   
import jakarta.servlet.annotation.WebServlet;                                              
import jakarta.servlet.http.HttpServlet;                                                   
import jakarta.servlet.http.HttpServletRequest;                                            
import jakarta.servlet.http.HttpServletResponse;                                           
import jakarta.servlet.http.HttpSession;                                                   
import java.io.IOException;                                                                
                                                                                               
@WebServlet(name = "EquipoServlet", urlPatterns = {                                        
    "/equipos/crear",                                                                      
    "/equipos/miembros/agregar",                                                           
    "/equipos/miembros/rol",                                                               
    "/equipos/miembros/eliminar"                                                           
})                                                                                         
public class EquipoServlet extends HttpServlet {                                           

    @EJB                                                                                   
    private EquipoFacade equipoFacade;                                                     

    @EJB                                                                                   
    private UsuarioFacade usuarioFacade;                                                   

    @Override                                                                              
    protected void doPost(HttpServletRequest request, HttpServletResponse response)        
            throws ServletException, IOException {                                         
        HttpSession session = request.getSession(false);                                   
        if (session == null || session.getAttribute("usuario") == null) {                  
            response.sendRedirect(request.getContextPath() + "/login");                    
            return;                                                                        
        }                                                                                  

        Usuario usuarioActual = (Usuario) session.getAttribute("usuario");                 
        String path = request.getServletPath();                                            

        switch (path) {                                                                    
            case "/equipos/crear":                                                         
                crearEquipo(request, response, usuarioActual);                             
                break;                                                                     
            case "/equipos/miembros/agregar":                                              
                agregarMiembro(request, response, usuarioActual);                          
                break;                                                                     
            case "/equipos/miembros/rol":                                                  
                cambiarRol(request, response, usuarioActual);                              
                break;                                                                     
            case "/equipos/miembros/eliminar":                                             
                eliminarMiembro(request, response, usuarioActual);                         
                break;                                                                     
            default:                                                                       
                response.sendRedirect(request.getContextPath() + "/dashboard");            
                break;                                                                     
        }                                                                                  
    }                                                                                      

    private void crearEquipo(HttpServletRequest request, HttpServletResponse response,     
    Usuario usuario)                                                                       
            throws IOException {                                                           
        String nombre = request.getParameter("nombre");                                    
        if (nombre != null && !nombre.trim().isEmpty()) {                                  
            equipoFacade.crearEquipo(nombre.trim(), usuario);                              
        }                                                                                  
        response.sendRedirect(request.getContextPath() + "/dashboard?tab=equipos");        
    }                                                                                      

    private void agregarMiembro(HttpServletRequest request, HttpServletResponse response,  
    Usuario adminActual)                                                                   
            throws IOException {                                                           
        String equipoIdStr = request.getParameter("equipoId");                             
        String email = request.getParameter("email");                                      
        String rol = request.getParameter("rol");                                          

        if (equipoIdStr != null && email != null && !email.trim().isEmpty()) {             
            try {                                                                          
                Long equipoId = Long.valueOf(equipoIdStr);                                 

                // Control RBAC: Solo ADMIN puede agregar miembros                         
                if (equipoFacade.esAdmin(equipoId, adminActual.getId())) {                 
                    Usuario usuarioNuevo = usuarioFacade.findByEmail(email.trim());        
                    if (usuarioNuevo != null) {                                            
                        String rolFinal = (rol != null && rol.equalsIgnoreCase("ADMIN"))   
                                ? MiembroEquipo.ROL_ADMIN                                  
                                : MiembroEquipo.ROL_MEMBER;                                
                        equipoFacade.agregarMiembro(equipoId, usuarioNuevo, rolFinal);     
                    }                                                                      
                }                                                                          
            } catch (NumberFormatException ignored) {}                                     
        }                                                                                  
        response.sendRedirect(request.getContextPath() + "/dashboard?tab=equipos");        
    }                                                                                      

    private void cambiarRol(HttpServletRequest request, HttpServletResponse response,      
    Usuario adminActual)                                                                   
            throws IOException {                                                           
        String equipoIdStr = request.getParameter("equipoId");                             
        String usuarioIdStr = request.getParameter("usuarioId");                           
        String nuevoRol = request.getParameter("rol");                                     

        if (equipoIdStr != null && usuarioIdStr != null && nuevoRol != null) {             
            try {                                                                          
                Long equipoId = Long.valueOf(equipoIdStr);                                 
                Long usuarioId = Long.valueOf(usuarioIdStr);                               

                // No permitir quitarse el rol de admin a uno mismo para no dejar el equipo huerfano
                if (equipoFacade.esAdmin(equipoId, adminActual.getId()) && !adminActual.getId().equals(usuarioId)) {                                  
                    String rolFinal = nuevoRol.equalsIgnoreCase("ADMIN") ?                 
                    MiembroEquipo.ROL_ADMIN : MiembroEquipo.ROL_MEMBER;                    
                    equipoFacade.cambiarRol(equipoId, usuarioId, rolFinal);                
                }                                                                          
            } catch (NumberFormatException ignored) {}                                     
        }                                                                                  
        response.sendRedirect(request.getContextPath() + "/dashboard?tab=equipos");        
    }                                                                                      

    private void eliminarMiembro(HttpServletRequest request, HttpServletResponse response, 
    Usuario usuarioActual)                                                                 
            throws IOException {                                                           
        String equipoIdStr = request.getParameter("equipoId");                             
        String usuarioIdStr = request.getParameter("usuarioId");                           

        if (equipoIdStr != null && usuarioIdStr != null) {                                 
            try {                                                                          
                Long equipoId = Long.valueOf(equipoIdStr);                                 
                Long usuarioId = Long.valueOf(usuarioIdStr);                               

                // Un admin puede eliminar a cualquiera; un miembro puede eliminarse a si mismo (abandonar equipo)
                boolean esAdmin = equipoFacade.esAdmin(equipoId, usuarioActual.getId());   
                boolean esAutoBaja = usuarioActual.getId().equals(usuarioId);              

                if (esAdmin || esAutoBaja) {                                               
                    equipoFacade.eliminarMiembro(equipoId, usuarioId);                     
                }                                                                          
            } catch (NumberFormatException ignored) {}                                     
        }                                                                                  
        response.sendRedirect(request.getContextPath() + "/dashboard?tab=equipos");        
    }                                                                                      
}             