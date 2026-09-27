package NarayanGroup.example.E_Commerce.service.Impl;

import NarayanGroup.example.E_Commerce.service.IRazorpayService;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class RazorpayService
        implements IRazorpayService {

    private final RazorpayClient razorpayClient;

    @Override
    public String createOrder(
            BigDecimal amount,
            String currency,
            String receipt
    ) throws RazorpayException {

        /*
         * Razorpay expects amount
         * in the smallest currency unit.
         *
         * Example:
         *
         * ₹500
         * ↓
         * 50000 paise
         */

        long amountInSmallestUnit =
                amount
                        .multiply(BigDecimal.valueOf(100))
                        .longValueExact();

        JSONObject orderRequest =
                new JSONObject();

        orderRequest.put(
                "amount",
                amountInSmallestUnit
        );

        orderRequest.put(
                "currency",
                currency
        );

        orderRequest.put(
                "receipt",
                receipt
        );

        Order razorpayOrder =
                razorpayClient.orders.create(
                        orderRequest
                );

        return razorpayOrder.get("id");
    }
    @Override
    public boolean isPaymentCaptured(String paymentId,
                                     String expectedOrderId,
                                     BigDecimal expectedAmount,
                                     String expectedCurrency) throws RazorpayException {
        Payment razorpayPayment = razorpayClient.payments.fetch(paymentId);

        String orderId = razorpayPayment.get("order_id");
        String status = razorpayPayment.get("status");
        String currency = razorpayPayment.get("currency");
        Number amount = razorpayPayment.get("amount");

        long expectedAmountInSmallestUnit = expectedAmount
                .multiply(BigDecimal.valueOf(100))
                .longValueExact();

        return expectedOrderId.equals(orderId)
                && expectedCurrency.equalsIgnoreCase(currency)
                && amount != null
                && amount.longValue() == expectedAmountInSmallestUnit
                && "captured".equalsIgnoreCase(status);
    }

    @Override
    public void refundPayment(String paymentId, BigDecimal amount, String currency) throws RazorpayException {
        if (paymentId == null || paymentId.trim().isEmpty()) {
            throw new RazorpayException("Razorpay payment id is missing");
        }

        long amountInSmallestUnit = amount
                .multiply(BigDecimal.valueOf(100))
                .longValueExact();

        JSONObject refundRequest = new JSONObject();
        refundRequest.put("amount", amountInSmallestUnit);
        refundRequest.put("notes", new JSONObject().put("reason", "Customer order cancellation"));

        com.razorpay.Refund refund = razorpayClient.payments.refund(paymentId, refundRequest);
        String status = refund.get("status");
        if (status == null || !("processed".equalsIgnoreCase(status) || "pending".equalsIgnoreCase(status))) {
            throw new RazorpayException("Razorpay refund was not accepted");
        }
    }

}

