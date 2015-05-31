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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.SnowballPorterFilterFactory;
import org.apache.solr.analysis.StandardTokenizerFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Entity
@Table(name = "articulos")
@NamedQueries({
    @NamedQuery(name = "Articulo.findAll", query = "SELECT a FROM Articulo a"),
    @NamedQuery(name = "Articulo.findById", query = "SELECT a FROM Articulo a WHERE a.id = :id"),
    @NamedQuery(name = "Articulo.findByVersion", query = "SELECT a FROM Articulo a WHERE a.version = :version"),
    @NamedQuery(name = "Articulo.findByContenido", query = "SELECT a FROM Articulo a WHERE a.contenido = :contenido"),
    @NamedQuery(name = "Articulo.findByDateCreated", query = "SELECT a FROM Articulo a WHERE a.dateCreated = :dateCreated"),
    @NamedQuery(name = "Articulo.findByDescripcion", query = "SELECT a FROM Articulo a WHERE a.descripcion = :descripcion"),
    @NamedQuery(name = "Articulo.findByLastUpdated", query = "SELECT a FROM Articulo a WHERE a.lastUpdated = :lastUpdated"),
    @NamedQuery(name = "Articulo.findByTitulo", query = "SELECT a FROM Articulo a WHERE a.titulo = :titulo"),
    @NamedQuery(name = "Articulo.findByVistas", query = "SELECT a FROM Articulo a WHERE a.vistas = :vistas")})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Indexed
@AnalyzerDef(name = "customanalyzer",
        tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
        filters = {
            @TokenFilterDef(factory = LowerCaseFilterFactory.class),
            @TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = {
                @Parameter(name = "language", value = "Spanish")
            })
        })
public class Articulo implements Serializable {

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
    @Field
    @Analyzer(definition = "customanalyzer")
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1000000)
    @Column(name = "contenido", nullable = false, length = 1000000)
    private String contenido;
    @Basic(optional = false)
    @NotNull
    @Column(name = "date_created", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
    @Field
    @Analyzer(definition = "customanalyzer")
    @Size(max = 10000)
    @Column(name = "descripcion", length = 10000)
    private String descripcion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "last_updated", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;
    @Field
    @Analyzer(definition = "customanalyzer")
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "titulo", nullable = false, length = 255)
    private String titulo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "vistas", nullable = false)
    private Integer vistas;
    @IndexedEmbedded
    @JoinColumn(name = "autor_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Usuario autor;
    @IndexedEmbedded
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "articulo")
    private List<Publicacion> publicaciones;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "articulo")
    private List<Vista> historial;

    public Articulo() {
    }

    public Articulo(Long id) {
        this.id = id;
    }

    public Articulo(Date dateCreated, Integer vistas) {
        this.dateCreated = dateCreated;
        this.vistas = vistas;
    }

    public Articulo(Long id, long version, String contenido, Date dateCreated, Date lastUpdated, String titulo, Integer vistas) {
        this.id = id;
        this.version = version;
        this.contenido = contenido;
        this.dateCreated = dateCreated;
        this.lastUpdated = lastUpdated;
        this.titulo = titulo;
        this.vistas = vistas;
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

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getVistas() {
        return vistas;
    }

    public void setVistas(Integer vistas) {
        this.vistas = vistas;
    }

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
    }

    public List<Publicacion> getPublicaciones() {
        return publicaciones;
    }

    public void setPublicaciones(List<Publicacion> publicaciones) {
        this.publicaciones = publicaciones;
    }

    public List<Vista> getHistorial() {
        return historial;
    }

    public void setHistorial(List<Vista> historial) {
        this.historial = historial;
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
        if (!(object instanceof Articulo)) {
            return false;
        }
        Articulo other = (Articulo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.davidmendoza.esu.model.Articulo[ id=" + id + " ]";
    }

}
