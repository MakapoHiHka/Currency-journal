package com.openSolutions.currencyJournal.converter;

import com.openSolutions.currencyJournal.domain.dto.request.PageQueryRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

/**
 * Маппер для преобразования PageQueryRequest в Pageable.
 */
@Component
public class PageableMapper implements PageableConverter<PageQueryRequest> {
    public Pageable getPageable(PageQueryRequest request) {
        Sort sort = "desc".equalsIgnoreCase(request.getSortDir())
                ? Sort.by(request.getSortBy()).descending()
                : Sort.by(request.getSortBy()).ascending();
        return PageRequest.of(request.getPage(), request.getSize(), sort);
    }
}
