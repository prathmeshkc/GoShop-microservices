package com.pcdev.orderservice.service;

import com.pcdev.orderservice.dto.InventoryResponse;
import com.pcdev.orderservice.dto.OrderLineItemDto;
import com.pcdev.orderservice.dto.OrderRequest;
import com.pcdev.orderservice.event.OrderPlacedEvent;
import com.pcdev.orderservice.model.Order;
import com.pcdev.orderservice.model.OrderLineItem;
import com.pcdev.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();

        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItem> orderLineItemList = orderRequest.getOrderLineItemDtoList()
                .stream()
                .map(this::mapFromDto).toList();

        order.setOrderLineItemList(orderLineItemList);

        //Call Inventory Service, and place order iff the product is in stock

        List<String> skuCodes = order.getOrderLineItemList()
                .stream()
                .map(OrderLineItem::getSkuCode).toList();

        InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory/",
                        uriBuilder -> uriBuilder.queryParam("skuCodes", skuCodes).build())
                .retrieve() //to retrieve the response
                .bodyToMono(InventoryResponse[].class)//specify the response data type
                .block();//to make it sync request. because it is auto async

        boolean allProductsInStock = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);


        if (allProductsInStock) {
            //Place Order
            orderRepository.save(order);
            kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
            return "Order Placed Successfully!";
        } else {
            throw new IllegalArgumentException("Product is Out of Stock!");
        }

    }

    private OrderLineItem mapFromDto(OrderLineItemDto orderLineItemDto) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setId(orderLineItemDto.getId());
        orderLineItem.setSkuCode(orderLineItemDto.getSkuCode());
        orderLineItem.setQuantity(orderLineItemDto.getQuantity());
        orderLineItem.setPrice(orderLineItemDto.getPrice());

        return orderLineItem;
    }
}
