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

import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.davidmendoza.esu.Constants;
import org.davidmendoza.esu.model.Articulo;
import org.davidmendoza.esu.service.ArticuloService;
import org.davidmendoza.esu.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private ArticuloService articuloService;

    @RequestMapping(value = {"", "/lista"}, method = RequestMethod.GET)
    public String lista(Model model,
            @RequestParam(value = "pagina", required = false) Integer pagina,
            @RequestParam(value = "ordena", required = false) String ordena,
            @RequestParam(value = "direccion", required = false) String direccion,
            @RequestParam(value = "filtro", required = false) String filtro) {
        log.debug("Pagina: {}", pagina);


        
        Map<String, Object> params = preparaPaginacion(pagina, ordena, direccion, filtro);
        
        Page<Articulo> page;
        PageRequest pageRequest = (PageRequest) params.get(Constants.PAGE_REQUEST);
        if (StringUtils.isNotBlank(filtro)) {
            page = articuloService.busca(filtro, pageRequest);
        } else {
            page = articuloService.lista(pageRequest);
        }

        pagina(model, page, params);
        
        model.addAttribute("articulos", page);
        return "admin/articulo/lista";
    }
}
