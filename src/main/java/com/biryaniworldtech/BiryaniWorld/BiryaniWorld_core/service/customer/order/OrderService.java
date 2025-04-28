package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.customer.order;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.authentication.User;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.Customer;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.menu.Item;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.order.OrderItem;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.order.Orders;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.exception.InvalidRequestException;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.exception.NoDataFoundException;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.order.OrderItemRequest;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.order.OrderRequest;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.order.OrderResponse;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.order.OrderItemResponse;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.repository.customer.CustomerRepository;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.repository.customer.menu.ItemRepository;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.repository.customer.order.OrderRepository;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.authentication.UserService;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.notification.EmailService;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.customer.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CustomerService customerService;

    public Orders convertToEntity(OrderRequest orderRequest) {
        return objectMapper.convertValue(orderRequest, Orders.class);
    }

    public OrderResponse convertToResponse(Orders orders) {
        return objectMapper.convertValue(orders, OrderResponse.class);
    }

    public List<Orders> getOrders() {
        return orderRepository.findAll();
    }

    public Optional<OrderResponse> getOrdersById(Integer userId, Integer orderId) {
        return Optional.ofNullable(objectMapper.convertValue(orderRepository.findById(Long.valueOf(orderId)), OrderResponse.class));
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest, String username) {
        logger.debug("Creating order for user: {}", username);
        
        // Get user and verify customer exists
        User user = userService.getUserByUsername(username);
        Customer customer = user.getCustomer();
        
        if (customer == null) {
            throw new NoDataFoundException("Customer profile not found for user: " + username);
        }

        // Create and save the order first
        final Orders order = new Orders();
        order.setCustomer(customer);
        order.setCustomerName(customer.getFirstName() + " " + customer.getLastName());
        order.setOrderStatus("PENDING");
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderFullFilled(false);
        order.setTotalAmount(calculateTotalAmount(orderRequest.getItems()));
        
        // Save the order first to get the ID
        final Orders savedOrder = orderRepository.save(order);
        
        // Create and save order items
        List<OrderItem> orderItems = orderRequest.getItems().stream()
                .map(itemRequest -> {
                    Item menuItem = itemRepository.findById(itemRequest.getItemId())
                            .orElseThrow(() -> new NoDataFoundException("Item not found with ID: " + itemRequest.getItemId()));
                    
                    OrderItem orderItem = new OrderItem();
                    orderItem.setItem(menuItem);
                    orderItem.setQuantity(itemRequest.getQuantity());
                    orderItem.setPrice(itemRequest.getPrice());
                    orderItem.setOrders(savedOrder);
                    return orderItem;
                })
                .collect(Collectors.toList());
        
        savedOrder.setItems(orderItems);
        
        // Save the order again with the items
        Orders finalOrder = orderRepository.save(savedOrder);
        logger.info("Order created successfully with ID: {}", finalOrder.getOrderId());
        emailService.sendOrderConfirmation(customer.getEmail(), order.getOrderId().toString(), order.getItems().toString(), customer.getPhone());
        return mapToOrderResponse(finalOrder);

    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders(String username) {
        logger.debug("Fetching all orders for user: {}", username);
        
        User user = userService.getUserByUsername(username);
        Customer customer = user.getCustomer();
        
        if (customer == null) {
            throw new NoDataFoundException("Customer profile not found for user: " + username);
        }

        return orderRepository.findByCustomer_CustomerId(customer.getCustomerId()).stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId, String username) {
        logger.debug("Fetching order {} for user: {}", orderId, username);
        
        User user = userService.getUserByUsername(username);
        Customer customer = user.getCustomer();
        
        if (customer == null) {
            throw new NoDataFoundException("Customer profile not found for user: " + username);
        }

        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoDataFoundException("Order not found with ID: " + orderId));

        if (!order.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            throw new NoDataFoundException("Order not found for user: " + username);
        }

        return mapToOrderResponse(order);
    }

    public OrderResponse requestOrderCancellation(Long orderId, String userName) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Verify the order belongs to the customer
        if (!order.getCustomer().getUser().getUsername().equals(userName)) {
            throw new RuntimeException("Unauthorized access to order");
        }
        
        // Update order status to CANCELLATION_REQUESTED
        order.setOrderStatus("CANCELLATION_REQUESTED");
        orderRepository.save(order);
        
        return mapToOrderResponse(order);
    }

    public OrderResponse updateOrderStatus(Long orderId, String status) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setOrderStatus(status);
        orderRepository.save(order);
        
        return mapToOrderResponse(order);
    }

    @Transactional
    public void deleteOrderById(Long orderId, String username) {
        logger.debug("Deleting order {} for user: {}", orderId, username);
        
        User user = userService.getUserByUsername(username);
        Customer customer = user.getCustomer();
        
        if (customer == null) {
            throw new NoDataFoundException("Customer profile not found for user: " + username);
        }

        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoDataFoundException("Order not found with ID: " + orderId));

        if (!order.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            throw new NoDataFoundException("Order not found for user: " + username);
        }

        orderRepository.delete(order);
        logger.info("Order deleted successfully for order ID: {}", orderId);
    }

    private double calculateTotalAmount(List<OrderItemRequest> items) {
        return items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    private OrderResponse mapToOrderResponse(Orders order) {
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .customerId(order.getCustomer().getCustomerId())
                .status(order.getOrderStatus())
                .totalAmount(order.getTotalAmount())
                .orderItems(order.getItems().stream()
                        .map(item -> {
                            Item menuItem = item.getItem();
                            if (menuItem == null) {
                                menuItem = itemRepository.findById(item.getItem().getItemId())
                                        .orElseThrow(() -> new NoDataFoundException("Item not found with ID: " + item.getItem().getItemId()));
                            }
                            return OrderItemResponse.builder()
                                    .id(item.getId())
                                    .itemId(menuItem.getItemId())
                                    .itemName(menuItem.getName())
                                    .description(menuItem.getDescription())
                                    .quantity(item.getQuantity())
                                    .price(item.getPrice())
                                    .itemQuantity(menuItem.getItemQuantity())
                                    .seasonal(menuItem.isSeasonal())
                                    .build();
                        })
                        .collect(Collectors.toList()))
                .build();
    }

    public OrderResponse updateOrderById(OrderRequest orderRequest, Long userId, Long orderId) {
        if(!Objects.equals(orderRequest.getOrderId(), orderId)){
            throw new InvalidRequestException("Order Id is not matching with the Request Order - Please confirm it again!");
        }
        Optional<Customer> customer = customerRepository.findById(userId);
        if(customer.isPresent()){
            List<Orders> orders = customer.get().getOrders();
            Orders matchingOrder = orders.stream()
                    .filter(order -> order != null && Objects.equals(orderRequest.getOrderId(), order.getOrderId()) && Objects.equals(orderId, order.getOrderId()))
                    .findFirst()
                    .orElse(null);
            if(matchingOrder != null) {
                Orders order = convertToEntity(orderRequest);
                orders.remove(matchingOrder);
                orders.add(order);
                customer.get().setOrders(orders);
                customerRepository.save(customer.get());
                Orders updatedOrder = orderRepository.save(order);
                logger.info("Order updated successfully for order ID: {}", updatedOrder.getOrderId());
                return convertToResponse(updatedOrder);
            }
            throw new NoDataFoundException("Order with Order-id - " + orderId + " not found for this user - " + userId);
        }
        else {
            throw new NoDataFoundException("No Customer found with this user-id - " + userId);
        }
    }

    public List<OrderResponse> getOrdersByDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
        List<Orders> orders = orderRepository.findByOrderDateTimeBetween(fromDate, toDate);
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

}
