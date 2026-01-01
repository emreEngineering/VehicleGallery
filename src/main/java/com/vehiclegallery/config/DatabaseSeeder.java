package com.vehiclegallery.config;

import com.vehiclegallery.entity.Vehicle;
import com.vehiclegallery.repository.VehicleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.ArrayList;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (vehicleRepository.count() == 0) {
            System.out.println("No vehicles found. seeding database with sample vehicles...");
            List<Vehicle> vehicles = new ArrayList<>();

            // 1. Fiat Egea
            Vehicle v1 = new Vehicle();
            v1.setBrand("Fiat");
            v1.setModel("Egea");
            v1.setYear(2023);
            v1.setColor("White");
            v1.setVehicleType("FUEL");
            v1.setFuelType("DIESEL");
            v1.setPlateNumber("34 FGS 101");
            v1.setMileage(15000);
            v1.setChassisNumber("CH1000001");
            vehicles.add(v1);

            // 2. Renault Clio
            Vehicle v2 = new Vehicle();
            v2.setBrand("Renault");
            v2.setModel("Clio");
            v2.setYear(2023);
            v2.setColor("Orange");
            v2.setVehicleType("FUEL");
            v2.setFuelType("GASOLINE");
            v2.setPlateNumber("34 RNC 202");
            v2.setMileage(10000);
            v2.setChassisNumber("CH1000002");
            vehicles.add(v2);

            // 3. Toyota Corolla
            Vehicle v3 = new Vehicle();
            v3.setBrand("Toyota");
            v3.setModel("Corolla");
            v3.setYear(2023);
            v3.setColor("Silver");
            v3.setVehicleType("HYBRID");
            v3.setFuelType("GASOLINE");
            v3.setPlateNumber("34 TYT 303");
            v3.setMileage(5000);
            v3.setChassisNumber("CH1000003");
            vehicles.add(v3);

            // 4. Honda Civic
            Vehicle v4 = new Vehicle();
            v4.setBrand("Honda");
            v4.setModel("Civic");
            v4.setYear(2022);
            v4.setColor("Blue");
            v4.setVehicleType("FUEL");
            v4.setFuelType("GASOLINE");
            v4.setPlateNumber("34 HND 404");
            v4.setMileage(25000);
            v4.setChassisNumber("CH1000004");
            vehicles.add(v4);

            // 5. Volkswagen Passat
            Vehicle v5 = new Vehicle();
            v5.setBrand("Volkswagen");
            v5.setModel("Passat");
            v5.setYear(2022);
            v5.setColor("Black");
            v5.setVehicleType("FUEL");
            v5.setFuelType("DIESEL");
            v5.setPlateNumber("34 VWG 505");
            v5.setMileage(40000);
            v5.setChassisNumber("CH1000005");
            vehicles.add(v5);

            // 6. Ford Focus
            Vehicle v6 = new Vehicle();
            v6.setBrand("Ford");
            v6.setModel("Focus");
            v6.setYear(2023);
            v6.setColor("Grey");
            v6.setVehicleType("FUEL");
            v6.setFuelType("DIESEL");
            v6.setPlateNumber("34 FRD 606");
            v6.setMileage(12000);
            v6.setChassisNumber("CH1000006");
            vehicles.add(v6);

            // 7. Hyundai i20
            Vehicle v7 = new Vehicle();
            v7.setBrand("Hyundai");
            v7.setModel("i20");
            v7.setYear(2024);
            v7.setColor("Red");
            v7.setVehicleType("FUEL");
            v7.setFuelType("GASOLINE");
            v7.setPlateNumber("34 HYD 707");
            v7.setMileage(2000);
            v7.setChassisNumber("CH1000007");
            vehicles.add(v7);

            vehicleRepository.saveAll(vehicles);
            System.out.println("Successfully seeded " + vehicles.size() + " sample vehicles.");
        } else {
            System.out.println("Vehicles table is not empty. Skipping seeding.");
        }
    }
}
