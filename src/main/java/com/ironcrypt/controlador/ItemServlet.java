package com.ironcrypt.controlador;                                                         

import com.ironcrypt.modelo.entidades.Boveda;                                              
import com.ironcrypt.modelo.entidades.ItemBoveda;                                          
import com.ironcrypt.modelo.entidades.Usuario;                                             
import com.ironcrypt.modelo.sesiones.BovedaFacade;                                         
import com.ironcrypt.modelo.sesiones.ItemBovedaFacade;                                     
import com.ironcrypt.seguridad.CryptoService;                                              
import com.ironcrypt.seguridad.CryptoService.ResultadoCifrado;                             
import jakarta.ejb.EJB;                                                                    
import jakarta.servlet.ServletException;                                                   
import jakarta.servlet.annotation.WebServlet;                                              
import jakarta.servlet.http.HttpServlet;                                                   
import jakarta.servlet.http.HttpServletRequest;                                            
import jakarta.servlet.http.HttpServletResponse;                                           
import jakarta.servlet.http.HttpSession;                                                   
import java.io.IOException;                                                                
import java.io.PrintWriter;                                                                

@WebServlet(name = "ItemServlet", urlPatterns = {"/items/crear", "/items/editar",          
"/items/revelar", "/items/eliminar"})                                                      
public class ItemServlet extends HttpServlet {                                             

    @EJB                                                                                   
    private BovedaFacade bovedaFacade;                                                     

    @EJB                                                                                   
    private ItemBovedaFacade itemFacade;                                                   

    @Override                                                                              
    protected void doPost(HttpServletRequest request, HttpServletResponse response)        
            throws ServletException, IOException {                                         
        HttpSession session = request.getSession(false);                                   
        if (session == null || session.getAttribute("usuario") == null) {                  
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);                       
            return;                                                                        
        }                                                                                  

        Usuario usuario = (Usuario) session.getAttribute("usuario");                       
        String path = request.getServletPath();                                            

