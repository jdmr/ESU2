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
import org.davidmendoza.esu.dao.BaseDao;
import org.davidmendoza.esu.dao.BusquedaDao;
import org.davidmendoza.esu.model.Articulo;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Repository
@Transactional
public class BusquedaDaoHibernate extends BaseDao implements BusquedaDao {

    @Override
    public void indexa() {
        try {
            FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            log.error("No se pudo indexar aplicacion", e);
            throw new RuntimeException("No se pudo indexar aplicacion", e);
        }
    }

    @Override
    public List<Articulo> buscar(String filtro) {
        // get the full text entity manager
        FullTextEntityManager fullTextEntityManager
                = org.hibernate.search.jpa.Search.
                getFullTextEntityManager(em);

        // create the query using Hibernate Search query DSL
        QueryBuilder queryBuilder
                = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(Articulo.class).get();

        // a very basic query by keywords
        org.apache.lucene.search.Query query
                = queryBuilder
                .keyword()
                .onFields("titulo", "descripcion", "contenido", "usuario.apellido", "usuario.nombre")
                .matching(filtro)
                .createQuery();

        // wrap Lucene query in an Hibernate Query object
        org.hibernate.search.jpa.FullTextQuery jpaQuery
                = fullTextEntityManager.createFullTextQuery(query, Articulo.class);

        // execute search and return results (sorted by relevance as default)
        @SuppressWarnings("unchecked")
        List<Articulo> results = jpaQuery.getResultList();
        return results;
    }

}
