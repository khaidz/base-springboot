package net.khaibq.ecommerce.service;

import net.khaibq.ecommerce.dtos.auth.ChangePwDto;
import net.khaibq.ecommerce.dtos.auth.ForgotPwDto;
import net.khaibq.ecommerce.dtos.auth.RegisterUserDto;
import net.khaibq.ecommerce.dtos.user.SearchUserDto;
import net.khaibq.ecommerce.dtos.user.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    void registerUser(RegisterUserDto dto);
    void activeUser(String key);
    void changePw(String key, ChangePwDto dto);
    void forgotPw(ForgotPwDto dto);

    Page<UserDto> getByPage(SearchUserDto searchUserDto, Pageable pageable);
}
