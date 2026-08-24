package NarayanGroup.example.E_Commerce.model.Repositry;

import NarayanGroup.example.E_Commerce.model.Entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IProductRepository extends JpaRepository<Product, Long> {

    // ✅ CHANGED: old method was `List<Product> findByCategory(String category)`
    // New: uses Pageable for pagination + filters out soft-deleted products
    Page<Product> findByCategoryAndIsDeletedFalse(String category, Pageable pageable);

    // ✅ NEW: get ALL products (no category filter), excluding soft-deleted, with pagination
    Page<Product> findByIsDeletedFalse(Pageable pageable);

    // ✅ NEW: search by title keyword (case-insensitive), excluding soft-deleted, with pagination
    // Used for the search feature (user searches product by name)
    @Query("SELECT p FROM Product p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p.isDeleted = false")
    Page<Product> searchByTitleKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select p from Product p where lower(p.name) like (concat('%',:keyword,'%') ) ")
    Page<Product> searchByName(String name,
                               Pageable pageable);
}
