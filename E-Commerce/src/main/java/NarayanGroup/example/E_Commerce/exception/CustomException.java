package NarayanGroup.example.E_Commerce.exception;

public class CustomException extends RuntimeException {
    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static class DuplicateUserException extends RuntimeException {
        public DuplicateUserException(String message) {
            super(message);
        }
    }

    public static class InvalidUserException extends RuntimeException {
        public InvalidUserException(String message) {
            super(message);
        }
    }
    public static class CartNotFoundException extends RuntimeException {
        public CartNotFoundException(String message) {
            super(message);
        }
    }

    public static class ImageNotFoundException extends RuntimeException {
        public ImageNotFoundException(String message) {
            super(message);
        }
    }
    public static class ProductNotFoundException extends RuntimeException {
        public ProductNotFoundException(String message) {
            super(message);
        }
    }
    public static class InvalidQuantityException extends RuntimeException {

        public InvalidQuantityException(String message) {
            super(message);
        }
    }
    public static class InsufficientStockException extends RuntimeException {

        public InsufficientStockException(String message) {
            super(message);
        }
    }



    public static class CartItemNotFoundException
            extends RuntimeException {

        public CartItemNotFoundException(String message) {
            super(message);
        }
    }


    public static class UnauthorizedCartAccessException
            extends RuntimeException {

        public UnauthorizedCartAccessException(String message) {
            super(message);
        }
    }


    public static class UnauthorizedException
            extends RuntimeException {

        public UnauthorizedException(String message) {
            super(message);
        }
    }


}
