package com.mukkebi.foodfinder.core.domain;

import com.mukkebi.foodfinder.core.api.controller.v1.request.ReviewRequest;
import com.mukkebi.foodfinder.core.api.controller.v1.response.ReviewResponse;
import com.mukkebi.foodfinder.core.enums.EntityStatus;
import com.mukkebi.foodfinder.core.support.error.CoreException;
import com.mukkebi.foodfinder.core.support.error.ErrorType;
import com.mukkebi.foodfinder.storage.ReviewRepository;
import com.mukkebi.foodfinder.storage.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;


    //리뷰 등록
    @Transactional
    public void saveReview(ReviewRequest reviewRequest, Long restaurantId, OAuth2User oauth2User) {

        //String githubId=oauth2User.getAttribute("login").toString();
//             if (oauth2User == null) {
//            throw new CoreException(ErrorType.DEFAULT_ERROR);
//        }
        String githubId="180543622";

        User user = userRepository.findByGithubId(githubId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        reviewRepository.save(
                Review.create(
                        reviewRequest.getContent(),
                        reviewRequest.getRating(),
                        user.getId(),
                        restaurantId
                )
        );
    }

    //리뷰 수정
    @Transactional
    public void updateReview(ReviewRequest reviewRequest, Long reviewId, OAuth2User oauth2User) {

        //String githubId=oauth2User.getAttribute("login").toString();
//             if (oauth2User == null) {
//            throw new CoreException(ErrorType.DEFAULT_ERROR);
//        }
        String githubId="180543622";


        User user = userRepository.findByGithubId(githubId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        if (!review.getUserId().equals(user.getId())) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }

        review.update(reviewRequest.getContent(), reviewRequest.getRating());
    }

    //리뷰 삭제
    @Transactional
    public void deleteReview(Long reviewId, OAuth2User oauth2User) {

//String githubId=oauth2User.getAttribute("login").toString();
//             if (oauth2User == null) {
//            throw new CoreException(ErrorType.DEFAULT_ERROR);
//        }
        String githubId="180543622";

        User user = userRepository.findByGithubId(githubId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        if (!review.getUserId().equals(user.getId())) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }
        review.deleted();
    }
}
