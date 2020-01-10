package com.penchi.restservices.contollers;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.penchi.restservices.dtos.UserMsDto;
import com.penchi.restservices.entities.User;
import com.penchi.restservices.exceptions.UserNotFoundException;
import com.penchi.restservices.mappers.UserMapper;
import com.penchi.restservices.services.UserService;

@RestController
@Validated
@RequestMapping(value = "/mapstruct/users")
public class UserMapStructController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserMapper userMapper;

	@GetMapping
	public List<UserMsDto> getAllUsers() {
		return userMapper.usersToUsersDtos(userService.getAllUsers());
	}

	@GetMapping("/{id}")
	public UserMsDto getUserById(@PathVariable("id") @Min(1) Long id) throws UserNotFoundException {

		Optional<User> optionalUser = userService.getUserById(id);
		return userMapper.userToUserMsDto(optionalUser.get());

	}

}
