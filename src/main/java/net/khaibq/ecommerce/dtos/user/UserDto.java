package net.khaibq.ecommerce.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String fullName;
    private Date birthday;
    private String birthdayFormat;
    private String address;
    private String avatar;
    private String roles;
    private Integer status;
    private Integer isDeleted;

    public String getBirthdayFormat() {
        if (birthday == null) return null;
        return new SimpleDateFormat("dd/MM/yyyy").format(birthday);
    }
}
