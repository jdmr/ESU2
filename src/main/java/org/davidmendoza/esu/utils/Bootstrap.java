/*
 * The MIT License
 *
 * Copyright 2014 Southwestern Adventist University.
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
package org.davidmendoza.esu.utils;

import java.util.Date;
import org.davidmendoza.esu.dao.RolRepository;
import org.davidmendoza.esu.dao.UsuarioRepository;
import org.davidmendoza.esu.model.Rol;
import org.davidmendoza.esu.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Component
public class Bootstrap implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);
    
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Validating ESU App.");

        Rol admin = rolRepository.findByAuthorityIgnoreCase("ROLE_ADMIN");
        if (admin == null) {
            admin = new Rol();
            admin.setAuthority("ROLE_ADMIN");
            rolRepository.save(admin);
        }
        
        Rol editor = rolRepository.findByAuthorityIgnoreCase("ROLE_EDITOR");
        if (editor == null) {
            editor = new Rol();
            editor.setAuthority("ROLE_EDITOR");
            rolRepository.save(editor);
        }
        
        Rol autor = rolRepository.findByAuthorityIgnoreCase("ROLE_AUTOR");
        if (autor == null) {
            autor = new Rol();
            autor.setAuthority("ROLE_AUTOR");
            rolRepository.save(autor);
        }
        
        Rol user = rolRepository.findByAuthorityIgnoreCase("ROLE_USER");
        if (user == null) {
            user = new Rol();
            user.setAuthority("ROLE_USER");
            rolRepository.save(user);
        }
        
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase("admin@um.edu.mx");
        if (usuario == null) {
            usuario = new Usuario();
            usuario.setUsername("admin@um.edu.mx");
            usuario.setPassword(passwordEncoder.encode("35U!@#$%"));
            usuario.setNombre("Usuario");
            usuario.setApellido("Administrador");
            usuario.getRoles().add(admin);
            Date date = new Date();
            usuario.setDateCreated(date);
            usuario.setLastUpdated(date);
            usuarioRepository.save(usuario);
        }

        log.info("Finished ESU App Validation.");
    }

}
