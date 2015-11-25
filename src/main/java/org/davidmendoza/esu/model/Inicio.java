/*
 * The MIT License
 *
 * Copyright 2014 J. David Mendoza.
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author J. David Mendoza <jdmendozar@gmail.com>
 */
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Inicio implements Serializable {

    private String anio;
    private String trimestre;
    private String leccion;
    private String dia;
    private String tema;
    private Publicacion publicacion;
    private List<Publicacion> dialoga;
    private List<Publicacion> comunica;
    private List<Publicacion> articulos;
    private Publicacion video;
    private Publicacion podcast;
    private Publicacion versiculo;
    private Publicacion temaSeleccionado;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date hoy;
    private final SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd/MMM/yyyy", new Locale("es"));
    private String hoyLiga;
    private Boolean esHoy = Boolean.FALSE;

    public Inicio() {
    }

    /**
     * @return the anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }

    /**
     * @return the trimestre
     */
    public String getTrimestre() {
        return trimestre;
    }

    /**
     * @param trimestre the trimestre to set
     */
    public void setTrimestre(String trimestre) {
        this.trimestre = trimestre;
    }

    /**
     * @return the leccion
     */
    public String getLeccion() {
        return leccion;
    }

    /**
     * @param leccion the leccion to set
     */
    public void setLeccion(String leccion) {
        this.leccion = leccion;
    }

    /**
     * @return the dia
     */
    public String getDia() {
        return dia;
    }

    /**
     * @param dia the dia to set
     */
    public void setDia(String dia) {
        this.dia = dia;
    }

    /**
     * @return the tema
     */
    public String getTema() {
        return tema;
    }

    /**
     * @param tema the tema to set
     */
    public void setTema(String tema) {
        this.tema = tema;
    }

    /**
     * @return the publicacion
     */
    public Publicacion getPublicacion() {
        return publicacion;
    }

    /**
     * @param publicacion the publicacion to set
     */
    public void setPublicacion(Publicacion publicacion) {
        this.publicacion = publicacion;
    }

    /**
     * @return the dialoga
     */
    public List<Publicacion> getDialoga() {
        return dialoga;
    }

    /**
     * @param dialoga the dialoga to set
     */
    public void setDialoga(List<Publicacion> dialoga) {
        this.dialoga = dialoga;
    }

    /**
     * @return the comunica
     */
    public List<Publicacion> getComunica() {
        return comunica;
    }

    /**
     * @param comunica the comunica to set
     */
    public void setComunica(List<Publicacion> comunica) {
        this.comunica = comunica;
    }

    /**
     * @return the articulos
     */
    public List<Publicacion> getArticulos() {
        return articulos;
    }

    /**
     * @param articulos the articulos to set
     */
    public void setArticulos(List<Publicacion> articulos) {
        this.articulos = articulos;
    }

    /**
     * @return the video
     */
    public Publicacion getVideo() {
        return video;
    }

    /**
     * @param video the video to set
     */
    public void setVideo(Publicacion video) {
        this.video = video;
    }

    /**
     * @return the podcast
     */
    public Publicacion getPodcast() {
        return podcast;
    }

    /**
     * @param podcast the podcast to set
     */
    public void setPodcast(Publicacion podcast) {
        this.podcast = podcast;
    }

    /**
     * @return the versiculo
     */
    public Publicacion getVersiculo() {
        return versiculo;
    }

    /**
     * @param versiculo the versiculo to set
     */
    public void setVersiculo(Publicacion versiculo) {
        this.versiculo = versiculo;
    }

    /**
     * @return the temaSeleccionado
     */
    public Publicacion getTemaSeleccionado() {
        return temaSeleccionado;
    }

    /**
     * @param temaSeleccionado the temaSeleccionado to set
     */
    public void setTemaSeleccionado(Publicacion temaSeleccionado) {
        this.temaSeleccionado = temaSeleccionado;
    }

    /**
     * @return the hoy
     */
    public Date getHoy() {
        return hoy;
    }

    /**
     * @param hoy the hoy to set
     */
    public void setHoy(Date hoy) {
        this.hoy = hoy;
    }
    
    public String getHoyString() {
        String hoyString = sdf.format(hoy);
        hoyString = StringUtils.capitalize(hoyString);
        return hoyString;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.getAnio());
        hash = 29 * hash + Objects.hashCode(this.getTrimestre());
        hash = 29 * hash + Objects.hashCode(this.getLeccion());
        hash = 29 * hash + Objects.hashCode(this.getDia());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Inicio other = (Inicio) obj;
        if (!Objects.equals(this.anio, other.anio)) {
            return false;
        }
        if (!Objects.equals(this.trimestre, other.trimestre)) {
            return false;
        }
        if (!Objects.equals(this.leccion, other.leccion)) {
            return false;
        }
        return Objects.equals(this.getDia(), other.getDia());
    }

    @Override
    public String toString() {
        return "Inicio{" + "anio=" + anio + ", trimestre=" + trimestre + ", leccion=" + leccion + ", dia=" + dia + ", hoy=" + hoy + '}';
    }

    public String getDescripcion() {
        if (publicacion != null && publicacion.getArticulo() != null && StringUtils.isNotBlank(publicacion.getArticulo().getDescripcion())) {
            return publicacion.getArticulo().getDescripcion();
        } else {
            return StringUtils.EMPTY;
        }
    }

    /**
     * @return the hoyLiga
     */
    public String getHoyLiga() {
        return hoyLiga;
    }

    /**
     * @param hoyLiga the hoyLiga to set
     */
    public void setHoyLiga(String hoyLiga) {
        this.hoyLiga = hoyLiga;
    }

    /**
     * @return the esHoy
     */
    public Boolean getEsHoy() {
        return esHoy;
    }

    /**
     * @param esHoy the esHoy to set
     */
    public void setEsHoy(Boolean esHoy) {
        this.esHoy = esHoy;
    }

}
