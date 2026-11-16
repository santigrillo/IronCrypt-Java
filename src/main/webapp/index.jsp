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

    <!-- NAVBAR -->
    <header class="navbar-container">
        <div class="navbar-wrapper">
            <a href="${pageContext.request.contextPath}/" class="brand">
                <img src="${pageContext.request.contextPath}/static/img/ironcrypt-icon.svg" alt="IronCrypt" class="brand-logo">
                <span class="brand-name">IronCrypt</span>
            </a>

            <nav class="nav-links">
                <a href="#features" class="nav-item">Casos de Uso</a>
                <a href="#security" class="nav-item">Seguridad</a>
                <a href="${pageContext.request.contextPath}/registro" class="nav-item nav-item-highlight">Comenzá gratis</a>
                <a href="${pageContext.request.contextPath}/login" class="btn-login">Iniciar Sesión</a>
            </nav>
        </div>
    </header>

    <!-- HERO SECTION -->
    <main>
        <section class="hero-section">
            <div class="badge-security">
                <span>🛡️ EL ADMINISTRADOR DE CONTRASEÑAS MÁS CONFIABLE</span>
            </div>

            <h1 class="hero-title">
                Protegé cada inicio de sesión de <span>personas, agentes y máquinas</span>
            </h1>

            <p class="hero-description">
                Simplificá los accesos para todos. Almacená, compartí y gestioná de forma segura las credenciales, 
                claves de acceso e información confidencial con cifrado de extremo a extremo que crece con vos y tu equipo.
            </p>

            <div class="hero-cta">
                <a href="${pageContext.request.contextPath}/registro" class="btn-cta-primary">
                    Comenzá gratis &rarr;
                </a>
                <a href="${pageContext.request.contextPath}/login" class="btn-cta-secondary">
                    Iniciar sesión
                </a>
            </div>
        </section>

        <!-- FEATURES SECTION -->
        <section id="features" class="features-section">
            <div class="section-header">
                <h2 class="section-title">Diseñado para personas y equipos en crecimiento</h2>
                <p class="section-subtitle">Gestioná todas tus credenciales en un solo lugar con aislamiento criptográfico garantizado.</p>
            </div>

            <div class="features-grid">
                <div class="feature-card">
                    <div class="feature-icon">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                            <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                        </svg>
                    </div>
                    <h3 class="feature-title">Bóvedas Personales</h3>
                    <p class="feature-text">
                        Tus contraseñas, tokens y notas confidenciales protegidas bajo tu propia frase de paso. Nadie más puede acceder a ellas.
                    </p>
                </div>

                <div class="feature-card">
                    <div class="feature-icon">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                            <circle cx="9" cy="7" r="4"></circle>
                            <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                            <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                        </svg>
                    </div>
                    <h3 class="feature-title">Bóvedas de Equipo</h3>
                    <p class="feature-text">
                        Compartí credenciales con tus colaboradores de forma segura mediante roles administrativos sin revelar contraseñas por canales inseguros.
                    </p>
                </div>

                <div class="feature-card">
                    <div class="feature-icon">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <rect x="2" y="3" width="20" height="14" rx="2" ry="2"></rect>
                            <line x1="8" y1="21" x2="16" y2="21"></line>
                            <line x1="12" y1="17" x2="12" y2="21"></line>
                        </svg>
                    </div>
                    <h3 class="feature-title">Cero Conocimiento</h3>
                    <p class="feature-text">
                        Cifrado simétrico en reposo con AES-256-GCM y PBKDF2. El servidor jamás almacena tus claves maestras en texto plano.
                    </p>
                </div>
            </div>
        </section>
    </main>

    <!-- FOOTER -->
    <footer class="footer-container">
        <p>&copy; 2026 IronCrypt — Desarrollo Backend III. Cifrado simétrico y bóvedas seguras en Jakarta EE 10.</p>
    </footer>

</body>
</html>
