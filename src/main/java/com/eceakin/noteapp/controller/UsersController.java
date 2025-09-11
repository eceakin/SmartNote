package com.eceakin.noteapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eceakin.noteapp.application.dto.CreateUserDto;
import com.eceakin.noteapp.application.dto.UpdateUserDto;
import com.eceakin.noteapp.application.dto.UserDto;
import com.eceakin.noteapp.application.service.UserService;
import com.eceakin.noteapp.security.SecurityUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {
	private final UserService userService;
	
	@GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        String username = SecurityUtils.getCurrentUsername();
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/me")
    public ResponseEntity<UserDto> updateCurrentUser(@Valid @RequestBody UpdateUserDto updateUserDto) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        UserDto userDto = userService.updateUser(currentUserId, updateUserDto);
        return ResponseEntity.ok(userDto);
    }
     /* 
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        userService.deleteUser(currentUserId);
        return ResponseEntity.noContent().build();
    }   */ 
}
