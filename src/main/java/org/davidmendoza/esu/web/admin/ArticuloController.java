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
import org.apache.commons.lang.StringUtils;
import org.davidmendoza.esu.dao.ArticuloRepository;
import org.davidmendoza.esu.model.Articulo;
import org.davidmendoza.esu.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author jdmr
 */
@Controller
@RequestMapping("/admin/articulo")
public class ArticuloController extends BaseController {

    @Autowired
    private ArticuloRepository articuloRepository;

    @RequestMapping(value = {"", "/lista"}, method = RequestMethod.GET)
    public String lista(Model model,
            @RequestParam(value = "pagina", required = false) Integer pagina,
            @RequestParam(value = "ordena", required = false) String ordena,
            @RequestParam(value = "direccion", required = false) String direccion) {
        log.debug("Pagina: {}", pagina);

        if (pagina == null) {
            pagina = 0;
        }
        if (StringUtils.isBlank(ordena)) {
            ordena = "lastUpdated";
            direccion = "desc";
        }
        if (StringUtils.isBlank(direccion)) {
            direccion = "asc";
        }
        Sort sort;
        String direccionContraria;
        switch (direccion) {
            case "desc":
                sort = new Sort(Sort.Direction.DESC, ordena);
                direccionContraria = "asc";
                break;
            default:
                sort = new Sort(Sort.Direction.ASC, ordena);
                direccionContraria = "desc";
        }
        PageRequest pageRequest = new PageRequest(pagina, 20, sort);

        Page<Articulo> page = articuloRepository.findAll(pageRequest);
        List<Integer> paginas = new ArrayList<>();

        int current = page.getNumber() + 1;
        int begin = Math.max(0, current - 6);
        int end = Math.min(begin + 11, page.getTotalPages());

        if (begin > 0) {
            paginas.add(0);
            if (begin > 1) {
                paginas.add(-1);
            }
        }
        for (int i = begin; i < end; i++) {
            paginas.add(i);
        }
        if (end < page.getTotalPages()) {
            paginas.add(-1);
            paginas.add(page.getTotalPages() - 1);
        }
        model.addAttribute("ordena", ordena);
        model.addAttribute("direccion", direccion);
        model.addAttribute("direccionContraria", direccionContraria);
        model.addAttribute("paginaActual", pagina);
        model.addAttribute("paginasTotales", page.getTotalPages() - 1);
        model.addAttribute("paginas", paginas);

        model.addAttribute("articulos", page);
        return "admin/articulo/lista";
    }
}
