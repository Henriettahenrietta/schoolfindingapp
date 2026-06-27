package com.schoolfinder.api;

import com.schoolfinder.api.Dtos.SchoolSummary;
import com.schoolfinder.security.CurrentUser;
import com.schoolfinder.service.FavoriteService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/favorites")
public class FavoriteController {

    private final FavoriteService favorites;

    public FavoriteController(FavoriteService favorites) {
        this.favorites = favorites;
    }

    @GetMapping
    public List<SchoolSummary> list() {
        return favorites.list(CurrentUser.require());
    }

    @PostMapping("/{schoolId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@PathVariable Long schoolId) {
        favorites.add(CurrentUser.require(), schoolId);
    }

    @DeleteMapping("/{schoolId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long schoolId) {
        favorites.remove(CurrentUser.require(), schoolId);
    }
}
