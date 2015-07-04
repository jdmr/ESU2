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

package org.davidmendoza.esu.dao;

import java.util.List;
import java.util.Map;
import org.davidmendoza.esu.model.Articulo;
import org.davidmendoza.esu.model.Publicacion;
import org.davidmendoza.esu.model.Usuario;

/**
 *
 * @author J. David Mendoza <jdmendozar@gmail.com>
 */
public interface PublicacionDao {

    public Publicacion obtiene(Integer anio, String trimestre, String leccion, String dia, String tipo);

    public List<Publicacion> obtiene(Integer anio, String trimestre, String leccion, String tipo);

    public Integer agregarVista(Articulo articulo);
    
    public void actualizaVistasDelDia();

    public List<Publicacion> publicaciones(Usuario autor);

    public List<Publicacion> publicacionesUnicasDeArticulos(Usuario usuario);

    public void nueva(Publicacion publicacion);

    public Long elimina(Long publicacionId);

    public List todas();

    public List<Publicacion> publicaciones(Articulo articulo);
}
