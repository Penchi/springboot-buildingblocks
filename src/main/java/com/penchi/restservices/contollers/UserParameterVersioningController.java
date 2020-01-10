package com.penchi.restservices.contollers;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import com.penchi.restservices.dtos.UserDtoV1;
import com.penchi.restservices.dtos.UserDtoV2;
import com.penchi.restservices.entities.User;
import com.penchi.restservices.exceptions.UserExistsException;
import com.penchi.restservices.exceptions.UserNameNotFoundException;
import com.penchi.restservices.exceptions.UserNotFoundException;
import com.penchi.restservices.services.UserService;

@RestController
@Validated
@RequestMapping(value = "/versioning/params/users")
public class UserParameterVersioningController {

	@Autowired
	private UserService userService;

	@Autowired
	private ModelMapper modelMapper;

	@GetMapping
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}

	@PostMapping
	public ResponseEntity<Void> createUser(@Valid @RequestBody User user, UriComponentsBuilder builder) {
		try {
			userService.createUser(user);
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(builder.path("/users/{id}").buildAndExpand(user.getId()).toUri());
			return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
		} catch (UserExistsException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	// Request Parameter Versioning- V1
	@GetMapping(value = "/{id}", params = "version=1")
	public UserDtoV1 getUserByIdV1(@PathVariable("id") @Min(1) Long id) throws UserNotFoundException {

		Optional<User> optionalUser = userService.getUserById(id);

		if (!optionalUser.isPresent()) {
			throw new UserNotFoundException("User Not Found");
		}

		User user = optionalUser.get();
		UserDtoV1 userDtoV1 = modelMapper.map(user, UserDtoV1.class);
		return userDtoV1;
	}

	// Parameter Versioning- V2
	@GetMapping(value = "/{id}", params = "version=2")
	public UserDtoV2 getUserByIdV2(@PathVariable("id") @Min(1) Long id) throws UserNotFoundException {

		Optional<User> optionalUser = userService.getUserById(id);

		if (!optionalUser.isPresent()) {
			throw new UserNotFoundException("User Not Found");
		}

		User user = optionalUser.get();
		UserDtoV2 userDtoV2 = modelMapper.map(user, UserDtoV2.class);
		return userDtoV2;
	}

	@PutMapping("/{id}")
	public User updateUserById(@PathVariable("id") Long id, @RequestBody User user) {
		try {
			return userService.updateUserById(id, user);
		} catch (UserNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	public void deleteUserById(@PathVariable("id") Long id) {
		userService.deleteUserById(id);
	}

	@GetMapping("/byusername/{userName}")
	public User getUserByUserName(@PathVariable("userName") String userName) throws UserNameNotFoundException {
		User user = userService.getUserByUserName(userName);
		if (user == null)
			throw new UserNameNotFoundException("User Name: " + userName + " not found in the user repository.");
		return user;
	}

}
