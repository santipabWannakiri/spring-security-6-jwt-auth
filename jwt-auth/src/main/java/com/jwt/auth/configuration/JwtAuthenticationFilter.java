package com.jwt.auth.configuration;

import com.jwt.auth.model.ReponseObject;
import com.jwt.auth.service.JwtTokenService;
import com.jwt.auth.service.UserService;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private UserSerivceImp userSerivceImp;

    @Autowired
    private UtilityService utilityService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/api/v1/user/") || path.equals("/h2") || path.equals("/favicon.ico"); // "/favicon also one of h2 path as well
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = jwtTokenService.extractTokenFromRequest(request,response);

        if(token == null){
            utilityService.sendErrorJson(response, 401, new ReponseObject("0300","INVALID_FORMAT","Authorization header not found in the request."));
            return;
        }

        if(jwtTokenService.validateToken(token) == false){
            utilityService.sendErrorJson(response, 400, new ReponseObject("0300","INVALID_TOKEN","Invalid token or token has expired."));
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String username = jwtTokenService.getUsernameFromToken(token);
            //Load user from DAO
            UserDetails userDetails = userSerivceImp.loadUserByUsername(username);
            //Authenticate process
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }else {
            utilityService.sendErrorJson(response, 500, new ReponseObject("0300","INTERNAL_ERROR","Unable to process request. Please try again."));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
