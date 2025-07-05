package com.HabeshaTreasure.HabeshaTreasure.Service;

import com.HabeshaTreasure.HabeshaTreasure.DTO.ReviewResponseDTO;
import com.HabeshaTreasure.HabeshaTreasure.Entity.Products;
import com.HabeshaTreasure.HabeshaTreasure.Entity.Review;
import com.HabeshaTreasure.HabeshaTreasure.Entity.User;
import com.HabeshaTreasure.HabeshaTreasure.Repository.ProductsRepo;
import com.HabeshaTreasure.HabeshaTreasure.Repository.ReviewRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    @Autowired
    private final ReviewRepo reviewRepo;
    @Autowired
    private ProductsRepo productsRepo;

    public void addOrUpdateReview(User user, Integer productId, int rating, String comment) {
        Products product = productsRepo.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));

        Optional<Review> existingReviewOpt = reviewRepo.findByUserAndProduct(user, product);

        if (existingReviewOpt.isPresent()) {
            Review existing = existingReviewOpt.get();
            int oldRating = existing.getRating();

            existing.setRating(rating);
            existing.setComment(comment);
            existing.setReviewedAt(LocalDateTime.now());
            reviewRepo.save(existing);

            // 🔁 Adjust rating average
            int total = product.getCount();
            double currentSum = product.getRate() * total;
            double newAvg = ((currentSum - oldRating + rating) / total);
            product.setRate(newAvg);
        } else {
            Review newReview = new Review();
            newReview.setUser(user);
            newReview.setProduct(product);
            newReview.setRating(rating);
            newReview.setComment(comment);
            newReview.setReviewedAt(LocalDateTime.now());
            reviewRepo.save(newReview);

            int total = product.getCount();
            double newAvg = ((product.getRate() * total) + rating) / (total + 1);
            product.setCount(total + 1);
            product.setRate(newAvg);
        }

        productsRepo.save(product);
    }

    public void deleteReview(User user, Integer productId) {
        Products product = productsRepo.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));

        Optional<Review> reviewOpt = reviewRepo.findByUserAndProduct(user, product);

        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();
            int rating = review.getRating();

            reviewRepo.delete(review);

            int total = product.getCount();
            if (total > 1) {
                double newAvg = ((product.getRate() * total) - rating) / (total - 1);
                product.setRate(newAvg);
                product.setCount(total - 1);
            } else {
                product.setRate(0.0);
                product.setCount(0);
            }

            productsRepo.save(product);
        }
    }


    public List<ReviewResponseDTO> getReviewsForProduct(Integer productId) {
        Products product = productsRepo.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));

        List<Review> reviews = reviewRepo.findByProduct(product);

        return reviews.stream()
                .map(r -> new ReviewResponseDTO(
                        r.getUser().getId(),
                        r.getUser().getUsersInfo().getFirstName() + " " + r.getUser().getUsersInfo().getLastName(),
                        r.getRating(),
                        r.getComment(),
                        r.getReviewedAt()
                ))
                .collect(Collectors.toList());
    }


}

