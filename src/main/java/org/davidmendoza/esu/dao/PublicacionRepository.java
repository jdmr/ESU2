/*
 * The MIT License
 *
 * Copyright 2016 J. David Mendoza.
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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.davidmendoza.esu.model.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {

    public List<Publicacion> findByAnioAndTrimestreAndLeccionAndDiaAndTipoAndEstatus(Integer anio, String trimestre, String leccion, String dia, String tipo, String estatus);

    public List<Publicacion> findByAnioAndTrimestreAndLeccionAndTipoAndEstatus(Integer anio, String trimestre, String leccion, String tipo, String estatus);
    
    public List<Publicacion> findByEstatusAndArticuloId(String estatus, Long articuloId);
    
    public List<Publicacion> findByEstatusAndArticuloTitulo(String estatus, String articuloTitulo);
    
    public List<Publicacion> findByEstatusAndArticuloAutorIdOrderByDateCreated(String estatus, Long articuloAutorId);
    
    public List<Publicacion> findByEstatusAndArticuloAutorIdAndTipoInOrderByDateCreated(String estatus, Long articuloAutorId, Collection<String> tipos);
    
    public List<Publicacion> findByEstatusAndArticuloIdIn(String estatus, Collection<Long> ids);
    
    @Query("select new map(p.id as publicacionId, p.tipo as tipo, p.estatus as estatus, p.articulo.id as articuloId, p.anio as anio, p.trimestre as trimestre, p.leccion as leccion, p.tema as tema) from Publicacion p order by p.articulo")
    public List<Map<String, Object>> todas();

}
