package pl.sk.coinTracker.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.sk.coinTracker.Support.Response;

import javax.persistence.*;
import javax.persistence.Table;
import javax.validation.constraints.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Data
@Table(name = "users")
@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Username can not be null")
    @NotBlank(message = "Username can not be blank")
    private String username;
    @NotNull(message = "Password can not be null")
    @Size(min = 10,message= "Password should contain at least 10 characters")
    private String password;
    @NotNull(message = "Email can not be null")
    @Email(message = "Wrong email")
    private String email;
    private String role;
    @NotNull
    private Date joiningDate = new Date();

    public User() {
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.joiningDate = new Date();
    }

    public ObjectNode toJson() {
        return new ObjectMapper().createObjectNode()
                .put("id", this.id)
                .put("username", this.username)
                .put("email", this.email)
                .put("role", this.role)
                .put("joiningDate", String.valueOf(this.joiningDate));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role));
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
