package com.jwt.auth.configuration;

import com.jwt.auth.model.json.response.JsonResponse;
import com.jwt.auth.service.TokenService;
import com.jwt.auth.service.UtilityService;
import com.jwt.auth.serviceImp.UserSerivceImp;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.jwt.auth.constants.Constants.*;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserSerivceImp userSerivceImp;

    @Autowired
    private UtilityService utilityService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/api/v1/user/")
                || path.equals("/h2")
                || path.equals("/favicon.ico")
                || path.startsWith("/swagger-ui/")
                || path.equals("/v3/api-docs/swagger-config")
                || path.equals("/v3/api-docs"); // "/favicon also one of h2 path as well
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = tokenService.extractTokenFromRequest(request, response);
        if (token == null) {
            utilityService.servletResponseMessage(response, 401, new JsonResponse(INVALID_FORMAT_ERROR_CODE, INVALID_FORMAT_MESSAGE_CODE, UNABLE_EXTRACT_TOKEN_MESSAGE));
            return;
        }
        if (tokenService.validateAccessToken(token) == false) {
            utilityService.servletResponseMessage(response, 400, new JsonResponse(INVALID_FORMAT_ERROR_CODE, INVALID_FORMAT_MESSAGE_CODE, INVALID_OR_EXPIRE_MESSAGE));
            return;
        }
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String username = tokenService.getUsernameFromToken(token);
            //Load user from DAO
            UserDetails userDetails = userSerivceImp.loadUserByUsername(username);
            //Authenticate process
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } else {
            utilityService.servletResponseMessage(response, 500, new JsonResponse(INTERNAL_ERROR_CODE, INTERNAL_MESSAGE_CODE, UNABLE_TO_PROCESS_MESSAGE));
            return;
        }
        filterChain.doFilter(request, response);
    }
}
