package com.schoolfinder.service;

import com.schoolfinder.api.Dtos.ImageDto;
import com.schoolfinder.api.Dtos.PageResponse;
import com.schoolfinder.api.Dtos.ReviewDto;
import com.schoolfinder.api.Dtos.SchoolDetail;
import com.schoolfinder.api.Dtos.SchoolUpsertRequest;
import com.schoolfinder.api.Dtos.UserDto;
import com.schoolfinder.api.NotFoundException;
import com.schoolfinder.domain.AppUser;
import com.schoolfinder.domain.Review;
import com.schoolfinder.domain.ReviewStatus;
import com.schoolfinder.domain.Role;
import com.schoolfinder.domain.School;
import com.schoolfinder.domain.SchoolImage;
import com.schoolfinder.repository.AppUserRepository;
import com.schoolfinder.repository.ReviewRepository;
import com.schoolfinder.repository.SchoolImageRepository;
import com.schoolfinder.repository.SchoolRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    private final SchoolRepository schools;
    private final AppUserRepository users;
    private final ReviewRepository reviews;
    private final SchoolImageRepository images;
    private final CloudinaryService cloudinary;
    private final SchoolService schoolService;

    public AdminService(
        SchoolRepository schools,
        AppUserRepository users,
        ReviewRepository reviews,
        SchoolImageRepository images,
        CloudinaryService cloudinary,
        SchoolService schoolService
    ) {
        this.schools = schools;
        this.users = users;
        this.reviews = reviews;
        this.images = images;
        this.cloudinary = cloudinary;
        this.schoolService = schoolService;
    }

    // ----- School images (Cloudinary) -----

    @Transactional
    public ImageDto uploadSchoolImage(Long id, byte[] bytes, String caption, boolean setCover) {
        School school = schools.findById(id).orElseThrow(() -> new NotFoundException("School " + id + " not found"));
        String url = cloudinary.upload(bytes);
        SchoolImage img = images.save(new SchoolImage(school, url, caption));
        if (setCover) {
            school.setCoverImageUrl(url);
            schools.save(school);
        }
        return new ImageDto(img.getId(), img.getUrl(), img.getCaption());
    }

    @Transactional
    public void deleteSchoolImage(Long imageId) {
        if (!images.existsById(imageId)) throw new NotFoundException("Image " + imageId + " not found");
        images.deleteById(imageId);
    }

    // ----- Schools -----

    @Transactional
    public SchoolDetail createSchool(SchoolUpsertRequest req) {
        School school = new School(req.name(), req.category());
        applyFrom(school, req);
        return schoolService.detail(schools.save(school).getId());
    }

    @Transactional
    public SchoolDetail updateSchool(Long id, SchoolUpsertRequest req) {
        School school = schools.findById(id).orElseThrow(() -> new NotFoundException("School " + id + " not found"));
        school.setName(req.name());
        school.setCategory(req.category());
        applyFrom(school, req);
        schools.save(school);
        return schoolService.detail(id);
    }

    @Transactional
    public void deleteSchool(Long id) {
        if (!schools.existsById(id)) throw new NotFoundException("School " + id + " not found");
        schools.deleteById(id);
    }

    private void applyFrom(School school, SchoolUpsertRequest req) {
        school.setDescription(req.description());
        school.setHistory(req.history());
        school.setCity(req.city());
        school.setRegion(req.region());
        school.setAddress(req.address());
        school.setLatitude(req.latitude());
        school.setLongitude(req.longitude());
        school.setTuitionFee(req.tuitionFee());
        school.setCurrency(req.currency());
        school.setWebsite(req.website());
        school.setPhone(req.phone());
        school.setEmail(req.email());
        school.setCoverImageUrl(req.coverImageUrl());
        school.setApproved(req.approved());
    }

    // ----- Users -----

    @Transactional(readOnly = true)
    public List<UserDto> listUsers() {
        return users.findAll().stream()
            .map(u -> new UserDto(u.getId(), u.getEmail(), u.getDisplayName(), u.getRole(), u.isActive(), u.getCreatedAt()))
            .toList();
    }

    @Transactional
    public UserDto setUserActive(Long id, boolean active) {
        AppUser u = users.findById(id).orElseThrow(() -> new NotFoundException("User " + id + " not found"));
        u.setActive(active);
        return new UserDto(u.getId(), u.getEmail(), u.getDisplayName(), u.getRole(), u.isActive(), u.getCreatedAt());
    }

    @Transactional
    public UserDto setUserRole(Long id, Role role) {
        AppUser u = users.findById(id).orElseThrow(() -> new NotFoundException("User " + id + " not found"));
        u.setRole(role);
        return new UserDto(u.getId(), u.getEmail(), u.getDisplayName(), u.getRole(), u.isActive(), u.getCreatedAt());
    }

    // ----- Review moderation -----

    @Transactional(readOnly = true)
    public PageResponse<ReviewDto> pendingReviews(Pageable pageable) {
        return PageResponse.of(
            reviews.findByStatusOrderByCreatedAtDesc(ReviewStatus.PENDING, pageable).map(this::toReviewDto));
    }

    @Transactional
    public ReviewDto moderateReview(Long id, ReviewStatus status) {
        Review review = reviews.findById(id).orElseThrow(() -> new NotFoundException("Review " + id + " not found"));
        review.setStatus(status);
        return toReviewDto(review);
    }

    // ----- Analytics -----

    @Transactional(readOnly = true)
    public Map<String, Object> analytics() {
        Map<String, Long> byCategory = new TreeMap<>();
        for (School s : schools.findAll()) {
            byCategory.merge(s.getCategory().name(), 1L, Long::sum);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalSchools", schools.count());
        result.put("totalUsers", users.count());
        result.put("totalReviews", reviews.count());
        result.put("schoolsByCategory", byCategory);
        return result;
    }

    private ReviewDto toReviewDto(Review r) {
        String displayName = r.getUser().getDisplayName() != null ? r.getUser().getDisplayName() : r.getUser().getEmail();
        return new ReviewDto(
            r.getId(),
            r.getSchool().getId(),
            r.getSchool().getName(),
            displayName,
            r.getRating(),
            r.getComment(),
            r.getStatus(),
            r.getCreatedAt()
        );
    }
}
