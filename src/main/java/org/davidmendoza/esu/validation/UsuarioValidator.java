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
package org.davidmendoza.esu.validation;

import org.apache.commons.lang.StringUtils;
import org.davidmendoza.esu.model.Usuario;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Component
public class UsuarioValidator implements Validator {

    @Override
    public boolean supports(Class<?> type) {
        return Usuario.class.isAssignableFrom(type);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Usuario usuario = (Usuario) o;
        if (StringUtils.isBlank(usuario.getNombre())) {
            errors.rejectValue("nombre", "NotBlank.usuario.nombre");
        }
        if (StringUtils.isBlank(usuario.getApellido())) {
            errors.rejectValue("apellido", "NotBlank.usuario.apellido");
        }
        if (StringUtils.isBlank(usuario.getUsername())) {
            errors.rejectValue("username", "NotBlank.usuario.username");
        }
        if (StringUtils.isBlank(usuario.getPassword())) {
            errors.rejectValue("password", "NotBlank.usuario.password");
        }
    }

}
