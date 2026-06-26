package com.schoolfinder.service

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.schoolfinder.config.AppProperties
import org.springframework.stereotype.Service

/**
 * Uploads images to Cloudinary (signed, server-side). Active only when CLOUDINARY_URL is set
 * (format: cloudinary://<api_key>:<api_secret>@<cloud_name>); otherwise [enabled] is false and
 * uploads are rejected with a clear error.
 */
@Service
class CloudinaryService(props: AppProperties) {

    private val folder = "hen2"

    private val cloudinary: Cloudinary? =
        props.cloudinary.url.takeIf { it.isNotBlank() }?.let { Cloudinary(it) }

    val enabled: Boolean get() = cloudinary != null

    @Suppress("UNCHECKED_CAST")
    fun upload(bytes: ByteArray): String {
        val cl = cloudinary
            ?: throw IllegalStateException("Cloudinary is not configured. Set CLOUDINARY_URL to enable image uploads.")
        val result = cl.uploader().upload(
            bytes,
            ObjectUtils.asMap("folder", folder, "resource_type", "image"),
        ) as Map<String, Any>
        return (result["secure_url"] ?: result["url"]).toString()
    }
}
