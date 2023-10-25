package net.khaibq.ecommerce.dtos.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePwDto {
    @NotBlank(message = "Password must not be blank")
    @Length(min = 3, max = 30, message = "Password length must be between 3 and 30")
    private String password;

    private String confirmPassword;
}
