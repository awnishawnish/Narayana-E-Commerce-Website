package NarayanGroup.example.E_Commerce.controller.Impl;

import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.model.Enum.OrderStatus;
import NarayanGroup.example.E_Commerce.service.IOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {
    private final IOrderService service=mock(IOrderService.class);
    private final OrderController controller=new OrderController(service);

    @Test void getOrders_shouldBuildSuccessResponse() {
        when(service.getOrders(1L)).thenReturn(java.util.List.of());
        var response=controller.getOrders(1L);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(CommonConstants.ORDERS_FETCHED_SUCCESSFULLY,response.getBody().getMessage());
        verify(service).getOrders(1L);
    }
    @Test void getOrder_shouldBuildSuccessResponse() {
        when(service.getOrder(1L,2L)).thenReturn(null);
        var response=controller.getOrder(1L,2L);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(CommonConstants.ORDER_FETCHED_SUCCESSFULLY,response.getBody().getMessage());
    }
    @Test void cancelOrder_shouldDelegateAndReturnSuccess() {
        var response=controller.cancelOrder(1L,2L,"customer changed mind");
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(CommonConstants.ORDER_CANCELLED_SUCCESSFULLY,response.getBody().getMessage());
        verify(service).cancelOrder(1L,2L,"customer changed mind");
    }
    @Test void cancelOrder_shouldAllowMissingReason() {
        var response=controller.cancelOrder(1L,2L,null);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        verify(service).cancelOrder(1L,2L,null);
    }
    @Test void invoice_shouldReturnPdfHeadersAndBody() {
        byte[] pdf={1,2,3}; when(service.generateInvoice(1L,2L)).thenReturn(pdf);
        var response=controller.invoice(1L,2L);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertArrayEquals(pdf,response.getBody());
        assertEquals("application/pdf",response.getHeaders().getContentType().toString());
        assertEquals("inline; filename=\"invoice-2.pdf\"",response.getHeaders().getFirst("Content-Disposition"));
        assertEquals("no-store",response.getHeaders().getFirst("Cache-Control"));
    }
}
