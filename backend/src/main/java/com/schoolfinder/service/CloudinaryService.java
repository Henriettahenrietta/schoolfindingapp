package com.schoolfinder.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.schoolfinder.config.AppProperties;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * Uploads images to Cloudinary (signed, server-side). Active only when CLOUDINARY_URL is set
 * (format: cloudinary://&lt;api_key&gt;:&lt;api_secret&gt;@&lt;cloud_name&gt;); otherwise
 * {@link #isEnabled()} is false and uploads are rejected with a clear error.
 */
@Service
public class CloudinaryService {

    private static final String FOLDER = "hen2";

    private final Cloudinary cloudinary;

    public CloudinaryService(AppProperties props) {
        String url = props.getCloudinary().getUrl();
        this.cloudinary = (url != null && !url.isBlank()) ? new Cloudinary(url) : null;
    }

    public boolean isEnabled() {
        return cloudinary != null;
    }

    @SuppressWarnings("unchecked")
    public String upload(byte[] bytes) {
        if (cloudinary == null) {
            throw new IllegalStateException(
                "Cloudinary is not configured. Set CLOUDINARY_URL to enable image uploads.");
        }
        try {
            Map<String, Object> result = (Map<String, Object>) cloudinary.uploader().upload(
                bytes,
                ObjectUtils.asMap("folder", FOLDER, "resource_type", "image")
            );
            Object url = result.get("secure_url");
            if (url == null) url = result.get("url");
            return String.valueOf(url);
        } catch (Exception ex) {
            throw new IllegalStateException("Image upload failed: " + ex.getMessage(), ex);
        }
    }
}
