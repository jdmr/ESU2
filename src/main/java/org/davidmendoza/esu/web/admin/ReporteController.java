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

import java.util.Date;
import java.util.List;
import org.davidmendoza.esu.model.ReporteArticulo;
import org.davidmendoza.esu.service.ArticuloService;
import org.davidmendoza.esu.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Controller
@RequestMapping("/admin/reporte")
public class ReporteController extends BaseController {
    
    @Autowired
    private ArticuloService articuloService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "redirect:/admin/reporte/articulos";
    }
    
    @RequestMapping(value = "/articulos", method = RequestMethod.GET)
    public String articulos(Model model) {
        List<ReporteArticulo> articulos = articuloService.articulosDelMes(new Date());
        model.addAttribute("articulos", articulos);
        
        return "admin/reporte/articulos";
    }
    
    @RequestMapping(value = "/articulos/enviar", method = RequestMethod.GET)
    public String articulosEnviar() {
        articuloService.enviarArticulosDelMes(new Date());
        return "admin/reporte/articulos-enviar";
    }
}
