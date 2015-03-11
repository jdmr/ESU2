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
import javax.validation.constraints.Size;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Entity
@Table(name = "rv1995")
@NamedQueries({
    @NamedQuery(name = "Rv1995.findAll", query = "SELECT r FROM Rv1995 r"),
    @NamedQuery(name = "Rv1995.findById", query = "SELECT r FROM Rv1995 r WHERE r.id = :id"),
    @NamedQuery(name = "Rv1995.findByCapitulo", query = "SELECT r FROM Rv1995 r WHERE r.capitulo = :capitulo"),
    @NamedQuery(name = "Rv1995.findByVersiculo", query = "SELECT r FROM Rv1995 r WHERE r.versiculo = :versiculo"),
    @NamedQuery(name = "Rv1995.findByTexto", query = "SELECT r FROM Rv1995 r WHERE r.texto = :texto")})
public class Rv1995 implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "capitulo", nullable = false)
    private int capitulo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "versiculo", nullable = false)
    private int versiculo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "texto", nullable = false, length = 2147483647)
    private String texto;
    @JoinColumn(name = "libro_id", referencedColumnName = "id")
    @ManyToOne
    private Libro libro;

    public Rv1995() {
    }

    public Rv1995(Long id) {
        this.id = id;
    }

    public Rv1995(Long id, int capitulo, int versiculo, String texto) {
        this.id = id;
        this.capitulo = capitulo;
        this.versiculo = versiculo;
        this.texto = texto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCapitulo() {
        return capitulo;
    }

    public void setCapitulo(int capitulo) {
        this.capitulo = capitulo;
    }

    public int getVersiculo() {
        return versiculo;
    }

    public void setVersiculo(int versiculo) {
        this.versiculo = versiculo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
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
        if (!(object instanceof Rv1995)) {
            return false;
        }
        Rv1995 other = (Rv1995) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.davidmendoza.esu.model.Rv1995[ id=" + id + " ]";
    }
    
}
