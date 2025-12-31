package com.mukkebi.foodfinder.core.domain;

import com.mukkebi.foodfinder.core.api.controller.v1.request.ReviewRequest;
import com.mukkebi.foodfinder.core.api.controller.v1.response.ReviewResponse;
import com.mukkebi.foodfinder.core.enums.EntityStatus;
import com.mukkebi.foodfinder.core.enums.RecommendationResult;
import com.mukkebi.foodfinder.core.support.error.CoreException;
import com.mukkebi.foodfinder.core.support.error.ErrorType;
import com.mukkebi.foodfinder.storage.RecommendRepository;
import com.mukkebi.foodfinder.storage.ReviewRepository;
import com.mukkebi.foodfinder.storage.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RecommendRepository recommendRepository;


    //리뷰 등록 (recommendId 기반)
    @Transactional
    public void saveReviewByRecommend(ReviewRequest reviewRequest, Long recommendId, Long userId) {

        if (reviewRequest.getRating() < 1 || reviewRequest.getRating() > 5) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        Recommend recommend = recommendRepository.findById(recommendId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        // 해당 추천이 현재 사용자의 것인지 확인
        if (!recommend.getUserId().equals(userId)) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }

        reviewRepository.save(
                Review.create(
                        reviewRequest.getContent(),
                        reviewRequest.getRating(),
                        userId,
                        user.getNickname(),
                        recommend.getRestaurantId(),
                        recommend.getRestaurantName()
                )
        );

        // 리뷰 작성 시 해당 추천을 ACCEPTED로 변경
        recommend.updateResult(RecommendationResult.ACCEPTED);
    }


    //리뷰 수정
    @Transactional
    public void updateReview(ReviewRequest reviewRequest, Long reviewId, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        if (!review.getUserId().equals(user.getId())) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }

        if(reviewRequest.getRating()<1||reviewRequest.getRating()>5) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }

        review.update(reviewRequest.getContent(), reviewRequest.getRating());
    }

    //리뷰 삭제
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        if (!review.getUserId().equals(user.getId())) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }
        review.deleted();
    }
}
