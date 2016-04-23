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

import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.lang.StringUtils;
import org.davidmendoza.esu.dao.ArticuloRepository;
import org.davidmendoza.esu.dao.PublicacionRepository;
import org.davidmendoza.esu.dao.VistaRepository;
import org.davidmendoza.esu.model.Articulo;
import org.davidmendoza.esu.model.Publicacion;
import org.davidmendoza.esu.model.Usuario;
import org.davidmendoza.esu.model.Vista;
import org.davidmendoza.esu.service.BaseService;
import org.davidmendoza.esu.service.PublicacionService;
import org.davidmendoza.esu.service.TrimestreService;
import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
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
    private PublicacionRepository publicacionRepository;
    @Autowired
    private TrimestreService trimestreService;
    @Autowired
    private ArticuloRepository articuloRepository;
    @Autowired
    private VistaRepository vistaRepository;
    @Autowired
    private SendGrid sendgrid;

    @Transactional(readOnly = true)
    @Override
    public Publicacion obtiene(Integer anio, String trimestre, String leccion, String dia, String tipo) {
        if (tipo.equals("leccion")) {
            List<Publicacion> publicaciones = publicacionRepository.findByAnioAndTrimestreAndLeccionAndDiaAndTipoAndEstatus(anio, trimestre, leccion, dia, tipo, "PUBLICADO");
            if (publicaciones != null && !publicaciones.isEmpty()) {
                return publicaciones.get(0);
            }
        } else {
            List<Publicacion> publicaciones = publicacionRepository.findByAnioAndTrimestreAndLeccionAndTipoAndEstatus(anio, trimestre, leccion, tipo, "PUBLICADO");
            if (publicaciones != null && !publicaciones.isEmpty()) {
                return publicaciones.get(0);
            }
        }
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public Publicacion obtiene(String titulo) {
        log.debug("Buscando por {}", titulo);
        List<Publicacion> publicaciones = publicacionRepository.findByEstatusAndArticuloTitulo("PUBLICADO", titulo);
        if (publicaciones != null && !publicaciones.isEmpty()) {
            return publicaciones.get(0);
        }
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Publicacion> obtiene(Integer anio, String trimestre, String leccion, String tipo) {
        return publicacionRepository.findByAnioAndTrimestreAndLeccionAndTipoAndEstatus(anio, trimestre, leccion, tipo, "PUBLICADO");
    }

    @Override
    public Integer agregarVista(Articulo articulo) {
        Integer vistas = articuloRepository.vistas(articulo.getId());
        if (vistas == null) {
            vistas = 0;
        }
        vistas++;
        articuloRepository.agregarVista(vistas, articulo.getId());
        return vistas;
    }

    @Scheduled(cron = "0 0 2 * * ?")
    @Override
    public void actualizaVistasDelDia() {
        log.info("Iniciando proceso de actualizacion de vistas del dia.");
        Date fecha = new Date();
        List<Map> articulos = articuloRepository.articulosConVistas();
        for(Map a : articulos) {
            Articulo articulo = articuloRepository.findOne((Long)a.get("id"));
            Vista vista = new Vista((Integer) a.get("vistas"), fecha, articulo);
            vistaRepository.save(vista);
        }
        log.info("Finalizo proceso de actualizacion de vistas del dia de {} articulos.", articulos.size());

        SendGrid.Email email = new SendGrid.Email();

        email.addTo("jdmendozar@gmail.com");
        email.setFrom("contactoesu@um.edu.mx");
        email.setSubject("ESU:Finalizo proceso de actualizacion de vistas del dia");
        email.setHtml("<p>Finalizo proceso de actualizacion de vistas del dia de <strong>" + articulos.size() + "</strong> articulos</p>");

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
        return publicacionRepository.findByEstatusAndArticuloAutorIdOrderByDateCreated("PUBLICADO", autor.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Publicacion> publicacionesUnicasDeArticulos(Usuario usuario) {
        List<String> tipos = new ArrayList<>();
        tipos.add("dialoga");
        tipos.add("comunica");
        return publicacionRepository.findByEstatusAndArticuloAutorIdAndTipoInOrderByDateCreated("PUBLICADO", usuario.getId(), tipos);
    }

    @Override
    @CacheEvict(value = {"diaCache", "inicioCache"}, allEntries = true)
    public void nueva(Publicacion publicacion) {
        publicacion.setPadre(trimestreService.obtiene(publicacion.getAnio() + publicacion.getTrimestre()));
        publicacionRepository.save(publicacion);
    }

    @Override
    @CacheEvict(value = {"diaCache", "inicioCache"}, allEntries = true)
    public Long elimina(Long publicacionId) {
        publicacionRepository.delete(publicacionId);
        return publicacionId;
    }

    @Override
    public List<Publicacion> populares(Integer anio, String trimestre, String leccion, Integer posicion) {
        List<Publicacion> articulos = new ArrayList<>();
        List<Publicacion> a = publicacionRepository.findByAnioAndTrimestreAndLeccionAndTipoAndEstatus(anio, trimestre, leccion, "dialoga","PUBLICADO");
        List<Publicacion> b = publicacionRepository.findByAnioAndTrimestreAndLeccionAndTipoAndEstatus(anio, trimestre, leccion, "comunica","PUBLICADO");

        Calendar cal = Calendar.getInstance();
        Date date1 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date date2 = cal.getTime();
        
        List<Vista> dia1 = vistaRepository.vistas(date2, date1);
        
        date1 = date2;
        cal.add(Calendar.DAY_OF_YEAR, -2);
        date2 = cal.getTime();

        List<Vista> dia2 = vistaRepository.vistas(date2, date1);
        
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
            List<Long> ids = map.get(vistas);
            if (ids == null) {
                ids = new ArrayList<>();
            }
            ids.add(vista.getArticulo().getId());
            map.put(vistas, ids);
        }
        List<Integer> keys = new ArrayList<>(map.keySet());
        log.debug("Keys: {}", keys.size());
        List<Long> ids = new ArrayList<>();
        if (!keys.isEmpty()) {
            posicion = keys.size() - 1 - posicion;
            int i = posicion - 10;
            if (i < 0) {
                i = 0;
            }
            for (Integer key : keys.subList(i, posicion)) {
                List<Long> j = map.get(key);
                for (Long k : j) {
                    if (ids.size() < 10 && !ids.contains(k)) {
                        ids.add(k);
                    }
                }
            }
        }
        log.debug("Articulos: {}", ids.size());
        
        List<Publicacion> c = publicacionRepository.findByEstatusAndArticuloIdIn("PUBLICADO", ids);

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
        Set<Long> m = new HashSet<>();
        for (Publicacion publicacion : articulos) {
            if (!m.contains(publicacion.getArticulo().getId())) {
                publicaciones.add(publicacion);
                m.add(publicacion.getArticulo().getId());
            }
        }

        publicaciones.stream().forEach((z) -> {
            log.debug("{} : {} : {} : {} : {} : {}", z.getAnio(), z.getTrimestre(), z.getLeccion(), z.getTipo(), z.getDia(), z.getTema());
            if (StringUtils.isNotBlank(z.getArticulo().getDescripcion())) {
                String descripcion = z.getArticulo().getDescripcion();
                if (StringUtils.isNotBlank(descripcion) && !StringUtils.contains(descripcion, "iframe")) {
                    descripcion = new HtmlToPlainText().getPlainText(Jsoup.parse(descripcion));
                    z.getArticulo().setDescripcion(StringUtils.abbreviate(descripcion, 280));
                } else if (StringUtils.isNotBlank(descripcion)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("<div class='embed-responsive embed-responsive-16by9'>");
                    sb.append(descripcion);
                    sb.append("</div>");
                    z.getArticulo().setDescripcion(sb.toString());
                }
            } else {
                Articulo articulo = articuloRepository.findOne(z.getArticulo().getId());
                String contenido = articulo.getContenido();
                if (StringUtils.isNotBlank(contenido) && !StringUtils.contains(contenido, "iframe")) {
                    contenido = new HtmlToPlainText().getPlainText(Jsoup.parse(contenido));
                    z.getArticulo().setDescripcion(StringUtils.abbreviate(contenido, 280));
                } else if (StringUtils.isNotBlank(contenido)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("<div class='embed-responsive embed-responsive-16by9'>");
                    sb.append(contenido);
                    sb.append("</div>");
                    z.getArticulo().setDescripcion(sb.toString());
                }
            }
        });
        return publicaciones;
    }

}
