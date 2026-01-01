package com.vehiclegallery.service;

import com.vehiclegallery.entity.ServiceRecord;
import com.vehiclegallery.repository.ServiceRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServiceRecordService {

    private final ServiceRecordRepository serviceRecordRepository;

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
