package NarayanGroup.example.E_Commerce.model.Repositry;

import NarayanGroup.example.E_Commerce.model.Entity.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ISubcategoryRepository extends JpaRepository<Subcategory, Long> {

    List<Subcategory> findByCategoryIdAndIsDeletedFalseOrderByNameAsc(Long categoryId);

    Optional<Subcategory> findByIdAndIsDeletedFalse(Long id);

    List<Subcategory> findByCategoryIdAndSellerIdAndIsDeletedFalseOrderByNameAsc(Long categoryId, Long sellerId);

    List<Subcategory> findByCategoryIdAndSellerIsNullAndIsDeletedFalseOrderByNameAsc(Long categoryId);
}
