package com.schoolfinder.service;

import com.schoolfinder.api.BadRequestException;
import com.schoolfinder.api.Dtos.CompareResponse;
import com.schoolfinder.api.Dtos.PageResponse;
import com.schoolfinder.api.Dtos.SchoolDetail;
import com.schoolfinder.api.Dtos.SchoolSummary;
import com.schoolfinder.api.NotFoundException;
import com.schoolfinder.domain.School;
import com.schoolfinder.domain.SchoolCategory;
import com.schoolfinder.repository.FavoriteRepository;
import com.schoolfinder.repository.ReviewRepository;
import com.schoolfinder.repository.SchoolRepository;
import com.schoolfinder.security.CurrentUser;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SchoolService {

    private final SchoolRepository schools;
    private final ReviewRepository reviews;
    private final FavoriteRepository favorites;

    public SchoolService(SchoolRepository schools, ReviewRepository reviews, FavoriteRepository favorites) {
        this.schools = schools;
        this.reviews = reviews;
        this.favorites = favorites;
    }

    @Transactional(readOnly = true)
    public PageResponse<SchoolSummary> search(
        String q,
        SchoolCategory category,
        String city,
        Double minRating,
        BigDecimal maxTuition,
        Pageable pageable
    ) {
        Specification<School> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isTrue(root.get("approved")));
            if (q != null && !q.isBlank()) {
                String like = "%" + q.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("name")), like),
                    cb.like(cb.lower(root.get("description")), like)
                ));
            }
            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category));
            }
            if (city != null && !city.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("city")), city.trim().toLowerCase()));
            }
            if (maxTuition != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("tuitionFee"), maxTuition));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<School> page = schools.findAll(spec, pageable);
        List<Long> ids = page.getContent().stream().map(School::getId).filter(java.util.Objects::nonNull).toList();
        RatingLookup ratings = ratingsFor(ids);
        List<SchoolSummary> summaries = page.getContent().stream()
            .map(s -> SchoolMapper.toSummary(s, ratings.ratingOf(s.getId())))
            // minRating is a post-aggregation filter (applied to the current page).
            .filter(s -> minRating == null || s.averageRating() >= minRating)
            .toList();

        return new PageResponse<>(
            summaries,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public SchoolDetail detail(Long id) {
        School school = schools.findById(id).orElseThrow(() -> new NotFoundException("School " + id + " not found"));
        Rating rating = ratingsFor(List.of(id)).ratingOf(id);
        return SchoolMapper.toDetail(school, rating, isFavorite(id));
    }

    @Transactional(readOnly = true)
    public CompareResponse compare(List<Long> ids) {
        if (ids.size() < 2 || ids.size() > 4) {
            throw new BadRequestException("Compare between 2 and 4 schools");
        }
        List<School> found = schools.findAllById(ids);
        if (found.size() != ids.size()) {
            throw new NotFoundException("One or more schools not found");
        }
        // Preserve the requested order.
        Map<Long, School> byId = new LinkedHashMap<>();
        for (School s : found) byId.put(s.getId(), s);
        RatingLookup ratings = ratingsFor(ids);

        List<SchoolDetail> details = ids.stream()
            .map(byId::get)
            .filter(java.util.Objects::nonNull)
            .map(s -> SchoolMapper.toDetail(s, ratings.ratingOf(s.getId()), isFavorite(s.getId())))
            .toList();

        Long cheapest = details.stream()
            .filter(d -> d.tuitionFee() != null)
            .min(java.util.Comparator.comparing(SchoolDetail::tuitionFee))
            .map(SchoolDetail::id)
            .orElse(null);
        Long highestRated = details.stream()
            .filter(d -> d.ratingCount() > 0)
            .max(java.util.Comparator.comparingDouble(SchoolDetail::averageRating))
            .map(SchoolDetail::id)
            .orElse(null);

        return new CompareResponse(details, cheapest, highestRated);
    }

    private boolean isFavorite(Long schoolId) {
        CurrentUser user = CurrentUser.current();
        if (user == null) return false;
        return favorites.existsByUserIdAndSchoolId(user.id(), schoolId);
    }

    private RatingLookup ratingsFor(List<Long> ids) {
        if (ids.isEmpty()) return new RatingLookup(Map.of());
        Map<Long, Rating> map = new LinkedHashMap<>();
        for (ReviewRepository.RatingAgg agg : reviews.ratingAggregates(ids)) {
            double avg = agg.getAvg() == null ? 0.0 : Math.round(agg.getAvg() * 10) / 10.0;
            map.put(agg.getSchoolId(), new Rating(avg, agg.getCnt()));
        }
        return new RatingLookup(map);
    }

    private static final class RatingLookup {
        private final Map<Long, Rating> map;

        RatingLookup(Map<Long, Rating> map) {
            this.map = map;
        }

        Rating ratingOf(Long id) {
            return map.getOrDefault(id, new Rating(0.0, 0));
        }
    }
}
