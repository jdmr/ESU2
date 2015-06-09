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
package org.davidmendoza.esu.web.admin;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.davidmendoza.esu.model.Inicio;
import org.davidmendoza.esu.model.Publicacion;
import org.davidmendoza.esu.service.InicioService;
import org.davidmendoza.esu.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Controller
@RequestMapping("/rss")
public class RSSController extends BaseController {

    @Autowired
    private InicioService inicioService;

    @RequestMapping(method = RequestMethod.GET)
    public String rss() {
        return "redirect:/rss/rss_2.0";
    }

    @RequestMapping(value = "/rss_2.0", method = RequestMethod.GET)
    public void rss2(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        try {
            createRSSFeed("rss_2.0", request, response, session);
        } catch (IOException | FeedException e) {
            log.error("No se pudo crear el rss", e);
            throw new RuntimeException("No se pudo crear el RSS", e);
        }
    }

    @RequestMapping(value = "/rss_1.0", method = RequestMethod.GET)
    public void rss1(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        try {
            createRSSFeed("rss_1.0", request, response, session);
        } catch (IOException | FeedException e) {
            log.error("No se pudo crear el rss", e);
            throw new RuntimeException("No se pudo crear el RSS", e);
        }
    }

    @RequestMapping(value = "/rss_0.94", method = RequestMethod.GET)
    public void rss94(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        try {
            createRSSFeed("rss_0.94", request, response, session);
        } catch (IOException | FeedException e) {
            log.error("No se pudo crear el rss", e);
            throw new RuntimeException("No se pudo crear el RSS", e);
        }
    }

    @RequestMapping(value = "/rss_0.93", method = RequestMethod.GET)
    public void rss93(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        try {
            createRSSFeed("rss_0.93", request, response, session);
        } catch (IOException | FeedException e) {
            log.error("No se pudo crear el rss", e);
            throw new RuntimeException("No se pudo crear el RSS", e);
        }
    }

    @RequestMapping(value = "/rss_0.92", method = RequestMethod.GET)
    public void rss92(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        try {
            createRSSFeed("rss_0.92", request, response, session);
        } catch (IOException | FeedException e) {
            log.error("No se pudo crear el rss", e);
            throw new RuntimeException("No se pudo crear el RSS", e);
        }
    }

    @RequestMapping(value = "/atom_0.3", method = RequestMethod.GET)
    public void atom3(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        try {
            createRSSFeed("atom_0.3", request, response, session);
        } catch (IOException | FeedException e) {
            log.error("No se pudo crear el rss", e);
            throw new RuntimeException("No se pudo crear el RSS", e);
        }
    }

    private void createRSSFeed(String feedType, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException, FeedException {
        Inicio inicio = new Inicio();
        String anio = (String) session.getAttribute("anio");
        if (StringUtils.isNotEmpty(anio)) {
            String trimestre = (String) session.getAttribute("trimestre");
            String leccion = (String) session.getAttribute("leccion");
            String dia = (String) session.getAttribute("dia");
            inicio.setAnio(anio);
            inicio.setTrimestre(trimestre);
            inicio.setLeccion(leccion);
            inicio.setDia(dia);

            inicio = inicioService.inicio(inicio);
        } else {
            inicio = inicioService.inicio(Calendar.getInstance());
        }

        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType(feedType);

        feed.setTitle("EscuelaSabaticaUniversitaria.ORG");
        feed.setLink(request.getRequestURL().append("/rss/").append(feedType).toString());
        feed.setDescription("Lección diaria de la Escuela Sabática de la Iglesia Adventista del Séptimo Día y Temas Semanales basados en las lecciones.");

        List<SyndEntry> entries = new ArrayList<>();
        SyndEntry entry;
        SyndContent description;

        if (inicio.getVersiculo() != null && inicio.getPublicacion() != null) {
            entry = new SyndEntryImpl();
            entry.setTitle("Versículo de Memoria");
            entry.setLink(request.getRequestURL()
                    .append("/estudia/")
                    .append(inicio.getVersiculo().getAnio())
                    .append("/")
                    .append(inicio.getVersiculo().getTrimestre())
                    .append("/")
                    .append(inicio.getVersiculo().getLeccion())
                    .append("/")
                    .append(inicio.getPublicacion().getDia())
                    .toString());
            entry.setPublishedDate(inicio.getHoy());
            description = new SyndContentImpl();
            description.setType("text/html");
            description.setValue(inicio.getVersiculo().getArticulo().getContenido());
            entry.setDescription(description);
            entries.add(entry);
        }

        if (inicio.getPublicacion() != null) {
            entry = new SyndEntryImpl();
            entry.setTitle(inicio.getPublicacion().getArticulo().getTitulo());
            entry.setLink(request.getRequestURL()
                    .append("/estudia/")
                    .append(inicio.getPublicacion().getAnio())
                    .append("/")
                    .append(inicio.getPublicacion().getTrimestre())
                    .append("/")
                    .append(inicio.getPublicacion().getLeccion())
                    .append("/")
                    .append(inicio.getPublicacion().getDia())
                    .toString());
            entry.setPublishedDate(inicio.getHoy());
            description = new SyndContentImpl();
            description.setType("text/plain");
            description.setValue(inicio.getPublicacion().getArticulo().getDescripcion());
            entry.setDescription(description);
            entries.add(entry);
        }

        for (Publicacion publicacion : inicio.getDialoga()) {
            entry = new SyndEntryImpl();
            entry.setTitle(publicacion.getArticulo().getTitulo());
            entry.setLink(request.getRequestURL()
                    .append("/profundiza/")
                    .append(publicacion.getAnio())
                    .append("/")
                    .append(publicacion.getTrimestre())
                    .append("/")
                    .append(publicacion.getLeccion())
                    .append("/")
                    .append(publicacion.getTema())
                    .toString());
            entry.setPublishedDate(inicio.getHoy());
            entry.setAuthor(publicacion.getArticulo().getAutor().getNombreCompleto());
            description = new SyndContentImpl();
            description.setType("text/plain");
            description.setValue(publicacion.getArticulo().getDescripcion());
            entry.setDescription(description);
            entries.add(entry);
        }

        for (Publicacion publicacion : inicio.getComunica()) {
            entry = new SyndEntryImpl();
            entry.setTitle(publicacion.getArticulo().getTitulo());
            entry.setLink(request.getRequestURL()
                    .append("/comunica/")
                    .append(publicacion.getAnio())
                    .append("/")
                    .append(publicacion.getTrimestre())
                    .append("/")
                    .append(publicacion.getLeccion())
                    .append("/")
                    .append(publicacion.getTema())
                    .toString());
            entry.setPublishedDate(inicio.getHoy());
            entry.setAuthor(publicacion.getArticulo().getAutor().getNombreCompleto());
            description = new SyndContentImpl();
            description.setType("text/plain");
            description.setValue(publicacion.getArticulo().getDescripcion());
            entry.setDescription(description);
            entries.add(entry);
        }

        feed.setEntries(entries);

        response.setContentType("application/xml");
        try (Writer writer = response.getWriter()) {
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(feed, writer);
        }

    }
}
