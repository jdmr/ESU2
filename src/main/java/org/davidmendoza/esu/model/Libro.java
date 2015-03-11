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
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Entity
@Table(name = "libros", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"nombre"})})
@NamedQueries({
    @NamedQuery(name = "Libro.findAll", query = "SELECT l FROM Libro l"),
    @NamedQuery(name = "Libro.findById", query = "SELECT l FROM Libro l WHERE l.id = :id"),
    @NamedQuery(name = "Libro.findByNombre", query = "SELECT l FROM Libro l WHERE l.nombre = :nombre"),
    @NamedQuery(name = "Libro.findByCapitulos", query = "SELECT l FROM Libro l WHERE l.capitulos = :capitulos")})
public class Libro implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "nombre", nullable = false, length = 64)
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Column(name = "capitulos", nullable = false)
    private short totalCapitulos;
    @OneToMany(mappedBy = "libro")
    private List<Rv1989> rv1989List;
    @OneToMany(mappedBy = "libro")
    private List<Rv1865> rv1865List;
    @OneToMany(mappedBy = "libro")
    private List<Rv1995> rv1995List;
    @OneToMany(mappedBy = "libro")
    private List<Rv1960> rv1960List;
    @OneToMany(mappedBy = "libro")
    private List<Rv2000> rv2000List;
    @OneToMany(mappedBy = "libro")
    private List<LibroCapitulo> capitulos;

    public Libro() {
    }

    public Libro(Integer id) {
        this.id = id;
    }

    /**
     * @return the serialVersionUID
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the totalCapitulos
     */
    public short getTotalCapitulos() {
        return totalCapitulos;
    }

    /**
     * @param totalCapitulos the totalCapitulos to set
     */
    public void setTotalCapitulos(short totalCapitulos) {
        this.totalCapitulos = totalCapitulos;
    }

    /**
     * @return the rv1989List
     */
    public List<Rv1989> getRv1989List() {
        return rv1989List;
    }

    /**
     * @param rv1989List the rv1989List to set
     */
    public void setRv1989List(List<Rv1989> rv1989List) {
        this.rv1989List = rv1989List;
    }

    /**
     * @return the rv1865List
     */
    public List<Rv1865> getRv1865List() {
        return rv1865List;
    }

    /**
     * @param rv1865List the rv1865List to set
     */
    public void setRv1865List(List<Rv1865> rv1865List) {
        this.rv1865List = rv1865List;
    }

    /**
     * @return the rv1995List
     */
    public List<Rv1995> getRv1995List() {
        return rv1995List;
    }

    /**
     * @param rv1995List the rv1995List to set
     */
    public void setRv1995List(List<Rv1995> rv1995List) {
        this.rv1995List = rv1995List;
    }

    /**
     * @return the rv1960List
     */
    public List<Rv1960> getRv1960List() {
        return rv1960List;
    }

    /**
     * @param rv1960List the rv1960List to set
     */
    public void setRv1960List(List<Rv1960> rv1960List) {
        this.rv1960List = rv1960List;
    }

    /**
     * @return the rv2000List
     */
    public List<Rv2000> getRv2000List() {
        return rv2000List;
    }

    /**
     * @param rv2000List the rv2000List to set
     */
    public void setRv2000List(List<Rv2000> rv2000List) {
        this.rv2000List = rv2000List;
    }

    /**
     * @return the capitulos
     */
    public List<LibroCapitulo> getCapitulos() {
        return capitulos;
    }

    /**
     * @param capitulos the capitulos to set
     */
    public void setCapitulos(List<LibroCapitulo> capitulos) {
        this.capitulos = capitulos;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Libro)) {
            return false;
        }
        Libro other = (Libro) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.davidmendoza.esu.model.Libro[ id=" + getId() + " ]";
    }
    
}
