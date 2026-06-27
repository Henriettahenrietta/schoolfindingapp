package com.schoolfinder.service;

import com.schoolfinder.api.ConflictException;
import com.schoolfinder.api.Dtos.SchoolSummary;
import com.schoolfinder.api.NotFoundException;
import com.schoolfinder.domain.Favorite;
import com.schoolfinder.domain.School;
import com.schoolfinder.repository.AppUserRepository;
import com.schoolfinder.repository.FavoriteRepository;
import com.schoolfinder.repository.ReviewRepository;
import com.schoolfinder.repository.SchoolRepository;
import com.schoolfinder.security.CurrentUser;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FavoriteService {

    private final FavoriteRepository favorites;
    private final SchoolRepository schools;
    private final AppUserRepository users;
    private final ReviewRepository reviews;

    public FavoriteService(
        FavoriteRepository favorites,
        SchoolRepository schools,
        AppUserRepository users,
        ReviewRepository reviews
    ) {
        this.favorites = favorites;
        this.schools = schools;
        this.users = users;
        this.reviews = reviews;
    }

    @Transactional(readOnly = true)
    public List<SchoolSummary> list(CurrentUser current) {
        List<School> schoolsList = favorites.findByUserIdOrderByCreatedAtDesc(current.id()).stream()
            .map(Favorite::getSchool)
            .toList();
        List<Long> ids = schoolsList.stream().map(School::getId).filter(java.util.Objects::nonNull).toList();
        Map<Long, Rating> ratings = new LinkedHashMap<>();
        if (!ids.isEmpty()) {
            for (ReviewRepository.RatingAgg agg : reviews.ratingAggregates(ids)) {
                double avg = agg.getAvg() == null ? 0.0 : Math.round(agg.getAvg() * 10) / 10.0;
                ratings.put(agg.getSchoolId(), new Rating(avg, agg.getCnt()));
            }
        }
        return schoolsList.stream()
            .map(s -> SchoolMapper.toSummary(s, ratings.getOrDefault(s.getId(), new Rating(0.0, 0))))
            .toList();
    }

    @Transactional
    public void add(CurrentUser current, Long schoolId) {
        if (!schools.existsById(schoolId)) throw new NotFoundException("School " + schoolId + " not found");
        if (favorites.existsByUserIdAndSchoolId(current.id(), schoolId)) {
            throw new ConflictException("School already in favourites");
        }
        favorites.save(new Favorite(
            users.getReferenceById(current.id()),
            schools.getReferenceById(schoolId)
        ));
    }

    @Transactional
    public void remove(CurrentUser current, Long schoolId) {
        Favorite fav = favorites.findByUserIdAndSchoolId(current.id(), schoolId);
        if (fav == null) throw new NotFoundException("Favourite not found");
        favorites.delete(fav);
    }
}
