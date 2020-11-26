package cal.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class JwtResponseDTO implements Serializable {
    private UUID userId;
    private String token;

    public JwtResponseDTO(UUID userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public JwtResponseDTO(){

    }
}
