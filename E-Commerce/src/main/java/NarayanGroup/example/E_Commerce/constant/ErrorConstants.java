//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package NarayanGroup.example.E_Commerce.constant;

public final class ErrorConstants {
    public static final String PAYMENT_METHOD_REQUIRED = "Payment method is required";
    public static final String INVALID_PAYMENT_METHOD = "Invalid payment method. ";
    public static final String SUPPORTED_PAYMENT_METHODS = "Supported methods are COD and RAZORPAY";
    public static final String CART_NOT_FOUND = "Cart not found";
    public static final String CART_EMPTY = "Cart is empty";
    public static final String NO_ITEMS_SELECTED_FOR_CHECKOUT = "No items selected for checkout";
    public static final String INVALID_SELECTED_CART_ITEMS = "One or more selected cart items are invalid";
    public static final String DIFFERENT_CURRENCIES = "Products with different currencies cannot be checked out together";
    public static final String RAZORPAY_ORDER_CREATION_FAILED = "Unable to create Razorpay order";
    public static final String PRODUCT_ID_REQUIRED = "Product Id is required";
    public static final String QUANTITY_REQUIRED = "Quantity is required";
    public static final String QUANTITY_GREATER_THAN_ZERO = "Quantity should be greater than zero";
    public static final String PRODUCTS_LIST_EMPTY = "Products list cannot be empty";
    public static final String ADDRESS_LINE_1_REQUIRED = "Address line 1 is required";
    public static final String CITY_REQUIRED = "City is required";
    public static final String FULL_NAME_REQUIRED = "Full name is required";
    public static final String PHONE_REQUIRED = "Phone is required";
    public static final String PHONE_NUMBER_EXACTLY_10_DIGITS = "Phone number must be exactly 10 digits";
    public static final String PINCODE_REQUIRED = "Pincode is required";
    public static final String PINCODE_EXACTLY_6_DIGITS = "Pincode must contain exactly 6 digits";
    public static final String STATE_REQUIRED = "State is required";
    public static final String CATEGORY_REQUIRED = "Category is required";
    public static final String PRICE_REQUIRED = "Price is required";
    public static final String PRICE_MUST_BE_POSITIVE = "Price must be positive";
    public static final String QUANTITY_MUST_BE_POSITIVE = "Quantity must be positive";
    public static final String TITLE_REQUIRED = "Title is required";
    public static final String EMAIL_REQUIRED = "Email is required";
    public static final String NAME_REQUIRED = "Name is required";
    public static final String PASSWORD_REQUIRED = "Password is required";
    public static final String CART_REQUEST_MUST_CONTAIN_PRODUCT = "Cart request must contain at least one product";
    public static final String PRODUCT_NOT_FOUND = "Product not found";
    public static final String CART_NOT_FOUND_FOR_USER = "Cart not found for user: ";
    public static final String CART_ITEM_NOT_FOUND = "Cart item not found with id: ";
    public static final String PRODUCT_NOT_FOUND_WITH_ID = "Product not found with id: ";
    public static final String USER_NOT_FOUND_WITH_EMAIL = "User not found with email: ";
    public static final String USER_NOT_FOUND_WITH_ID = "User not found with id: ";
    public static final String INSUFFICIENT_STOCK_FOR_PRODUCT = "Insufficient stock for product: ";
    public static final String INVALID_QUANTITY = "Quantity must be greater than zero";
    public static final String UNAUTHORIZED_CART_ITEM = "You are not authorized to modify this cart item";
    public static final String USER_NOT_AUTHENTICATED = "User is not authenticated";
    public static final String USER_AUTHENTICATION_FAILED = "User authentication failed or authentication context is unavailable";
    public static final String FAILED_TO_SEND_OTP = "Failed to send OTP. Email may not be registered.";
    public static final String INVALID_OR_EXPIRED_OTP = "Invalid or expired OTP";
    public static final String ONE_OR_MORE_USERS_NOT_FOUND = "No users found.";
    public static final String EMAIL_CANNOT_BE_NULL_OR_EMPTY = "Email cannot be null or empty";
    public static final String SEARCH_KEYWORD_CANNOT_BE_EMPTY = "Search keyword cannot be empty";
    public static final String ADDRESS_NOT_FOUND = "Address not found";
    public static final String ORDER_NOT_FOUND = "Order not found";
    public static final String PAYMENT_NOT_FOUND = "Payment not found";
    public static final String PAYMENT_FAILED = "Payment failed";
    public static final String ORDER_NOT_RAZORPAY_PAYMENT = "Order is not a Razorpay payment";
    public static final String RAZORPAY_ORDER_MISMATCH = "Razorpay order does not match our payment";
    public static final String INVALID_RAZORPAY_SIGNATURE = "Invalid Razorpay payment signature";
    public static final String RAZORPAY_PAYMENT_INVALID = "Razorpay payment is not captured or does not match the order";
    public static final String RAZORPAY_VALIDATION_FAILED = "Unable to validate Razorpay payment";
    public static final String RAZORPAY_VERIFICATION_FAILED = "Unable to verify Razorpay payment";
    public static final String RAZORPAY_PAYMENT_ID_MISSING = "Razorpay payment id is missing";
    public static final String RAZORPAY_REFUND_NOT_ACCEPTED = "Razorpay refund was not accepted";
    public static final String PAYMENT_WINDOW_EXPIRED = "Payment window expired";
    public static final String ORDER_CANNOT_BE_CANCELLED = "This order can no longer be cancelled";
    public static final String REFUND_FAILED = "Unable to start the Razorpay refund. Order was not cancelled.";
    public static final String INVOICE_GENERATION_FAILED = "Unable to generate invoice PDF";
    public static final String INVALID_INVENTORY_RESERVATION = "Invalid inventory reservation for product: ";
    public static final String INVALID_CANCELLATION_QUANTITY = "Invalid cancellation quantity for product: ";
    public static final String INVENTORY_NOT_FOUND = "Inventory not found for product: ";
    public static final String PRODUCT_QUANTITY_INSUFFICIENT = "Product quantity is insufficient for product: ";
    public static final String INSUFFICIENT_STOCK = "Insufficient stock. Available: ";
    public static final String IMAGE_NOT_FOUND_FOR_PRODUCT = "Image not found for product: ";
    public static final String INVALID_REFRESH_TOKEN = "Invalid refresh token";
    public static final String REFRESH_TOKEN_MISSING = "Refresh token is missing";
    public static final String LOGIN_FAILED = "Login failed!";
    public static final String TOKEN_INVALID = "Invalid token";
    public static final String TOKEN_EXPIRED_OR_INVALID = "Token expired or invalid";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String UNEXPECTED_ERROR = "An unexpected error occurred: ";
    public static final String VALIDATION_FAILED = "Validation failed";

    private ErrorConstants() {
    }
}
