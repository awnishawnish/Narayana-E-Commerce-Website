package NarayanGroup.example.E_Commerce.service;

import com.razorpay.RazorpayException;

import java.math.BigDecimal;

public interface IRazorpayService {

    String createOrder(BigDecimal amount, String currency, String receipt) throws RazorpayException;

    boolean isPaymentCaptured(String paymentId,
                              String expectedOrderId,
                              BigDecimal expectedAmount,
                              String expectedCurrency) throws RazorpayException;

    void refundPayment(String paymentId, BigDecimal amount, String currency) throws RazorpayException;
}
