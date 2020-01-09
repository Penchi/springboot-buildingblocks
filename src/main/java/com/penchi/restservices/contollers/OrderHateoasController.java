package com.penchi.restservices.contollers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import com.penchi.restservices.entities.Order;
import com.penchi.restservices.entities.User;
import com.penchi.restservices.exceptions.UserNotFoundException;
import com.penchi.restservices.repositories.OrderRepository;
import com.penchi.restservices.repositories.UserRepository;

@RestController
@RequestMapping(value = "/hateoas/users")
public class OrderHateoasController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OrderRepository orderRepository;

	@GetMapping("/{userid}/orders")
	public CollectionModel<Order> getAllOrders(@PathVariable Long userid) throws UserNotFoundException {

		Optional<User> useroptional = userRepository.findById(userid);

		if (!useroptional.isPresent())
			throw new UserNotFoundException("User Not Found");

		List<Order> allOrders = useroptional.get().getOrders();
		CollectionModel<Order> finalOrders = new CollectionModel<>(allOrders);
		return finalOrders;

	}

	@PostMapping("/{userid}/orders")
	public ResponseEntity<Void> createOrder(@PathVariable Long userid, @RequestBody Order order,
			UriComponentsBuilder builder) throws UserNotFoundException {
		Optional<User> userOptional = userRepository.findById(userid);

		if (!userOptional.isPresent())
			throw new UserNotFoundException("User Not Found");

		User user = userOptional.get();
		order.setUser(user);
		orderRepository.save(order);
		Map<String, Object> map = new HashMap<>();
		map.put("userid", user.getId());
		map.put("orderid", order.getOrderid());

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(builder.path("/users/{userid}/orders/{orderid}").buildAndExpand(map).toUri());
		return new ResponseEntity<Void>(headers, HttpStatus.CREATED);

	}

	@GetMapping("/{userid}/orders/{orderid}")
	public Order getOrderByOrderId(@PathVariable Long userid, @PathVariable Long orderid) throws UserNotFoundException {
		Optional<User> userOptional = userRepository.findById(userid);

		if (!userOptional.isPresent())
			throw new UserNotFoundException("User Not Found");

		Optional<Order> orderOptional = orderRepository.findById(orderid);

		if (!orderOptional.isPresent())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order Not Found");

		Order order = orderOptional.get();

		if (order.getUser().getId() != userid)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order Not Found with Respect to User");

		return order;
	}

}
