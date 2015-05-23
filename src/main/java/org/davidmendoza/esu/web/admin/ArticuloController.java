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

import java.security.Principal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.davidmendoza.esu.Constants;
import org.davidmendoza.esu.model.Articulo;
import org.davidmendoza.esu.model.Inicio;
import org.davidmendoza.esu.model.Publicacion;
import org.davidmendoza.esu.model.Usuario;
import org.davidmendoza.esu.service.ArticuloService;
import org.davidmendoza.esu.service.InicioService;
import org.davidmendoza.esu.service.PublicacionService;
import org.davidmendoza.esu.service.UsuarioService;
import org.davidmendoza.esu.utils.LabelValueBean;
import org.davidmendoza.esu.validation.PublicacionValidator;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author jdmr
 */
@Controller
@RequestMapping("/admin/articulo")
public class ArticuloController extends BaseController {

    @Autowired
    private ArticuloService articuloService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private InicioService inicioService;
    @Autowired
    private PublicacionValidator publicacionValidator;
    @Autowired
    private PublicacionService publicacionService;

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

    @RequestMapping(value = "/ver/{articuloId}", method = RequestMethod.GET)
    public String ver(@PathVariable Long articuloId, Model model) {
        Articulo articulo = articuloService.obtiene(articuloId);
        model.addAttribute("articulo", articulo);
        return "admin/articulo/ver";
    }

    @RequestMapping(value = "/nuevo", method = RequestMethod.GET)
    public String nuevo(Model model, Principal principal) {
        Usuario autor = usuarioService.obtiene(principal.getName());
        Articulo articulo = new Articulo();
        articulo.setAutor(autor);
        model.addAttribute("articulo", articulo);
        return "admin/articulo/nuevo";
    }

    @RequestMapping(value = "/editar/{articuloId}", method = RequestMethod.GET)
    public String editar(@PathVariable Long articuloId, Model model) {
        Articulo articulo = articuloService.obtiene(articuloId);
        model.addAttribute("articulo", articulo);
        return "admin/articulo/editar";
    }

    @RequestMapping(value = "/eliminar/{articuloId}", method = RequestMethod.GET)
    public String eliminar(@PathVariable Long articuloId, Model model, RedirectAttributes redirectAttributes) {
        Articulo articulo = articuloService.elimina(articuloId);
        StringBuilder sb = new StringBuilder();
        sb.append("Se ha eliminado el articulo ").append(articulo.getTitulo());
        redirectAttributes.addFlashAttribute("message", sb.toString());
        return "redirect:/admin/articulo";
    }

    @RequestMapping(value = "/autores", params = {"term"}, produces = "application/json")
    public @ResponseBody
    List<LabelValueBean> autores(@RequestParam("term") String filtro) {
        log.debug("filtro: {}", filtro);
        List<Usuario> usuarios = usuarioService.busca(filtro);
        List<LabelValueBean> autores = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            autores.add(new LabelValueBean(usuario.getNombreCompleto(), usuario.getId().toString()));
        }
        return autores;
    }

    @RequestMapping(value = "/publicacion/{articuloId}", method = RequestMethod.GET)
    public String nuevaPublicacion(@PathVariable("articuloId") Long articuloId, Model model) {
        Articulo articulo = new Articulo();
        articulo.setId(articuloId);

        Inicio inicio = inicioService.inicio();
        Integer anio = new Integer(inicio.getAnio());

        Publicacion publicacion = new Publicacion();
        publicacion.setArticulo(articulo);
        publicacion.setAnio(anio);
        publicacion.setTrimestre(inicio.getTrimestre());
        publicacion.setLeccion(inicio.getLeccion());
        model.addAttribute("publicacion", publicacion);

        inicializaPublicacion(model, publicacion);

        return "admin/articulo/publicacion";
    }

    @RequestMapping(value = "/publicacion", method = RequestMethod.POST)
    public String nuevaPublicacion(@ModelAttribute("publicacion") Publicacion publicacion, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model, Principal principal) {
        publicacionValidator.validate(publicacion, bindingResult);
        if (bindingResult.hasErrors()) {
            log.warn("No se pudo crear la publicacion. {}", bindingResult.getAllErrors());
            inicializaPublicacion(model, publicacion);
            return "admin/articulo/publicacion";
        }

        try {
            Usuario usuario = usuarioService.obtiene(principal.getName());
            publicacion.setEditor(usuario);
            publicacionService.nueva(publicacion);
        } catch (Exception e) {
            log.error("No se pudo crear la publicacion", e);
            inicializaPublicacion(model, publicacion);
            bindingResult.reject("No se pudo crear la publicacion. {}", e.getMessage());
            return "admin/articulo/publicacion";
        }

        return "redirect:/admin/articulo/ver/" + publicacion.getArticulo().getId();
    }

    @RequestMapping(value = "/publicacion/elimina/{publicacionId}")
    public String eliminaPublicacion(@PathVariable("publicacionId") Long publicacionId) {
        Long articuloId = publicacionService.elimina(publicacionId);
        return "redirect:/admin/articulo/ver/" + articuloId;
    }

    private void inicializaPublicacion(Model model, Publicacion publicacion) {
        List<Integer> anios = new ArrayList<>();
        for (int i = (publicacion.getAnio() + 1); i >= 2011; i--) {
            anios.add(i);
        }
        model.addAttribute("anios", anios);

        List<String> trimestres = new ArrayList<>();
        trimestres.add("t1");
        trimestres.add("t2");
        trimestres.add("t3");
        trimestres.add("t4");
        model.addAttribute("trimestres", trimestres);

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(2);
        List<String> lecciones = new ArrayList<>();
        for (int i = 1; i < 15; i++) {
            lecciones.add("l" + nf.format(i));
        }
        model.addAttribute("lecciones", lecciones);

        List<String> temas = new ArrayList<>();
        temas.add("tema1");
        temas.add("tema2");
        temas.add("tema3");
        temas.add("tema4");
        temas.add("tema5");
        model.addAttribute("temas", temas);

        List<String> tipos = new ArrayList<>();
        tipos.add("comunica");
        tipos.add("dialoga");
        tipos.add("leccion");
        tipos.add("versiculo");
        tipos.add("video");
        tipos.add("imagen");
        tipos.add("jovenes");
        tipos.add("podcast");
        model.addAttribute("tipos", tipos);

        List<String> dias = new ArrayList<>();
        dias.add("domingo");
        dias.add("lunes");
        dias.add("martes");
        dias.add("miercoles");
        dias.add("jueves");
        dias.add("viernes");
        dias.add("sabado");
        model.addAttribute("dias", dias);

        List<String> estados = new ArrayList<>();
        estados.add("PUBLICADO");
        estados.add("PENDIENTE");
        estados.add("RECHAZADO");
        model.addAttribute("estados", estados);
    }
}
