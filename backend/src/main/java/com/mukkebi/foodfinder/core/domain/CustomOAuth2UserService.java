package com.mukkebi.foodfinder.core.domain;

import com.mukkebi.foodfinder.storage.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        Long githubId = Long.valueOf(oAuth2User.getAttribute("id").toString());
        String login = oAuth2User.getAttribute("login");

        // DB에 User가 이미 있으면 조회, 없으면 새로 생성
        User user = userRepository.findByGithubId(githubId.toString())
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .githubId(githubId.toString())
                            .nickname(login)
                            .build();
                    return userRepository.save(newUser);
                });

        return oAuth2User;
    }
}
