package cafe.dialed.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    @Value("${cloudinary.folder}")
    private String cloudinaryFolder;

    /**
     * Deletes an image from Cloudinary using a standard naming convention.
     *
     * @param type The entity type (e.g., "grinder", "bean")
     * @param id   The UUID of the entity
     */
    public void deleteImage(String type, UUID id) {

        if (id == null || type == null) {
            System.err.println("CloudinaryService: Type or ID is null, skipping delete.");
            return;
        }

        try {
            // Builds the full path, e.g., "development/grinder_93a08a..."
            String publicId = cloudinaryFolder + "/" + type + "_" + id.toString();
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            // Failing to delete an image shouldn't fail the user's main request.
            System.err.println("Failed to delete image from Cloudinary. Error: " + e.getMessage());
        }

    }

}
