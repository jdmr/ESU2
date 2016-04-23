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
package org.davidmendoza.esu.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.davidmendoza.esu.model.Articulo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
public interface ArticuloRepository extends PagingAndSortingRepository<Articulo, Long> {

    public Page<Articulo> findByTituloLikeIgnoreCaseOrDescripcionLikeIgnoreCaseOrAutor_ApellidoLikeIgnoreCaseOrAutor_NombreLikeIgnoreCase(String titulo, String descripcion, String apellido, String nombre, Pageable pageable);

    @Query("select new Articulo(a.dateCreated, a.vistas) from Articulo a where a.id = :articuloId")
    public Articulo dateCreated(@Param("articuloId") Long articuloId);
    
    public List<Articulo> findByTitulo(String titulo);
    
    @Query("select new map(v.articulo.id as articuloId, v.articulo.titulo as articulo, v.articulo.autor.nombre as nombre, v.articulo.autor.apellido as apellido, v.cantidad as vistas) from Vista v where v.fecha between :fecha1 and :fecha2 order by v.cantidad desc")
    public List<Map<String, Object>> articulosDelDia(@Param("fecha1") Date fecha1, @Param("fecha2") Date fecha2);
    
    @Query("select a.vistas from Articulo a where a.id = :articuloId")
    public Integer vistas(@Param("articuloId") Long articuloId);

    @Modifying
    @Query("update Articulo a set a.vistas = :vistas where a.id = :articuloId")
    public int agregarVista(@Param("vistas") Integer vistas, @Param("articuloId") Long articuloId);
    
    @Query("select new map(a.id as id, a.vistas as vistas) from Articulo a")
    public List<Map> articulosConVistas();
}
