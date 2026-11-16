<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>IronCrypt</title>
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/static/img/ironcrypt-icon.svg">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>

    <div class="auth-wrapper">
        <div class="auth-card">
            <a href="${pageContext.request.contextPath}/" class="back-link" aria-label="Volver al inicio">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M19 12H5M12 19l-7-7 7-7"/>
                </svg>
                <span>Volver</span>
            </a>

            <div class="card-header">
                <img src="${pageContext.request.contextPath}/static/img/ironcrypt-icon.svg" alt="IronCrypt" class="brand-logo">
                <h1 class="card-title">Iniciar sesión</h1>
                <p class="card-subtitle">Ingresá tus credenciales para desbloquear tu bóveda</p>
            </div>

            <%-- Mensajes de error o éxito --%>
            <% if (request.getAttribute("error") != null) { %>
                <div class="alert-error" role="alert">
                    <span><%= request.getAttribute("error") %></span>
                </div>
            <% } %>

            <% if ("true".equals(request.getParameter("logout"))) { %>
                <div class="alert-success" role="alert">
                    <span>Sesión cerrada exitosamente. Tu clave volátil ha sido destruida de la memoria.</span>
                </div>
            <% } %>

            <% if ("auth".equals(request.getParameter("error"))) { %>
                <div class="alert-error" role="alert">
                    <span>Debés iniciar sesión para acceder a este recurso.</span>
                </div>
            <% } %>

            <form class="form-vertical" method="POST" action="${pageContext.request.contextPath}/login">
                <div class="form-group">
                    <label for="email">Correo electrónico</label>
                    <input id="email" type="email" name="email" value="<%= request.getAttribute("emailValue") != null ? request.getAttribute("emailValue") : "" %>" placeholder="tu@email.com" required autofocus>
                </div>

                <div class="form-group">
                    <label for="password">Contraseña maestra</label>
                    <input id="password" type="password" name="password" placeholder="••••••••••••" required>
                </div>

                <button type="submit" class="btn-primary">
                    Iniciar sesión
                </button>

                <div class="auth-footer">
                    <span>¿No tenés una cuenta?</span>
                    <a href="${pageContext.request.contextPath}/registro">Crear cuenta gratis</a>
                </div>
            </form>
        </div>
    </div>

</body>
</html>
