package com.ironcrypt.controlador;

import com.ironcrypt.modelo.entidades.Usuario;
import com.ironcrypt.modelo.sesiones.UsuarioFacade;
import com.ironcrypt.seguridad.PasswordHasher;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "AuthServlet", urlPatterns = {"/login", "/registro", "/logout"})
public class AuthServlet extends HttpServlet {

    @EJB
    private UsuarioFacade usuarioFacade;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        switch (path) {
            case "/login":
                if (request.getSession().getAttribute("usuario") != null) {
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                    return;
                }
                request.getRequestDispatcher("/WEB-INF/vista/login.jsp").forward(request, response);
                break;

            case "/registro":
                if (request.getSession().getAttribute("usuario") != null) {
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                    return;
                }
                request.getRequestDispatcher("/WEB-INF/vista/registro.jsp").forward(request, response);
                break;

            case "/logout":
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
                response.sendRedirect(request.getContextPath() + "/login?logout=true");
                break;

            default:
                response.sendRedirect(request.getContextPath() + "/");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/login".equals(path)) {
            procesarLogin(request, response);
        } else if ("/registro".equals(path)) {
            procesarRegistro(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void procesarLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("error", "Por favor, completá todos los campos.");
            request.getRequestDispatcher("/WEB-INF/vista/login.jsp").forward(request, response);
            return;
        }

        Usuario usuario = usuarioFacade.findByEmail(email);

        if (usuario != null && PasswordHasher.check(password, usuario.getPasswordHash())) {
            HttpSession session = request.getSession(true);
            session.setAttribute("usuario", usuario);
            session.setAttribute("masterKey", password);
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            request.setAttribute("error", "Correo o contraseña incorrectos.");
            request.setAttribute("emailValue", email);
            request.getRequestDispatcher("/WEB-INF/vista/login.jsp").forward(request, response);
        }
    }

    private void procesarRegistro(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nombre = request.getParameter("nombreCompleto");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (nombre == null || nombre.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.isEmpty()) {
            request.setAttribute("error", "Todos los campos son obligatorios.");
            request.getRequestDispatcher("/WEB-INF/vista/registro.jsp").forward(request, response);
            return;
        }

        if (password.length() < 6) {
            request.setAttribute("error", "La contraseña debe tener al menos 6 caracteres.");
            request.setAttribute("nombreValue", nombre);
            request.setAttribute("emailValue", email);
            request.getRequestDispatcher("/WEB-INF/vista/registro.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Las contraseñas no coinciden.");
            request.setAttribute("nombreValue", nombre);
            request.setAttribute("emailValue", email);
            request.getRequestDispatcher("/WEB-INF/vista/registro.jsp").forward(request, response);
            return;
        }

        if (usuarioFacade.findByEmail(email) != null) {
            request.setAttribute("error", "Ya existe una cuenta registrada con este correo electrónico.");
            request.setAttribute("nombreValue", nombre);
            request.getRequestDispatcher("/WEB-INF/vista/registro.jsp").forward(request, response);
            return;
        }

        String hash = PasswordHasher.hash(password);
        Usuario nuevoUsuario = new Usuario(email.trim().toLowerCase(), hash, nombre.trim());
        usuarioFacade.create(nuevoUsuario);

        HttpSession session = request.getSession(true);
        session.setAttribute("usuario", nuevoUsuario);
        session.setAttribute("masterKey", password);

        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}
