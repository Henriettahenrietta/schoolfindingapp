package com.schoolfinder.api;

import com.schoolfinder.api.Dtos.MeDto;
import com.schoolfinder.api.Dtos.ReviewDto;
import com.schoolfinder.domain.AppUser;
import com.schoolfinder.repository.AppUserRepository;
import com.schoolfinder.security.CurrentUser;
import com.schoolfinder.service.ReviewService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me")
public class MeController {

    private final AppUserRepository users;
    private final ReviewService reviewService;

    public MeController(AppUserRepository users, ReviewService reviewService) {
        this.users = users;
        this.reviewService = reviewService;
    }

    @GetMapping
    public MeDto me() {
        CurrentUser current = CurrentUser.require();
        AppUser user = users.findById(current.id()).orElseThrow(() -> new NotFoundException("User not found"));
        return new MeDto(user.getId(), user.getFirebaseUid(), user.getEmail(), user.getDisplayName(), user.getRole());
    }

    @GetMapping("/reviews")
    public List<ReviewDto> myReviews() {
        return reviewService.myReviews(CurrentUser.require());
    }

    @DeleteMapping("/reviews/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteOwn(id, CurrentUser.require());
    }
}
