package com.schoolfinder.api

import com.schoolfinder.config.AppProperties
import com.schoolfinder.domain.SchoolCategory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/meta")
class MetaController(private val props: AppProperties) {

    @GetMapping
    fun meta() = MetaResponse(
        appName = "UniMatch Cameroon",
        tagline = "Find Your Future University",
        country = props.region.country,
        currency = props.region.currency,
        mapCenterLat = props.region.mapCenterLat,
        mapCenterLng = props.region.mapCenterLng,
        categories = SchoolCategory.entries.map { it.name },
        firebaseEnabled = props.firebase.enabled,
    )
}
