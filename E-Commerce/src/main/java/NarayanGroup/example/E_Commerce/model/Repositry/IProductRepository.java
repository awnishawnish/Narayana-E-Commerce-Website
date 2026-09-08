package NarayanGroup.example.E_Commerce.model.Repositry;

import NarayanGroup.example.E_Commerce.model.Entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IProductRepository
        extends JpaRepository<Product, Long> {

    Page<Product> findByCategoryAndIsDeletedFalse(
            String category,
            Pageable pageable
    );

    Page<Product> findByIsDeletedFalse(
            Pageable pageable
    );

    @Query("SELECT p FROM Product p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p.isDeleted = false")
    Page<Product> searchByTitleKeyword(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p.isDeleted = false")
    Page<Product> searchByName(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    java.util.Optional<Product> findByIdAndIsDeletedFalse(Long id);
}