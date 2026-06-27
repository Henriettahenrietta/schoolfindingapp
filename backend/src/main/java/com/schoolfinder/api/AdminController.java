package com.schoolfinder.api;

import com.schoolfinder.api.Dtos.ImageDto;
import com.schoolfinder.api.Dtos.PageResponse;
import com.schoolfinder.api.Dtos.ReviewDto;
import com.schoolfinder.api.Dtos.SchoolDetail;
import com.schoolfinder.api.Dtos.SchoolUpsertRequest;
import com.schoolfinder.api.Dtos.UserDto;
import com.schoolfinder.domain.ReviewStatus;
import com.schoolfinder.domain.Role;
import com.schoolfinder.service.AdminService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService admin;

    public AdminController(AdminService admin) {
        this.admin = admin;
    }

    // ----- Schools -----

    @PostMapping("/schools")
    @ResponseStatus(HttpStatus.CREATED)
    public SchoolDetail createSchool(@Valid @RequestBody SchoolUpsertRequest body) {
        return admin.createSchool(body);
    }

    @PutMapping("/schools/{id}")
    public SchoolDetail updateSchool(@PathVariable Long id, @Valid @RequestBody SchoolUpsertRequest body) {
        return admin.updateSchool(id, body);
    }

    @DeleteMapping("/schools/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSchool(@PathVariable Long id) {
        admin.deleteSchool(id);
    }

    // ----- School images (Cloudinary upload) -----

    @PostMapping(value = "/schools/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ImageDto uploadImage(
        @PathVariable Long id,
        @RequestParam("file") MultipartFile file,
        @RequestParam(required = false) String caption,
        @RequestParam(defaultValue = "false") boolean setCover
    ) throws IOException {
        return admin.uploadSchoolImage(id, file.getBytes(), caption, setCover);
    }

    @DeleteMapping("/schools/images/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(@PathVariable Long imageId) {
        admin.deleteSchoolImage(imageId);
    }

    // ----- Users -----

    @GetMapping("/users")
    public List<UserDto> users() {
        return admin.listUsers();
    }

    @PutMapping("/users/{id}/active")
    public UserDto setActive(@PathVariable Long id, @RequestParam boolean active) {
        return admin.setUserActive(id, active);
    }

    @PutMapping("/users/{id}/role")
    public UserDto setRole(@PathVariable Long id, @RequestParam Role role) {
        return admin.setUserRole(id, role);
    }

    // ----- Review moderation -----

    @GetMapping("/reviews/pending")
    public PageResponse<ReviewDto> pending(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        int boundedSize = Math.min(100, Math.max(1, size));
        return admin.pendingReviews(PageRequest.of(page, boundedSize));
    }

    @PutMapping("/reviews/{id}/status")
    public ReviewDto moderate(@PathVariable Long id, @RequestParam ReviewStatus status) {
        return admin.moderateReview(id, status);
    }

    // ----- Analytics -----

    @GetMapping("/analytics")
    public Map<String, Object> analytics() {
        return admin.analytics();
    }
}
