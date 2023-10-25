package net.khaibq.ecommerce.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageDto {
    private Long totalElement;
    private Integer totalPage;
    private Integer currentPage;
    private Integer size;

    public long getFromElement() {
        long temp = (long) currentPage * size + 1;
        if (temp > totalElement) {
            temp = 0;
        }
        return temp;
    }

    public long getToElement() {
        if (getFromElement() == 0L) return 0;
        long temp = (long) currentPage * size + size;
        if (temp > totalElement) {
            temp = totalElement;
        }
        return temp;
    }

    public List<Integer> getShowPage(){

        List<Integer> showPage = new ArrayList<>();
        showPage.add(currentPage);
        int left = currentPage - 1;
        int right= currentPage + 1;
        do {
            if (left >= 0) {
                showPage.add(0, left);
                left--;
            }
            if (right <= totalPage - 1) {
                showPage.add(right);
                right++;
            }
        } while (showPage.size() < 7 && (left >= 0 || right < totalPage));
        return showPage;
    }

    public boolean enableNextPage() {
        return currentPage < totalPage - 1;
    }

    public boolean enablePreviousPage() {
        return currentPage > 0;
    }
}
