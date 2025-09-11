package com.eceakin.noteapp.application.service;

import java.util.List;
import java.util.Optional;

import com.eceakin.noteapp.application.dto.CreateUserDto;
import com.eceakin.noteapp.application.dto.UpdateUserDto;
import com.eceakin.noteapp.application.dto.UserDto;

public interface UserService {
	
	UserDto createUser(CreateUserDto createUserDto);

	Optional<UserDto> getUserById(Long id);

	Optional<UserDto> getUserByUsername(String username);

	List<UserDto> getAllUsers();

	UserDto updateUser(Long id, UpdateUserDto updateUserDto);

	//void deleteUser(Long id);

}
