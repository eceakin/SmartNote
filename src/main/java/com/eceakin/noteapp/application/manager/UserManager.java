package com.eceakin.noteapp.application.manager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eceakin.noteapp.application.dto.CreateUserDto;
import com.eceakin.noteapp.application.dto.UpdateUserDto;
import com.eceakin.noteapp.application.dto.UserDto;
import com.eceakin.noteapp.application.rules.UserBusinessRules;
import com.eceakin.noteapp.application.service.UserService;
import com.eceakin.noteapp.model.User;
import com.eceakin.noteapp.repository.UserRepository;
import com.eceakin.noteapp.shared.mapper.ModelMapperService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserManager implements UserService {
	private final ModelMapperService modelMapperService;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserBusinessRules businessRules;

	@Override
	public UserDto createUser(CreateUserDto createUserDto) {
		this.businessRules.checkIfEmailExists(createUserDto.getEmail());
		this.businessRules.checkIfUsernameExists(createUserDto.getUsername());
		this.businessRules.checkIfPasswordStrong(createUserDto.getPassword());

		User user = this.modelMapperService.forRequest().map(createUserDto, User.class);
		User savedUser = userRepository.save(user);
		return this.modelMapperService.forResponse().map(savedUser, UserDto.class);
	}

	@Override
	public Optional<UserDto> getUserById(Long id) {
		this.businessRules.checkIfUserExists(id);
		return userRepository.findById(id).map(user -> modelMapperService.forResponse().map(user, UserDto.class));
	}

	@Override
	public Optional<UserDto> getUserByUsername(String username) {
		return userRepository.findByUsername(username)
				.map(user -> modelMapperService.forResponse().map(user, UserDto.class));
	}

	@Override
	public List<UserDto> getAllUsers() {
		return userRepository.findAll().stream().map(user -> modelMapperService.forResponse().map(user, UserDto.class))
				.collect(Collectors.toList());
	}

	@Override
	public UserDto updateUser(Long id, UpdateUserDto updateUserDto) {

		
		User user = userRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
		
		 // Eğer email değişiyorsa kontrol et
        if (!user.getEmail().equals(updateUserDto.getEmail())) {
            this.businessRules.checkIfEmailExists(updateUserDto.getEmail());
        }

        // Eğer username değişiyorsa kontrol et
        if (!user.getUsername().equals(updateUserDto.getUsername())) {
            this.businessRules.checkIfUsernameExists(updateUserDto.getUsername());
        }
		
		user.setUsername(updateUserDto.getUsername());
		user.setEmail(updateUserDto.getEmail());
		user.setFirstName(updateUserDto.getFirstName());
		user.setLastName(updateUserDto.getLastName());
		if (updateUserDto.getPassword() != null && !updateUserDto.getPassword().isBlank()) {
			this.businessRules.checkIfPasswordStrong(updateUserDto.getPassword());
			user.setPassword(passwordEncoder.encode(updateUserDto.getPassword()));
		}

		User updatedUser = userRepository.save(user);
		return modelMapperService.forResponse().map(updatedUser, UserDto.class);
	}

}
