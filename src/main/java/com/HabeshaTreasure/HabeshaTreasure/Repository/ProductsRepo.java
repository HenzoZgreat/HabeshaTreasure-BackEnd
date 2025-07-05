package com.HabeshaTreasure.HabeshaTreasure.Repository;

import com.HabeshaTreasure.HabeshaTreasure.Entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductsRepo extends JpaRepository<Products, Integer> {
    List<Products> findByIsFeaturedTrue();
    List<Products> findByStatusIgnoreCase(String status);

    @Query("SELECT DISTINCT p.category FROM Products p")
    List<String> findDistinctCategories();

    @Query("SELECT DISTINCT p.status FROM Products p")
    List<String> findDistinctStatuses();

}
