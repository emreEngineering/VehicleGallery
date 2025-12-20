package com.vehiclegallery.service;

import com.vehiclegallery.entity.ServiceRecord;
import com.vehiclegallery.repository.ServiceRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceRecordService {

    @Autowired
    private ServiceRecordRepository serviceRecordRepository;

    public List<ServiceRecord> findAll() {
        return serviceRecordRepository.findAll();
    }

    public Optional<ServiceRecord> findById(Long id) {
        return serviceRecordRepository.findById(id);
    }

    public ServiceRecord save(ServiceRecord serviceRecord) {
        return serviceRecordRepository.save(serviceRecord);
    }

    public void deleteById(Long id) {
        serviceRecordRepository.deleteById(id);
    }

    public List<ServiceRecord> findByVehicleId(Long vehicleId) {
        return serviceRecordRepository.findByVehicleId(vehicleId);
    }

    public long count() {
        return serviceRecordRepository.count();
    }
}
