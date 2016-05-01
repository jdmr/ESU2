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
import org.davidmendoza.esu.model.Perfil;
import org.davidmendoza.esu.model.Rol;
import org.davidmendoza.esu.model.Usuario;
import org.davidmendoza.esu.service.PerfilService;
import org.davidmendoza.esu.service.UsuarioService;
import org.davidmendoza.esu.validation.PerfilValidator;
import org.davidmendoza.esu.validation.UsuarioValidator;
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
@RequestMapping("/admin/usuario")
public class UsuarioController extends BaseController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private UsuarioValidator usuarioValidator;
    @Autowired
    private PerfilService perfilService;
    @Autowired
    private PerfilValidator perfilValidator;

    @RequestMapping(value = {"", "/lista"}, method = RequestMethod.GET)
    public String lista(Model model,
            @RequestParam(value = "pagina", required = false) Integer pagina,
            @RequestParam(value = "ordena", required = false) String ordena,
            @RequestParam(value = "direccion", required = false) String direccion,
            @RequestParam(value = "filtro", required = false) String filtro) {
        log.debug("Pagina: {}", pagina);

        if (StringUtils.isBlank(ordena)) {
            ordena = "nombre";
            direccion = "asc";
        }
        Map<String, Object> params = preparaPaginacion(pagina, ordena, direccion, filtro);

        Page<Usuario> page;
        PageRequest pageRequest = (PageRequest) params.get(Constants.PAGE_REQUEST);
        if (StringUtils.isNotBlank(filtro)) {
            page = usuarioService.busca(filtro, pageRequest);
        } else {
            page = usuarioService.lista(pageRequest);
        }

        pagina(model, page, params);

        model.addAttribute("usuarios", page);
        return "admin/usuario/lista";
    }

    @RequestMapping(value = "/nuevo", method = RequestMethod.GET)
    public String nuevo(Model model) {
        Usuario usuario = new Usuario();
        model.addAttribute("usuario", usuario);
        return "admin/usuario/nuevo";
    }

    @RequestMapping(value = "/nuevo", method = RequestMethod.POST)
    public String nuevo(@ModelAttribute("usuario") Usuario usuario, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        usuarioValidator.validate(usuario, bindingResult);
        if (bindingResult.hasErrors()) {
            log.warn("No se pudo crear usuario. {}", bindingResult.getAllErrors());
            return "admin/usuario/nuevo";
        }

        try {
            usuarioService.crea(usuario);
        } catch (Exception e) {
            log.error("No se pudo crear usuario", e);
            bindingResult.reject("No se pudo crear usuario. {}", e.getMessage());
            return "admin/usuario/nuevo";
        }
        return "redirect:/admin/usuario/ver/" + usuario.getId();
    }

    @RequestMapping(value = "/ver/{usuarioId}", method = RequestMethod.GET)
    public String ver(@PathVariable("usuarioId") Long usuarioId, Model model) {
        Perfil perfil = perfilService.obtienePorUsuario(usuarioId);
        model.addAttribute("perfil", perfil);
        model.addAttribute("usuario", perfil.getUsuario());
        return "admin/usuario/ver";
    }

    @RequestMapping(value = "/editar/{usuarioId}", method = RequestMethod.GET)
    public String editar(@PathVariable Long usuarioId, Model model) {
        Perfil perfil = perfilService.obtienePorUsuario(usuarioId);
        Usuario usuario = perfil.getUsuario();
        for(Rol rol : usuario.getRoles()) {
            if (rol.getAuthority().equals("ROLE_ADMIN")) {
                usuario.setAdmin(Boolean.TRUE);
            }
            if (rol.getAuthority().equals("ROLE_EDITOR")) {
                usuario.setEditor(Boolean.TRUE);
            }
            if (rol.getAuthority().equals("ROLE_AUTOR")) {
                usuario.setAutor(Boolean.TRUE);
            }
            if (rol.getAuthority().equals("ROLE_USER")) {
                usuario.setUser(Boolean.TRUE);
            } else {
                usuario.setUser(Boolean.FALSE);
            }
        }
        model.addAttribute("perfil", perfil);
        return "admin/usuario/editar";
    }

    @RequestMapping(value = "/editar", method = RequestMethod.POST)
    public String editar(@ModelAttribute("perfil") Perfil perfil, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        perfilValidator.validate(perfil, bindingResult);
        if (bindingResult.hasErrors()) {
            log.warn("No se pudo actualizar usuario. {}", bindingResult.getAllErrors());
            return "admin/usuario/editar";
        }
        Usuario usuario = perfil.getUsuario();

        try {
            perfilService.actualiza(perfil);
        } catch (Exception e) {
            log.error("No se pudo actualizar usuario", e);
            bindingResult.reject("No se pudo actualizar usuario. {}", e.getMessage());
            return "admin/usuario/editar";
        }
        return "redirect:/admin/usuario/ver/" + usuario.getId();
    }

    @RequestMapping(value = "/eliminar/{usuarioId}", method = RequestMethod.GET)
    public String eliminar(@PathVariable Long usuarioId) {
        try {
            usuarioService.elimina(usuarioId);
        } catch (Exception e) {
            log.error("No se pudo eliminar usuario", e);
            throw new RuntimeException("No se pudo eliminar usuario", e);
        }
        return "redirect:/admin/usuario";
    }
}
