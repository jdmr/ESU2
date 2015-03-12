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

import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.davidmendoza.esu.model.Inicio;
import org.davidmendoza.esu.model.Publicacion;
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
@RequestMapping("/profundiza")
public class ProfundizaController extends BaseController {

    @Autowired
    private InicioService inicioService;

    @RequestMapping(method = RequestMethod.GET)
    public String profundiza(HttpSession session) {
        String anio = (String) session.getAttribute("anio");
        if (StringUtils.isNotEmpty(anio)) {
            String trimestre = (String) session.getAttribute("trimestre");
            String leccion = (String) session.getAttribute("leccion");

            StringBuilder url = new StringBuilder();
            url.append("redirect:/profundiza/");
            url.append(anio).append("/");
            url.append(trimestre).append("/");
            url.append(leccion).append("/");
            url.append("tema1");
            return url.toString();
        } else {
            Inicio inicio = inicioService.inicio();
            if (inicio != null) {
                StringBuilder url = new StringBuilder();
                url.append("redirect:/profundiza/");
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

        String tema = inicio.getTema();
        
        inicio = inicioService.inicio(inicio);
        
        for (Publicacion publicacion : inicio.getDialoga()) {
            if (publicacion.getTema().equals(tema)) {
                model.addAttribute("tema", publicacion);
                break;
            }
        }

        model.addAttribute("profundiza", inicio);

        return "profundiza/tema";
    }
}
