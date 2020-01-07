package com.penchi.restservices.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.penchi.restservices.entities.User;
import com.penchi.restservices.exceptions.UserExistsException;
import com.penchi.restservices.exceptions.UserNotFoundException;
import com.penchi.restservices.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	public List<User> getAllUsers(){
		return userRepository.findAll();
	}
	
	public User createUser(User user) throws UserExistsException {
		
		User existingUser = userRepository.findByUserName(user.getUserName());
		if(existingUser != null) {
			throw new UserExistsException("User Already Exists in Repository");
		}
		
		return userRepository.save(user);
	}
	
	public Optional<User> getUserById(Long id) throws UserNotFoundException{
		Optional<User> user = userRepository.findById(id);
		if(!user.isPresent()) {
			throw new UserNotFoundException("User Not Found in User Repository");
		}
		return user;
	}
	
	public User updateUserById(Long id, User user) throws UserNotFoundException {
		Optional<User> optUser = userRepository.findById(id);
		if(!optUser.isPresent()) {
			throw new UserNotFoundException("User Not Found in User Repository, Provide the Correct User Id");
		}
		user.setId(id);
		return userRepository.save(user);
	}
	
	public void deleteUserById(Long id) {
		
		Optional<User> optUser = userRepository.findById(id);
		if(!optUser.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User Not Found in User Repository, Provide the Correct User Id");
		}
		//if(userRepository.findById(id).isPresent()) {
			userRepository.deleteById(id);
		//}
	}
	
	public User getUserByUserName(String userName){
		return userRepository.findByUserName(userName);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
