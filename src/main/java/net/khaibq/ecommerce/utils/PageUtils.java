package net.khaibq.ecommerce.utils;

import net.khaibq.ecommerce.dtos.PageDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PageUtils {
    public PageDto getPage(Page<?> page) {
        PageDto pageDto = new PageDto();
        pageDto.setTotalElement(page.getTotalElements());
        pageDto.setTotalPage(page.getTotalPages());
        pageDto.setCurrentPage(page.getPageable().getPageNumber());
        pageDto.setSize(page.getPageable().getPageSize());
        return pageDto;
    }
}
