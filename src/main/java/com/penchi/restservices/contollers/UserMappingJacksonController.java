package com.penchi.restservices.contollers;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.penchi.restservices.entities.User;
import com.penchi.restservices.exceptions.UserExistsException;
import com.penchi.restservices.exceptions.UserNameNotFoundException;
import com.penchi.restservices.exceptions.UserNotFoundException;
import com.penchi.restservices.services.UserService;

@RestController
@Validated
@RequestMapping(value = "/jacksonfilter/users")
public class UserMappingJacksonController {

	@Autowired
	private UserService userService;

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

	// Static filtering by hard coded set
	@GetMapping("/{id}")
	public MappingJacksonValue getUserById(@PathVariable("id") @Min(1) Long id) {
		try {
			Optional<User> optionalUser = userService.getUserById(id);
			User user = optionalUser.get();

			Set<String> fields = new HashSet<String>();
			fields.add("userName");
			fields.add("id");
			fields.add("ssn");

			FilterProvider filterProvider = new SimpleFilterProvider().addFilter("userFilter",
					SimpleBeanPropertyFilter.filterOutAllExcept(fields));

			MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(user);
			mappingJacksonValue.setFilters(filterProvider);
			return mappingJacksonValue;

		} catch (UserNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	// Dynamic filtering by @RequestParams
	@GetMapping("/params/{id}")
	public MappingJacksonValue getUserById1(@PathVariable("id") @Min(1) Long id, @RequestParam Set<String> fields) {
		try {
			Optional<User> optionalUser = userService.getUserById(id);
			User user = optionalUser.get();

			/*
			 * Set<String> fields = new HashSet<String>(); fields.add("userName");
			 * fields.add("id"); fields.add("ssn");
			 */

			FilterProvider filterProvider = new SimpleFilterProvider().addFilter("userFilter",
					SimpleBeanPropertyFilter.filterOutAllExcept(fields));

			MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(user);
			mappingJacksonValue.setFilters(filterProvider);
			return mappingJacksonValue;

		} catch (UserNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
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
