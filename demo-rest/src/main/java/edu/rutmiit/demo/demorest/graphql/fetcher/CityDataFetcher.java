package edu.rutmiit.demo.demorest.graphql.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.rutmiit.demo.demorest.graphql.types.CityPageGql;
import edu.rutmiit.demo.demorest.graphql.types.CreateCityGql;
import edu.rutmiit.demo.demorest.graphql.types.PageInfoGql;
import edu.rutmiit.demo.demorest.service.CityService;
import edu.rutmiit.demo.way_finder_contract.dto.CityRequest;
import edu.rutmiit.demo.way_finder_contract.dto.CityResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DgsComponent
public class CityDataFetcher {
    private final CityService cityService;

    public CityDataFetcher(CityService cityService) {
        this.cityService = cityService;
    }

    @DgsQuery
    public CityResponse city(@InputArgument String id) {
        return cityService.findById(Integer.parseInt(id));
    }


    @DgsQuery
    public CityPageGql cities(
            @InputArgument Integer page,
            @InputArgument Integer size) {
        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 10;
        Page<CityResponse> paged = cityService.findAll(PageRequest.of(pageNum, pageSize));
        return new CityPageGql(
                paged.getContent(),
                new PageInfoGql(paged.getPageable().getPageNumber(), paged.getPageable().getPageSize(), paged.getTotalPages(), paged.isLast()),
                (int) paged.getTotalElements()
        );
    }

    @DgsMutation
    public CityResponse createCity(@InputArgument CreateCityGql input) {
        CityRequest request = CityRequest.builder()
                .address(input.address())
                .name(input.name())
                .timeZone(input.timeZone())
                .build();
        return cityService.create(request);
    }

}
