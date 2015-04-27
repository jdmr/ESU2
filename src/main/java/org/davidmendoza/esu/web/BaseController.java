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
package org.davidmendoza.esu.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.davidmendoza.esu.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;

public abstract class BaseController {

    protected final transient Logger log = LoggerFactory.getLogger(getClass());
    
    protected Map<String, Object> preparaPaginacion(Integer pagina, String ordena, String direccion, String filtro) {
        Map<String, Object> params = new HashMap<>();
        String direccionContraria;
        if (pagina == null) {
            pagina = 0;
        }
        if (StringUtils.isBlank(ordena)) {
            ordena = "lastUpdated";
            direccion = "desc";
        }
        if (StringUtils.isBlank(direccion)) {
            direccion = "asc";
        }
        Sort sort;
        switch (direccion) {
            case "desc":
                sort = new Sort(Sort.Direction.DESC, ordena);
                direccionContraria = "asc";
                break;
            default:
                sort = new Sort(Sort.Direction.ASC, ordena);
                direccionContraria = "desc";
        }
        params.put(Constants.PAGINA, pagina);
        params.put(Constants.ORDENA, ordena);
        params.put(Constants.DIRECCION, direccion);
        params.put(Constants.DIRECCION_CONTRARIA, direccionContraria);
        params.put(Constants.FILTRO, filtro);
        PageRequest pageRequest = new PageRequest(pagina, 20, sort);
        params.put(Constants.PAGE_REQUEST, pageRequest);

        return params;
    }
    
    protected void pagina(Model model, Page page, Map<String, Object> params) {
        Integer pagina = (Integer) params.get(Constants.PAGINA);
        String ordena = (String) params.get(Constants.ORDENA);
        String direccion = (String) params.get(Constants.DIRECCION);
        String direccionContraria = (String) params.get(Constants.DIRECCION_CONTRARIA);
        String filtro = (String) params.get(Constants.FILTRO);
        if (pagina == null) {
            pagina = 0;
        }
        List<Integer> paginas = new ArrayList<>();

        int current = page.getNumber() + 1;
        int begin = Math.max(0, current - 6);
        int end = Math.min(begin + 11, page.getTotalPages());

        if (begin > 0) {
            paginas.add(0);
            if (begin > 1) {
                paginas.add(-1);
            }
        }
        for (int i = begin; i < end; i++) {
            paginas.add(i);
        }
        if (end < page.getTotalPages()) {
            paginas.add(-1);
            paginas.add(page.getTotalPages() - 1);
        }
        model.addAttribute("ordena", ordena);
        model.addAttribute("direccion", direccion);
        model.addAttribute("direccionContraria", direccionContraria);
        model.addAttribute("paginaActual", pagina);
        model.addAttribute("paginasTotales", page.getTotalPages() - 1);
        model.addAttribute("paginas", paginas);
        model.addAttribute("filtro", filtro);
    }

    protected void paginate(Map<String, Object> params, Model model, Long page) {
        if (page != null) {
            params.put("page", page);
            model.addAttribute("page", page);
        } else {
            page = 1L;
            model.addAttribute("page", page);
        }
        // pagination start
        Long totalItems = (Long) params.get("totalItems");
        Integer max = (Integer) params.get("max");
        List<Long> pageList = pagination(page, totalItems, max);
        List<?> list = (List<?>) params.get("list");
        Long first = ((page - 1) * max) + 1;
        Long last = first + (list.size() - 1);
        String[] pagination = new String[]{first.toString(),
            last.toString(), totalItems.toString()};
        model.addAttribute("pagination", pagination);
        model.addAttribute("pages", pageList);
        // pagination end
    }

    private List<Long> pagination(Long page, Long totalItems, Integer max) {
        Long numberOfPages = totalItems / max;
        if (totalItems % max > 0) {
            numberOfPages++;
        }
        log.debug("Pagination: {} {} {} {}", new Object[]{page, totalItems,
            max, numberOfPages});
        Set<Long> pageList = new LinkedHashSet<>();
        long h = page - 1;
        long i = page;
        long j = page + 1;
        boolean isThousands = false;
        boolean isFiveHundreds = false;
        boolean isHundreds = false;
        boolean isFifties = false;
        boolean isTens = false;
        boolean isStarted = false;
        if (h > 0 && h > 1000) {
            for (long y = 0; y < h; y += 1000) {
                if (y == 0) {
                    isStarted = true;
                    pageList.add(1l);
                } else {
                    pageList.add(y);
                }
            }
        } else if (h > 0 && h > 500) {
            for (long y = 0; y < h; y += 500) {
                if (y == 0) {
                    isStarted = true;
                    pageList.add(1l);
                } else {
                    pageList.add(y);
                }
            }
        } else if (h > 0 && h > 100) {
            for (long y = 0; y < h; y += 100) {
                if (y == 0) {
                    isStarted = true;
                    pageList.add(1l);
                } else {
                    pageList.add(y);
                }
            }
        } else if (h > 0 && h > 50) {
            for (long y = 0; y < h; y += 50) {
                if (y == 0) {
                    isStarted = true;
                    pageList.add(1l);
                } else {
                    pageList.add(y);
                }
            }
        } else if (h > 0 && h > 10) {
            for (long y = 0; y < h; y += 10) {
                if (y == 0) {
                    isStarted = true;
                    pageList.add(1l);
                } else {
                    pageList.add(y);
                }
            }
        }
        if (i > 1 && i < 4) {
            for (long x = 1; x < i; x++) {
                pageList.add(x);
            }
        } else if (h > 0) {
            if (!isStarted) {
                pageList.add(1L);
            }
            for (long x = h; x < i; x++) {
                pageList.add(x);
            }
        }
        do {
            pageList.add(i);
            if (i > j) {
                if (isThousands || (i + 1000) < numberOfPages) {
                    isThousands = true;
                    i -= i % 1000;
                    i += 999;
                } else if (isFiveHundreds || (i + 500) < numberOfPages) {
                    isFiveHundreds = true;
                    i -= i % 500;
                    i += 499;
                } else if (isHundreds || (i + 100) < numberOfPages) {
                    isHundreds = true;
                    i -= i % 100;
                    i += 99;
                } else if (isFifties || (i + 50) < numberOfPages) {
                    isFifties = true;
                    i -= i % 50;
                    i += 49;
                } else if (isTens || (i + 10) < numberOfPages) {
                    isTens = true;
                    i -= i % 10;
                    i += 9;
                }
            }
        } while (i++ < numberOfPages);
        if (numberOfPages > 0) {
            pageList.add(numberOfPages);
        }

        log.debug("Pages {}: {}", page, pageList);
        return new ArrayList<>(pageList);
    }
}
