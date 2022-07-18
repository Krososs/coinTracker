package pl.sk.coinTracker.User;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.sk.coinTracker.Security.AuthUtil;
import pl.sk.coinTracker.Support.Response;
import pl.sk.coinTracker.Support.Validation;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;


@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("users/authenticate")
    public ResponseEntity<?> authenticate(@RequestHeader("authorization") String token) {

        if (token == null || token.length() == 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        try {
            DecodedJWT jwt = JWT.decode(token);
            String username = AuthUtil.getUsernameFromToken(token);
            if (userService.getUserFromUsernamne(username) == null)
                return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_DOES_NOT_EXISTS.ToString()), HttpStatus.UNAUTHORIZED);


        } catch (TokenExpiredException exception) {
            return new ResponseEntity<>(Validation.getErrorResponse(Response.TOKEN_EXPIRED.ToString()), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("users/token/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("authorization") String refreshToken) {

        if (refreshToken == null || refreshToken.length() == 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        try {
            String username = AuthUtil.getUsernameFromToken(refreshToken);
            User user = userService.getUserFromUsernamne(username);

            Map<String, String> data = new HashMap<>();
            data.put("acces_token", AuthUtil.getAccesToken(user));
            data.put("refresh_token", AuthUtil.getRefreshToken(user));

            return new ResponseEntity<>(data, HttpStatus.OK);

        } catch (TokenExpiredException exception) {
            return new ResponseEntity<>(Validation.getErrorResponse(Response.TOKEN_EXPIRED.ToString()), HttpStatus.UNAUTHORIZED);
        } catch (JWTDecodeException exception) {
            return new ResponseEntity<>(Validation.getErrorResponse(Response.TOKEN_EXPIRED.ToString()), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("users/register")
    public ResponseEntity<?> register(@Valid User user, BindingResult result) {

        if (result.hasErrors())
            return new ResponseEntity<>(Validation.getErrorResponse(result.getAllErrors()), HttpStatus.CONFLICT);
        if (userService.usernameExists(user.getUsername()))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USERNAME_TAKEN.ToString()), HttpStatus.CONFLICT);
        if (userService.emailExists(user.getEmail()))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.EMAIL_TAKEN.ToString()), HttpStatus.CONFLICT);

        userService.registerUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("users/info")
    public ResponseEntity<?> getUserInfo(@RequestHeader("authorization") String token) {
        String username = AuthUtil.getUsernameFromToken(token);
        User user = userService.getUserFromUsernamne(username);
        return new ResponseEntity<>(user.toJson(), HttpStatus.OK);
    }
}
