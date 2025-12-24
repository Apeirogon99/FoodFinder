package com.mukkebi.foodfinder.core.domain;

import com.mukkebi.foodfinder.core.api.controller.v1.request.ReviewRequest;
import com.mukkebi.foodfinder.core.api.controller.v1.response.ReviewResponse;
import com.mukkebi.foodfinder.core.support.error.CoreException;
import com.mukkebi.foodfinder.core.support.error.ErrorType;
import com.mukkebi.foodfinder.storage.ReviewRepository;
import com.mukkebi.foodfinder.storage.UserRepository;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository,UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    //리뷰 가져오기
    @Transactional(readOnly = true)
    public List<ReviewResponse> getMyReviews(String githubId) {
        User user = userRepository.findByGithubId(githubId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        return reviewRepository.findByUserId(user.getId()).stream()
                .map(r -> new ReviewResponse(
                        r.getContent(),
                        r.getRating(),
                        r.getUserId(),
                        r.getRecommendationId()
                ))
                .toList();
    }

    //리뷰 등록
    @Transactional
    public void saveReview(ReviewRequest reviewRequest, Long recommendationId, String githubId) {
        User user = userRepository.findByGithubId(githubId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        reviewRepository.save(
                ReviewEntity.create(
                        reviewRequest.getContent(),
                        reviewRequest.getRating(),
                        user.getId(),
                        recommendationId
                )
        );
    }

    //리뷰 수정
    @Transactional
    public void updateReview(ReviewRequest reviewRequest, Long reviewId, String githubId) {
        User user = userRepository.findByGithubId(githubId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        if (!review.getUserId().equals(user.getId())) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }

        review.update(reviewRequest.getContent(), reviewRequest.getRating());
    }

    //리뷰 삭제
    @Transactional
    public void deleteReview(Long reviewId, String githubId) {
        User user = userRepository.findByGithubId(githubId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        if (!review.getUserId().equals(user.getId())) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }

        reviewRepository.delete(review);
    }




}
