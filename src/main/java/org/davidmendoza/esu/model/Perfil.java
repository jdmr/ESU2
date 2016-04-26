/*
 * The MIT License
 *
 * Copyright 2015 J. David Mendoza <jdmendoza@swau.edu>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.davidmendoza.esu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Type;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Entity
@Table(name = "perfiles")
@NamedQueries({
    @NamedQuery(name = "Perfil.findAll", query = "SELECT p FROM Perfil p"),
    @NamedQuery(name = "Perfil.findById", query = "SELECT p FROM Perfil p WHERE p.id = :id"),
    @NamedQuery(name = "Perfil.findByVersion", query = "SELECT p FROM Perfil p WHERE p.version = :version"),
    @NamedQuery(name = "Perfil.findByNombreImagen", query = "SELECT p FROM Perfil p WHERE p.nombreImagen = :nombreImagen"),
    @NamedQuery(name = "Perfil.findByTamano", query = "SELECT p FROM Perfil p WHERE p.tamano = :tamano"),
    @NamedQuery(name = "Perfil.findByTexto", query = "SELECT p FROM Perfil p WHERE p.texto = :texto"),
    @NamedQuery(name = "Perfil.findByTipoContenido", query = "SELECT p FROM Perfil p WHERE p.tipoContenido = :tipoContenido")})
public class Perfil implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "version", nullable = false)
    @JsonIgnore
    private long version;
    @Type(type="org.hibernate.type.BinaryType")
    @Column(name = "archivo")
    @JsonIgnore
    private byte[] archivo;
    @Size(max = 255)
    @Column(name = "nombre_imagen", length = 255)
    @JsonIgnore
    private String nombreImagen;
    @Column(name = "tamano")
    @JsonIgnore
    private Long tamano;
    @Size(max = 2000)
    @Column(name = "texto", length = 2000)
    private String texto;
    @Size(max = 255)
    @Column(name = "tipo_contenido", length = 255)
    @JsonIgnore
    private String tipoContenido;
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    @JsonIgnore
    private Usuario usuario;
    @Transient
    @JsonIgnore
    private List<Publicacion> publicacionesUnicas = new ArrayList<>();

    public Perfil() {
    }

    public Perfil(Long id) {
        this.id = id;
    }

    public Perfil(Long id, long version) {
        this.id = id;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public byte[] getArchivo() {
        return archivo;
    }

    public void setArchivo(byte[] archivo) {
        this.archivo = archivo;
    }

    public String getNombreImagen() {
        return nombreImagen;
    }

    public void setNombreImagen(String nombreImagen) {
        this.nombreImagen = nombreImagen;
    }

    public Long getTamano() {
        return tamano;
    }

    public void setTamano(Long tamano) {
        this.tamano = tamano;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getTipoContenido() {
        return tipoContenido;
    }

    public void setTipoContenido(String tipoContenido) {
        this.tipoContenido = tipoContenido;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Perfil)) {
            return false;
        }
        Perfil other = (Perfil) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.davidmendoza.esu.model.Perfil[ id=" + id + " ]";
    }

    /**
     * @return the publicacionesUnicas
     */
    public List<Publicacion> getPublicacionesUnicas() {
        return publicacionesUnicas;
    }

    /**
     * @param publicacionesUnicas the publicacionesUnicas to set
     */
    public void setPublicacionesUnicas(List<Publicacion> publicacionesUnicas) {
        this.publicacionesUnicas = publicacionesUnicas;
    }
    
}
