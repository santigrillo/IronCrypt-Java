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
@Table(name = "items_boveda")
@NamedQueries({
    @NamedQuery(name = "ItemBoveda.findAll", query = "SELECT i FROM ItemBoveda i"),
    @NamedQuery(name = "ItemBoveda.findByBoveda", query = "SELECT i FROM ItemBoveda i WHERE i.boveda.id = :bovedaId ORDER BY i.fechaCreacion DESC")
})
public class ItemBoveda implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "boveda_id", referencedColumnName = "id", nullable = false)
    private Boveda boveda;

    @Basic(optional = false)
    @Column(name = "titulo", nullable = false, length = 255)
    private String titulo;

    @Column(name = "website_url", length = 500)
    private String websiteUrl;

    @Column(name = "username", length = 255)
    private String username;

    @Basic(optional = false)
    @Column(name = "password_cifrada", nullable = false, columnDefinition = "TEXT")
    private String passwordCifrada;

    @Basic(optional = false)
    @Column(name = "iv_nonce", nullable = false, length = 255)
    private String ivNonce;

    @Basic(optional = false)
    @Column(name = "salt", nullable = false, length = 255)
    private String salt;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    @Basic(optional = false)
    @Column(name = "fecha_creacion", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Basic(optional = false)
    @Column(name = "fecha_actualizacion", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaActualizacion;

    public ItemBoveda() {
        this.fechaCreacion = new Date();
        this.fechaActualizacion = new Date();
    }

    public ItemBoveda(Boveda boveda, String titulo, String websiteUrl, String username,
                      String passwordCifrada, String ivNonce, String salt, String notas) {
        this.boveda = boveda;
        this.titulo = titulo;
        this.websiteUrl = websiteUrl;
        this.username = username;
        this.passwordCifrada = passwordCifrada;
        this.ivNonce = ivNonce;
        this.salt = salt;
        this.notas = notas;
        this.fechaCreacion = new Date();
        this.fechaActualizacion = new Date();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Boveda getBoveda() { return boveda; }
    public void setBoveda(Boveda boveda) { this.boveda = boveda; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getWebsiteUrl() { return websiteUrl; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordCifrada() { return passwordCifrada; }
    public void setPasswordCifrada(String passwordCifrada) { this.passwordCifrada = passwordCifrada; }

    public String getIvNonce() { return ivNonce; }
    public void setIvNonce(String ivNonce) { this.ivNonce = ivNonce; }

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Date getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(Date fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ItemBoveda other = (ItemBoveda) obj;
        return Objects.equals(id, other.id);
    }
}
