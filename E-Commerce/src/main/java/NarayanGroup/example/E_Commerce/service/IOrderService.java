package NarayanGroup.example.E_Commerce.service;

import NarayanGroup.example.E_Commerce.DTO.response.OrderResponseDTO;

import java.util.List;

public interface IOrderService {
    List<OrderResponseDTO> getOrders(Long userId);
    OrderResponseDTO getOrder(Long userId, Long orderId);
    void cancelOrder(Long userId, Long orderId, String reason);
    byte[] generateInvoice(Long userId, Long orderId);
    void expirePendingPayment(Long orderId);
}
