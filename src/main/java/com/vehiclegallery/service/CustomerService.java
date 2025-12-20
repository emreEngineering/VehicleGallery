package com.vehiclegallery.service;

import com.vehiclegallery.entity.Customer;
import com.vehiclegallery.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    public void deleteById(Long id) {
        customerRepository.deleteById(id);
    }

    public List<Customer> findByCustomerType(String customerType) {
        return customerRepository.findByCustomerType(customerType);
    }

    public long count() {
        return customerRepository.count();
    }
}
