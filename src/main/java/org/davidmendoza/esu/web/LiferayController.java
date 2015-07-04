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

import org.apache.commons.lang.StringUtils;
import org.davidmendoza.esu.model.Publicacion;
import org.davidmendoza.esu.service.PublicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Controller
public class LiferayController {

    @Autowired
    private PublicacionService publicacionService;

    @RequestMapping(value = "/articulo/{nombre}", method = RequestMethod.GET)
    public String articulo(@PathVariable("nombre") String nombre) {
        if (StringUtils.isNotBlank(nombre)) {
            Publicacion publicacion = publicacionService.obtiene(nombre);
            if (publicacion != null) {
                switch (publicacion.getTipo()) {
                    case "dialoga":
                        return "redirect:/profundiza/" + publicacion.getAnio() + "/" + publicacion.getTrimestre() + "/" + publicacion.getLeccion() + "/" + publicacion.getTema();
                    case "comunica":
                        return "redirect:/comparte/" + publicacion.getAnio() + "/" + publicacion.getTrimestre() + "/" + publicacion.getLeccion() + "/" + publicacion.getTema();
                    case "leccion":
                        return "redirect:/estudia/" + publicacion.getAnio() + "/" + publicacion.getTrimestre() + "/" + publicacion.getLeccion() + "/" + publicacion.getDia();
                }
            }
        }
        return "redirect:/";
    }

}
