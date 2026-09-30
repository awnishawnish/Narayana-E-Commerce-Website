package NarayanGroup.example.E_Commerce.service.Impl;

import NarayanGroup.example.E_Commerce.exception.CustomException;
import NarayanGroup.example.E_Commerce.model.Entity.Inventory;
import NarayanGroup.example.E_Commerce.model.Entity.Product;
import NarayanGroup.example.E_Commerce.model.Repositry.IInventoryRepository;
import NarayanGroup.example.E_Commerce.model.Repositry.IProductRepository;
import NarayanGroup.example.E_Commerce.service.IInventoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import NarayanGroup.example.E_Commerce.constant.CommonConstants;
import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
@Service
@AllArgsConstructor
public class InventoryService implements IInventoryService {

    private final IInventoryRepository inventoryRepository;
    private final IProductRepository productRepository;

    @Override
    public Inventory findByProductId(Long productId) {
        return inventoryRepository.findByProductIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new CustomException.InventoryNotFoundException(
                        ErrorConstants.INVENTORY_NOT_FOUND + productId));
    }

    @Override
    @Transactional
    public Inventory reserveStock(Long productId, Long quantity) {
        Inventory inventory = inventoryRepository.findForUpdate(productId)
                .orElseThrow(() -> new CustomException.InventoryNotFoundException(
                        ErrorConstants.INVENTORY_NOT_FOUND + productId));

        if (quantity == null || quantity <= 0) {
            throw new CustomException.InsufficientStockException(ErrorConstants.INVALID_QUANTITY);
        }
        if (inventory.getAvailableQuantity() < quantity) {
            throw new CustomException.InsufficientStockException(
                    ErrorConstants.INSUFFICIENT_STOCK_FOR_PRODUCT + productId);
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantity);
        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
        return inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public Inventory finalizeReservation(Long productId, Long quantity) {
        Inventory inventory = inventoryRepository.findForUpdate(productId)
                .orElseThrow(() -> new CustomException.InventoryNotFoundException(
                        ErrorConstants.INVENTORY_NOT_FOUND + productId));

        if (quantity == null || quantity <= 0 || inventory.getReservedQuantity() < quantity) {
            throw new CustomException.CheckoutException(ErrorConstants.INVALID_INVENTORY_RESERVATION + productId);
        }

        Product product = inventory.getProduct();
        if (product.getQuantity() == null || product.getQuantity() < quantity) {
            throw new CustomException.InsufficientStockException(
                    ErrorConstants.PRODUCT_QUANTITY_INSUFFICIENT + productId);
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        product.setQuantity(product.getQuantity() - quantity.intValue());
        productRepository.save(product);
        return inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public Inventory releaseReservation(Long productId, Long quantity) {
        Inventory inventory = inventoryRepository.findForUpdate(productId)
                .orElseThrow(() -> new CustomException.InventoryNotFoundException(
                        ErrorConstants.INVENTORY_NOT_FOUND + productId));

        if (quantity == null || quantity <= 0 || inventory.getReservedQuantity() < quantity) {
            throw new CustomException.CheckoutException(ErrorConstants.INVALID_INVENTORY_RESERVATION + productId);
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory save(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }
    @Override
    @Transactional
    public Inventory cancelReservationOrRestoreStock(Long productId, Long quantity) {
        Inventory inventory = inventoryRepository.findForUpdate(productId)
                .orElseThrow(() -> new CustomException.InventoryNotFoundException(
                        ErrorConstants.INVENTORY_NOT_FOUND + productId));

        if (quantity == null || quantity <= 0) {
            throw new CustomException.CheckoutException(ErrorConstants.INVALID_CANCELLATION_QUANTITY + productId);
        }

        long fromReservation = Math.min(inventory.getReservedQuantity(), quantity);
        long remaining = quantity - fromReservation;

        inventory.setReservedQuantity(inventory.getReservedQuantity() - fromReservation);
        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);

        Product product = inventory.getProduct();
        if (product != null) {
            int restoredQuantity = (int) (inventory.getAvailableQuantity() + inventory.getReservedQuantity());
            product.setQuantity(restoredQuantity);
            productRepository.save(product);
        }

        return inventoryRepository.save(inventory);
    }

}
