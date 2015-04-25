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
package org.davidmendoza.esu.dao.impl;

import java.util.Date;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.davidmendoza.esu.dao.BaseDao;
import org.davidmendoza.esu.dao.TrimestreDao;
import org.davidmendoza.esu.model.Trimestre;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author J. David Mendoza <jdmendozar@gmail.com>
 */
@Repository
@Transactional
public class TrimestreDaoHibernate extends BaseDao implements TrimestreDao {

    @Transactional(readOnly = true)
    @Override
    public Trimestre obtiene(Date fecha) {
        Query query = em.createNamedQuery("Trimestre.findByDate");
        query.setParameter("date", fecha);
        try {
            return (Trimestre) query.getSingleResult();
        } catch(NoResultException e) {
            log.error("No hay trimestre configurado para {}", fecha, e);
            return null;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Trimestre obtiene(String nombre) {
        Query query = em.createNamedQuery("Trimestre.findByNombre");
        query.setParameter("nombre", nombre);
        try {
            return (Trimestre) query.getSingleResult();
        } catch(NoResultException e) {
            log.error("No hay trimestre configurado para {}", nombre, e);
            return null;
        }
    }

}
