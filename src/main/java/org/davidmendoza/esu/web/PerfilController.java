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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.davidmendoza.esu.model.Perfil;
import org.davidmendoza.esu.model.Publicacion;
import org.davidmendoza.esu.service.PerfilService;
import org.davidmendoza.esu.service.PublicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Controller
@RequestMapping("/perfil")
public class PerfilController extends BaseController {

    @Autowired
    private PerfilService perfilService;
    
    @Autowired
    private PublicacionService publicacionService;

    @RequestMapping(value = "/{perfilId}", method = RequestMethod.GET)
    public String perfil(@PathVariable Long perfilId, Model model) {
        Perfil perfil = perfilService.obtiene(perfilId);
        if (perfil != null) {
            model.addAttribute("perfil", perfil);
            
            Map<Long, Long> articulos = new HashMap<>();
            List<Publicacion> publicaciones = new ArrayList<>();
            for(Publicacion publicacion : publicacionService.publicaciones(perfil.getUsuario())) {
                if (!articulos.containsKey(publicacion.getArticulo().getId())) {
                    articulos.put(publicacion.getArticulo().getId(), publicacion.getArticulo().getId());
                    publicaciones.add(publicacion);
                }
            }
            model.addAttribute("publicaciones", publicaciones);
            
            return "perfil/usuario";
        }
        throw new RuntimeException("No se encontro el perfil "+ perfilId);
    }

    @RequestMapping(value = "/imagen/{perfilId}", method = RequestMethod.GET)
    public void obtieneImagen(@PathVariable Long perfilId, HttpServletRequest request, HttpServletResponse response) {
        Perfil perfil = perfilService.obtiene(perfilId);
        if (perfil != null) {
            if (StringUtils.isNotBlank(perfil.getNombreImagen())) {
                response.setContentType(perfil.getTipoContenido());
                response.setContentLength(perfil.getTamano().intValue());
                try {
                    response.getOutputStream().write(perfil.getArchivo());
                    return;
                } catch (IOException e) {
                    log.error("No se pudo escribir el archivo", e);
                    throw new RuntimeException(
                            "No se pudo escribir el archivo en el outputstream");
                }
            }
        }

        try {
            response.sendRedirect(request.getContextPath()
                    + "/images/sin-foto.jpg");
        } catch (IOException e) {
            log.error("No se pudo obtener la imagen", e);
        }
    }
}
