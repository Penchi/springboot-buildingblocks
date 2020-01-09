package com.penchi.restservices.contollers;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
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

import com.penchi.restservices.entities.Order;
import com.penchi.restservices.entities.User;
import com.penchi.restservices.exceptions.UserExistsException;
import com.penchi.restservices.exceptions.UserNameNotFoundException;
import com.penchi.restservices.exceptions.UserNotFoundException;
import com.penchi.restservices.services.UserService;

@RestController
@Validated
@RequestMapping(value = "/hateoas/users")
public class UserHateoasController {

	@Autowired
	private UserService userService;

	@GetMapping
	public CollectionModel<User> getAllUsers() throws UserNotFoundException {
		List<User> allUsers = userService.getAllUsers();
		for (User user : allUsers) {
			Long userId = user.getId();
			Link selfLink = WebMvcLinkBuilder.linkTo(this.getClass()).slash(userId).withSelfRel();
			user.add(selfLink);

			// Relationship Linking with getAllOrders()
			CollectionModel<Order> finalOrdersRes = WebMvcLinkBuilder.methodOn(OrderHateoasController.class)
					.getAllOrders(userId);

			Link orderLinks = WebMvcLinkBuilder.linkTo(finalOrdersRes).withRel("all-orders");
			user.add(orderLinks);

		}

		//
		Link selfLinkGetAllUsers = WebMvcLinkBuilder.linkTo(this.getClass()).withSelfRel();
		CollectionModel<User> finalResources = new CollectionModel<>(allUsers, selfLinkGetAllUsers);
		return finalResources;

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

	@GetMapping("/{id}")
	public Optional<User> getUserById(@PathVariable("id") @Min(1) Long id) {
		try {
			return userService.getUserById(id);
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
