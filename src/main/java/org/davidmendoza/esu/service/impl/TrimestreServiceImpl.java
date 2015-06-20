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

import java.util.Date;
import org.davidmendoza.esu.dao.TrimestreDao;
import org.davidmendoza.esu.dao.TrimestreRepository;
import org.davidmendoza.esu.model.Trimestre;
import org.davidmendoza.esu.service.BaseService;
import org.davidmendoza.esu.service.TrimestreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author J. David Mendoza <jdmendozar@gmail.com>
 */
@Service
@Transactional
public class TrimestreServiceImpl extends BaseService implements TrimestreService {

    @Autowired
    private TrimestreDao trimestreDao;
    @Autowired
    private TrimestreRepository trimestreRepository;

    @Transactional(readOnly = true)
    @Override
    public Trimestre obtiene(Date fecha) {
        return trimestreDao.obtiene(fecha);
    }

    @Transactional(readOnly = true)
    @Override
    public Trimestre obtiene(String nombre) {
        return trimestreDao.obtiene(nombre);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Trimestre> busca(String filtro, PageRequest pageRequest) {
        StringBuilder sb = new StringBuilder();
        sb.append("%");
        sb.append(filtro);
        sb.append("%");
        return trimestreRepository.findByNombreLikeIgnoreCase(sb.toString(), pageRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Trimestre> lista(PageRequest pageRequest) {
        return trimestreRepository.findAll(pageRequest);
    }

    @Override
    @CacheEvict(value = {"diaCache", "inicioCache"})
    public void crea(Trimestre trimestre) {
        trimestreRepository.save(trimestre);
    }

    @Transactional(readOnly = true)
    @Override
    public Trimestre obtiene(Long trimestreId) {
        return trimestreRepository.findOne(trimestreId);
    }

    @Override
    public void actualiza(Trimestre trimestre) {
        trimestreRepository.save(trimestre);
    }

    @Override
    @CacheEvict(value = {"diaCache", "inicioCache"})
    public void elimina(Long trimestreId) {
        trimestreRepository.delete(trimestreId);
    }

}
