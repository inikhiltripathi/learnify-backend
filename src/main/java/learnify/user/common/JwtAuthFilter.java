package learnify.user.common;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import learnify.user.service.JwtService;
import learnify.user.service.MyUserDetailsService;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    
    private final JwtService jwtService; 
    private final ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        System.out.println("This is Custom Filter : Jwt Filter");
        String authHeader = request.getHeader("Authorization");
        System.out.println("Header : "+authHeader);
        String jwtToken = null;
        String username = null;

        try {
            //Agar header nahi hai ya "Bearer " se start nahi hota, to public URL ho sakta hai
            // and these code will not execute :-
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwtToken = authHeader.substring(7);
                
                // Ye line exception fek sakti hai agar token expired/invalid hai
                username = jwtService.extractUserName(jwtToken);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Here I get UserEntity because it implemented UserDetails
                UserDetails userDetails = context.getBean(MyUserDetailsService.class).loadUserByUsername(username);

               
                if (jwtService.isTokenValid(jwtToken, userDetails)) {
                    //this constructor will make authentication token with setAuthenticated(true);
                    UsernamePasswordAuthenticationToken authenticatedToken = new UsernamePasswordAuthenticationToken(
                            userDetails, 
                            null, 
                            userDetails.getAuthorities()
                    );
                    System.out.println(authenticatedToken);
                    
                    authenticatedToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticatedToken);
                }
            }
        } catch (Exception e) {
            // Agar token galat hai, to Authentication Context null rahega to Spring Security
            // khud user ko 401/403 de dega.
            // SecurityContextHolder.clearContext();
            System.out.println("JWT Error: " + e.getMessage());
            System.out.println("My JWT Error --> " + e.getClass());
        }

        filterChain.doFilter(request, response);
    }
}
