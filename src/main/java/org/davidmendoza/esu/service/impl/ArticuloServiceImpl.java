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

import com.sendgrid.SendGrid;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang.StringUtils;
import org.davidmendoza.esu.dao.ArticuloDao;
import org.davidmendoza.esu.dao.ArticuloRepository;
import org.davidmendoza.esu.dao.PublicacionDao;
import org.davidmendoza.esu.dao.UsuarioRepository;
import org.davidmendoza.esu.model.Articulo;
import org.davidmendoza.esu.model.ReporteArticulo;
import org.davidmendoza.esu.model.Usuario;
import org.davidmendoza.esu.service.ArticuloService;
import org.davidmendoza.esu.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
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
    @Autowired
    private ArticuloDao articuloDao;
    @Autowired
    private PublicacionDao publicacionDao;
    @Autowired
    private SendGrid sendgrid;
    @Autowired
    private UsuarioRepository usuarioRepository;

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
    @CacheEvict(value = {"diaCache", "inicioCache"}, allEntries = true)
    public Articulo obtiene(Long articuloId) {
        return articuloRepository.findOne(articuloId);
    }

    @Override
    @CacheEvict(value = {"diaCache", "inicioCache"}, allEntries = true)
    public Articulo elimina(Long articuloId) {
        Articulo articulo = articuloRepository.findOne(articuloId);
        articuloRepository.delete(articulo);
        return articulo;
    }

    @Override
    @CacheEvict(value = {"diaCache", "inicioCache"}, allEntries = true)
    public void crea(Articulo articulo) {
        Date date = new Date();
        articulo.setDateCreated(date);
        articulo.setLastUpdated(date);
        articulo.setVistas(0);
        articuloRepository.save(articulo);
    }

    @Override
    @CacheEvict(value = {"diaCache", "inicioCache"}, allEntries = true)
    public void actualiza(Articulo articulo) {
        Articulo old = articuloRepository.dateCreated(articulo.getId());
        Date date = new Date();
        articulo.setDateCreated(old.getDateCreated());
        articulo.setLastUpdated(date);
        articulo.setVistas(old.getVistas());
        articuloRepository.save(articulo);
    }

    @Override
    public List<ReporteArticulo> articulosDelMes(Date date) {
        List<Map<String, Object>> x = publicacionDao.todas();
        Map<Long, Object> publicaciones = new HashMap<>();
        Long articuloId = 0l;
        for (Map<String, Object> a : x) {
            if (!Objects.equals(articuloId, a.get("articuloId"))) {
                articuloId = (Long) a.get("articuloId");
                if (a.get("estatus").equals("PUBLICADO") && (a.get("tipo").equals("dialoga") || a.get("tipo").equals("comunica"))) {
                    publicaciones.put(articuloId, a);
                }
            }
        }

        List<Map<String, Object>> lista = articuloDao.articulosDelDia(date);

        log.debug("Lista: {}", lista.size());
        List<ReporteArticulo> articulos = new ArrayList<>();
        Map<Long, ReporteArticulo> mapa = new HashMap<>();
        for (Map<String, Object> a : lista) {
            log.debug("Agregando {}", a.get("articulo"));
            Map<String, Object> c = (Map) publicaciones.get((Long) a.get("articuloId"));
            if (c != null) {
                ReporteArticulo b = new ReporteArticulo(
                        (Long) a.get("articuloId"),
                        (String) a.get("articulo"),
                        (String) a.get("nombre") + " "
                        + (String) a.get("apellido"),
                        (Integer) a.get("vistas")
                );
                b.setUrl("http://escuelasabaticauniversitaria.org/"
                        + (c.get("tipo").equals("dialoga") ? "profundiza/" : "comparte/")
                        + c.get("anio")
                        + "/" + c.get("trimestre")
                        + "/" + c.get("leccion")
                        + "/" + c.get("tema"));

                articulos.add(b);
                mapa.put(b.getArticuloId(), b);
            }
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, -1);

        lista = articuloDao.articulosDelDia(cal.getTime());
        for (Map<String, Object> a : lista) {
            if (publicaciones.get((Long) a.get("articuloId")) != null) {
                ReporteArticulo b = mapa.get((Long) a.get("articuloId"));
                if (b != null) {
                    b.setVistas2((Integer) a.get("vistas"));
                }
            }
        }

        cal.add(Calendar.MONTH, -1);

        lista = articuloDao.articulosDelDia(cal.getTime());
        for (Map<String, Object> a : lista) {
            if (publicaciones.get((Long) a.get("articuloId")) != null) {
                ReporteArticulo b = mapa.get((Long) a.get("articuloId"));
                if (b != null) {
                    b.setVistas3((Integer) a.get("vistas"));
                }
            }
        }

        cal.add(Calendar.MONTH, -1);

        lista = articuloDao.articulosDelDia(cal.getTime());
        for (Map<String, Object> a : lista) {
            if (publicaciones.get((Long) a.get("articuloId")) != null) {
                ReporteArticulo b = mapa.get((Long) a.get("articuloId"));
                if (b != null) {
                    b.setVistas4((Integer) a.get("vistas"));
                }
            }
        }

        return articulos;
    }

    @Override
    public void enviarArticulosDelMes(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        List<ReporteArticulo> articulos = this.articulosDelMes(date);

        StringBuilder sb = new StringBuilder();
        sb.append("<style>");
        sb.append("@import url(http://fonts.googleapis.com/css?family=Ubuntu);");
        sb.append("body {");
        sb.append("font: 16px/1.625em 'Ubuntu',Tahoma,sans-serif;");
        sb.append("font-size-adjust:none;");
        sb.append("font-style:normal;");
        sb.append("font-variant:normal;");
        sb.append("font-weight:normal;");
        sb.append("text-rendering: optimizeLegibility;");
        sb.append("}");
        sb.append("tbody tr:nth-child(odd) {background-color: #f9f9f9;}");
        sb.append("thead tr th, tbody tr td {padding: 8px;}");
        sb.append("tbody tr td{border-top: 1px solid #ddd;}");
        sb.append("</style>");
        sb.append("<h1>Vista de articulos hasta el ").append(sdf.format(date)).append("</h1>");
        sb.append("<p>Gracias por contribuir con uno o algunos de los ").append(articulos.size()).append(" artículos. Debajo encontrará una lista de cada artículo con cuántas vistas del mismo tenemos registradas a la fecha y 3 meses anteriores para que pueda ver el comportamiento que ha tenido últimamente su artículo.</p>");
        sb.append("<p>Los instamos a seguir contribuyendo con nuevos artículos, promocionar los artículos ya existentes en facebook, twitter, instagram, etc. y, con la ayuda del Espíritu Santo, estos seguirán ayudando a difundir el mensaje de la Segunda Venida de nuestro Señor Jesucristo.</p>");
        sb.append("<p>Si no desea recibir estos correos, al final del correo hay una liga para ser eliminado de la lista.</p>");
        sb.append("<table>");
        sb.append("<thead>");
        sb.append("<tr>");
        sb.append("<th style='text-align:left;'>").append("Artículo").append("</th>");
        sb.append("<th style='text-align:left;'>").append("Autor").append("</th>");
        sb.append("<th style='text-align:right;'>").append("Vistas").append("</th>");
        sb.append("<th style='text-align:right;'>").append("Hace 1 mes").append("</th>");
        sb.append("<th style='text-align:right;'>").append("Hace 2 meses").append("</th>");
        sb.append("<th style='text-align:right;'>").append("Hace 3 meses").append("</th>");
        sb.append("</tr>");
        sb.append("</thead>");
        sb.append("<tbody>");
        for (ReporteArticulo a : articulos) {
            String nombre = StringUtils.abbreviate(a.getNombre(), 40);
            sb.append("<tr>");
            sb.append("<td>").append("<a href='").append(a.getUrl()).append("'>").append(nombre).append("</a></td>");
            sb.append("<td>").append(a.getAutor()).append("</td>");
            sb.append("<td style='text-align:right;'>").append(a.getVistas1()).append("</td>");
            sb.append("<td style='text-align:right;'>").append(a.getVistas2()).append("</td>");
            sb.append("<td style='text-align:right;'>").append(a.getVistas3()).append("</td>");
            sb.append("<td style='text-align:right;'>").append(a.getVistas4()).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</tbody>");
        sb.append("</table>");

        try {
            log.debug("Creando correo");

            for (Usuario usuario : usuarioRepository.findAll()) {
                if (usuario.getUsername().equals("editor@um.edu.mx")
                        || usuario.getUsername().equals("autor@um.edu.mx")
                        || usuario.getUsername().equals("usuario@um.edu.mx")
                        || usuario.getUsername().equals("admin@um.edu.mx")) {
                    continue;
                }
                SendGrid.Email email = new SendGrid.Email();
                email.addTo(usuario.getUsername());
                email.setFrom("contactoesu@um.edu.mx");
                email.setSubject("ESU:Vista de artículos hasta el " + sdf.format(date));
                email.setHtml(sb.toString());

                sendgrid.send(email);
            }
        } catch (Exception e) {
            log.error("No se pudo enviar correo", e);
            throw new RuntimeException("No se pudo enviar el correo: " + e.getMessage(), e);
        }

    }

}
