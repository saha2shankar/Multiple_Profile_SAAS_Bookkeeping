package com.harishankar.config;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.harishankar.model.User;
import com.harishankar.repository.UserRepo;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthSucessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepo userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws IOException, ServletException {

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        String email = authentication.getName(); // assuming email is the username
        User user = userRepository.findByEmail(email);

        Long companyId = null;
        if (user != null && user.getCompany() != null) {
            companyId = user.getCompany().getId();
            request.getSession().setAttribute("companyId", companyId);
        }

        if (roles.contains("ROLE_MAIN_ADMIN")) {
            response.sendRedirect("/admin/profile");
        } else {
            response.sendRedirect("/company/dashboard/" + companyId);
        }
    }
}
