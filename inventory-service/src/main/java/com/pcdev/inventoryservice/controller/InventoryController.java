package com.pcdev.inventoryservice.controller;

import com.pcdev.inventoryservice.dto.InventoryResponse;
import com.pcdev.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    //Format for multiple path variables
    //http://localhost:8082/api/inventory/iphone_13,iphone_13_red

    //Format for multiple request parameters
    //http://localhost:8082/api/inventory?skuCodes=iphone_13&skuCodes=iphone_13_red

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCodes) {
        return inventoryService.isInStock(skuCodes);
    }
}
