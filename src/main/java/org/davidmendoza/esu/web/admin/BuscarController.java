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

import java.util.ArrayList;
import java.util.List;
import org.davidmendoza.esu.model.Articulo;
import org.davidmendoza.esu.model.Publicacion;
import org.davidmendoza.esu.service.BusquedaService;
import org.davidmendoza.esu.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Controller
@RequestMapping("/buscar")
public class BuscarController extends BaseController {

    @Autowired
    private BusquedaService busquedaService;

    @RequestMapping(method = RequestMethod.GET, params = {"filtro"})
    public String buscar(@RequestParam("filtro") String filtro, Model model) {
        log.debug("Buscando: {}", filtro);
        List<Articulo> articulos = busquedaService.buscar(filtro);
        List<Publicacion> publicaciones = new ArrayList<>();
        for (Articulo articulo : articulos) {
            List<Publicacion> lista = articulo.getPublicaciones();
            if (lista != null && !lista.isEmpty()) {
                Publicacion p = lista.get(0);
                log.debug("{} : {}", p.getTipo(), p.getArticulo().getTitulo());
                publicaciones.add(p);
            }
        }
        model.addAttribute("publicaciones", publicaciones);
        return "buscar/resultados";
    }
}
