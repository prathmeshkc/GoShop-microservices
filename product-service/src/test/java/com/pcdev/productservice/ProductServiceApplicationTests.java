package com.pcdev.productservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcdev.productservice.dto.ProductRequest;
import com.pcdev.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {


    /**
     * At the start of integration test, the test will start the mongodb container by
     * downloading the below image. After starting the container, it will get the
     * replica set url and add it to spring.data.mongodb.uri property.
     */
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void shouldCreateProduct() throws Exception {

        ProductRequest productRequest = getProductRequest();
        String productRequestJSON = objectMapper.writeValueAsString(productRequest);
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/product/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productRequestJSON))
                .andExpect(status().isCreated());

        Assertions.assertEquals(1, productRepository.findAll().size());
    }

    @Test
    void shouldGetProduct() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/product/")
        ).andExpect(status().is(200));

    }

    private ProductRequest getProductRequest() {
        return ProductRequest
                .builder()
                .name("iPhone 14")
                .description("Apple iPhone")
                .price(BigDecimal.valueOf(1200))
                .build();
    }


}
