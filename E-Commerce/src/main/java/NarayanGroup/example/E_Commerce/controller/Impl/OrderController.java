package NarayanGroup.example.E_Commerce.controller.Impl;

import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import NarayanGroup.example.E_Commerce.controller.IOrderController;
import NarayanGroup.example.E_Commerce.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController implements IOrderController {
    private final IOrderService orderService;

    @GetMapping("/{userId}")
    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> getOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS).httpStatus(200)
                .message(CommonConstants.ORDERS_FETCHED_SUCCESSFULLY)
                .data(orderService.getOrders(userId)).build());
    }

    @GetMapping("/{userId}/{orderId}")
    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> getOrder(@PathVariable Long userId, @PathVariable Long orderId) {
        return ResponseEntity.ok(ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS).httpStatus(200)
                .message(CommonConstants.ORDER_FETCHED_SUCCESSFULLY)
                .data(orderService.getOrder(userId, orderId)).build());
    }

    @PostMapping("/{userId}/{orderId}/cancel")
    @Override
    public ResponseEntity<ResponseMessageUtilityDTO> cancelOrder(
            @PathVariable Long userId, @PathVariable Long orderId,
            @RequestParam(required = false) String reason) {
        orderService.cancelOrder(userId, orderId, reason);
        return ResponseEntity.ok(ResponseMessageUtilityDTO.builder()
                .status(CommonConstants.SUCCESS).httpStatus(200)
                .message(CommonConstants.ORDER_CANCELLED_SUCCESSFULLY).build());
    }

    @GetMapping("/{userId}/{orderId}/invoice")
    @Override
    public ResponseEntity<byte[]> invoice(@PathVariable Long userId, @PathVariable Long orderId) {
        byte[] pdf = orderService.generateInvoice(userId, orderId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename("invoice-" + orderId + ".pdf").build());
        headers.setCacheControl("no-store");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
