package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.customer;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.authentication.User;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.Customer;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.loyalty.MembershipTier;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.exception.InvalidRequestException;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.exception.NoDataFoundException;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.CustomerRequest;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.repository.customer.CustomerRepository;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.authentication.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    public Customer convertToEntity(CustomerRequest customerRequest) {
        return objectMapper.convertValue(customerRequest, Customer.class);
    }

    public CustomerRequest convertToDTO(Customer customer) {
        return objectMapper.convertValue(customer, CustomerRequest.class);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerByUserName(String userName) {
        User user = userService.getUserByUsername(userName);
        if(user != null) {
            return customerRepository.findByEmail(user.getEmail())
                    .orElseThrow(() -> new RuntimeException("Customer not found with userName: " + userName));
        }
        else
            throw new NoDataFoundException("No User found with the UserName - " + userName);
    }

    public Optional<Customer> getCustomerById(Long customerId) {
        return customerRepository.findById(customerId);
    }

    public Customer createCustomer(CustomerRequest customerRequest) {
        Customer customer = new Customer();
        updateCustomerFromRequest(customer, customerRequest);
        return customerRepository.save(customer);
    }

    @Transactional
    public Customer createCustomerForUser(CustomerRequest customerRequest, Long userId) {
        // Get the user
        User user = userService.getUserById(userId);
        
        // Check if user already has a customer profile
        if (user.getCustomer() != null) {
            throw new InvalidRequestException("User already has a customer profile");
        }

        // Create customer profile
        Customer customer = this.convertToEntity(customerRequest);
        customer.setUser(user);
        customer.setIsActive(true);
        customer.setMembershipTier(MembershipTier.BRONZE);
        customer.setRewardPoints(0);

        // Set up bidirectional relationship
        user.setCustomer(customer);

        // Save the customer (which will cascade to update the user)
        return customerRepository.save(customer);
    }

    public Customer updateCustomerProfile(CustomerRequest customerRequest, String email) {
        Customer customer = getCustomerByUserName(email);
        updateCustomerFromRequest(customer, customerRequest);
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(CustomerRequest customerRequest, Long customerId) {
        Customer customer = getCustomerById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));
        updateCustomerFromRequest(customer, customerRequest);
        return customerRepository.save(customer);
    }

    public void requestAccountDeletion(String userName) {
        Customer customer = getCustomerByUserName(userName);
        customer.setIsActive(false);
        customer.setNotes("User Requested for Cancellation");
        customerRepository.save(customer);
    }

    public void deleteCustomer(Long customerId) {
        Customer customer = getCustomerById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));
        customerRepository.delete(customer);
    }

    private void updateCustomerFromRequest(Customer customer, CustomerRequest request) {
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        // Need to Add other fields as needed
    }

    public void deleteCustomerById(Long customerId) {
        customerRepository.deleteById(customerId);
    }

    public boolean existsById(Long customerId) {
       return customerRepository.existsById(customerId);
    }

}
