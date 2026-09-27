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

@Service
@AllArgsConstructor
public class InventoryService implements IInventoryService {

    private final IInventoryRepository inventoryRepository;
    private final IProductRepository productRepository;

    @Override
    public Inventory findByProductId(Long productId) {
        return inventoryRepository.findByProductIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new CustomException.InventoryNotFoundException(
                        "Inventory not found for product: " + productId));
    }

    @Override
    @Transactional
    public Inventory reserveStock(Long productId, Long quantity) {
        Inventory inventory = inventoryRepository.findForUpdate(productId)
                .orElseThrow(() -> new CustomException.InventoryNotFoundException(
                        "Inventory not found for product: " + productId));

        if (quantity == null || quantity <= 0) {
            throw new CustomException.InsufficientStockException("Quantity must be greater than zero");
        }
        if (inventory.getAvailableQuantity() < quantity) {
            throw new CustomException.InsufficientStockException(
                    "Insufficient stock for product: " + productId);
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
                        "Inventory not found for product: " + productId));

        if (quantity == null || quantity <= 0 || inventory.getReservedQuantity() < quantity) {
            throw new CustomException.CheckoutException("Invalid inventory reservation for product: " + productId);
        }

        Product product = inventory.getProduct();
        if (product.getQuantity() == null || product.getQuantity() < quantity) {
            throw new CustomException.InsufficientStockException(
                    "Product quantity is insufficient for product: " + productId);
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
                        "Inventory not found for product: " + productId));

        if (quantity == null || quantity <= 0 || inventory.getReservedQuantity() < quantity) {
            throw new CustomException.CheckoutException("Invalid inventory reservation for product: " + productId);
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
                        "Inventory not found for product: " + productId));

        if (quantity == null || quantity <= 0) {
            throw new CustomException.CheckoutException("Invalid cancellation quantity for product: " + productId);
        }

        long fromReservation = Math.min(inventory.getReservedQuantity(), quantity);
        long remaining = quantity - fromReservation;

        inventory.setReservedQuantity(inventory.getReservedQuantity() - fromReservation);
        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);

        if (remaining > 0) {
            Product product = inventory.getProduct();
            int currentQuantity = product.getQuantity() == null ? 0 : product.getQuantity();
            product.setQuantity(currentQuantity + (int) remaining);
            productRepository.save(product);
        }

        return inventoryRepository.save(inventory);
    }

}
