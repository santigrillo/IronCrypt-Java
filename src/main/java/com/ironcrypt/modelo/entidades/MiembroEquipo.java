package com.ironcrypt.modelo.entidades;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "miembros_equipo")
@NamedQueries({
    @NamedQuery(name = "MiembroEquipo.findByEquipo", 
                query = "SELECT m FROM MiembroEquipo m WHERE m.equipo.id = :equipoId ORDER BY m.rol ASC, m.usuario.nombreCompleto ASC"),
    @NamedQuery(name = "MiembroEquipo.findByUsuario", 
                query = "SELECT m FROM MiembroEquipo m WHERE m.usuario.id = :usuarioId ORDER BY m.equipo.nombre ASC"),
    @NamedQuery(name = "MiembroEquipo.findByEquipoYUsuario", 
                query = "SELECT m FROM MiembroEquipo m WHERE m.equipo.id = :equipoId AND m.usuario.id = :usuarioId")
})
public class MiembroEquipo implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String ROL_ADMIN = "ADMIN";
    public static final String ROL_MEMBER = "MEMBER";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "equipo_id", referencedColumnName = "id", nullable = false)
    private Equipo equipo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", nullable = false)
    private Usuario usuario;

    @Basic(optional = false)
    @Column(name = "rol", nullable = false, length = 50)
    private String rol = ROL_MEMBER;

    @Basic(optional = false)
    @Column(name = "fecha_union", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaUnion;

    public MiembroEquipo() {
        this.fechaUnion = new Date();
    }

    public MiembroEquipo(Equipo equipo, Usuario usuario, String rol) {
        this.equipo = equipo;
        this.usuario = usuario;
        this.rol = (rol != null && !rol.trim().isEmpty()) ? rol.trim().toUpperCase() : ROL_MEMBER;
        this.fechaUnion = new Date();
    }

    public boolean isAdmin() {
        return ROL_ADMIN.equalsIgnoreCase(this.rol);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Date getFechaUnion() {
        return fechaUnion;
    }

    public void setFechaUnion(Date fechaUnion) {
        this.fechaUnion = fechaUnion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MiembroEquipo other = (MiembroEquipo) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return "MiembroEquipo{" + "id=" + id + ", equipo=" + (equipo != null ? equipo.getNombre() : null) 
                + ", usuario=" + (usuario != null ? usuario.getEmail() : null) + ", rol=" + rol + '}';
    }
}
