package com.penchi.restservices.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.penchi.restservices.dtos.UserMsDto;
import com.penchi.restservices.entities.User;

@Mapper(componentModel = "Spring")
public interface UserMapper {

	UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

	// User to UserMsDto
	@Mappings({ @Mapping(source = "email", target = "emailaddress"), @Mapping(source = "role", target = "rolename") })
	UserMsDto userToUserMsDto(User user);

	// List<User> to List<UserMsDto>
	List<UserMsDto> usersToUsersDtos(List<User> users);

}
