package com.eceakin.noteapp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eceakin.noteapp.application.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {
	private final UserService userService;
	/* 
	@GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        String username = SecurityUtils.getCurrentUsername();
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/me")
    public ResponseEntity<UserDto> updateCurrentUser(@Valid @RequestBody CreateUserDto updateUserDto) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        UserDto userDto = userService.updateUser(currentUserId, updateUserDto);
        return ResponseEntity.ok(userDto);
    }
    
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        userService.deleteUser(currentUserId);
        return ResponseEntity.noContent().build();
    } */ 
}
