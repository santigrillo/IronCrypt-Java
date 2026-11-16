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
                <h1 class="card-title">Crear cuenta</h1>
                <p class="card-subtitle">Comenzá a proteger tus credenciales con cifrado simétrico</p>
            </div>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert-error" role="alert">
                    <span><%= request.getAttribute("error") %></span>
                </div>
            <% } %>

            <form class="form-vertical" method="POST" action="${pageContext.request.contextPath}/registro">
                <div class="form-group">
                    <label for="nombreCompleto">Nombre completo</label>
                    <input id="nombreCompleto" type="text" name="nombreCompleto" value="<%= request.getAttribute("nombreValue") != null ? request.getAttribute("nombreValue") : "" %>" placeholder="Tu nombre y apellido" required autofocus>
                </div>

                <div class="form-group">
                    <label for="email">Correo electrónico</label>
                    <input id="email" type="email" name="email" value="<%= request.getAttribute("emailValue") != null ? request.getAttribute("emailValue") : "" %>" placeholder="usuario@dominio.com" required>
                </div>

                <div class="form-group">
                    <label for="password">Contraseña maestra (mínimo 6 caracteres)</label>
                    <input id="password" type="password" name="password" placeholder="••••••••••••" minlength="6" required>
                </div>

                <div class="form-group">
                    <label for="confirmPassword">Confirmar contraseña</label>
                    <input id="confirmPassword" type="password" name="confirmPassword" placeholder="••••••••••••" minlength="6" required>
                </div>

                <button type="submit" class="btn-primary">
                    Crear cuenta gratis
                </button>

                <div class="auth-footer">
                    <span>¿Ya tenés una cuenta?</span>
                    <a href="${pageContext.request.contextPath}/login">Iniciar sesión</a>
                </div>
            </form>
        </div>
    </div>

</body>
</html>
