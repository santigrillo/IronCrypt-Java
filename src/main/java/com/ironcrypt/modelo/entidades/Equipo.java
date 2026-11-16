/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ironcrypt.modelo.entidades;

/**
 *
 * @author santiagogrillo
 */


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
@Table(name = "equipos")                                                               
@NamedQueries({                                                                        
    @NamedQuery(name = "Equipo.findAll", query = "SELECT e FROM Equipo e")             
})                                                                                     
public class Equipo implements Serializable {                                          

    private static final long serialVersionUID = 1L;                                   

    @Id                                                                                
    @GeneratedValue(strategy = GenerationType.IDENTITY)                                
    @Basic(optional = false)                                                           
    @Column(name = "id")                                                               
    private Long id;                                                                   

    @Basic(optional = false)                                                           
    @Column(name = "nombre", nullable = false, length = 255)                           
    private String nombre;                                                             

    @ManyToOne(optional = false)                                                       
    @JoinColumn(name = "creado_por", referencedColumnName = "id", nullable = false)    
    private Usuario creadoPor;                                                         

    @Basic(optional = false)                                                           
    @Column(name = "fecha_creacion", nullable = false)                                 
    @Temporal(TemporalType.TIMESTAMP)                                                  
    private Date fechaCreacion;                                                        

    public Equipo() {                                                                  
        this.fechaCreacion = new Date();                                               
    }                                                                                  

    public Equipo(String nombre, Usuario creadoPor) {                                  
        this.nombre = nombre;                                                          
        this.creadoPor = creadoPor;                                                    
        this.fechaCreacion = new Date();                                               
    }                                                                                  

    public Long getId() {                                                              
        return id;                                                                     
    }                                                                                  

    public void setId(Long id) {                                                       
        this.id = id;                                                                  
    }                                                                                  

    public String getNombre() {                                                        
        return nombre;                                                                 
    }                                                                                  

    public void setNombre(String nombre) {                                             
        this.nombre = nombre;                                                          
    }                                                                                  

    public Usuario getCreadoPor() {                                                    
        return creadoPor;                                                              
    }                                                                                  

    public void setCreadoPor(Usuario creadoPor) {                                      
        this.creadoPor = creadoPor;                                                    
    }                                                                                  

    public Date getFechaCreacion() {                                                   
        return fechaCreacion;                                                          
    }                                                                                  

    public void setFechaCreacion(Date fechaCreacion) {                                 
        this.fechaCreacion = fechaCreacion;                                            
    }                                                                                  

    @Override                                                                          
    public int hashCode() {                                                            
        return Objects.hash(id);                                                       
    }                                                                                  

    @Override                                                                          
    public boolean equals(Object obj) {                                                
        if (this == obj) return true;                                                  
        if (obj == null || getClass() != obj.getClass()) return false;                 
        Equipo other = (Equipo) obj;                                                   
        return Objects.equals(id, other.id);                                           
    }                                                                                  

    @Override                                                                          
    public String toString() {                                                         
        return "Equipo{" + "id=" + id + ", nombre=" + nombre + '}';                    
    }                                                                                  
}                 
