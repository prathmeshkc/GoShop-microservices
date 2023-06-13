package com.pcdev.inventoryservice;

import com.pcdev.inventoryservice.model.Inventory;
import com.pcdev.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(InventoryRepository inventoryRepository) {

        return args -> {
            Inventory iPhone13Inventory = new Inventory();
            iPhone13Inventory.setSkuCode("iphone_13");
            iPhone13Inventory.setQuantity(100);

            Inventory iPhone13RedInventory = new Inventory();
            iPhone13RedInventory.setSkuCode("iphone_13_red");
            iPhone13RedInventory.setQuantity(0);

            inventoryRepository.save(iPhone13Inventory);
            inventoryRepository.save(iPhone13RedInventory);

        };

    }


}
