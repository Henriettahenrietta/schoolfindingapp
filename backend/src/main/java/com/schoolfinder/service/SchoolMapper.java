package com.schoolfinder.service;

import com.schoolfinder.api.Dtos.ImageDto;
import com.schoolfinder.api.Dtos.ProgramDto;
import com.schoolfinder.api.Dtos.SchoolDetail;
import com.schoolfinder.api.Dtos.SchoolSummary;
import com.schoolfinder.domain.School;
import java.util.List;

public final class SchoolMapper {

    private SchoolMapper() {
    }

    public static SchoolSummary toSummary(School s, Rating rating) {
        return new SchoolSummary(
            s.getId(),
            s.getName(),
            s.getCategory(),
            s.getCity(),
            s.getRegion(),
            s.getTuitionFee(),
            s.getCurrency(),
            s.getCoverImageUrl(),
            s.getLatitude(),
            s.getLongitude(),
            rating.average(),
            rating.count()
        );
    }

    public static SchoolDetail toDetail(School s, Rating rating, boolean isFavorite) {
        List<ProgramDto> programs = s.getPrograms().stream()
            .map(p -> new ProgramDto(p.getId(), p.getName(), p.getFaculty(), p.getLevel(), p.getDurationMonths(), p.getTuitionFee()))
            .toList();
        List<ImageDto> images = s.getImages().stream()
            .map(i -> new ImageDto(i.getId(), i.getUrl(), i.getCaption()))
            .toList();
        return new SchoolDetail(
            s.getId(),
            s.getName(),
            s.getCategory(),
            s.getDescription(),
            s.getHistory(),
            s.getCity(),
            s.getRegion(),
            s.getAddress(),
            s.getLatitude(),
            s.getLongitude(),
            s.getTuitionFee(),
            s.getCurrency(),
            s.getWebsite(),
            s.getPhone(),
            s.getEmail(),
            s.getCoverImageUrl(),
            rating.average(),
            rating.count(),
            isFavorite,
            programs,
            images
        );
    }
}
