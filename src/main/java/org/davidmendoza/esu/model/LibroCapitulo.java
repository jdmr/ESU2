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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Entity
@Table(name = "libros_capitulos")
@NamedQueries({
    @NamedQuery(name = "LibroCapitulo.findAll", query = "SELECT l FROM LibroCapitulo l"),
    @NamedQuery(name = "LibroCapitulo.findById", query = "SELECT l FROM LibroCapitulo l WHERE l.id = :id"),
    @NamedQuery(name = "LibroCapitulo.findByCapitulo", query = "SELECT l FROM LibroCapitulo l WHERE l.capitulo = :capitulo"),
    @NamedQuery(name = "LibroCapitulo.findByVersiculos", query = "SELECT l FROM LibroCapitulo l WHERE l.versiculos = :versiculos")})
public class LibroCapitulo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "capitulo", nullable = false)
    private short capitulo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "versiculos", nullable = false)
    private short versiculos;
    @JoinColumn(name = "libro_id", referencedColumnName = "id")
    @ManyToOne
    private Libro libro;

    public LibroCapitulo() {
    }

    public LibroCapitulo(Integer id) {
        this.id = id;
    }

    public LibroCapitulo(Integer id, short capitulo, short versiculos) {
        this.id = id;
        this.capitulo = capitulo;
        this.versiculos = versiculos;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public short getCapitulo() {
        return capitulo;
    }

    public void setCapitulo(short capitulo) {
        this.capitulo = capitulo;
    }

    public short getVersiculos() {
        return versiculos;
    }

    public void setVersiculos(short versiculos) {
        this.versiculos = versiculos;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
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
        if (!(object instanceof LibroCapitulo)) {
            return false;
        }
        LibroCapitulo other = (LibroCapitulo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.davidmendoza.esu.model.LibroCapitulo[ id=" + id + " ]";
    }
    
}
