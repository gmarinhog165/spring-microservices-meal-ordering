package com.example.customerservice.service;

import com.example.customerservice.entity.Customer;
import com.example.customerservice.repository.CustomerRepository;
import com.example.customerservice.request.CreateCustomerRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomerService {

    private CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void create(CreateCustomerRequest request){
        Customer customer = Customer.builder()
                .keycloakId(request.getKeycloakId())
                .email(request.getEmail())
                .name(request.getName())
                .build();
        customerRepository.save(customer);
        log.info("Customer created: {}", customer);
    }
}
