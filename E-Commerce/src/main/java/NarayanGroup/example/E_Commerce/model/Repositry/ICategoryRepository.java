package NarayanGroup.example.E_Commerce.model.Repositry;

import NarayanGroup.example.E_Commerce.model.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ICategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByIdAndIsDeletedFalse(Long id);

    List<Category> findByIsDeletedFalseAndSellerIsNullOrderByNameAsc();

    List<Category> findByIsDeletedFalseOrderByNameAsc();

    List<Category> findByIsDeletedFalseAndSellerIdOrderByNameAsc(Long sellerId);
}
