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
package org.davidmendoza.esu.service.impl;

import java.util.Date;
import org.davidmendoza.esu.dao.ArticuloRepository;
import org.davidmendoza.esu.model.Articulo;
import org.davidmendoza.esu.service.ArticuloService;
import org.davidmendoza.esu.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Service
@Transactional
public class ArticuloServiceImpl extends BaseService implements ArticuloService {
    
    @Autowired
    private ArticuloRepository articuloRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Articulo> lista(PageRequest pageRequest) {
        return articuloRepository.findAll(pageRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Articulo> busca(String filtro, PageRequest pageRequest) {
        StringBuilder sb = new StringBuilder();
        sb.append("%");
        sb.append(filtro);
        sb.append("%");
        return articuloRepository.findByTituloLikeIgnoreCaseOrDescripcionLikeIgnoreCaseOrAutor_ApellidoLikeIgnoreCaseOrAutor_NombreLikeIgnoreCase(sb.toString(), sb.toString(), sb.toString(), sb.toString(), pageRequest);
    }

    @Override
    public Articulo obtiene(Long articuloId) {
        return articuloRepository.findOne(articuloId);
    }

    @Override
    public Articulo elimina(Long articuloId) {
        Articulo articulo = articuloRepository.findOne(articuloId);
        articuloRepository.delete(articulo);
        return articulo;
    }

    @Override
    public void crea(Articulo articulo) {
        Date date = new Date();
        articulo.setDateCreated(date);
        articulo.setLastUpdated(date);
        articulo.setVistas(0);
        articuloRepository.save(articulo);
    }

    @Override
    public void actualiza(Articulo articulo) {
        Articulo old = articuloRepository.dateCreated(articulo.getId());
        Date date = new Date();
        articulo.setDateCreated(old.getDateCreated());
        articulo.setLastUpdated(date);
        articulo.setVistas(old.getVistas());
        articuloRepository.save(articulo);
    }
    
}
