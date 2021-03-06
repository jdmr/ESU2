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
package org.davidmendoza.esu.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.davidmendoza.esu.dao.PerfilRepository;
import org.davidmendoza.esu.dao.RolRepository;
import org.davidmendoza.esu.dao.UsuarioRepository;
import org.davidmendoza.esu.model.Perfil;
import org.davidmendoza.esu.model.Publicacion;
import org.davidmendoza.esu.model.Usuario;
import org.davidmendoza.esu.service.PerfilService;
import org.davidmendoza.esu.service.PublicacionService;
import org.davidmendoza.esu.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Service
@Transactional
public class PerfilServiceImpl extends BaseController implements PerfilService {

    @Autowired
    private PerfilRepository perfilRepository;
    @Autowired
    private PublicacionService publicacionService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RolRepository rolRepository;

    @Override
    @Transactional(readOnly = true)
    public Perfil obtienePorUsuario(Usuario usuario) {
        return perfilRepository.findByUsuario(usuario);
    }

    @Override
    public Perfil obtiene(Long perfilId) {
        return perfilRepository.findOne(perfilId);
    }

    @Cacheable(value = "equipoCache")
    @Override
    public List<Perfil> todos() {
        List<Perfil> perfiles = perfilRepository.todos();
        for (Perfil perfil : perfiles) {
            List<Publicacion> publicaciones = publicacionService.publicacionesUnicasDeArticulos(perfil.getUsuario());
            List<Publicacion> unicas = new ArrayList<>();
            Map<Long, Long> articulos = new HashMap<>();
            for (Publicacion publicacion : publicaciones) {
                Long articuloId = articulos.get(publicacion.getArticulo().getId());
                if (articuloId == null) {
                    unicas.add(publicacion);
                    articulos.put(publicacion.getArticulo().getId(), publicacion.getArticulo().getId());
                }
            }
            perfil.setPublicacionesUnicas(unicas);
        }
        return perfiles;
    }

    @Override
    public Perfil obtienePorUsuario(Long usuarioId) {
        return perfilRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public void actualiza(Perfil perfil) {
        Usuario usuario = perfil.getUsuario();
        Usuario u = usuarioRepository.dateCreated(usuario.getId());
        usuario.setDateCreated(u.getDateCreated());
        usuario.setLastUpdated(new Date());
        if (!usuario.getPassword().equals(u.getPassword())) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }

        usuario.getRoles().clear();
        if (usuario.getAdmin()) {
            usuario.getRoles().add(rolRepository.findByAuthorityIgnoreCase("ROLE_ADMIN"));
        }
        if (usuario.getEditor()) {
            usuario.getRoles().add(rolRepository.findByAuthorityIgnoreCase("ROLE_EDITOR"));
        }
        if (usuario.getAutor()) {
            usuario.getRoles().add(rolRepository.findByAuthorityIgnoreCase("ROLE_AUTOR"));
        }
        if (usuario.getUser()) {
            usuario.getRoles().add(rolRepository.findByAuthorityIgnoreCase("ROLE_USER"));
        }

        usuarioRepository.save(usuario);

        if (perfil.getFile() != null) {
            try {
                perfil.setTamano(perfil.getFile().getSize());
                perfil.setTipoContenido(perfil.getFile().getContentType());
                perfil.setNombreImagen(perfil.getFile().getOriginalFilename());
                perfil.setArchivo(perfil.getFile().getBytes());
            } catch (IOException e) {
                log.warn("No se pudo subir la imagen", e);
            }
        }
        perfilRepository.save(perfil);
    }

}
