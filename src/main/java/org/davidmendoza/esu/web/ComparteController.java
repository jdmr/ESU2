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
package org.davidmendoza.esu.web;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.davidmendoza.esu.model.Inicio;
import org.davidmendoza.esu.model.Popular;
import org.davidmendoza.esu.model.Publicacion;
import org.davidmendoza.esu.service.InicioService;
import org.davidmendoza.esu.service.PerfilService;
import org.davidmendoza.esu.service.PublicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Controller
@RequestMapping("/comparte")
public class ComparteController extends BaseController {

    @Autowired
    private InicioService inicioService;
    @Autowired
    private PublicacionService publicacionService;
    @Autowired
    private PerfilService perfilService;

    @RequestMapping(method = RequestMethod.GET)
    public String comparte(HttpSession session, TimeZone timeZone) {
        String anio = (String) session.getAttribute("anio");
        if (StringUtils.isNotEmpty(anio)) {
            String trimestre = (String) session.getAttribute("trimestre");
            String leccion = (String) session.getAttribute("leccion");

            StringBuilder url = new StringBuilder();
            url.append("redirect:/comparte/");
            url.append(anio).append("/");
            url.append(trimestre).append("/");
            url.append(leccion).append("/");
            url.append("tema1");
            return url.toString();
        } else {
            Inicio inicio = inicioService.inicio(timeZone);
            if (inicio != null) {
                StringBuilder url = new StringBuilder();
                url.append("redirect:/comparte/");
                url.append(inicio.getAnio()).append("/");
                url.append(inicio.getTrimestre()).append("/");
                url.append(inicio.getLeccion()).append("/");
                url.append("tema1");
                return url.toString();
            } else {
                return "inicio/trimestre";
            }
        }
    }

    @RequestMapping(value = "/{anio}/{trimestre}/{leccion}/{tema}")
    public String tema(@ModelAttribute Inicio inicio, Model model, HttpSession session) {
        log.info("Comparte: {} : {} : {} : {}", inicio.getAnio(), inicio.getTrimestre(), inicio.getLeccion(), inicio.getTema());
                
        String tema = inicio.getTema();
        
        inicio = inicioService.inicio(inicio);
        
        boolean notFound = true;
        for (Publicacion publicacion : inicio.getComunica()) {
            if (publicacion.getTema().equals(tema)) {
                publicacion.getArticulo().setVistas(publicacionService.agregarVista(publicacion.getArticulo()));
                model.addAttribute("tema", publicacion);
                model.addAttribute("perfil", perfilService.obtienePorUsuario(publicacion.getArticulo().getAutor()));
                notFound = false;
                break;
            }
        }
        
        if (notFound) {
            Popular popular = publicacionService.obtieneSiguientePopularComunica(0);
            popular.getPublicacion().getArticulo().setVistas(publicacionService.agregarVista(popular.getPublicacion().getArticulo()));
            model.addAttribute("tema", popular.getPublicacion());
            model.addAttribute("posicion", popular.getId());
            model.addAttribute("perfil", perfilService.obtienePorUsuario(popular.getPublicacion().getArticulo().getAutor()));
//            return "redirect:/inicio";
        }

        Inicio hoy = inicioService.inicio(Calendar.getInstance());
        if (inicio.getAnio().equals(hoy.getAnio())
                && inicio.getTrimestre().equals(hoy.getTrimestre())
                && inicio.getLeccion().equals(hoy.getLeccion())
                && inicio.getDia().equals(hoy.getDia())) {
            inicio.setEsHoy(Boolean.TRUE);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("/estudia");
            sb.append("/").append(hoy.getAnio());
            sb.append("/").append(hoy.getTrimestre());
            sb.append("/").append(hoy.getLeccion());
            sb.append("/").append(hoy.getDia());
            inicio.setHoyLiga(sb.toString());
            inicio.setEsHoy(Boolean.FALSE);
        }
        
        model.addAttribute("comparte", inicio);

        return "comparte/tema";
    }
    
    @RequestMapping(value = "/popular/{posicion}", method = RequestMethod.GET)
    @ResponseBody
    public Map popular(@PathVariable Integer posicion) {
        Map resultado = new HashMap();
        Popular popular = publicacionService.obtieneSiguientePopularComunica(posicion);
        popular.getPublicacion().getArticulo().setVistas(publicacionService.agregarVista(popular.getPublicacion().getArticulo()));
        resultado.put("publicacion", popular.getPublicacion());
        resultado.put("posicion", popular.getId());
        resultado.put("perfil", perfilService.obtienePorUsuario(popular.getPublicacion().getArticulo().getAutor()));
        return resultado;
    } 
}
