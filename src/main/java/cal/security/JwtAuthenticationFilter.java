package cal.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtProvider JwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {

            DecodedJWT token = JwtProvider.verify(request.getHeader("Authorization"));

            if (token != null)
                SecurityContextHolder.getContext().setAuthentication(new JwtAuthentication(token));

        } catch (JWTVerificationException e) {
            // JWT Verification failed, do nothing
        }

        filterChain.doFilter(request, response);
    }
}
