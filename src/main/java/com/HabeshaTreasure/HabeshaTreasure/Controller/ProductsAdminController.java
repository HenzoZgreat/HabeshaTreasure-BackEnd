package com.HabeshaTreasure.HabeshaTreasure.Controller;

import com.HabeshaTreasure.HabeshaTreasure.DTO.BulkDeleteRequestDTO;
import com.HabeshaTreasure.HabeshaTreasure.DTO.ProductRequestDTO;
import com.HabeshaTreasure.HabeshaTreasure.Entity.Products;
import com.HabeshaTreasure.HabeshaTreasure.Service.ProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/admin/products")
public class ProductsAdminController {

    @Autowired
    private ProductsService productsService;

    @GetMapping
    public ResponseEntity<List<Products>> getAllProducts() {
        return ResponseEntity.ok(productsService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(productsService.getProductById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductRequestDTO dto) {
        try {
            productsService.createProduct(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Product created");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Integer id, @RequestBody ProductRequestDTO dto) {
        try {
            productsService.updateProduct(id, dto);
            return ResponseEntity.ok("Product updated");
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        try {
            productsService.deleteProduct(id);
            return ResponseEntity.ok("Product deleted");
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Delete failed: " + e.getMessage());
        }
    }

    @PostMapping("/bulk-delete")
    public ResponseEntity<?> bulkDeleteProducts(@RequestBody BulkDeleteRequestDTO request) {
        try {
            productsService.deleteMultipleProducts(request.getIds());
            return ResponseEntity.ok("Products Deleted");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Bulk delete failed: " + e.getMessage());
        }
    }


    @GetMapping("/{id}/favorites")
    public ResponseEntity<Integer> getFavorites(@PathVariable Integer id) {
        return ResponseEntity.ok(productsService.getFavoritesCount(id));
    }


    @GetMapping("/featured")
    public ResponseEntity<List<Products>> getFeaturedProducts() {
        return ResponseEntity.ok(productsService.getFeaturedProducts());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Products>> getProductsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(productsService.getProductsByStatus(status));
    }

    @GetMapping("/distinct/categories")
    public ResponseEntity<List<String>> getDistinctCategories() {
        return ResponseEntity.ok(productsService.getDistinctCategories());
    }

    @GetMapping("/distinct/statuses")
    public ResponseEntity<List<String>> getDistinctStatuses() {
        return ResponseEntity.ok(productsService.getDistinctStatuses());
    }

}
