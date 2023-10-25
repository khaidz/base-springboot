package net.khaibq.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import net.khaibq.ecommerce.dtos.user.SearchUserDto;
import net.khaibq.ecommerce.dtos.user.UserDto;
import net.khaibq.ecommerce.service.UserService;
import net.khaibq.ecommerce.utils.PageUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PageUtils pageUtils;

    @GetMapping("/admin/user")
    public String userListPage(SearchUserDto searchUserDto, @PageableDefault(size = 7) Pageable pageable, Model model) {
        Page<UserDto> pageList = userService.getByPage(searchUserDto, pageable);
        model.addAttribute("dataList", pageList.toList());
        model.addAttribute("page", pageUtils.getPage(pageList));
        return "pages/admin/user";
    }
}
