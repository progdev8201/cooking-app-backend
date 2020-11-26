package cal.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document
@Data
public class Image {
    @Id
    private UUID id;
    private String fileName;
    private String fileType;
    private byte[] data;

    public Image() {
    }

    public Image(UUID id, String fileName, String fileType, byte[] data) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
    }
}
