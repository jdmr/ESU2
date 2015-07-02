/*
 * The MIT License
 *
 * Copyright 2015 J. David Mendoza.
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

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
public class ReporteArticulo implements Serializable {
    private Long articuloId;
    private String nombre;
    private String autor;
    private Integer vistas1 = 0;
    private Integer vistas2 = 0;
    private Integer vistas3 = 0;
    private Integer vistas4 = 0;

    public ReporteArticulo() {
    }

    public ReporteArticulo(Long articuloId, String nombre, String autor, Integer vistas1) {
        this.articuloId = articuloId;
        this.nombre = nombre;
        this.autor = autor;
        this.vistas1 = vistas1;
    }

    /**
     * @return the articuloId
     */
    public Long getArticuloId() {
        return articuloId;
    }

    /**
     * @param articuloId the articuloId to set
     */
    public void setArticuloId(Long articuloId) {
        this.articuloId = articuloId;
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
     * @return the autor
     */
    public String getAutor() {
        return autor;
    }

    /**
     * @param autor the autor to set
     */
    public void setAutor(String autor) {
        this.autor = autor;
    }

    /**
     * @return the vistas1
     */
    public Integer getVistas1() {
        return vistas1;
    }

    /**
     * @param vistas1 the vistas1 to set
     */
    public void setVistas1(Integer vistas1) {
        this.vistas1 = vistas1;
    }

    /**
     * @return the vistas2
     */
    public Integer getVistas2() {
        return vistas2;
    }

    /**
     * @param vistas2 the vistas2 to set
     */
    public void setVistas2(Integer vistas2) {
        this.vistas2 = vistas2;
    }

    /**
     * @return the vistas3
     */
    public Integer getVistas3() {
        return vistas3;
    }

    /**
     * @param vistas3 the vistas3 to set
     */
    public void setVistas3(Integer vistas3) {
        this.vistas3 = vistas3;
    }

    /**
     * @return the vistas4
     */
    public Integer getVistas4() {
        return vistas4;
    }

    /**
     * @param vistas4 the vistas4 to set
     */
    public void setVistas4(Integer vistas4) {
        this.vistas4 = vistas4;
    }
}
