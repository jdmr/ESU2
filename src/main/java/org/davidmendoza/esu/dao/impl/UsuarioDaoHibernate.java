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
package org.davidmendoza.esu.dao.impl;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.davidmendoza.esu.dao.BaseDao;
import org.davidmendoza.esu.dao.UsuarioDao;
import org.davidmendoza.esu.model.Usuario;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jdmr
 */
@Repository
@Transactional
public class UsuarioDaoHibernate extends BaseDao implements UsuarioDao {

    @Override
    @Transactional(readOnly = true)
    public Usuario obtiene(String username) {
        Query query = em.createNamedQuery("Usuario.findByUsername");
        query.setParameter("username", username);
        try {
            return (Usuario) query.getSingleResult();
        } catch(NoResultException e) {
            log.error("No encontre al usuario "+ username, e);
            return null;
        }
    }
    
}
