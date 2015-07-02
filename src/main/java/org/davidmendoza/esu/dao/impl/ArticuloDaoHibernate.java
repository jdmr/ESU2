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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import org.davidmendoza.esu.dao.ArticuloDao;
import org.davidmendoza.esu.dao.BaseDao;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Repository
@Transactional(readOnly = true)
public class ArticuloDaoHibernate extends BaseDao implements ArticuloDao {

    @Override
    public List articulosDelDia(Date date) {
        log.debug("Buscando por fecha: {}", date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date fecha1 = cal.getTime();
        
        log.debug("Buscando por fecha: {} - {}", fecha1, date);
        Query query = em.createQuery("select new map(v.articulo.id as articuloId, v.articulo.titulo as articulo, v.articulo.autor.nombre as nombre, v.articulo.autor.apellido as apellido, v.cantidad as vistas) from Vista v where v.fecha between :fecha1 and :fecha2 order by v.cantidad desc");
        query.setParameter("fecha1", fecha1);
        query.setParameter("fecha2", date);
        
        return query.getResultList();
    }
    
}
