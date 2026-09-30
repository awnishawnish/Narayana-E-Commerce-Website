package NarayanGroup.example.E_Commerce.service.Impl;


import NarayanGroup.example.E_Commerce.model.Entity.Payment;
import com.razorpay.Order;
import com.razorpay.OrderClient;
import com.razorpay.PaymentClient;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RazorpayServiceTest {
    @Test void createOrder_shouldSendSmallestCurrencyUnit() throws Exception {
        RazorpayClient client=mock(RazorpayClient.class); OrderClient orders=mock(OrderClient.class);
        client.orders=orders;
        com.razorpay.Order gatewayOrder=mock(com.razorpay.Order.class);
        when(gatewayOrder.get("id")).thenReturn("order_123");
        when(orders.create(any(JSONObject.class))).thenAnswer(invocation -> {
            JSONObject request=invocation.getArgument(0);
            assertEquals(12500,request.getLong("amount"));
            assertEquals("INR",request.getString("currency"));
            assertEquals("receipt-1",request.getString("receipt"));
            return gatewayOrder;
        });
        assertEquals("order_123",new RazorpayService(client).createOrder(new BigDecimal("125.00"),"INR","receipt-1"));
    }

    @Test void isPaymentCaptured_shouldValidateOrderCurrencyAmountAndStatus() throws Exception {
        RazorpayClient client=mock(RazorpayClient.class); PaymentClient payments=mock(PaymentClient.class); client.payments=payments;
        com.razorpay.Payment payment=mock(com.razorpay.Payment.class); when(payments.fetch("pay_1")).thenReturn(payment);
        when(payment.get("order_id")).thenReturn("order_1"); when(payment.get("currency")).thenReturn("INR");
        when(payment.get("amount")).thenReturn(10000); when(payment.get("status")).thenReturn("captured");
        var service=new RazorpayService(client);
        assertTrue(service.isPaymentCaptured("pay_1","order_1",new BigDecimal("100"),"INR"));
        when(payment.get("status")).thenReturn("failed"); assertFalse(service.isPaymentCaptured("pay_1","order_1",new BigDecimal("100"),"INR"));
        when(payment.get("amount")).thenReturn(null); assertFalse(service.isPaymentCaptured("pay_1","order_1",new BigDecimal("100"),"INR"));
    }

    @Test void refundPayment_shouldRejectMissingPaymentId() {
        RazorpayService service=new RazorpayService(mock(RazorpayClient.class));
        assertThrows(com.razorpay.RazorpayException.class,()->service.refundPayment(" ",new BigDecimal("100"),"INR"));
    }
}
