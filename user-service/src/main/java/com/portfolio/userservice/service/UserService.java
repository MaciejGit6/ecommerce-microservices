package com.portfolio.userservice.service;

import com.portfolio.userservice.dto.UserRequest;
import com.portfolio.userservice.dto.UserResponse;
import com.portfolio.userservice.entity.User;
import com.portfolio.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    
    @Transactional
    public UserResponse createUser(UserRequest request) {
        log.info("Creating user with email: {}", request.getEmail());

        // Guard against duplicate emails upfront rather than letting the DB constraint fire and having to unwrap a DataIntegrityViolationException
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException(
                    "A user with email " + request.getEmail() + " already exists"
            );
        }

        
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        User saved = userRepository.save(user);
        log.info("User created with ID: {}", saved.getId());
        return toResponse(saved);
    }

    //read

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<UserResponse> getUserById(Long id) {
        return userRepository.findById(id).map(this::toResponse);
    }

    //update 

    @Transactional
    public Optional<UserResponse> updateUser(Long id, UserRequest request) {
        return userRepository.findById(id).map(existing -> {
            log.info("Updating user ID: {}", id);
            existing.setUsername(request.getUsername());
            existing.setEmail(request.getEmail());
            // Again, hash before storing in real life
            existing.setPassword(request.getPassword());
            return toResponse(userRepository.save(existing));
        });
    }

    // delete
    @Transactional
    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        log.info("Deleting user ID: {}", id);
        userRepository.deleteById(id);
        return true;
    }

    // helpers
    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}