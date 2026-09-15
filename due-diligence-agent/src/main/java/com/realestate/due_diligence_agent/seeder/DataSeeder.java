package com.realestate.due_diligence_agent.seeder;

import com.realestate.due_diligence_agent.entity.*;
import com.realestate.due_diligence_agent.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyHistoryRepository propertyHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedAdminUser();
        seedPropertiesAndHistory();
    }

    private void seedAdminUser() {
        if (userRepository.findByEmail("admin@realestateapp.com").isEmpty()) {
            User admin = User.builder()
                    .fullName("Default Administrator")
                    .email("admin@realestateapp.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(Role.ADMINISTRATOR)
                    .build();
            userRepository.save(admin);
            System.out.println("Default Administrator seeded: admin@realestateapp.com / Admin@123");
        }
    }

    private void seedPropertiesAndHistory() {
        if (propertyRepository.count() == 0) {
            Property p1 = Property.builder()
                    .address("742 Evergreen Terrace")
                    .city("Springfield")
                    .state("IL")
                    .zipCode("62704")
                    .latitude(39.7817)
                    .longitude(-89.6501)
                    .propertyType("RESIDENTIAL")
                    .price(new BigDecimal("350000.00"))
                    .bedrooms(4)
                    .bathrooms(2)
                    .squareFeet(2200.0)
                    .yearBuilt(1989)
                    .ownerName("Homer Simpson")
                    .description("Spacious family suburban home with backyard and garage.")
                    .build();

            Property p2 = Property.builder()
                    .address("1007 Mountain Drive")
                    .city("Gotham")
                    .state("NJ")
                    .zipCode("07001")
                    .latitude(40.7128)
                    .longitude(-74.0060)
                    .propertyType("RESIDENTIAL")
                    .price(new BigDecimal("12500000.00"))
                    .bedrooms(8)
                    .bathrooms(10)
                    .squareFeet(15000.0)
                    .yearBuilt(1939)
                    .ownerName("Bruce Wayne")
                    .description("Large luxury manor estate with private grounds.")
                    .build();

            Property p3 = Property.builder()
                    .address("350 5th Avenue")
                    .city("New York")
                    .state("NY")
                    .zipCode("10118")
                    .latitude(40.7484)
                    .longitude(-73.9857)
                    .propertyType("COMMERCIAL")
                    .price(new BigDecimal("85000000.00"))
                    .bedrooms(0)
                    .bathrooms(20)
                    .squareFeet(50000.0)
                    .yearBuilt(1931)
                    .ownerName("Empire Holdings LLC")
                    .description("Prime high-rise commercial retail and office building.")
                    .build();

            propertyRepository.saveAll(List.of(p1, p2, p3));

            // Seed sample history for Property 1 (742 Evergreen Terrace)
            PropertyHistory h1 = PropertyHistory.builder()
                    .property(p1)
                    .eventDate(LocalDate.of(2023, 5, 12))
                    .eventType("SALE")
                    .price(new BigDecimal("320000.00"))
                    .description("Sold to Homer Simpson")
                    .recordedBy("County Land Registry")
                    .build();

            PropertyHistory h2 = PropertyHistory.builder()
                    .property(p1)
                    .eventDate(LocalDate.of(2020, 8, 20))
                    .eventType("TAX_ASSESSMENT")
                    .price(new BigDecimal("295000.00"))
                    .description("Annual property tax reassessment")
                    .recordedBy("State Tax Assessor")
                    .build();

            propertyHistoryRepository.saveAll(List.of(h1, h2));
            System.out.println("Properties and History records seeded successfully!");
        }
    }
}