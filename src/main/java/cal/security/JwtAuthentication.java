package cal.security;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;

public class JwtAuthentication implements Authentication {

    private DecodedJWT token;

    public JwtAuthentication(DecodedJWT token) {
        this.token = token;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        final Claim type = token.getClaim("Role");

        return type.isNull() ? Set.of() : Set.of(new SimpleGrantedAuthority(type.asString()));
    }

    @Override
    public void setAuthenticated(boolean authenticated) throws IllegalArgumentException {
        throw new IllegalArgumentException("Could not set authenticated state");
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public Object getPrincipal() {
        return token.getSubject();
    }

    @Override
    public String getName() {
        return token.getSubject();
    }

    @Override
    public Object getDetails() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

}
