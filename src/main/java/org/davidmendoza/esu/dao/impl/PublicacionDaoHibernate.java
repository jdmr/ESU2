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
import java.util.List;
import java.util.Map;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.davidmendoza.esu.dao.BaseDao;
import org.davidmendoza.esu.dao.PublicacionDao;
import org.davidmendoza.esu.model.Articulo;
import org.davidmendoza.esu.model.Publicacion;
import org.davidmendoza.esu.model.Vista;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author J. David Mendoza <jdmendozar@gmail.com>
 */
@Repository
@Transactional
public class PublicacionDaoHibernate extends BaseDao implements PublicacionDao {

    @Transactional(readOnly = true)
    @Override
    public Publicacion obtiene(Integer anio, String trimestre, String leccion, String dia, String tipo) {
        Query query;
        try {
            switch (tipo) {
                case "leccion":
                    query = em.createNamedQuery("Publicacion.findLeccion");
                    query.setParameter("anio", anio);
                    query.setParameter("trimestre", trimestre);
                    query.setParameter("leccion", leccion);
                    query.setParameter("dia", dia);
                    query.setParameter("tipo", tipo);
                    return (Publicacion) query.getSingleResult();
                case "versiculo":
                case "video":
                case "podcast":
                    query = em.createNamedQuery("Publicacion.findByTipo");
                    query.setParameter("anio", anio);
                    query.setParameter("trimestre", trimestre);
                    query.setParameter("leccion", leccion);
                    query.setParameter("tipo", tipo);
                    return (Publicacion) query.getSingleResult();
                default:
                    return null;
            }
        } catch (NoResultException e) {
            log.error("No se pudo obtener publicacion para {} {} {} {} {}", anio, trimestre, leccion, dia, tipo);
            return null;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Publicacion> obtiene(Integer anio, String trimestre, String leccion, String tipo) {
        Query query = em.createNamedQuery("Publicacion.findByTipo");
        query.setParameter("anio", anio);
        query.setParameter("trimestre", trimestre);
        query.setParameter("leccion", leccion);
        query.setParameter("tipo", tipo);
        return query.getResultList();
    }

    @Override
    public Integer agregarVista(Articulo articulo) {
        Query query = em.createQuery("select a.vistas from Articulo a where a.id = :articuloId");
        query.setParameter("articuloId", articulo.getId());
        Integer vistas = 0;
        try {
            vistas = (Integer) query.getSingleResult();
        } catch (NoResultException e) {
            // do nothing
        }
        vistas++;

        query = em.createQuery("update Articulo a set a.vistas = :vistas where a.id = :articuloId");
        query.setParameter("vistas", vistas);
        query.setParameter("articuloId", articulo.getId());
        query.executeUpdate();

        return vistas;
    }

    @Async
    @Scheduled(cron = "0 0 2 * * ?")
    @Override
    public void actualizaVistasDelDia() {
        log.info("Iniciando proceso de actualizacion de vistas del dia.");
        Date fecha = new Date();
        Query query = em.createQuery("select new map(a.id as id, a.vistas as vistas) from Articulo a");
        List<Map> results = query.getResultList();
        for (Map result : results) {
            Articulo articulo = em.getReference(Articulo.class, result.get("id"));
            Vista vista = new Vista((Integer) result.get("vistas"), fecha, articulo);
            em.persist(vista);
        }
        log.info("Finalizo proceso de actualizacion de vistas del dia de {} articulos.", results.size());
    }

}
