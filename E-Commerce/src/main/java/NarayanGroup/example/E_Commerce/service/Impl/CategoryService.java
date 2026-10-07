package NarayanGroup.example.E_Commerce.service.Impl;

import NarayanGroup.example.E_Commerce.constant.ErrorConstants;
import NarayanGroup.example.E_Commerce.DTO.request.CustomCategoryRequestDTO;
import NarayanGroup.example.E_Commerce.DTO.request.CustomSubcategoryRequestDTO;
import NarayanGroup.example.E_Commerce.model.Entity.Category;
import NarayanGroup.example.E_Commerce.model.Entity.Seller;
import NarayanGroup.example.E_Commerce.model.Entity.Subcategory;
import NarayanGroup.example.E_Commerce.model.Repositry.ICategoryRepository;
import NarayanGroup.example.E_Commerce.model.Repositry.ISubcategoryRepository;
import NarayanGroup.example.E_Commerce.model.Repositry.IUserRepository;
import NarayanGroup.example.E_Commerce.service.ICategoryService;
import NarayanGroup.example.E_Commerce.service.ISellerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService implements ICategoryService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CategoryService.class);

    private final ICategoryRepository categoryRepository;
    private final ISubcategoryRepository subcategoryRepository;
    private final ISellerService sellerService;
    private final IUserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Category> getCategories() {
        return categoryRepository.findByIsDeletedFalseAndSellerIsNullOrderByNameAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getCategoriesForSeller(String email) {
        Long sellerId = sellerService.getSellerByEmail(email).getId();
        List<Category> categories = new java.util.ArrayList<>(
                categoryRepository.findByIsDeletedFalseAndSellerIsNullOrderByNameAsc()
        );
        categories.addAll(categoryRepository.findByIsDeletedFalseAndSellerIdOrderByNameAsc(sellerId));
        return categories;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subcategory> getSubcategories(Long categoryId) {
        return subcategoryRepository.findByCategoryIdAndIsDeletedFalseOrderByNameAsc(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subcategory> getSubcategoriesForSeller(String email, Long categoryId) {
        Long sellerId = sellerService.getSellerByEmail(email).getId();
        List<Subcategory> subcategories = new java.util.ArrayList<>(
                subcategoryRepository.findByCategoryIdAndSellerIsNullAndIsDeletedFalseOrderByNameAsc(categoryId)
        );
        subcategories.addAll(
                subcategoryRepository.findByCategoryIdAndSellerIdAndIsDeletedFalseOrderByNameAsc(
                        categoryId, sellerId
                )
        );
        return subcategories;
    }

    @Override
    @Transactional(readOnly = true)
    public Category findCategoryById(Long categoryId) {
        return categoryRepository.findByIdAndIsDeletedFalse(categoryId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorConstants.CATEGORY_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public Subcategory findSubcategoryById(Long subcategoryId) {
        return subcategoryRepository.findByIdAndIsDeletedFalse(subcategoryId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorConstants.SUBCATEGORY_NOT_FOUND));
    }

    @Override
    @Transactional
    public Category createCustomCategory(String email, CustomCategoryRequestDTO request) {
        Seller seller = sellerService.getActiveSellerByUserId(getUserIdByEmail(email));

        Category category = Category.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .isCustom(true)
                .seller(seller)
                .isDeleted(false)
                .build();

        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Subcategory createCustomSubcategory(String email, Long categoryId, CustomSubcategoryRequestDTO request) {
        Seller seller = sellerService.getActiveSellerByUserId(getUserIdByEmail(email));
        Category category = categoryRepository.findByIdAndIsDeletedFalse(categoryId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorConstants.CATEGORY_NOT_FOUND));

        if (category.isCustom() && category.getSeller() != null
                && !category.getSeller().getId().equals(seller.getId())) {
            throw new IllegalArgumentException("You cannot add a subcategory to another seller's category");
        }

        Subcategory subcategory = Subcategory.builder()
                .category(category)
                .name(request.getName().trim())
                .description(request.getDescription())
                .isCustom(true)
                .seller(seller)
                .isDeleted(false)
                .build();

        return subcategoryRepository.save(subcategory);
    }

    private Long getUserIdByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(ErrorConstants.AUTHENTICATED_USER_EMAIL_MISSING);
        }
        var user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException(ErrorConstants.AUTHENTICATED_USER_NOT_FOUND);
        }
        return user.getId();
    }
}
