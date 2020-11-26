package cal.security;

import cal.model.entity.User;
import cal.model.enums.RoleName;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JwtProviderTest {
    @Autowired
    private JwtProvider jwtProvider;

    @Test
    public void generateTokenTest() {

        // Arrange
        User user = new User(UUID.randomUUID(),null,null,null,null,RoleName.CLIENT.toString());

        // Act
        String token = jwtProvider.generate(user);
        DecodedJWT decodedToken = JWT.decode(token);

        // Assert
        assertNotNull(token);
    }

    @Test
    public void verifyValidTokenTest() {

        // Arrange
        User user = new User(UUID.randomUUID(),null,null,null,null,RoleName.CLIENT.toString());

        // Act
        String token = jwtProvider.generate(user);
        DecodedJWT decodedToken = jwtProvider.verify(token);

        // Assert
        assertEquals(user.getUniqueId().toString(), decodedToken.getSubject());
        assertEquals(user.getRole(), decodedToken.getClaim("Role").asString());
    }

    @Test
    public void verifyInvalidTokenTest() {

        // Arrange
        User user = new User(UUID.randomUUID(),null,null,null,null,RoleName.CLIENT.toString());

        // Act
        final String token = jwtProvider.generate(user);
        final StringBuilder sb = new StringBuilder(token);

        sb.deleteCharAt(token.length() / 2); // Should not be able to modify token

        //Assert
        assertThrows(JWTVerificationException.class, () -> jwtProvider.verify(sb.toString()));
    }
}
