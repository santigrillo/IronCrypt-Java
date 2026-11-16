<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.ironcrypt.modelo.entidades.Usuario" %>
<%@ page import="com.ironcrypt.modelo.entidades.Equipo" %>
<%@ page import="com.ironcrypt.modelo.entidades.MiembroEquipo" %>
<%@ page import="com.ironcrypt.modelo.entidades.Boveda" %>
<%@ page import="com.ironcrypt.modelo.entidades.ItemBoveda" %>
<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    List<Boveda> bovedas = (List<Boveda>) request.getAttribute("bovedas");
    Map<Long, List<ItemBoveda>> itemsPorBoveda = (Map<Long, List<ItemBoveda>>) request.getAttribute("itemsPorBoveda");
    Map<Long, Boolean> permisosEscrituraPorBoveda = (Map<Long, Boolean>) request.getAttribute("permisosEscrituraPorBoveda");
    List<Equipo> misEquipos = (List<Equipo>) request.getAttribute("misEquipos");
    List<Equipo> equiposAdmin = (List<Equipo>) request.getAttribute("equiposAdmin");
    Map<Long, List<MiembroEquipo>> miembrosPorEquipo = (Map<Long, List<MiembroEquipo>>) request.getAttribute("miembrosPorEquipo");
    Map<Long, Boolean> esAdminEnEquipo = (Map<Long, Boolean>) request.getAttribute("esAdminEnEquipo");

    String inicial = (usuario.getNombreCompleto() != null && !usuario.getNombreCompleto().isEmpty())
            ? usuario.getNombreCompleto().substring(0, 1).toUpperCase()
            : "U";
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>IronCrypt</title>
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/static/img/ironcrypt-icon.svg">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body class="dashboard-layout">

    <!-- HEADER SUPERIOR -->
    <header class="dashboard-header">
        <div class="header-left">
            <a href="${pageContext.request.contextPath}/dashboard" class="brand">
                <img src="${pageContext.request.contextPath}/static/img/ironcrypt-icon.svg" alt="IronCrypt" class="brand-logo">
                <span class="brand-name">IronCrypt</span>
            </a>
        </div>

        <div class="header-right">
            <div class="user-badge">
                <div class="user-avatar"><%= inicial %></div>
                <span><%= usuario.getNombreCompleto() %></span>
            </div>

            <a href="${pageContext.request.contextPath}/logout" class="btn-logout" title="Cerrar sesión">
                Cerrar sesión
            </a>
        </div>
    </header>

    <!-- CONTENIDO PRINCIPAL -->
    <main class="dashboard-main">
        <div class="dashboard-toolbar">
            <div class="toolbar-title">
                <h1>Mis Bóvedas</h1>
            </div>

            <div style="display: flex; align-items: center; gap: 1rem;">
                <span class="session-info-badge">
                    <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
                    </svg>
                    Bóveda Desbloqueada (Sesión Activa)
                </span>

                <button type="button" class="btn-teams-toolbar" onclick="abrirModalEquipos()">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                        <circle cx="9" cy="7" r="4"></circle>
                        <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                        <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                    </svg>
                    Mis Equipos (<%= misEquipos != null ? misEquipos.size() : 0 %>)
                </button>

                <button class="btn-new-vault" onclick="abrirModalCrear()">
                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                        <line x1="12" y1="5" x2="12" y2="19"></line>
                        <line x1="5" y1="12" x2="19" y2="12"></line>
                    </svg>
                    Nueva Bóveda
                </button>
            </div>
        </div>

        <!-- LISTADO DE BÓVEDAS (ACORDEÓN) -->
        <% if (bovedas == null || bovedas.isEmpty()) { %>
            <div class="empty-state">
                <div class="empty-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                        <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                    </svg>
                </div>
                <h3 style="font-size: 1.25rem; font-weight: 600;">Aún no tenés ninguna bóveda</h3>
                <p style="max-width: 480px; color: var(--texto-muted); font-size: 0.95rem; line-height: 1.5;">
                    Creá tu primera bóveda personal para empezar a organizar y proteger tus contraseñas con cifrado simétrico en PostgreSQL.
                </p>
                <button class="btn-new-vault" onclick="abrirModalCrear()" style="margin-top: 0.5rem;">
                    Crear mi primera bóveda
                </button>
            </div>
        <% } else { %>
            <div class="vault-accordion">
                <% for (Boveda boveda : bovedas) { 
                    List<ItemBoveda> items = (itemsPorBoveda != null) ? itemsPorBoveda.get(boveda.getId()) : null;
                    int cantidadItems = (items != null) ? items.size() : 0;
                    boolean puedeEscribir = permisosEscrituraPorBoveda != null && Boolean.TRUE.equals(permisosEscrituraPorBoveda.get(boveda.getId()));
                %>
                    <div class="accordion-item" id="accordion-<%= boveda.getId() %>">
                        <!-- CABECERA DE BÓVEDA -->
                        <div class="accordion-header" onclick="toggleAccordion('<%= boveda.getId() %>')">
                            <div class="accordion-title-col">
                                <div class="vault-icon">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                                        <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                                    </svg>
                                </div>
                                <div class="vault-info">
                                    <div class="vault-name-row">
                                        <span class="vault-name"><%= boveda.getNombre() %></span>
                                        <% if (boveda.isPersonal()) { %>
                                            <span class="badge-personal">PERSONAL</span>
                                        <% } else { %>
                                            <span class="badge-team">EQUIPO: <%= boveda.getEquipo().getNombre() %></span>
                                        <% } %>
                                        <% if (!puedeEscribir) { %>
                                            <span class="badge-readonly">Solo lectura</span>
                                        <% } %>
                                        <span style="font-size: 0.75rem; color: var(--texto-muted); margin-left: 0.25rem;">
                                            (<%= cantidadItems %> <%= cantidadItems == 1 ? "credencial" : "credenciales" %>)
                                        </span>
                                    </div>
                                    <% if (boveda.getDescripcion() != null && !boveda.getDescripcion().isEmpty()) { %>
                                        <span class="vault-desc"><%= boveda.getDescripcion() %></span>
                                    <% } %>
                                </div>
                            </div>

                            <div class="accordion-actions">
                                <% if (puedeEscribir) { %>
                                <button type="button" class="btn-icon-secondary" 
                                        onclick="event.stopPropagation(); abrirModalEditar('<%= boveda.getId() %>', '<%= boveda.getNombre().replace("'", "\\'") %>', '<%= boveda.getDescripcion() != null ? boveda.getDescripcion().replace("'", "\\'") : "" %>')" 
                                        title="Configurar Bóveda">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <circle cx="12" cy="12" r="3"></circle>
                                        <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"></path>
                                    </svg>
                                </button>
                                <% } %>

                                <svg class="accordion-chevron" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <polyline points="6 9 12 15 18 9"></polyline>
                                </svg>
                            </div>
                        </div>

                        <!-- CUERPO EXPANDIBLE: TABLA DE CREDENCIALES -->
                        <div class="accordion-content">
                            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                                <span style="font-size: 0.9rem; color: var(--texto-muted);">
                                    <%= cantidadItems %> credenciales protegidas con AES-256-GCM
                                </span>
                                <% if (puedeEscribir) { %>
                                <button type="button" class="btn-primary" style="width: auto; padding: 0.5rem 1rem; font-size: 0.85rem;" 
                                        onclick="abrirModalNuevoItem('<%= boveda.getId() %>')">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                                        <line x1="12" y1="5" x2="12" y2="19"></line>
                                        <line x1="5" y1="12" x2="19" y2="12"></line>
                                    </svg>
                                    Agregar Contraseña
                                </button>
                                <% } %>
                            </div>

                            <% if (items == null || items.isEmpty()) { %>
                                <div style="text-align: center; padding: 2rem; color: var(--texto-muted); font-size: 0.9rem;">
                                    No hay credenciales registradas en esta bóveda todavía. Hacé clic en <strong>Agregar Contraseña</strong> para cifrar tu primera clave.
                                </div>
                            <% } else { %>
                                <div class="items-table-container">
                                    <table class="items-table">
                                        <thead>
                                            <tr>
                                                <th>Servicio / Título</th>
                                                <th>Usuario o Email</th>
                                                <th>Sitio Web</th>
                                                <th>Contraseña</th>
                                                <th>Notas</th>
                                                <th style="text-align: right;">Acciones</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <% for (ItemBoveda item : items) { 
                                                String domain = "";
                                                if (item.getWebsiteUrl() != null && !item.getWebsiteUrl().isEmpty()) {
                                                    String url = item.getWebsiteUrl().replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)", "");
                                                    int slashIdx = url.indexOf('/');
                                                    domain = (slashIdx != -1) ? url.substring(0, slashIdx) : url;
                                                }
                                                String faviconUrl = !domain.isEmpty() 
                                                    ? "https://www.google.com/s2/favicons?domain=" + domain + "&sz=32" 
                                                    : request.getContextPath() + "/static/img/favicon.svg";
                                            %>
                                                <tr id="item-row-<%= item.getId() %>">
                                                    <!-- SERVICIO CON FAVICON -->
                                                    <td>
                                                        <div class="service-badge">
                                                            <img src="<%= faviconUrl %>" class="favicon-icon" alt="" onerror="this.src='${pageContext.request.contextPath}/static/img/favicon.svg'">
                                                            <strong style="color: var(--texto-blanco);"><%= item.getTitulo() %></strong>
                                                        </div>
                                                    </td>

                                                    <!-- USUARIO / EMAIL -->
                                                    <td>
                                                        <% if (item.getUsername() != null && !item.getUsername().isEmpty()) { %>
                                                            <div style="display: flex; align-items: center; gap: 0.4rem;">
                                                                <span><%= item.getUsername() %></span>
                                                                <button type="button" class="btn-icon-cell" style="width:24px; height:24px;" 
                                                                        onclick="copiarAlPortapapeles('<%= item.getUsername().replace("'", "\\'") %>', 'Usuario copiado')" 
                                                                        title="Copiar usuario">
                                                                    <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                                                        <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
                                                                        <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
                                                                    </svg>
                                                                </button>
                                                            </div>
                                                        <% } else { %>
                                                            <span style="color: var(--texto-muted);">&mdash;</span>
                                                        <% } %>
                                                    </td>

                                                    <!-- SITIO WEB -->
                                                    <td>
                                                        <% if (item.getWebsiteUrl() != null && !item.getWebsiteUrl().isEmpty()) { %>
                                                            <a href="<%= item.getWebsiteUrl().startsWith("http") ? item.getWebsiteUrl() : "https://" + item.getWebsiteUrl() %>" 
                                                               target="_blank" rel="noopener noreferrer" style="color: var(--azul-boton); text-decoration: none; font-size: 0.85rem;">
                                                                <%= domain %>
                                                            </a>
                                                        <% } else { %>
                                                            <span style="color: var(--texto-muted);">&mdash;</span>
                                                        <% } %>
                                                    </td>

                                                    <!-- CONTRASEÑA CIFRADA CON REVELACIÓN 1-CLIC -->
                                                    <td>
                                                        <div style="display: flex; align-items: center; gap: 0.5rem;">
                                                            <span id="pass-text-<%= item.getId() %>" class="password-display">••••••••••••</span>
                                                            <button type="button" id="btn-reveal-<%= item.getId() %>" class="btn-icon-cell" 
                                                                    onclick="alternarRevelar('<%= item.getId() %>')" title="Revelar en 1 clic">
                                                                <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                                                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                                                    <circle cx="12" cy="12" r="3"></circle>
                                                                </svg>
                                                            </button>
                                                            <button type="button" class="btn-icon-cell" 
                                                                    onclick="copiarPassword('<%= item.getId() %>')" title="Copiar contraseña">
                                                                <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                                                    <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
                                                                    <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
                                                                </svg>
                                                            </button>
                                                        </div>
                                                    </td>

                                                    <!-- NOTAS -->
                                                    <td>
                                                        <% if (item.getNotas() != null && !item.getNotas().isEmpty()) { %>
                                                            <span style="color: var(--texto-muted); font-size: 0.85rem;" title="<%= item.getNotas() %>">
                                                                <%= item.getNotas().length() > 30 ? item.getNotas().substring(0, 30) + "..." : item.getNotas() %>
                                                            </span>
                                                        <% } else { %>
                                                            <span style="color: var(--texto-muted);">&mdash;</span>
                                                        <% } %>
                                                    </td>

                                                    <!-- ACCIONES (EDITAR Y BORRAR) -->
                                                    <td style="text-align: right;">
                                                        <div style="display: inline-flex; align-items: center; gap: 0.35rem; justify-content: flex-end;">
                                                            <% if (puedeEscribir) { %>
                                                            <button type="button" class="btn-icon-cell" title="Editar credencial"
                                                                    data-id="<%= item.getId() %>"
                                                                    data-titulo="<%= item.getTitulo() != null ? item.getTitulo().replace("\"", "&quot;") : "" %>"
                                                                    data-url="<%= item.getWebsiteUrl() != null ? item.getWebsiteUrl().replace("\"", "&quot;") : "" %>"
                                                                    data-username="<%= item.getUsername() != null ? item.getUsername().replace("\"", "&quot;") : "" %>"
                                                                    data-notas="<%= item.getNotas() != null ? item.getNotas().replace("\"", "&quot;").replace("\n", " ").replace("\r", "") : "" %>"
                                                                    onclick="abrirModalEditarItem(this)">
                                                                <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                                                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                                                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                                                </svg>
                                                            </button>
                                                            <form method="POST" action="${pageContext.request.contextPath}/items/eliminar" style="display:inline; margin: 0;" 
                                                                  onsubmit="return confirm('¿Eliminar esta credencial cifrada?');">
                                                                <input type="hidden" name="id" value="<%= item.getId() %>">
                                                                <button type="submit" class="btn-icon-cell" style="color: #fca5a5;" title="Eliminar credencial">
                                                                    <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                                                        <polyline points="3 6 5 6 21 6"></polyline>
                                                                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                                                                    </svg>
                                                                </button>
                                                            </form>
                                                            <% } else { %>
                                                                <span style="font-size: 0.75rem; color: var(--texto-muted);">Lectura</span>
                                                            <% } %>
                                                        </div>
                                                    </td>
                                                </tr>
                                            <% } %>
                                        </tbody>
                                    </table>
                                </div>
                            <% } %>
                        </div>
                    </div>
                <% } %>
            </div>
        <% } %>
    </main>

    <!-- MODAL: NUEVA CREDENCIAL CIFRADA -->
    <div id="modalNuevoItem" class="modal-backdrop">
        <div class="modal-card">
            <div class="modal-header">
                <h3 class="modal-title">Nueva Credencial Cifrada</h3>
                <button type="button" class="btn-close-modal" onclick="cerrarModalNuevoItem()">&times;</button>
            </div>
            <form method="POST" action="${pageContext.request.contextPath}/items/crear" class="form-vertical">
                <input type="hidden" id="itemBovedaId" name="bovedaId">

                <div class="form-group">
                    <label for="itemTitulo">Servicio o Título *</label>
                    <input id="itemTitulo" type="text" name="titulo" placeholder="Ej: GitHub, AWS, Google Workspace" required>
                </div>

                <div class="form-group">
                    <label for="itemUrl">Sitio Web (URL)</label>
                    <input id="itemUrl" type="text" name="websiteUrl" placeholder="https://github.com">
                </div>

                <div class="form-group">
                    <label for="itemUsername">Usuario o Correo</label>
                    <input id="itemUsername" type="text" name="username" placeholder="usuario@empresa.com">
                </div>

                <div class="form-group">
                    <label for="itemPassword">Contraseña a Proteger *</label>
                    <input id="itemPassword" type="password" name="password" placeholder="••••••••••••" required>
                </div>

                <div class="form-group">
                    <label for="itemPassphrase">Frase de Paso de Cifrado (opcional)</label>
                    <input id="itemPassphrase" type="password" name="passphrase" placeholder="Dejar en blanco para usar tu Contraseña Maestra">
                    <small style="color: var(--texto-muted); font-size: 0.75rem; margin-top: 0.25rem;">
                        Por defecto se utiliza tu clave activa para habilitar el descifrado instantáneo en 1 clic.
                    </small>
                </div>

                <div class="form-group">
                    <label for="itemNotas">Notas complementarias</label>
                    <textarea id="itemNotas" name="notas" rows="2" placeholder="Tokens de recuperación, PIN, etc."></textarea>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn-secondary" onclick="cerrarModalNuevoItem()">Cancelar</button>
                    <button type="submit" class="btn-primary" style="width: auto;">Guardar y Cifrar</button>
                </div>
            </form>
        </div>
    </div>

    <!-- MODAL: EDITAR CREDENCIAL CIFRADA -->
    <div id="modalEditarItem" class="modal-backdrop">
        <div class="modal-card">
            <div class="modal-header">
                <h3 class="modal-title">Editar Credencial</h3>
                <button type="button" class="btn-close-modal" onclick="cerrarModalEditarItem()">&times;</button>
            </div>
            <form method="POST" action="${pageContext.request.contextPath}/items/editar" class="form-vertical">
                <input type="hidden" id="editItemId" name="id">

                <div class="form-group">
                    <label for="editItemTitulo">Servicio o Título *</label>
                    <input id="editItemTitulo" type="text" name="titulo" required>
                </div>

                <div class="form-group">
                    <label for="editItemUrl">Sitio Web (URL)</label>
                    <input id="editItemUrl" type="text" name="websiteUrl" placeholder="https://ejemplo.com">
                </div>

                <div class="form-group">
                    <label for="editItemUsername">Usuario o Correo</label>
                    <input id="editItemUsername" type="text" name="username">
                </div>

                <div class="form-group">
                    <label for="editItemPassword">Nueva Contraseña (opcional)</label>
                    <input id="editItemPassword" type="password" name="password" placeholder="•••••••••••• (dejar en blanco para no cambiar)">
                    <small style="color: var(--texto-muted); font-size: 0.75rem; margin-top: 0.25rem;">
                        Dejá este campo vacío si querés conservar la contraseña cifrada actual.
                    </small>
                </div>

                <div class="form-group">
                    <label for="editItemPassphrase">Frase de Paso de Cifrado (opcional)</label>
                    <input id="editItemPassphrase" type="password" name="passphrase" placeholder="Dejar en blanco para usar tu Contraseña Maestra">
                    <small style="color: var(--texto-muted); font-size: 0.75rem; margin-top: 0.25rem;">
                        Solo se utiliza si ingresás una nueva contraseña para cifrarla.
                    </small>
                </div>

                <div class="form-group">
                    <label for="editItemNotas">Notas complementarias</label>
                    <textarea id="editItemNotas" name="notas" rows="2" placeholder="Tokens de recuperación, PIN, etc."></textarea>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn-secondary" onclick="cerrarModalEditarItem()">Cancelar</button>
                    <button type="submit" class="btn-primary" style="width: auto;">Guardar Cambios</button>
                </div>
            </form>
        </div>
    </div>

    <!-- MODALES DE BÓVEDA (CREAR / EDITAR) -->
    <div id="modalCrear" class="modal-backdrop">
        <div class="modal-card">
            <div class="modal-header">
                <h3 class="modal-title">Nueva Bóveda</h3>
                <button type="button" class="btn-close-modal" onclick="cerrarModalCrear()">&times;</button>
            </div>
            <form method="POST" action="${pageContext.request.contextPath}/bovedas/crear" class="form-vertical">
                <div class="form-group">
                    <label for="nombreCrear">Nombre de la bóveda *</label>
                    <input id="nombreCrear" type="text" name="nombre" placeholder="Ej: Personal, Trabajo, Finanzas" required>
                </div>
                <div class="form-group">
                    <label for="equipoIdCrear">Destino de la Bóveda</label>
                    <select id="equipoIdCrear" name="equipoId">
                        <option value="">Personal (Sólo para mí)</option>
                        <% if (equiposAdmin != null) { 
                            for (Equipo eq : equiposAdmin) { %>
                                <option value="<%= eq.getId() %>">Equipo: <%= eq.getNombre() %></option>
                        <%  } 
                           } %>
                    </select>
                </div>
                <div class="form-group">
                    <label for="descCrear">Descripción (opcional)</label>
                    <textarea id="descCrear" name="descripcion" rows="3" placeholder="Detalle sobre el contenido de esta bóveda"></textarea>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn-secondary" onclick="cerrarModalCrear()">Cancelar</button>
                    <button type="submit" class="btn-primary" style="width: auto;">Crear Bóveda</button>
                </div>
            </form>
        </div>
    </div>

    <div id="modalEditar" class="modal-backdrop">
        <div class="modal-card modal-card-lg">
            <div class="modal-header">
                <h3 class="modal-title">Configurar Bóveda</h3>
                <button type="button" class="btn-close-modal" onclick="cerrarModalEditar()">&times;</button>
            </div>
            <form method="POST" action="${pageContext.request.contextPath}/bovedas/editar" class="form-vertical">
                <input type="hidden" id="idEditar" name="id">
                <div class="form-group">
                    <label for="nombreEditar">Nombre de la bóveda</label>
                    <input id="nombreEditar" type="text" name="nombre" required>
                </div>
                <div class="form-group">
                    <label for="descEditar">Descripción</label>
                    <textarea id="descEditar" name="descripcion" rows="3"></textarea>
                </div>
                <div class="modal-footer modal-footer-between">
                    <button type="button" class="btn-danger" onclick="confirmarEliminar()">Eliminar Bóveda</button>
                    <div class="footer-btn-group">
                        <button type="button" class="btn-secondary" onclick="cerrarModalEditar()">Cancelar</button>
                        <button type="submit" class="btn-primary">Guardar Cambios</button>
                    </div>
                </div>
            </form>

            <form id="formEliminar" method="POST" action="${pageContext.request.contextPath}/bovedas/eliminar" style="display:none;">
                <input type="hidden" id="idEliminar" name="id">
            </form>
        </div>
    </div>

    <!-- MODAL: GESTIÓN DE EQUIPOS -->
    <div id="modalEquipos" class="modal-backdrop">
        <div class="modal-card modal-card-lg" style="max-height: 85vh; overflow-y: auto;">
            <div class="modal-header">
                <h3 class="modal-title">Gestión de Equipos</h3>
                <button type="button" class="btn-close-modal" onclick="cerrarModalEquipos()">&times;</button>
            </div>

            <!-- CREAR NUEVO EQUIPO -->
            <div class="team-card" style="background-color: rgba(76, 118, 255, 0.05); border-color: rgba(76, 118, 255, 0.2);">
                <h4 style="margin-bottom: 0.5rem; font-size: 0.95rem; color: var(--texto-blanco);">Crear un nuevo equipo</h4>
                <form method="POST" action="${pageContext.request.contextPath}/equipos/crear" class="inline-form">
                    <input type="text" name="nombre" placeholder="Nombre del equipo (ej: Desarrollo, Finanzas)" required style="flex: 1;">
                    <button type="submit" class="btn-primary">Crear</button>
                </form>
            </div>

            <!-- LISTADO DE EQUIPOS A LOS QUE PERTENECE -->
            <h4 style="margin: 1.5rem 0 0.75rem 0; font-size: 0.95rem; color: var(--texto-muted);">Equipos a los que pertenecés</h4>
            
            <% if (misEquipos == null || misEquipos.isEmpty()) { %>
                <p style="color: var(--texto-muted); font-size: 0.9rem; text-align: center; padding: 1.5rem;">
                    No pertenecés a ningún equipo todavía. ¡Creá el primero arriba para compartir bóvedas!
                </p>
            <% } else { %>
                <% for (Equipo eq : misEquipos) { 
                    boolean soyAdmin = esAdminEnEquipo != null && Boolean.TRUE.equals(esAdminEnEquipo.get(eq.getId()));
                    List<MiembroEquipo> integrantes = (miembrosPorEquipo != null) ? miembrosPorEquipo.get(eq.getId()) : null;
                %>
                    <div class="team-card">
                        <div class="team-header-row">
                            <div>
                                <strong style="font-size: 1.05rem; color: var(--texto-blanco);"><%= eq.getNombre() %></strong>
                                <span class="badge-role <%= soyAdmin ? "badge-role-admin" : "badge-role-member" %>" style="margin-left: 0.5rem;">
                                    <%= soyAdmin ? "ADMIN" : "MIEMBRO" %>
                                </span>
                            </div>
                            
                            <!-- Abandonar equipo si es miembro común -->
                            <% if (!soyAdmin) { %>
                                <form method="POST" action="${pageContext.request.contextPath}/equipos/miembros/eliminar" onsubmit="return confirm('¿Seguro que deseás abandonar este equipo?');">
                                    <input type="hidden" name="equipoId" value="<%= eq.getId() %>">
                                    <input type="hidden" name="usuarioId" value="<%= usuario.getId() %>">
                                    <button type="submit" class="btn-secondary" style="font-size: 0.75rem; padding: 0.3rem 0.6rem;">Abandonar</button>
                                </form>
                            <% } %>
                        </div>

                        <!-- Lista de integrantes -->
                        <div style="margin-bottom: 0.75rem;">
                            <% if (integrantes != null) { 
                                for (MiembroEquipo m : integrantes) { %>
                                    <div class="member-row">
                                        <div>
                                            <span style="font-weight: 500; color: var(--texto-blanco);"><%= m.getUsuario().getNombreCompleto() %></span>
                                            <span style="font-size: 0.8rem; color: var(--texto-muted); margin-left: 0.35rem;">(<%= m.getUsuario().getEmail() %>)</span>
                                            <span class="badge-role <%= m.isAdmin() ? "badge-role-admin" : "badge-role-member" %>" style="margin-left: 0.4rem;">
                                                <%= m.getRol() %>
                                            </span>
                                        </div>

                                        <% if (soyAdmin && !m.getUsuario().getId().equals(usuario.getId())) { %>
                                            <div style="display: flex; gap: 0.4rem;">
                                                <!-- Cambiar Rol -->
                                                <form method="POST" action="${pageContext.request.contextPath}/equipos/miembros/rol" style="margin: 0;">
                                                    <input type="hidden" name="equipoId" value="<%= eq.getId() %>">
                                                    <input type="hidden" name="usuarioId" value="<%= m.getUsuario().getId() %>">
                                                    <input type="hidden" name="rol" value="<%= m.isAdmin() ? "MEMBER" : "ADMIN" %>">
                                                    <button type="submit" class="btn-icon-cell" style="font-size: 0.75rem; width: auto; padding: 0.2rem 0.5rem;" title="Alternar Rol">
                                                        Hacer <%= m.isAdmin() ? "Miembro" : "Admin" %>
                                                    </button>
                                                </form>

                                                <!-- Expulsar del equipo -->
                                                <form method="POST" action="${pageContext.request.contextPath}/equipos/miembros/eliminar" style="margin: 0;" onsubmit="return confirm('¿Remover a este integrante?');">
                                                    <input type="hidden" name="equipoId" value="<%= eq.getId() %>">
                                                    <input type="hidden" name="usuarioId" value="<%= m.getUsuario().getId() %>">
                                                    <button type="submit" class="btn-icon-cell" style="color: #fca5a5;" title="Remover del equipo">&times;</button>
                                                </form>
                                            </div>
                                        <% } %>
                                    </div>
                            <%  } 
                               } %>
                        </div>

                        <!-- Formulario para agregar miembro (Solo el rol es ADMIN) -->
                        <% if (soyAdmin) { %>
                            <form method="POST" action="${pageContext.request.contextPath}/equipos/miembros/agregar" class="inline-form" style="border-top: 1px dashed rgba(255, 255, 255, 0.08); padding-top: 0.75rem;">
                                <input type="hidden" name="equipoId" value="<%= eq.getId() %>">
                                <input type="email" name="email" placeholder="correo@compañero.com" required style="flex: 2;">
                                <select name="rol" style="flex: 1;">
                                    <option value="MEMBER">Miembro</option>
                                    <option value="ADMIN">Admin</option>
                                </select>
                                <button type="submit" class="btn-primary">Agregar</button>
                            </form>
                        <% } %>
                    </div>
                <% } %>
            <% } %>

            <div class="modal-footer">
                <button type="button" class="btn-secondary" onclick="cerrarModalEquipos()">Cerrar</button>
            </div>
        </div>
    </div>

    <!-- TOAST NOTIFICACIÓN COPIADO -->
    <div id="toastFeedback" class="toast-feedback">
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="20 6 9 17 4 12"></polyline>
        </svg>
        <span id="toastMsg">¡Copiado al portapapeles!</span>
    </div>

    <!-- SCRIPTS INTERACTIVOS (DESCIFRADO 1-CLIC + COPIADO) -->
    <script>
        const decryptedCache = {};

        function toggleAccordion(id) {
            const item = document.getElementById('accordion-' + id);
            if (item) item.classList.toggle('active');
        }

        function abrirModalNuevoItem(bovedaId) {
            document.getElementById('itemBovedaId').value = bovedaId;
            document.getElementById('modalNuevoItem').classList.add('open');
            document.getElementById('itemTitulo').focus();
        }

        function cerrarModalNuevoItem() {
            document.getElementById('modalNuevoItem').classList.remove('open');
        }

        function abrirModalEditarItem(btn) {
            document.getElementById('editItemId').value = btn.dataset.id;
            document.getElementById('editItemTitulo').value = btn.dataset.titulo || '';
            document.getElementById('editItemUrl').value = btn.dataset.url || '';
            document.getElementById('editItemUsername').value = btn.dataset.username || '';
            document.getElementById('editItemPassword').value = '';
            document.getElementById('editItemPassphrase').value = '';
            document.getElementById('editItemNotas').value = btn.dataset.notas || '';
            document.getElementById('modalEditarItem').classList.add('open');
            document.getElementById('editItemTitulo').focus();
        }

        function cerrarModalEditarItem() {
            document.getElementById('modalEditarItem').classList.remove('open');
        }

        function abrirModalCrear() {
            document.getElementById('modalCrear').classList.add('open');
            document.getElementById('nombreCrear').focus();
        }

        function cerrarModalCrear() {
            document.getElementById('modalCrear').classList.remove('open');
        }

        function abrirModalEditar(id, nombre, descripcion) {
            document.getElementById('idEditar').value = id;
            document.getElementById('idEliminar').value = id;
            document.getElementById('nombreEditar').value = nombre;
            document.getElementById('descEditar').value = descripcion;
            document.getElementById('modalEditar').classList.add('open');
        }

        function cerrarModalEditar() {
            document.getElementById('modalEditar').classList.remove('open');
        }

        function confirmarEliminar() {
            const nombre = document.getElementById('nombreEditar').value;
            if (confirm('¿Eliminar la bóveda "' + nombre + '" y todas sus claves asociadas?')) {
                document.getElementById('formEliminar').submit();
            }
        }

        // Descifrado interactivo en 1 clic
        async function alternarRevelar(itemId) {
            const passEl = document.getElementById('pass-text-' + itemId);
            const btnEl = document.getElementById('btn-reveal-' + itemId);

            // Si ya está revelada, la volvemos a enmascarar
            if (decryptedCache[itemId]) {
                delete decryptedCache[itemId];
                passEl.innerText = '••••••••••••';
                btnEl.classList.remove('active-eye');
                return;
            }

            // Descifrar consumiendo ItemServlet (/items/revelar)
            try {
                const params = new URLSearchParams();
                params.append('itemId', itemId);

                const res = await fetch('${pageContext.request.contextPath}/items/revelar', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: params.toString()
                });

                const data = await res.json();
                if (data.ok && data.password) {
                    decryptedCache[itemId] = data.password;
                    passEl.innerText = data.password;
                    btnEl.classList.add('active-eye');
                } else {
                    alert(data.error || 'No se pudo descifrar la credencial.');
                }
            } catch (err) {
                alert('Error al comunicar con el servidor.');
            }
        }

        // Copiado rápido al portapapeles
        function copiarAlPortapapeles(texto, mensaje) {
            navigator.clipboard.writeText(texto).then(() => {
                mostrarToast(mensaje || '¡Copiado!');
            });
        }

        async function copiarPassword(itemId) {
            if (decryptedCache[itemId]) {
                copiarAlPortapapeles(decryptedCache[itemId], 'Contraseña copiada');
                return;
            }

            // Si está oculta, la descifra primero y luego copia
            try {
                const params = new URLSearchParams();
                params.append('itemId', itemId);

                const res = await fetch('${pageContext.request.contextPath}/items/revelar', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: params.toString()
                });

                const data = await res.json();
                if (data.ok && data.password) {
                    copiarAlPortapapeles(data.password, 'Contraseña copiada');
                } else {
                    alert('Error al obtener la contraseña para copiar.');
                }
            } catch (err) {
                alert('Error al copiar contraseña.');
            }
        }

        function mostrarToast(msg) {
            const toast = document.getElementById('toastFeedback');
            document.getElementById('toastMsg').innerText = msg;
            toast.style.display = 'flex';
            setTimeout(() => {
                toast.style.display = 'none';
            }, 2000);
        }

        function abrirModalEquipos() {
            document.getElementById('modalEquipos').classList.add('open');
        }

        function cerrarModalEquipos() {
            document.getElementById('modalEquipos').classList.remove('open');
        }

        // Si viene de una acción de equipos, vuelve a abrir el modal automáticamente
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.get('tab') === 'equipos') {
            abrirModalEquipos();
        }

        window.addEventListener('keydown', function(e) {
            if (e.key === 'Escape') {
                cerrarModalCrear();
                cerrarModalEditar();
                cerrarModalNuevoItem();
                cerrarModalEditarItem();
                cerrarModalEquipos();
            }
        });
    </script>

</body>
</html>
