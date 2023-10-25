package net.khaibq.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import net.khaibq.ecommerce.dtos.auth.ChangePwDto;
import net.khaibq.ecommerce.dtos.auth.ForgotPwDto;
import net.khaibq.ecommerce.dtos.auth.RegisterUserDto;
import net.khaibq.ecommerce.exception.BaseException;
import net.khaibq.ecommerce.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    @GetMapping("/login")
    public String loginPage() {
        return "pages/auth/auth-login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerDto", new RegisterUserDto());
        return "pages/auth/auth-register";
    }

    @PostMapping("/register")
    public String doRegister(@Valid @ModelAttribute("registerDto") RegisterUserDto registerUserDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()){
            handleValidateDto(bindingResult, model);
            return "pages/auth/auth-register";
        }
        try {
            userService.registerUser(registerUserDto);
            redirectAttributes.addFlashAttribute("email", registerUserDto.getEmail());
            return "pages/auth/auth-register-success";
        } catch (BaseException ex){
            model.addAttribute("errors", List.of(ex.getMessage()));
        }
        return "pages/auth/auth-register";
    }

    @GetMapping("/register/active/{key}")
    public String activeUserPage(@PathVariable String key, Model model) {
        try {
            userService.activeUser(key);
            model.addAttribute("alert", "Kích hoạt tài khoản thành công. Tiến hành đăng nhập để sử dụng các dịch vụ hệ thống");
        } catch (BaseException ex){
            model.addAttribute("error", ex.getMessage());
        }
        return "pages/auth/auth-register-active";
    }

    @GetMapping("/recoverpw")
    public String recoverpwPage(Model model) {
        model.addAttribute("forgotPwDto", new ForgotPwDto());
        return "pages/auth/auth-recoverpw";
    }

    @PostMapping("/recoverpw")
    public String doRecoverpw(@Valid @ModelAttribute ForgotPwDto dto, Model model) {
        try {
            userService.forgotPw(dto);
            model.addAttribute("email", dto.getEmail());
        } catch (BaseException ex){
            model.addAttribute("error", ex.getMessage());
            return "pages/auth/auth-recoverpw";
        }
        return "pages/auth/auth-recoverpw-email";
    }

    @GetMapping("/recoverpw/{key}")
    public String changepwPage(@PathVariable String key, Model model) {
        model.addAttribute("changePwDto", new ChangePwDto());
        return "pages/auth/auth-changepw";
    }

    @PostMapping("/recoverpw/{key}")
    public String doChangepw(@PathVariable String key, @Valid @ModelAttribute ChangePwDto dto,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()){
            handleValidateDto(bindingResult, model);
            return "pages/auth/auth-changepw";
        }
        try {
            userService.changePw(key, dto);
            model.addAttribute("alert", "Thay đổi mật khẩu thành công");
        } catch (BaseException ex){
            model.addAttribute("error", ex.getMessage());
        }
        return "pages/auth/auth-changepw";
    }

    @GetMapping("/logout")
    public String logoutPage() {
        return "pages/auth/auth-logout";
    }

    private void handleValidateDto(BindingResult bindingResult, Model model) {
        Map<String, String> map = new HashMap<>();
        bindingResult.getFieldErrors()
                .forEach(x -> {
                    if (!map.containsKey(x.getField())){
                        map.put(x.getField(), x.getDefaultMessage());
                    }
                });
        model.addAttribute("errors", map.values());
    }
}
