/*
 * The MIT License
 *
 * Copyright 2016 J. David Mendoza.
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
package org.davidmendoza.esu.dao;

import java.util.List;
import org.davidmendoza.esu.model.Rv2000;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
public interface BibliaRepository extends JpaRepository<Rv2000, Long> {

    @Query("select v.id from Rv2000 v where v.libro.id = :libro and v.capitulo = :capitulo and v.versiculo = :versiculo")
    public Long getVersiculoId(@Param("libro") Integer libro, @Param("capitulo") Integer capitulo, @Param("versiculo") Integer versiculo);

    @Query("select v from Rv2000 v where v.id between :inicio and :fin order by v.id")
    public List<Rv2000> getVersiculos(@Param("inicio") Long inicio, @Param("fin") Long fin);

}
