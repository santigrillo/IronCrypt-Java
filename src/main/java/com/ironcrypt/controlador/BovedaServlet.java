  package com.ironcrypt.controlador;                                                         
                                                                                               
    import com.ironcrypt.modelo.entidades.Boveda;                                              
    import com.ironcrypt.modelo.entidades.Equipo;                                              
    import com.ironcrypt.modelo.entidades.Usuario;                                             
    import com.ironcrypt.modelo.sesiones.BovedaFacade;                                         
    import com.ironcrypt.modelo.sesiones.EquipoFacade;                                         
    import jakarta.ejb.EJB;                                                                    
    import jakarta.servlet.ServletException;                                                   
    import jakarta.servlet.annotation.WebServlet;                                              
    import jakarta.servlet.http.HttpServlet;                                                   
    import jakarta.servlet.http.HttpServletRequest;                                            
    import jakarta.servlet.http.HttpServletResponse;                                           
    import jakarta.servlet.http.HttpSession;                                                   
    import java.io.IOException;                                                                
                                                                                               
    @WebServlet(name = "BovedaServlet", urlPatterns = {"/bovedas/crear", "/bovedas/editar",    
    "/bovedas/eliminar"})                                                                      
    public class BovedaServlet extends HttpServlet {                                           
                                                                                               
        @EJB                                                                                   
        private BovedaFacade bovedaFacade;                                                     
                                                                                               
        @EJB                                                                                   
        private EquipoFacade equipoFacade;                                                     
                                                                                               
        @Override                                                                              
        protected void doPost(HttpServletRequest request, HttpServletResponse response)        
                throws ServletException, IOException {                                         
            HttpSession session = request.getSession(false);                                   
            if (session == null || session.getAttribute("usuario") == null) {                  
                response.sendRedirect(request.getContextPath() + "/login");                    
                return;                                                                        
            }                                                                                  
                                                                                               
            Usuario usuario = (Usuario) session.getAttribute("usuario");                       
            String path = request.getServletPath();                                            
                                                                                               
            switch (path) {                                                                    
                case "/bovedas/crear":                                                         
                    crearBoveda(request, response, usuario);                                   
                    break;                                                                     
                case "/bovedas/editar":                                                        
                    editarBoveda(request, response, usuario);                                  
                    break;                                                                     
                case "/bovedas/eliminar":                                                      
                    eliminarBoveda(request, response, usuario);                                
                    break;                                                                     
                default:                                                                       
                    response.sendRedirect(request.getContextPath() + "/dashboard");            
                    break;                                                                     
            }                                                                                  
        }                                                                                      
                                                                                               
        private void crearBoveda(HttpServletRequest request, HttpServletResponse response,     
        Usuario usuario)                                                                       
                throws IOException {                                                           
            String nombre = request.getParameter("nombre");                                    
            String descripcion = request.getParameter("descripcion");                          
            String equipoIdStr = request.getParameter("equipoId");                             
                                                                                               
            if (nombre != null && !nombre.trim().isEmpty()) {                                  
                Equipo equipoAsignado = null;                                                  
                                                                                               
                if (equipoIdStr != null && !equipoIdStr.trim().isEmpty()) {                    
                    try {                                                                      
                        Long eqId = Long.valueOf(equipoIdStr);                                 
                        // Solo un ADMIN del equipo puede crear bóvedas en él                  
                        if (equipoFacade.esAdmin(eqId, usuario.getId())) {                     
                            equipoAsignado = equipoFacade.find(eqId);                          
                        }                                                                      
                    } catch (NumberFormatException ignored) {}                                 
                }                                                                              
                                                                                               
                Boveda nuevaBoveda = new Boveda(nombre.trim(), descripcion != null ?           
                descripcion.trim() : null, usuario, equipoAsignado);                           
                bovedaFacade.create(nuevaBoveda);                                              
            }                                                                                  
            response.sendRedirect(request.getContextPath() + "/dashboard");                    
        }                                                                                      
                                                                                               
        private void editarBoveda(HttpServletRequest request, HttpServletResponse response,    
        Usuario usuario)                                                                       
                throws IOException {                                                           
            String idStr = request.getParameter("id");                                         
            String nombre = request.getParameter("nombre");                                    
            String descripcion = request.getParameter("descripcion");                          
                                                                                               
            if (idStr != null && nombre != null && !nombre.trim().isEmpty()) {                 
                try {                                                                          
                    Long id = Long.valueOf(idStr);                                             
                    Boveda boveda = bovedaFacade.find(id);                                     
                    // Control RBAC: validar permiso de escritura                              
                    if (boveda != null && bovedaFacade.canUserWrite(boveda, usuario.getId())) {
                        boveda.setNombre(nombre.trim());                                       
                        boveda.setDescripcion(descripcion != null ? descripcion.trim() : null);
                        bovedaFacade.edit(boveda);                                             
                    }                                                                          
                } catch (NumberFormatException ignored) {}                                     
            }                                                                                  
            response.sendRedirect(request.getContextPath() + "/dashboard");                    
        }                                                                                      
                                                                                               
        private void eliminarBoveda(HttpServletRequest request, HttpServletResponse response,  
        Usuario usuario)                                                                       
                throws IOException {                                                           
            String idStr = request.getParameter("id");                                         
            if (idStr != null) {                                                               
                try {                                                                          
                    Long id = Long.valueOf(idStr);                                             
                    Boveda boveda = bovedaFacade.find(id);                                     
                    // Control RBAC: validar permiso de escritura                              
                    if (boveda != null && bovedaFacade.canUserWrite(boveda, usuario.getId())) {
                        bovedaFacade.remove(boveda);                                           
                    }                                                                          
                } catch (NumberFormatException ignored) {}                                     
            }                                                                                  
            response.sendRedirect(request.getContextPath() + "/dashboard");                    
        }                                                                                      
    }    