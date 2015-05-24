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
import org.davidmendoza.esu.model.Trimestre;
import org.davidmendoza.esu.service.TrimestreService;
import org.davidmendoza.esu.validation.TrimestreValidator;
import org.davidmendoza.esu.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Controller
@RequestMapping("/admin/trimestre")
public class TrimestreController extends BaseController {

    @Autowired
    private TrimestreService trimestreService;
    @Autowired
    private TrimestreValidator trimestreValidator;

    @RequestMapping(value = {"", "/lista"}, method = RequestMethod.GET)
    public String lista(Model model,
            @RequestParam(value = "pagina", required = false) Integer pagina,
            @RequestParam(value = "ordena", required = false) String ordena,
            @RequestParam(value = "direccion", required = false) String direccion,
            @RequestParam(value = "filtro", required = false) String filtro) {
        log.debug("Pagina: {}", pagina);

        if (StringUtils.isBlank(ordena)) {
            ordena = "id";
            direccion = "desc";
        }
        Map<String, Object> params = preparaPaginacion(pagina, ordena, direccion, filtro);

        Page<Trimestre> page;
        PageRequest pageRequest = (PageRequest) params.get(Constants.PAGE_REQUEST);
        if (StringUtils.isNotBlank(filtro)) {
            page = trimestreService.busca(filtro, pageRequest);
        } else {
            page = trimestreService.lista(pageRequest);
        }

        pagina(model, page, params);

        model.addAttribute("trimestres", page);
        return "admin/trimestre/lista";
    }

    @RequestMapping(value = "/nuevo", method = RequestMethod.GET)
    public String nuevo(Model model) {
        Trimestre trimestre = new Trimestre();
        model.addAttribute("trimestre", trimestre);
        return "admin/trimestre/nuevo";
    }

    @RequestMapping(value = "/nuevo", method = RequestMethod.POST)
    public String nuevo(@ModelAttribute("trimestre") Trimestre trimestre, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        trimestreValidator.validate(trimestre, bindingResult);
        if (bindingResult.hasErrors()) {
            log.warn("No se pudo crear trimestre. {}", bindingResult.getAllErrors());
            return "admin/trimestre/nuevo";
        }

        try {
            trimestreService.crea(trimestre);
        } catch (Exception e) {
            log.error("No se pudo crear trimestre", e);
            bindingResult.reject("No se pudo crear trimestre. {}", e.getMessage());
            return "admin/trimestre/nuevo";
        }
        return "redirect:/admin/trimestre/ver/" + trimestre.getId();
    }

    @RequestMapping(value = "/ver/{trimestreId}", method = RequestMethod.GET)
    public String ver(@PathVariable("trimestreId") Long trimestreId, Model model) {
        Trimestre trimestre = trimestreService.obtiene(trimestreId);
        model.addAttribute("trimestre", trimestre);
        return "admin/trimestre/ver";
    }

    @RequestMapping(value = "/editar/{trimestreId}", method = RequestMethod.GET)
    public String editar(@PathVariable Long trimestreId, Model model) {
        Trimestre trimestre = trimestreService.obtiene(trimestreId);
        model.addAttribute("trimestre", trimestre);
        return "admin/trimestre/editar";
    }

    @RequestMapping(value = "/editar", method = RequestMethod.POST)
    public String editar(@ModelAttribute("trimestre") Trimestre trimestre, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        trimestreValidator.validate(trimestre, bindingResult);
        if (bindingResult.hasErrors()) {
            log.warn("No se pudo actualizar trimestre. {}", bindingResult.getAllErrors());
            return "admin/trimestre/editar";
        }

        try {
            trimestreService.actualiza(trimestre);
        } catch (Exception e) {
            log.error("No se pudo actualizar trimestre", e);
            bindingResult.reject("No se pudo actualizar trimestre. {}", e.getMessage());
            return "admin/trimestre/editar";
        }
        return "redirect:/admin/trimestre/ver/" + trimestre.getId();
    }

    @RequestMapping(value = "/eliminar/{trimestreId}", method = RequestMethod.GET)
    public String eliminar(@PathVariable Long trimestreId) {
        try {
            trimestreService.elimina(trimestreId);
        } catch (Exception e) {
            log.error("No se pudo eliminar trimestre", e);
            throw new RuntimeException("No se pudo eliminar trimestre", e);
        }
        return "redirect:/admin/trimestre";
    }
}
