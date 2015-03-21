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

import static com.sun.corba.se.spi.presentation.rmi.StubAdapter.request;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.davidmendoza.esu.model.Perfil;
import org.davidmendoza.esu.service.PerfilService;
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
@RequestMapping("/perfil")
public class PerfilController extends BaseController {

    @Autowired
    private PerfilService perfilService;

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
                    + "/static/img/sin-foto.jpg");
        } catch (IOException e) {
            log.error("No se pudo obtener la imagen", e);
        }
    }
}
