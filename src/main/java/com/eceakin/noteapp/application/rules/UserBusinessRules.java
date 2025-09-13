package com.eceakin.noteapp.application.rules;

import org.springframework.stereotype.Service;

import com.eceakin.noteapp.repository.UserRepository;
import com.eceakin.noteapp.shared.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserBusinessRules {

    private final UserRepository userRepository;

    // Email daha önce kullanılmış mı?
    public void checkIfEmailExists(String email) {
        if (this.userRepository.existsByEmail(email)) {
            throw new BusinessException("Email already used!");
        }
    }

    // Username daha önce alınmış mı?
    public void checkIfUsernameExists(String username) {
        if (this.userRepository.findByUsername(username).isPresent()) {
            throw new BusinessException("Username already taken!");
        }
    }

    // User var mı? (update, delete gibi işlemler için)
    public void checkIfUserExists(Long id) {
        if (!this.userRepository.existsById(id)) {
            throw new BusinessException("User not found with id: " + id);
        }
    }

    // Şifre güçlü mü?
    public void checkIfPasswordStrong(String password) {
        if (password == null ||
            password.length() < 8 ||
            !password.matches(".*[A-Z].*") ||               // En az 1 büyük harf
            !(password.contains(".") || password.contains("-"))) { // Nokta veya -
            throw new BusinessException(
                "Şifre yeterince güçlü değil! En az 8 karakter, bir büyük harf ve '.' veya '-' içermeli."
            );
        }
    }
}