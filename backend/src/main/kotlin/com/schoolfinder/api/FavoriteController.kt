package com.schoolfinder.api

import com.schoolfinder.security.CurrentUser
import com.schoolfinder.service.FavoriteService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/favorites")
class FavoriteController(private val favorites: FavoriteService) {

    @GetMapping
    fun list(): List<SchoolSummary> = favorites.list(CurrentUser.require())

    @PostMapping("/{schoolId}")
    @ResponseStatus(HttpStatus.CREATED)
    fun add(@PathVariable schoolId: Long) = favorites.add(CurrentUser.require(), schoolId)

    @DeleteMapping("/{schoolId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remove(@PathVariable schoolId: Long) = favorites.remove(CurrentUser.require(), schoolId)
}
