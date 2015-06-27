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

package org.davidmendoza.esu.service.impl;

import java.util.List;
import org.davidmendoza.esu.dao.PublicacionDao;
import org.davidmendoza.esu.model.Articulo;
import org.davidmendoza.esu.model.Publicacion;
import org.davidmendoza.esu.model.Usuario;
import org.davidmendoza.esu.service.BaseService;
import org.davidmendoza.esu.service.PublicacionService;
import org.davidmendoza.esu.service.TrimestreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author J. David Mendoza <jdmendozar@gmail.com>
 */
@Service
@Transactional
public class PublicacionServiceImpl extends BaseService implements PublicacionService {
    
    @Autowired
    private PublicacionDao publicacionDao;
    @Autowired
    private TrimestreService trimestreService;

    @Transactional(readOnly = true)
    @Override
    public Publicacion obtiene(Integer anio, String trimestre, String leccion, String dia, String tipo) {
        return publicacionDao.obtiene(anio, trimestre, leccion, dia, tipo);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Publicacion> obtiene(Integer anio, String trimestre, String leccion, String tipo) {
        return publicacionDao.obtiene(anio, trimestre, leccion, tipo);
    }

    @Override
    public Integer agregarVista(Articulo articulo) {
        return publicacionDao.agregarVista(articulo);
    }

    @Override
    public void actualizaVistasDelDia() {
        publicacionDao.actualizaVistasDelDia();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Publicacion> publicaciones(Usuario autor) {
        return publicacionDao.publicaciones(autor);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Publicacion> publicacionesUnicasDeArticulos(Usuario usuario) {
        return publicacionDao.publicacionesUnicasDeArticulos(usuario);
    }

    @Override
    @CacheEvict(value = {"diaCache", "inicioCache"}, allEntries = true)
    public void nueva(Publicacion publicacion) {
        publicacion.setPadre(trimestreService.obtiene(publicacion.getAnio()+publicacion.getTrimestre()));
        publicacionDao.nueva(publicacion);
    }

    @Override
    @CacheEvict(value = {"diaCache", "inicioCache"}, allEntries = true)
    public Long elimina(Long publicacionId) {
        return publicacionDao.elimina(publicacionId);
    }
    
}
