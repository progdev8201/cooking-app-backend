package cal.repository;

import cal.model.entity.Image;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface ImageRepository extends MongoRepository<Image, UUID> {
}
