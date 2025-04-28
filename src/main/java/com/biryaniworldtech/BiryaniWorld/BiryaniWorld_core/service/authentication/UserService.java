package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.authentication;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.authentication.User;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.Customer;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.loyalty.MembershipTier;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.authentication.SignupRequest;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.authentication.UserUpdateRequest;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.repository.authentication.UserRepository;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.repository.customer.CustomerRepository;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.exception.ResourceAlreadyExistsException;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Loading user by username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User not found with username: {}", username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });

        logger.debug("User found: {}", user.getUsername());
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>()
        );
    }

    @Transactional
    public void registerUser(SignupRequest signupRequest) {
        logger.debug("Attempting to register new user: {}", signupRequest.getUsername());

        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            logger.error("Username already taken: {}", signupRequest.getUsername());
            throw new ResourceAlreadyExistsException("Username is already taken!");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            logger.error("Email already in use: {}", signupRequest.getEmail());
            throw new ResourceAlreadyExistsException("Email is already in use!");
        }

        // Create User
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setPhoneNumber(signupRequest.getPhoneNumber());

        // Create Customer
        Customer customer = Customer.builder()
                .firstName(signupRequest.getFirstName())
                .lastName(signupRequest.getLastName())
                .email(signupRequest.getEmail())
                .phone(signupRequest.getPhoneNumber())
                .isActive(true)
                .membershipTier(MembershipTier.BRONZE)
                .rewardPoints(0)
                .build();

        // Set up bidirectional relationship
        user.setCustomer(customer);
        customer.setUser(user);

        // Save the user (which will cascade to save the customer)
        userRepository.save(user);
        logger.info("Successfully registered new user: {}", user.getUsername());
    }

    @Transactional
    public User getUserById(Long id) {
        logger.debug("Fetching user by ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });
    }

    @Transactional
    public void updateUserRole(Long userId, User.Role newRole) {
        logger.debug("Updating role for user ID: {} to role: {}", userId, newRole);
        User user = getUserById(userId);
        user.setRole(newRole);
        userRepository.save(user);
        logger.info("Successfully updated role for user: {} to {}", user.getUsername(), newRole);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        logger.debug("Fetching all users");
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        logger.debug("Fetching user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User not found with username: {}", username);
                    return new ResourceNotFoundException("User not found with username: " + username);
                });
    }

    @Transactional
    public void deleteUserByUsername(String username) {
        logger.debug("Deleting user with username: {}", username);
        User user = getUserByUsername(username);
        
        // First, remove the customer if it exists
        if (user.getCustomer() != null) {
            Customer customer = user.getCustomer();
            // Remove the bidirectional relationship
            customer.setUser(null);
            user.setCustomer(null);
            // Delete the customer
            customerRepository.delete(customer);
        }
        
        // Now delete the user
        userRepository.delete(user);
        logger.info("Successfully deleted user: {}", username);
    }

    @Transactional
    public User updateUser(Long userId, UserUpdateRequest updateRequest) {
        logger.debug("Updating user with ID: {}", userId);
        User user = getUserById(userId);

        // Check if username is being changed and if it's already taken
        if (!user.getUsername().equals(updateRequest.getUsername()) &&
            userRepository.existsByUsername(updateRequest.getUsername())) {
            throw new ResourceAlreadyExistsException("Username is already taken!");
        }

        // Check if email is being changed and if it's already in use
        if (!user.getEmail().equals(updateRequest.getEmail()) &&
            userRepository.existsByEmail(updateRequest.getEmail())) {
            throw new ResourceAlreadyExistsException("Email is already in use!");
        }

        // Update username and email
        user.setUsername(updateRequest.getUsername());
        user.setEmail(updateRequest.getEmail());

        // Update password if provided
        if (updateRequest.getCurrentPassword() != null && updateRequest.getNewPassword() != null) {
            if (!passwordEncoder.matches(updateRequest.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Current password is incorrect");
            }
            user.setPassword(passwordEncoder.encode(updateRequest.getNewPassword()));
        }

        User updatedUser = userRepository.save(user);
        logger.info("Successfully updated user: {}", updatedUser.getUsername());
        return updatedUser;
    }
} 