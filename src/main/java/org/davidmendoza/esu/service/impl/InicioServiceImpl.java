/*
 * The MIT License
 *
 * Copyright 2014 J. David Mendoza.
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

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang.StringUtils;
import org.davidmendoza.esu.model.Inicio;
import org.davidmendoza.esu.model.Publicacion;
import org.davidmendoza.esu.model.Trimestre;
import org.davidmendoza.esu.service.BaseService;
import org.davidmendoza.esu.service.InicioService;
import org.davidmendoza.esu.service.PublicacionService;
import org.davidmendoza.esu.service.TrimestreService;
import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author J. David Mendoza <jdmendozar@gmail.com>
 */
@Service
@Transactional
public class InicioServiceImpl extends BaseService implements InicioService {

    private final NumberFormat dosDigitos = NumberFormat.getInstance();
    private final NumberFormat nf = NumberFormat.getInstance();

    {
        dosDigitos.setMinimumIntegerDigits(2);
    }

    @Autowired
    private TrimestreService trimestreService;
    @Autowired
    private PublicacionService publicacionService;

    @Cacheable(value = "diaCache")
    @Transactional(readOnly = true)
    @Override
    public Inicio inicio() {
        Calendar hoy = new GregorianCalendar(Locale.ENGLISH);
        log.debug("Hoy: {}", hoy.getTime());

        Trimestre trimestre = trimestreService.obtiene(hoy.getTime());

        if (trimestre != null) {
            DateTime a = new DateTime(trimestre.getInicia());
            DateTime b = new DateTime(hoy);
            Weeks c = Weeks.weeksBetween(a, b);
            int weeks = c.getWeeks() + 1;
            log.debug("Weeks: {}", weeks);
            StringBuilder leccion = new StringBuilder();
            leccion.append("l").append(dosDigitos.format(weeks));

            Inicio inicio = new Inicio();
            inicio.setAnio(trimestre.getNombre().substring(0, 4));
            inicio.setTrimestre(trimestre.getNombre().substring(4));
            inicio.setLeccion(leccion.toString());
            inicio.setDia(obtieneDia(hoy.get(Calendar.DAY_OF_WEEK)));
            return inicio;
        } else {
            return null;
        }

    }

    @Cacheable(value = "inicioCache")
    @Transactional(readOnly = true)
    @Override
    public Inicio inicio(Inicio inicio) {
        Integer anio = new Integer(inicio.getAnio());
        String trimestre = inicio.getTrimestre();
        String leccion = inicio.getLeccion();
        String dia = inicio.getDia();
        if (StringUtils.isBlank(dia)) {
            dia = obtieneDia(new GregorianCalendar(Locale.ENGLISH).get(Calendar.DAY_OF_WEEK));
            inicio.setDia(dia);
        }
        log.debug("DIA: {}", dia);

        Publicacion leccion1 = publicacionService.obtiene(anio, trimestre, leccion, dia, "leccion");

        Publicacion versiculo = publicacionService.obtiene(anio, trimestre, leccion, null, "versiculo");

        Publicacion video = publicacionService.obtiene(anio, trimestre, leccion, null, "video");

        Publicacion podcast = publicacionService.obtiene(anio, trimestre, leccion, null, "podcast");

        List<Publicacion> dialoga = publicacionService.obtiene(anio, trimestre, leccion, "dialoga");

        List<Publicacion> comunica = publicacionService.obtiene(anio, trimestre, leccion, "comunica");

        Trimestre t = trimestreService.obtiene(anio + trimestre);
        if (t != null) {
            try {
                Calendar cal = new GregorianCalendar(Locale.ENGLISH);
                cal.setTime(t.getInicia());
                cal.add(Calendar.SECOND, 1);
                cal.set(Calendar.DAY_OF_WEEK, obtieneDia(dia));
                int weeks = ((Long) nf.parse(leccion.substring(1))).intValue();
                if (dia.equals("sabado")) {
                    weeks--;
                }
                cal.add(Calendar.WEEK_OF_YEAR, weeks);
                Date hoy = cal.getTime();

                inicio.setPublicacion(leccion1);
                inicio.setDialoga(dialoga);
                inicio.setComunica(comunica);
                inicio.setVideo(video);
                inicio.setPodcast(podcast);
                inicio.setVersiculo(versiculo);
                inicio.setHoy(hoy);

                return inicio;
            } catch (ParseException e) {
                log.error("No pude poner la fecha de hoy", e);
            }
        }

        return null;
    }

    private String obtieneDia(int dia) {
        switch (dia) {
            case Calendar.SUNDAY:
                return "domingo";
            case Calendar.MONDAY:
                return "lunes";
            case Calendar.TUESDAY:
                return "martes";
            case Calendar.WEDNESDAY:
                return "miercoles";
            case Calendar.THURSDAY:
                return "jueves";
            case Calendar.FRIDAY:
                return "viernes";
            default:
                return "sabado";
        }
    }

    private Integer obtieneDia(String dia) {
        switch (dia) {
            case "domingo":
                return Calendar.SUNDAY;
            case "lunes":
                return Calendar.MONDAY;
            case "martes":
                return Calendar.TUESDAY;
            case "miercoles":
                return Calendar.WEDNESDAY;
            case "jueves":
                return Calendar.THURSDAY;
            case "viernes":
                return Calendar.FRIDAY;
            default:
                return Calendar.SATURDAY;
        }
    }

}
