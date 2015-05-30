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

import java.util.Date;
import java.util.List;
import org.davidmendoza.esu.dao.RolRepository;
import org.davidmendoza.esu.dao.UsuarioDao;
import org.davidmendoza.esu.dao.UsuarioRepository;
import org.davidmendoza.esu.model.Usuario;
import org.davidmendoza.esu.service.BaseService;
import org.davidmendoza.esu.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Service
@Transactional
public class UsuarioServiceImpl extends BaseService implements UsuarioService {

    @Autowired
    private UsuarioDao usuarioDao;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RolRepository rolRepository;
    
    @Override
    public List<Usuario> busca(String filtro) {
        return usuarioDao.busca(filtro);
    }

    @Override
    public Usuario obtiene(String username) {
        return usuarioDao.obtiene(username);
    }

    @Override
    public Usuario obtiene(Long usuarioId) {
        return usuarioRepository.findOne(usuarioId);
    }

    @Override
    public Page<Usuario> busca(String filtro, PageRequest pageRequest) {
                StringBuilder sb = new StringBuilder();
        sb.append("%");
        sb.append(filtro);
        sb.append("%");
        return usuarioRepository.findByUsernameLikeOrNombreLikeOrApellidoLikeAllIgnoreCase(sb.toString(), sb.toString(), sb.toString(), pageRequest);

    }

    @Override
    public Page<Usuario> lista(PageRequest pageRequest) {
        return usuarioRepository.findAll(pageRequest);
    }

    @Override
    public void crea(Usuario usuario) {
        Date date = new Date();
        usuario.setDateCreated(date);
        usuario.setLastUpdated(date);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        
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
    }

    @Override
    public void actualiza(Usuario usuario) {
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
    }

    @Override
    public void elimina(Long usuarioId) {
        usuarioRepository.delete(usuarioId);
    }
    
}
