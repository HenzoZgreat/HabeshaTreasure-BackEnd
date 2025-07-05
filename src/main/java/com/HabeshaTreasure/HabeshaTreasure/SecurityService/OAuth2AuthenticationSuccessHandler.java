package com.HabeshaTreasure.HabeshaTreasure.SecurityService;

import com.HabeshaTreasure.HabeshaTreasure.Service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Service
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;

    public OAuth2AuthenticationSuccessHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        try{
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            String email = oidcUser.getEmail();
            String firstName = oidcUser.getGivenName();
            String lastName = oidcUser.getFamilyName();

            String jwtToken = authService.handleOAuth2User(email, firstName, lastName);

            String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth2/redirect")
                    .queryParam("token", jwtToken)
                    .build()
                    .toUriString();

            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } catch (Exception e) {
            System.err.println("OAuth Error: " + e.getMessage());
            throw e;
        }
    }
}