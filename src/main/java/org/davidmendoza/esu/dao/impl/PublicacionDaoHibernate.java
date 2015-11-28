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

import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.davidmendoza.esu.dao.BaseDao;
import org.davidmendoza.esu.dao.PublicacionDao;
import org.davidmendoza.esu.model.Articulo;
import org.davidmendoza.esu.model.Publicacion;
import org.davidmendoza.esu.model.Usuario;
import org.davidmendoza.esu.model.Vista;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private SendGrid sendgrid;

    @Transactional(readOnly = true)
    @Override
    public Publicacion obtiene(Integer anio, String trimestre, String leccion, String dia, String tipo) {
        Query query;
        List<Publicacion> publicaciones;
        try {
            switch (tipo) {
                case "leccion":
                    query = em.createNamedQuery("Publicacion.findLeccion");
                    query.setParameter("anio", anio);
                    query.setParameter("trimestre", trimestre);
                    query.setParameter("leccion", leccion);
                    query.setParameter("dia", dia);
                    query.setParameter("tipo", tipo);
                    publicaciones = query.getResultList();
                    if (publicaciones != null && !publicaciones.isEmpty()) {
                        return publicaciones.get(0);
                    } else {
                        return null;
                    }
                case "versiculo":
                case "video":
                case "podcast":
                    query = em.createNamedQuery("Publicacion.findByTipo");
                    query.setParameter("anio", anio);
                    query.setParameter("trimestre", trimestre);
                    query.setParameter("leccion", leccion);
                    query.setParameter("tipo", tipo);
                    publicaciones = query.getResultList();
                    if (publicaciones != null && !publicaciones.isEmpty()) {
                        return publicaciones.get(0);
                    } else {
                        return null;
                    }
                default:
                    return null;
            }
        } catch (NoResultException e) {
            log.error("No se pudo obtener publicacion para {} {} {} {} {}", anio, trimestre, leccion, dia, tipo);
            log.error("No tuvo resultados", e);
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

        SendGrid.Email email = new SendGrid.Email();

        email.addTo("jdmendozar@gmail.com");
        email.setFrom("contactoesu@um.edu.mx");
        email.setSubject("ESU:Finalizo proceso de actualizacion de vistas del dia");
        email.setHtml("<p>Finalizo proceso de actualizacion de vistas del dia de <strong>" + results.size() + "</strong> articulos</p>");

        try {
            SendGrid.Response response = sendgrid.send(email);
            log.debug("Resultado de enviar correo: " + response);
        } catch (SendGridException e) {
            log.error("No se pudo enviar correo: " + e.getMessage(), e);
        }

    }

    @Transactional(readOnly = true)
    @Override
    public List<Publicacion> publicaciones(Usuario autor) {
        log.debug("Buscando publicaciones de autor {} : {}", autor.getNombreCompleto(), autor.getId());
        Query query = em.createQuery("select p from Publicacion p where p.articulo.autor.id = :autorId order by p.dateCreated");
        query.setParameter("autorId", autor.getId());
        List<Publicacion> publicaciones = query.getResultList();
        log.debug("Publicaciones: {}", publicaciones);
        return publicaciones;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Publicacion> publicacionesUnicasDeArticulos(Usuario autor) {
        Query query = em.createQuery("select p from Publicacion p where p.articulo.autor.id = :autorId and (p.tipo = 'dialoga' or p.tipo = 'comunica') and p.estatus = 'PUBLICADO' order by p.dateCreated");
        query.setParameter("autorId", autor.getId());
        List<Publicacion> publicaciones = query.getResultList();
        return publicaciones;
    }

    @Override
    public void nueva(Publicacion publicacion) {
        Date date = new Date();
        publicacion.setFecha(date);
        publicacion.setDateCreated(date);
        publicacion.setLastUpdated(date);
        em.persist(publicacion);
    }

    @Override
    public Long elimina(Long publicacionId) {
        Publicacion publicacion = (Publicacion) em.find(Publicacion.class, publicacionId);
        Articulo articulo = publicacion.getArticulo();
        Long articuloId = articulo.getId();
        em.remove(publicacion);
        return articuloId;
    }

    @Transactional(readOnly = true)
    @Override
    public List todas() {
        Query query = em.createQuery("select new map(p.id as publicacionId, p.tipo as tipo, p.estatus as estatus, p.articulo.id as articuloId, p.anio as anio, p.trimestre as trimestre, p.leccion as leccion, p.tema as tema) from Publicacion p order by p.articulo");
        return query.getResultList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Publicacion> publicaciones(Articulo articulo) {
        Query query = em.createQuery("select p from Publicacion p where p.estatus = 'PUBLICADO' and p.articulo.id = :articuloId");
        query.setParameter("articuloId", articulo.getId());
        return query.getResultList();
    }

    @Override
    public List<Publicacion> publicaciones(List<Long> articulos) {
        if (!articulos.isEmpty()) {
            Query query = em.createQuery("select new Publicacion("
                    + "p.id, "
                    + "p.anio, "
                    + "p.trimestre, "
                    + "p.leccion, "
                    + "p.tipo, "
                    + "p.dia, "
                    + "p.tema, "
                    + "p.articulo.id, "
                    + "p.articulo.titulo, "
                    + "p.articulo.descripcion, "
                    + "p.articulo.autor.nombre, "
                    + "p.articulo.autor.apellido) "
                    + "from Publicacion p where p.articulo.id in :articulos");
            query.setParameter("articulos", articulos);
            return query.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Long> vistasPopulares(Integer posicion) {
        Calendar cal = Calendar.getInstance();
        Date date1 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date date2 = cal.getTime();

        Query query = em.createQuery("select new Vista(v.cantidad, v.articulo.id) from Vista v where v.fecha between :fecha1 and :fecha2 order by v.articulo.id");
        query.setParameter("fecha1", date2);
        query.setParameter("fecha2", date1);
        List<Vista> dia1 = query.getResultList();
        log.debug("Dia1: {} : {} : {}", date2, date1, dia1.size());

        date1 = date2;
        cal.add(Calendar.DAY_OF_YEAR, -2);
        date2 = cal.getTime();
        query.setParameter("fecha1", date2);
        query.setParameter("fecha2", date1);
        List<Vista> dia2 = query.getResultList();
        log.debug("Dia2: {} : {} : {}", date2, date1, dia2.size());

        Map<Long, Vista> x = new HashMap<>();
        dia2.stream().forEach((vista) -> {
            x.put(vista.getArticulo().getId(), vista);
        });

        Map<Integer, List<Long>> map = new TreeMap<>();

        for (Vista vista : dia1) {
            Integer vistas;
            Vista y = x.get(vista.getArticulo().getId());
            if (y != null) {
                vistas = vista.getCantidad() - y.getCantidad();
            } else {
                vistas = vista.getCantidad();
            }
            List<Long> articulos = map.get(vistas);
            if (articulos == null) {
                articulos = new ArrayList<>();
            }
            articulos.add(vista.getArticulo().getId());
            map.put(vistas, articulos);
        }
        List<Integer> keys = new ArrayList<>(map.keySet());
        log.debug("Keys: {}", keys.size());
        List<Long> articulos = new ArrayList<>();
        if (!keys.isEmpty()) {
            posicion = keys.size() - 1 - posicion;
            int a = posicion - 10;
            if (a < 0) {
                a = 0;
            }
            for (Integer key : keys.subList(a, posicion)) {
                List<Long> b = map.get(key);
                for (Long c : b) {
                    if (articulos.size() < 10 && !articulos.contains(c)) {
                        articulos.add(c);
                    }
                }
            }
        }
        log.debug("Articulos: {}", articulos.size());

        return articulos;
    }

}
