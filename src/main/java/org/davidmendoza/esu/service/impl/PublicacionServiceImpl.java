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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.davidmendoza.esu.dao.ArticuloDao;
import org.davidmendoza.esu.dao.ArticuloRepository;
import org.davidmendoza.esu.dao.PublicacionDao;
import org.davidmendoza.esu.model.Articulo;
import org.davidmendoza.esu.model.Publicacion;
import org.davidmendoza.esu.model.Usuario;
import org.davidmendoza.esu.service.BaseService;
import org.davidmendoza.esu.service.PublicacionService;
import org.davidmendoza.esu.service.TrimestreService;
import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;
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
    @Autowired
    private ArticuloDao articuloDao;
    @Autowired
    private ArticuloRepository articuloRepository;

    @Transactional(readOnly = true)
    @Override
    public Publicacion obtiene(Integer anio, String trimestre, String leccion, String dia, String tipo) {
        return publicacionDao.obtiene(anio, trimestre, leccion, dia, tipo);
    }

    @Transactional(readOnly = true)
    @Override
    public Publicacion obtiene(String titulo) {
        log.debug("Buscando por {}", titulo);
        List<Articulo> articulos = articuloDao.buscarPorTitulo(titulo);
        if (!articulos.isEmpty()) {
            Articulo articulo = articulos.get(0);
            List<Publicacion> publicaciones = publicacionDao.publicaciones(articulo);
            if (!publicaciones.isEmpty()) {
                return publicaciones.get(0);
            }
        }
        return null;
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
        publicacion.setPadre(trimestreService.obtiene(publicacion.getAnio() + publicacion.getTrimestre()));
        publicacionDao.nueva(publicacion);
    }

    @Override
    @CacheEvict(value = {"diaCache", "inicioCache"}, allEntries = true)
    public Long elimina(Long publicacionId) {
        return publicacionDao.elimina(publicacionId);
    }

    @Override
    public List<Publicacion> populares(Integer anio, String trimestre, String leccion, Integer posicion) {
        List<Publicacion> articulos = new ArrayList<>();
        List<Publicacion> a = this.obtiene(anio, trimestre, leccion, "dialoga");
        List<Publicacion> b = this.obtiene(anio, trimestre, leccion, "comunica");

        List<Long> vistas = publicacionDao.vistasPopulares(posicion);
        List<Publicacion> c = publicacionDao.publicaciones(vistas);

        int max = Math.max(a.size(), b.size());

        for (int i = 0; i < max; i++) {
            if (a.size() > i) {
                articulos.add(a.get(i));
            }
            if (b.size() > i) {
                articulos.add(b.get(i));
            }
        }
        articulos.addAll(c);
        List<Publicacion> publicaciones = new ArrayList<>();
        Set<Long> ids = new HashSet<>();
        for (Publicacion publicacion : articulos) {
            if (!ids.contains(publicacion.getArticulo().getId())) {
                publicaciones.add(publicacion);
                ids.add(publicacion.getArticulo().getId());
            }
        }

        publicaciones.stream().forEach((x) -> {
            log.debug("{} : {} : {} : {} : {} : {}", x.getAnio(), x.getTrimestre(), x.getLeccion(), x.getTipo(), x.getDia(), x.getTema());
            if (StringUtils.isNotBlank(x.getArticulo().getDescripcion())) {
                String descripcion = x.getArticulo().getDescripcion();
                if (StringUtils.isNotBlank(descripcion) && !StringUtils.contains(descripcion, "iframe")) {
                    descripcion = new HtmlToPlainText().getPlainText(Jsoup.parse(descripcion));
                    x.getArticulo().setDescripcion(StringUtils.abbreviate(descripcion, 280));
                } else if (StringUtils.isNotBlank(descripcion)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("<div class='embed-responsive embed-responsive-16by9'>");
                    sb.append(descripcion);
                    sb.append("</div>");
                    x.getArticulo().setDescripcion(sb.toString());
                }
            } else {
                Articulo articulo = articuloRepository.findOne(x.getArticulo().getId());
                String contenido = articulo.getContenido();
                if (StringUtils.isNotBlank(contenido) && !StringUtils.contains(contenido, "iframe")) {
                    contenido = new HtmlToPlainText().getPlainText(Jsoup.parse(contenido));
                    x.getArticulo().setDescripcion(StringUtils.abbreviate(contenido, 280));
                } else if (StringUtils.isNotBlank(contenido)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("<div class='embed-responsive embed-responsive-16by9'>");
                    sb.append(contenido);
                    sb.append("</div>");
                    x.getArticulo().setDescripcion(sb.toString());
                }
            }
        });
        return publicaciones;
    }

}