        switch (path) {                                                                    
            case "/items/crear":                                                           
                crearItem(request, response, session, usuario);                            
                break;                                                                     
            case "/items/editar":                                                          
                editarItem(request, response, session, usuario);                           
                break;                                                                     
            case "/items/revelar":                                                         
                revelarPassword(request, response, session, usuario);                      
                break;                                                                     
            case "/items/eliminar":                                                        
                eliminarItem(request, response, usuario);                                  
                break;                                                                     
            default:                                                                       
                response.sendRedirect(request.getContextPath() + "/dashboard");            
                break;                                                                     
        }                                                                                  
    }                                                                                      

    private void crearItem(HttpServletRequest request, HttpServletResponse response,       
                           HttpSession session, Usuario usuario) throws IOException {      
        String bovedaIdStr = request.getParameter("bovedaId");                             
        String titulo = request.getParameter("titulo");                                    
        String websiteUrl = request.getParameter("websiteUrl");                            
        String username = request.getParameter("username");                                
        String rawPassword = request.getParameter("password");                             
        String customPassphrase = request.getParameter("passphrase");                      
        String notas = request.getParameter("notas");                                      

        if (bovedaIdStr == null || titulo == null || rawPassword == null ||                
            titulo.trim().isEmpty() || rawPassword.isEmpty()) {                            
            response.sendRedirect(request.getContextPath() + "/dashboard");                
            return;                                                                        
        }                                                                                  

        try {                                                                              
            Long bovedaId = Long.valueOf(bovedaIdStr);                                     
            Boveda boveda = bovedaFacade.find(bovedaId);                                   

            // Control RBAC: Debe tener permisos de escritura (Dueño o Admin del equipo)   
            if (boveda != null && bovedaFacade.canUserWrite(boveda, usuario.getId())) {    
                String claveParaCifrar = (customPassphrase != null &&                      
                !customPassphrase.trim().isEmpty())                                        
                        ? customPassphrase.trim()                                          
                        : (String) session.getAttribute("masterKey");                      

                if (claveParaCifrar != null) {                                             
                    ResultadoCifrado resultado = CryptoService.cifrar(rawPassword,         
                    claveParaCifrar);                                                      

                    ItemBoveda nuevoItem = new ItemBoveda(                                 
                        boveda,                                                            
                        titulo.trim(),                                                     
                        websiteUrl != null && !websiteUrl.trim().isEmpty() ?               
                        websiteUrl.trim() : null,                                          
                        username != null && !username.trim().isEmpty() ? username.trim() : 
                        null,                                                              
                        resultado.getTextoCifrado(),                                       
                        resultado.getIvNonce(),                                            
                        resultado.getSalt(),                                               
                        notas != null && !notas.trim().isEmpty() ? notas.trim() : null     
                    );                                                                     
                    itemFacade.create(nuevoItem);                                          
                }                                                                          
            }                                                                              
        } catch (Exception e) {                                                            
            e.printStackTrace();                                                           
        }                                                                                  
        response.sendRedirect(request.getContextPath() + "/dashboard");                    
    }                                                                                      

    private void editarItem(HttpServletRequest request, HttpServletResponse response,      
                            HttpSession session, Usuario usuario) throws IOException {     
        String itemIdStr = request.getParameter("id");                                     
        String titulo = request.getParameter("titulo");                                    
        String websiteUrl = request.getParameter("websiteUrl");                            
        String username = request.getParameter("username");                                
        String rawPassword = request.getParameter("password");                             
        String customPassphrase = request.getParameter("passphrase");                      
        String notas = request.getParameter("notas");                                      

        if (itemIdStr == null || titulo == null || titulo.trim().isEmpty()) {              
            response.sendRedirect(request.getContextPath() + "/dashboard");                
            return;                                                                        
        }                                                                                  

        try {                                                                              
            Long itemId = Long.valueOf(itemIdStr);                                         
            ItemBoveda item = itemFacade.find(itemId);                                     

            // Control RBAC: Debe tener permisos de escritura sobre la bóveda              
            if (item != null && bovedaFacade.canUserWrite(item.getBoveda(),                
            usuario.getId())) {                                                            
                item.setTitulo(titulo.trim());                                             
                item.setWebsiteUrl(websiteUrl != null && !websiteUrl.trim().isEmpty() ?    
                websiteUrl.trim() : null);                                                 
                item.setUsername(username != null && !username.trim().isEmpty() ?          
                username.trim() : null);                                                   
                item.setNotas(notas != null && !notas.trim().isEmpty() ? notas.trim() :    
                null);                                                                     
                item.setFechaActualizacion(new java.util.Date());                          

                if (rawPassword != null && !rawPassword.trim().isEmpty()) {                
                    String claveParaCifrar = (customPassphrase != null &&                  
                    !customPassphrase.trim().isEmpty())                                    
                            ? customPassphrase.trim()                                      
                            : (String) session.getAttribute("masterKey");                  

                    if (claveParaCifrar != null) {                                         
                        ResultadoCifrado resultado = CryptoService.cifrar(rawPassword,     
                        claveParaCifrar);                                                  
                        item.setPasswordCifrada(resultado.getTextoCifrado());              
                        item.setIvNonce(resultado.getIvNonce());                           
                        item.setSalt(resultado.getSalt());                                 
                    }                                                                      
                }                                                                          

                itemFacade.edit(item);                                                     
            }                                                                              
        } catch (Exception e) {                                                            
            e.printStackTrace();                                                           
        }                                                                                  
        response.sendRedirect(request.getContextPath() + "/dashboard");                    
    }                                                                                      

    private void revelarPassword(HttpServletRequest request, HttpServletResponse response, 
                                 HttpSession session, Usuario usuario) throws IOException {
        response.setContentType("application/json");                                       
        response.setCharacterEncoding("UTF-8");                                            
        PrintWriter out = response.getWriter();                                            

        String itemIdStr = request.getParameter("itemId");                                 
        String customPassphrase = request.getParameter("passphrase");                      

        if (itemIdStr == null) {                                                           
            out.print("{\"ok\":false,\"error\":\"ID inválido\"}");                         
            return;                                                                        
        }                                                                                  

        try {                                                                              
            Long itemId = Long.valueOf(itemIdStr);                                         
            ItemBoveda item = itemFacade.find(itemId);                                     

            // Control RBAC: Cualquier miembro del equipo o dueño personal puede revelar   
            if (item == null || !bovedaFacade.canUserRead(item.getBoveda(), usuario.getId())) {
                out.print("{\"ok\":false,\"error\":\"Acceso denegado o ítem inexistente\"}");
                return;
            }                                                                              

            String claveParaDescifrar = (customPassphrase != null &&                       
            !customPassphrase.trim().isEmpty())                                            
                    ? customPassphrase.trim()                                              
                    : (String) session.getAttribute("masterKey");                          

            if (claveParaDescifrar == null || claveParaDescifrar.isEmpty()) {              
                out.print("{\"ok\":false,\"requiresPassphrase\":true}");                   
                return;                                                                    
            }                                                                              

            String textoPlano = CryptoService.descifrar(                                   
                item.getPasswordCifrada(),                                                 
                item.getIvNonce(),                                                         
                item.getSalt(),                                                            
                claveParaDescifrar                                                         
            );                                                                             

            if (textoPlano == null) {                                                      
                out.print("{\"ok\":false,\"error\":\"Frase de paso incorrecta\"}");        
            } else {                                                                       
                String safeJsonPass = textoPlano                                           
                        .replace("\\", "\\\\")                                             
                        .replace("\"", "\\\"")                                             
                        .replace("\b", "\\b")                                              
                        .replace("\f", "\\f")                                              
                        .replace("\n", "\\n")                                              
                        .replace("\r", "\\r")                                              
                        .replace("\t", "\\t");                                             
                out.print("{\"ok\":true,\"password\":\"" + safeJsonPass + "\"}");          
            }                                                                              
        } catch (Exception e) {                                                            
            out.print("{\"ok\":false,\"error\":\"Error interno al procesar el descifrado\"}");                                                               
        }                                                                                  
    }                                                                                      

    private void eliminarItem(HttpServletRequest request, HttpServletResponse response,    
    Usuario usuario)                                                                       
            throws IOException {                                                           
        String idStr = request.getParameter("id");                                         
        if (idStr != null) {                                                               
            try {                                                                          
                Long id = Long.valueOf(idStr);                                             
                ItemBoveda item = itemFacade.find(id);                                     
                // Control RBAC: Solo dueño personal o Admin de equipo                     
                if (item != null && bovedaFacade.canUserWrite(item.getBoveda(),            
                usuario.getId())) {                                                        
                    itemFacade.remove(item);                                               
                }                                                                          
            } catch (NumberFormatException ignored) {}                                     
        }                                                                                  
        response.sendRedirect(request.getContextPath() + "/dashboard");                    
    }                                                                                      
}     