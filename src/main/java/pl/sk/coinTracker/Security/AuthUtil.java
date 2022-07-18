package pl.sk.coinTracker.Security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.sk.coinTracker.Support.Response;

import java.util.Date;
import java.util.stream.Collectors;

public class AuthUtil {
    private final static Algorithm algorithm = Algorithm.HMAC256("secrect".getBytes());
    private final static JWTVerifier verifier = JWT.require(algorithm).build();

    public static String getAccesToken(UserDetails user) {

        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) //24h
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining()))
                .sign(algorithm);
    }

    public static String getRefreshToken(UserDetails user) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 100)) //100h
                .sign(algorithm);
    }

    public static String getUsernameFromToken(String _token) {
        DecodedJWT decodedJWT;
        try {
            decodedJWT = verifier.verify(_token);
        } catch (TokenExpiredException exception) {
            throw new TokenExpiredException(Response.TOKEN_EXPIRED.ToString());
        }
        return decodedJWT.getSubject();
    }

    public static String getRolesFromToken(String token) {
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getClaim("roles").toString();
    }
}
