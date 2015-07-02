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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.Field;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Entity
@Table(name = "publicaciones")
@NamedQueries({
    @NamedQuery(name = "Publicacion.findAll", query = "SELECT p FROM Publicacion p"),
    @NamedQuery(name = "Publicacion.findById", query = "SELECT p FROM Publicacion p WHERE p.id = :id"),
    @NamedQuery(name = "Publicacion.findByVersion", query = "SELECT p FROM Publicacion p WHERE p.version = :version"),
    @NamedQuery(name = "Publicacion.findByAnio", query = "SELECT p FROM Publicacion p WHERE p.anio = :anio"),
    @NamedQuery(name = "Publicacion.findByDateCreated", query = "SELECT p FROM Publicacion p WHERE p.dateCreated = :dateCreated"),
    @NamedQuery(name = "Publicacion.findByDia", query = "SELECT p FROM Publicacion p WHERE p.dia = :dia"),
    @NamedQuery(name = "Publicacion.findByEstatus", query = "SELECT p FROM Publicacion p WHERE p.estatus = :estatus"),
    @NamedQuery(name = "Publicacion.findByFecha", query = "SELECT p FROM Publicacion p WHERE p.fecha = :fecha"),
    @NamedQuery(name = "Publicacion.findByLastUpdated", query = "SELECT p FROM Publicacion p WHERE p.lastUpdated = :lastUpdated"),
    @NamedQuery(name = "Publicacion.findLeccion", query = "SELECT p FROM Publicacion p WHERE p.anio = :anio AND p.trimestre = :trimestre AND p.leccion = :leccion AND p.dia = :dia AND p.tipo = :tipo AND p.estatus = 'PUBLICADO'"),
    @NamedQuery(name = "Publicacion.findByTema", query = "SELECT p FROM Publicacion p WHERE p.tema = :tema"),
    @NamedQuery(name = "Publicacion.findByTipo", query = "SELECT p FROM Publicacion p WHERE p.anio = :anio AND p.trimestre = :trimestre AND p.leccion = :leccion AND p.tipo = :tipo AND p.estatus = 'PUBLICADO'"),
    @NamedQuery(name = "Publicacion.findByTrimestre", query = "SELECT p FROM Publicacion p WHERE p.trimestre = :trimestre")})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Publicacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "version", nullable = false)
    private Long version;
    @Field
    @Basic(optional = false)
    @NotNull
    @Column(name = "anio", nullable = false)
    private Integer anio;
    @Basic(optional = false)
    @NotNull
    @Column(name = "date_created", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
    @Field
    @Size(max = 10)
    @Column(name = "dia", length = 10)
    private String dia;
    @Field
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "estatus", nullable = false, length = 10)
    private String estatus;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Basic(optional = false)
    @NotNull
    @Column(name = "last_updated", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;
    @Field
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 3)
    @Column(name = "leccion", nullable = false, length = 3)
    private String leccion;
    @Field
    @Size(max = 5)
    @Column(name = "tema", length = 5)
    private String tema;
    @Field
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "tipo", nullable = false, length = 10)
    private String tipo;
    @Field
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "trimestre", nullable = false, length = 2)
    private String trimestre;
    @JoinColumn(name = "es_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Articulo articulo;
    @JoinColumn(name = "padre_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Trimestre padre;
    @JoinColumn(name = "editor_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Usuario editor;

    public Publicacion() {
    }

    public Publicacion(Long id) {
        this.id = id;
    }

    public Publicacion(Long id, long version, int anio, Date dateCreated, String estatus, Date fecha, Date lastUpdated, String leccion, String tipo, String trimestre) {
        this.id = id;
        this.version = version;
        this.anio = anio;
        this.dateCreated = dateCreated;
        this.estatus = estatus;
        this.fecha = fecha;
        this.lastUpdated = lastUpdated;
        this.leccion = leccion;
        this.tipo = tipo;
        this.trimestre = trimestre;
    }

    public Publicacion(Long id, String tipo, String estatus, Articulo articulo) {
        this.id = id;
        this.estatus = estatus;
        this.tipo = tipo;
        this.articulo = articulo;
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

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getLeccion() {
        return leccion;
    }

    public void setLeccion(String leccion) {
        this.leccion = leccion;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTrimestre() {
        return trimestre;
    }

    public void setTrimestre(String trimestre) {
        this.trimestre = trimestre;
    }

    public Articulo getArticulo() {
        return articulo;
    }

    public void setArticulo(Articulo articulo) {
        this.articulo = articulo;
    }

    public Trimestre getPadre() {
        return padre;
    }

    public void setPadre(Trimestre padre) {
        this.padre = padre;
    }

    public Usuario getEditor() {
        return editor;
    }

    public void setEditor(Usuario editor) {
        this.editor = editor;
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
        if (!(object instanceof Publicacion)) {
            return false;
        }
        Publicacion other = (Publicacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.davidmendoza.esu.model.Publicacion[ id=" + id + " ]";
    }

    public String getNombre() {
        StringBuilder sb = new StringBuilder();
        sb.append(anio);
        sb.append(" ");
        sb.append(trimestre);
        sb.append(" ");
        sb.append(leccion);
        sb.append(" ");
        sb.append(tipo);
        switch (tipo) {
            case "leccion":
                sb.append(" ");
                sb.append(dia);
                break;
            case "dialoga":
            case "comunica":
                sb.append(" ");
                sb.append(tema);
                break;

        }
        sb.append(" ");
        sb.append(estatus);
        
        return sb.toString();
    }

}
