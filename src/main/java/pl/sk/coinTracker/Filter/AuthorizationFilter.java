package pl.sk.coinTracker.Filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.sk.coinTracker.Security.AuthUtil;
import pl.sk.coinTracker.Support.Response;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class AuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (request.getServletPath().equals("/login") || request.getServletPath().equals("/users/register") || request.getServletPath().equals("/users/token/refresh")) {
            filterChain.doFilter(request, response);
        } else {
            String authHeader = request.getHeader(AUTHORIZATION);
            if (authHeader != null) {
                try {
                    String token = authHeader;
                    DecodedJWT jwt = JWT.decode(token);
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority(AuthUtil.getRolesFromToken(authHeader)));
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(jwt.getSubject(), null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request, response);

                } catch (TokenExpiredException exception) {

                    Map<String, String> error = new HashMap<>();
                    error.put("message", Response.TOKEN_EXPIRED.ToString());
                    response.setContentType(APPLICATION_JSON_VALUE);
                    response.setStatus(401);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                }
                catch (JWTDecodeException exception) {
                    Map<String, String> error = new HashMap<>();
                    error.put("message", Response.TOKEN_EXPIRED.ToString());
                    response.setContentType(APPLICATION_JSON_VALUE);
                    response.setStatus(401);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }
}
