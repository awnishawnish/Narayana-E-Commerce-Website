package NarayanGroup.example.E_Commerce.exception;

import NarayanGroup.example.E_Commerce.DTO.response.ResponseMessageUtilityDTO;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ResponseMessageUtilityDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .toList();

        ResponseMessageUtilityDTO message = new ResponseMessageUtilityDTO(
                "FAIL",
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                "MSID",
                buildErrorPayload(errors)
        );
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ResponseMessageUtilityDTO> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();

        ResponseMessageUtilityDTO message = new ResponseMessageUtilityDTO(
                "FAIL",
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                "MSID",
                buildErrorPayload(errors)
        );
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ResponseMessageUtilityDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
        ResponseMessageUtilityDTO message = new ResponseMessageUtilityDTO(
                "FAIL",
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "MSID",
                null
        );
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseEntity<ResponseMessageUtilityDTO> handleNoResourceFoundException(NoResourceFoundException ex) {
        ResponseMessageUtilityDTO message = new ResponseMessageUtilityDTO(
                "FAIL",
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                "MSID",
                null
        );
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<ResponseMessageUtilityDTO> handleAllExceptions(Exception ex) {
        ResponseMessageUtilityDTO message = new ResponseMessageUtilityDTO(
                "FAIL",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred: " + ex.getMessage(),
                "MSID",
                null
        );
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }

    private Map<String, Object> buildErrorPayload(List<String> errors) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("errors", errors);
        return payload;
    }

    @ExceptionHandler({
            CustomException.UserNotFoundException.class,
            CustomException.DuplicateUserException.class,
            CustomException.InvalidUserException.class
    })
    @ResponseBody
    public ResponseEntity<ResponseMessageUtilityDTO> handleCustomExceptions(RuntimeException ex) {

        HttpStatus status;

        if (ex instanceof CustomException.UserNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex instanceof CustomException.DuplicateUserException) {
            status = HttpStatus.CONFLICT;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }

        ResponseMessageUtilityDTO message = new ResponseMessageUtilityDTO(
                "FAIL",
                status.value(),
                ex.getMessage(),
                "MSID",
                null
        );

        return new ResponseEntity<>(message, status);
    }


    @ExceptionHandler(CustomException.ImageNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseEntity<ResponseMessageUtilityDTO> ImageNotFoundException(NoResourceFoundException ex) {
        ResponseMessageUtilityDTO message = new ResponseMessageUtilityDTO(
                "FAIL",
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                "MSID",
                null
        );
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomException.ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseEntity<ResponseMessageUtilityDTO> ProductNotFoundException(NoResourceFoundException ex) {
        ResponseMessageUtilityDTO message = new ResponseMessageUtilityDTO(
                "FAIL",
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                "MSID",
                null
        );
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomException.InsufficientStockException.class)
    public ResponseEntity<ResponseMessageUtilityDTO> handleInsufficientStock(
            CustomException.InsufficientStockException ex) {

        ResponseMessageUtilityDTO response =
                ResponseMessageUtilityDTO.builder()
                        .status("FAILED")
                        .httpStatus(HttpStatus.CONFLICT.value())
                        .message(ex.getMessage())
                        .build();

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(CustomException.InvalidQuantityException.class)
    public ResponseEntity<ResponseMessageUtilityDTO> handleInvalidQuantity(
            CustomException.InvalidQuantityException ex) {

        ResponseMessageUtilityDTO response =
                ResponseMessageUtilityDTO.builder()
                        .status("FAILED")
                        .httpStatus(HttpStatus.BAD_REQUEST.value())
                        .message(ex.getMessage())
                        .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }
//
//    @ExceptionHandler(CustomException.CartNotFoundException.class)
//    public ResponseEntity<ResponseMessageUtilityDTO> handleCartNotFoundException(
//            CustomException.CartNotFoundException ex) {
//
//        ResponseMessageUtilityDTO response =
//                ResponseMessageUtilityDTO.builder()
//                        .status("FAILED")
//                        .httpStatus(HttpStatus.NOT_FOUND.value())
//                        .message(ex.getMessage())
//                        .msId("CART")
//                        .data(null)
//                        .build();
//
//        return ResponseEntity
//                .status(HttpStatus.NOT_FOUND)
//                .body(response);
//    }


    // ============================================================
    // CART ITEM NOT FOUND
    // ============================================================

    @ExceptionHandler(CustomException.CartItemNotFoundException.class)
    public ResponseEntity<ResponseMessageUtilityDTO> handleCartItemNotFoundException(
            CustomException.CartItemNotFoundException ex) {

        ResponseMessageUtilityDTO response =
                ResponseMessageUtilityDTO.builder()
                        .status("FAILED")
                        .httpStatus(HttpStatus.NOT_FOUND.value())
                        .message(ex.getMessage())
                        .msId("CART")
                        .data(null)
                        .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

        @ExceptionHandler(CustomException.UnauthorizedCartAccessException.class)
    public ResponseEntity<ResponseMessageUtilityDTO> handleUnauthorizedCartAccessException(
            CustomException.UnauthorizedCartAccessException ex) {

        ResponseMessageUtilityDTO response =
                ResponseMessageUtilityDTO.builder()
                        .status("FAILED")
                        .httpStatus(HttpStatus.FORBIDDEN.value())
                        .message(ex.getMessage())
                        .msId("CART")
                        .data(null)
                        .build();

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }


    // ============================================================
    // UNAUTHORIZED
    // ============================================================

    @ExceptionHandler(CustomException.UnauthorizedException.class)
    public ResponseEntity<ResponseMessageUtilityDTO> handleUnauthorizedException(
            CustomException.UnauthorizedException ex) {

        ResponseMessageUtilityDTO response =
                ResponseMessageUtilityDTO.builder()
                        .status("FAILED")
                        .httpStatus(HttpStatus.UNAUTHORIZED.value())
                        .message(ex.getMessage())
                        .msId("AUTH")
                        .data(null)
                        .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler({
            CustomException.CartNotFoundException.class,
            CustomException.AddressNotFoundException.class,
            CustomException.InventoryNotFoundException.class,
            CustomException.OrderNotFoundException.class
    })
    public ResponseEntity<ResponseMessageUtilityDTO>
    handleNotFound(RuntimeException ex) {

        ResponseMessageUtilityDTO response =
                ResponseMessageUtilityDTO.builder()
                        .status("FAILED")
                        .httpStatus(HttpStatus.NOT_FOUND.value())
                        .message(ex.getMessage())
                        .msId("RESOURCE")
                        .data(null)
                        .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(CustomException.CheckoutException.class)
    public ResponseEntity<ResponseMessageUtilityDTO>
    handleCheckoutException(
            CustomException.CheckoutException ex) {

        ResponseMessageUtilityDTO response =
                ResponseMessageUtilityDTO.builder()
                        .status("FAILED")
                        .httpStatus(HttpStatus.BAD_REQUEST.value())
                        .message(ex.getMessage())
                        .msId("CHECKOUT")
                        .data(null)
                        .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

}