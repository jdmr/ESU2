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

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Entity
@Table(name = "trimestres")
@NamedQueries({
    @NamedQuery(name = "Trimestre.findAll", query = "SELECT t FROM Trimestre t"),
    @NamedQuery(name = "Trimestre.findById", query = "SELECT t FROM Trimestre t WHERE t.id = :id"),
    @NamedQuery(name = "Trimestre.findByVersion", query = "SELECT t FROM Trimestre t WHERE t.version = :version"),
    @NamedQuery(name = "Trimestre.findByInicia", query = "SELECT t FROM Trimestre t WHERE t.inicia = :inicia"),
    @NamedQuery(name = "Trimestre.findByNombre", query = "SELECT t FROM Trimestre t WHERE t.nombre = :nombre"),
    @NamedQuery(name = "Trimestre.findByPublicado", query = "SELECT t FROM Trimestre t WHERE t.publicado = :publicado"),
    @NamedQuery(name = "Trimestre.findByTermina", query = "SELECT t FROM Trimestre t WHERE t.termina = :termina"),
    @NamedQuery(name = "Trimestre.findByDate", query = "SELECT t FROM Trimestre t WHERE :date BETWEEN t.inicia AND t.termina AND t.publicado = true")
})
public class Trimestre implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "version", nullable = false)
    private long version;
    @Basic(optional = false)
    @NotNull
    @Column(name = "inicia", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date inicia;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 6)
    @Column(name = "nombre", nullable = false, length = 6)
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Column(name = "publicado", nullable = false)
    private boolean publicado;
    @Basic(optional = false)
    @NotNull
    @Column(name = "termina", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date termina;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "padre")
    private List<Publicacion> publicaciones;

    public Trimestre() {
    }

    public Trimestre(Long id) {
        this.id = id;
    }

    public Trimestre(Long id, long version, Date inicia, String nombre, boolean publicado, Date termina) {
        this.id = id;
        this.version = version;
        this.inicia = inicia;
        this.nombre = nombre;
        this.publicado = publicado;
        this.termina = termina;
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

    public Date getInicia() {
        return inicia;
    }

    public void setInicia(Date inicia) {
        this.inicia = inicia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean getPublicado() {
        return publicado;
    }

    public void setPublicado(boolean publicado) {
        this.publicado = publicado;
    }

    public Date getTermina() {
        return termina;
    }

    public void setTermina(Date termina) {
        this.termina = termina;
    }

    public List<Publicacion> getPublicaciones() {
        return publicaciones;
    }

    public void setPublicaciones(List<Publicacion> publicaciones) {
        this.publicaciones = publicaciones;
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
        if (!(object instanceof Trimestre)) {
            return false;
        }
        Trimestre other = (Trimestre) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.davidmendoza.esu.model.Trimestre[ id=" + id + " ]";
    }
    
}
