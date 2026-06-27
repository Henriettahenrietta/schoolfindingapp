package com.schoolfinder.api;

import com.schoolfinder.api.Dtos.MetaResponse;
import com.schoolfinder.config.AppProperties;
import com.schoolfinder.domain.SchoolCategory;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/meta")
public class MetaController {

    private final AppProperties props;

    public MetaController(AppProperties props) {
        this.props = props;
    }

    @GetMapping
    public MetaResponse meta() {
        List<String> categories = Arrays.stream(SchoolCategory.values()).map(Enum::name).toList();
        return new MetaResponse(
            "UniMatch Cameroon",
            "Find Your Future University",
            props.getRegion().getCountry(),
            props.getRegion().getCurrency(),
            props.getRegion().getMapCenterLat(),
            props.getRegion().getMapCenterLng(),
            categories,
            props.getFirebase().isEnabled()
        );
    }
}
