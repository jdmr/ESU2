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

import java.util.ArrayList;
import java.util.List;
import org.davidmendoza.esu.model.Biblia;
import org.davidmendoza.esu.model.Rv2000;
import org.davidmendoza.esu.service.BibliaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Controller
@RequestMapping("/biblia")
public class BibliaController extends BaseController {

    @Autowired
    private BibliaService bibliaService;
    private static final Integer versiculos = 6;

    @RequestMapping(value = "/{libro}", method = RequestMethod.GET)
    public @ResponseBody
    Biblia libro(@PathVariable Integer libro) {
        return convierte(bibliaService.biblia(libro, 1, 1, versiculos));
    }

    @RequestMapping(value = "/{libro}/{capitulo}", method = RequestMethod.GET)
    public @ResponseBody
    Biblia capitulo(@PathVariable Integer libro, @PathVariable Integer capitulo) {
        return convierte(bibliaService.biblia(libro, capitulo, 1, versiculos));
    }

    @RequestMapping(value = "/{libro}/{capitulo}/{versiculo}", method = RequestMethod.GET)
    public @ResponseBody
    Biblia versiculo(@PathVariable Integer libro, @PathVariable Integer capitulo, @PathVariable Integer versiculo) {
        return convierte(bibliaService.biblia(libro, capitulo, versiculo, versiculos));
    }

    public Biblia convierte(List<Rv2000> lista) {
        Biblia biblia = new Biblia();
        int vid = 0;
        int lid = 0;
        int cid = 0;
        List<String> contenido = new ArrayList<>();
        for (int i = 0; i < lista.size(); i++) {
            Rv2000 v = lista.get(i);
            if (i == 0 && v.getId() > 1) {
                StringBuilder anterior = new StringBuilder();
                anterior.append("/").append(v.getLibro().getId());
                anterior.append("/").append(v.getCapitulo());
                anterior.append("/").append(v.getVersiculo());
                biblia.setAnterior(anterior.toString());
                continue;
            }
            if (i == (lista.size() - 1)) {
                StringBuilder siguiente = new StringBuilder();
                siguiente.append("/").append(v.getLibro().getId());
                siguiente.append("/").append(v.getCapitulo());
                siguiente.append("/").append(v.getVersiculo());
                biblia.setSiguiente(siguiente.toString());
                break;
            }
            StringBuilder sb = new StringBuilder();
            if (cid != v.getCapitulo() || lid != v.getLibro().getId()) {
                sb.append("<h2>");
                if (lid != v.getLibro().getId()) {
                    sb.append(v.getLibro().getNombre()).append(" ");
                }
                sb.append(v.getCapitulo()).append(" : ");
                sb.append(v.getVersiculo());
                sb.append("</h2>");
            }
            vid = v.getVersiculo();
            cid = v.getCapitulo();
            lid = v.getLibro().getId();
            sb.append("<p><strong>").append(vid).append("</strong> ").append(v.getTexto()).append("</p>");
            contenido.add(sb.toString());
        }
        biblia.setContenido(contenido);
        return biblia;
    }
}
