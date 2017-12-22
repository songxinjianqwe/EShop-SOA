package cn.sinjinsong.eshop.service.product;


import cn.sinjinsong.eshop.common.domain.entity.product.ProductCategoryDO;
import cn.sinjinsong.eshop.common.domain.entity.product.ProductDO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * Created by SinjinSong on 2017/10/6.
 */
public interface ProductService {
    ProductCategoryDO findCategoryById(Long categoryId);

    List<ProductCategoryDO> findAllCategories(boolean containsProducts);

    List<ProductCategoryDO> findCategoriesOnBoard();

    List<ProductDO> findProductsOnPromotion();

    PageInfo<ProductDO> findProductByCategory(Long categoryId, Integer pageNum, Integer pageSize);

    List<ProductDO> findSimpleProductByCategory(Long categoryId);

    List<Long> findProductIdsByCategory(Long categoryId);
     
    ProductDO findProductById(Long productId);

    void saveProduct(ProductDO product);

    void updateProduct(ProductDO product);

    void saveCategory(String categoryName);

    void updateCategory(ProductCategoryDO category);
}
