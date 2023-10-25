package net.khaibq.ecommerce.mapping;

import net.khaibq.ecommerce.dtos.user.UserDto;
import net.khaibq.ecommerce.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends EntityMapper<User, UserDto> {
}
