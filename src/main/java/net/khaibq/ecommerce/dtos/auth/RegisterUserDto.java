package net.khaibq.ecommerce.dtos.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDto {
    @NotBlank(message = "Username must not be blank")
    @Length(min = 3, max = 30, message = "Username length must be between 3 and 30")
    private String username;
    @NotBlank(message = "Password must not be blank")
    @Length(min = 3, max = 30, message = "Password length must be between 3 and 30")
    private String password;
    @NotNull
    @Email
    private String email;
    @NotBlank(message = "Fullname must not be blank")
    @Length(max = 50, message = "Fullname length must be less than 50")
    private String fullName;
}
