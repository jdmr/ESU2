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

import java.util.TimeZone;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.davidmendoza.esu.model.Inicio;
import org.davidmendoza.esu.service.InicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Controller
@RequestMapping({"/", "/inicio"})
public class InicioController extends BaseController {

    @Autowired
    private InicioService inicioService;

    @RequestMapping(method = RequestMethod.GET)
    public String inicio(HttpSession session, TimeZone timeZone) {
        log.debug("Mostrando inicio");
        String anio = (String) session.getAttribute("anio");
        if (StringUtils.isNotEmpty(anio)) {
            String trimestre = (String) session.getAttribute("trimestre");
            String leccion = (String) session.getAttribute("leccion");
            String dia = (String) session.getAttribute("dia");
            StringBuilder url = new StringBuilder();
            url.append("redirect:/inicio/");
            url.append(anio).append("/");
            url.append(trimestre).append("/");
            url.append(leccion).append("/");
            url.append(dia);
            return url.toString();
        }

        Inicio inicio = inicioService.inicio(timeZone);
        if (inicio != null) {
            StringBuilder url = new StringBuilder();
            url.append("redirect:/inicio/");
            url.append(inicio.getAnio()).append("/");
            url.append(inicio.getTrimestre()).append("/");
            url.append(inicio.getLeccion()).append("/");
            url.append(inicio.getDia());
            return url.toString();
        } else {
            return "inicio/trimestre";
        }
    }

    @RequestMapping(value = "/inicio/{anio}/{trimestre}/{leccion}/{dia}")
    public String inicio(Model model, @ModelAttribute Inicio inicio, HttpSession session) {

        session.setAttribute("anio", inicio.getAnio());
        session.setAttribute("trimestre", inicio.getTrimestre());
        session.setAttribute("leccion", inicio.getLeccion());
        session.setAttribute("dia", inicio.getDia());

        log.debug("Anio: {} | Trimestre: {} | Leccion: {} | Dia: {}", new Object[]{inicio.getAnio(), inicio.getTrimestre(), inicio.getLeccion(), inicio.getDia()});

        inicio = inicioService.inicio(inicio);

        model.addAttribute("inicio", inicio);

        return "inicio/inicio";
    }
}
