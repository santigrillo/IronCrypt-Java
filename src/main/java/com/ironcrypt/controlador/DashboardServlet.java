package com.ironcrypt.controlador;                                                         

 import com.ironcrypt.modelo.entidades.Boveda;                                              
 import com.ironcrypt.modelo.entidades.Equipo;                                              
 import com.ironcrypt.modelo.entidades.ItemBoveda;                                          
 import com.ironcrypt.modelo.entidades.MiembroEquipo;                                       
 import com.ironcrypt.modelo.entidades.Usuario;                                             
 import com.ironcrypt.modelo.sesiones.BovedaFacade;                                         
 import com.ironcrypt.modelo.sesiones.EquipoFacade;                                         
 import com.ironcrypt.modelo.sesiones.ItemBovedaFacade;                                     
 import jakarta.ejb.EJB;                                                                    
 import jakarta.servlet.ServletException;                                                   
 import jakarta.servlet.annotation.WebServlet;                                              
 import jakarta.servlet.http.HttpServlet;                                                   
 import jakarta.servlet.http.HttpServletRequest;                                            
 import jakarta.servlet.http.HttpServletResponse;                                           
 import jakarta.servlet.http.HttpSession;                                                   
 import java.io.IOException;                                                                
 import java.util.HashMap;                                                                  
 import java.util.List;                                                                     
 import java.util.Map;                                                                      

 @WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})                       
 public class DashboardServlet extends HttpServlet {                                        

     @EJB                                                                                   
     private BovedaFacade bovedaFacade;                                                     

     @EJB                                                                                   
     private ItemBovedaFacade itemFacade;                                                   

     @EJB                                                                                   
     private EquipoFacade equipoFacade;                                                     

     @Override                                                                              
     protected void doGet(HttpServletRequest request, HttpServletResponse response)         
             throws ServletException, IOException {                                         
         HttpSession session = request.getSession(false);                                   
         if (session == null || session.getAttribute("usuario") == null) {                  
             response.sendRedirect(request.getContextPath() + "/login");                    
             return;                                                                        
         }                                                                                  

         Usuario usuario = (Usuario) session.getAttribute("usuario");                       

         // 1. Cargar bóvedas accesibles (personales + equipos a los que pertenece)         
         List<Boveda> bovedas = bovedaFacade.findDisponiblesParaUsuario(usuario.getId());   

         // 2. Cargar ítems por bóveda y permisos de escritura por bóveda                   
         Map<Long, List<ItemBoveda>> itemsPorBoveda = new HashMap<>();                      
         Map<Long, Boolean> permisosEscrituraPorBoveda = new HashMap<>();                   

         for (Boveda b : bovedas) {                                                         
             itemsPorBoveda.put(b.getId(), itemFacade.findByBoveda(b.getId()));             
             permisosEscrituraPorBoveda.put(b.getId(), bovedaFacade.canUserWrite(b,         
             usuario.getId()));
         }

         // 3. Cargar equipos del usuario y equipos donde es ADMIN
         List<Equipo> misEquipos = equipoFacade.findEquiposPorUsuario(usuario.getId());     
         List<Equipo> equiposAdmin =
         equipoFacade.findEquiposAdminPorUsuario(usuario.getId());

         // 4. Cargar miembros de cada equipo y rol del usuario actual en cada uno          
         Map<Long, List<MiembroEquipo>> miembrosPorEquipo = new HashMap<>();
         Map<Long, Boolean> esAdminEnEquipo = new HashMap<>();

         for (Equipo eq : misEquipos) {
             miembrosPorEquipo.put(eq.getId(),
             equipoFacade.findMiembrosPorEquipo(eq.getId()));
             esAdminEnEquipo.put(eq.getId(), equipoFacade.esAdmin(eq.getId(),
             usuario.getId()));
         }

         request.setAttribute("usuarioActual", usuario);
         request.setAttribute("bovedas", bovedas);
         request.setAttribute("itemsPorBoveda", itemsPorBoveda);
         request.setAttribute("permisosEscrituraPorBoveda", permisosEscrituraPorBoveda);    
         request.setAttribute("misEquipos", misEquipos);
         request.setAttribute("equiposAdmin", equiposAdmin);
         request.setAttribute("miembrosPorEquipo", miembrosPorEquipo);
         request.setAttribute("esAdminEnEquipo", esAdminEnEquipo);

         request.getRequestDispatcher("/WEB-INF/vista/dashboard.jsp").forward(request,      
         response);
     }
 }
