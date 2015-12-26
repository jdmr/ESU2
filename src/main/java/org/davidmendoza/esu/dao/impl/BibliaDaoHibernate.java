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
package org.davidmendoza.esu.dao.impl;

import java.util.List;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.davidmendoza.esu.dao.BaseDao;
import org.davidmendoza.esu.dao.BibliaDao;
import org.davidmendoza.esu.model.Rv2000;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Repository
@Transactional
public class BibliaDaoHibernate extends BaseDao implements BibliaDao {

    @Override
    @SuppressWarnings("unchecked")
    public List<Rv2000> biblia(Integer libro, Integer capitulo, Integer versiculo, Integer versiculos) {
        try {
            Query query = em.createQuery("select v.id from Rv2000 v where v.libro.id = :libro and v.capitulo = :capitulo and v.versiculo = :versiculo");
            query.setParameter("libro", libro);
            query.setParameter("capitulo", capitulo);
            query.setParameter("versiculo", versiculo);
            Long versiculoId = (Long) query.getSingleResult();

            if (versiculoId > 1) {
                versiculoId--;
            }
            query = em.createQuery("select v from Rv2000 v where v.id between :inicio and :fin order by v.id");
            query.setParameter("inicio", versiculoId);
            query.setParameter("fin", versiculoId + versiculos);
            return query.getResultList();
        } catch (NoResultException e) {
            log.error("No encontre ningun versiculo", e);
            throw new RuntimeException("No encontre ningun versiculo", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Rv2000> biblia(Long versiculoId, Integer versiculos) {
        try {
            if (versiculoId > 1) {
                versiculoId--;
            }
            log.debug("Biblia entre {} : {}", versiculoId, versiculoId + versiculos);
            Query query = em.createQuery("select v from Rv2000 v where v.id between :inicio and :fin order by v.id");
            query.setParameter("inicio", versiculoId);
            query.setParameter("fin", versiculoId + versiculos);
            return query.getResultList();
        } catch (NoResultException e) {
            log.error("No encontre ningun versiculo", e);
            throw new RuntimeException("No encontre ningun versiculo", e);
        }
    }

}
