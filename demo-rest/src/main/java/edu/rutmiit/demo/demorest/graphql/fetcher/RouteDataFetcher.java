package edu.rutmiit.demo.demorest.graphql.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.rutmiit.demo.demorest.graphql.types.PageInfoGql;
import edu.rutmiit.demo.demorest.graphql.types.RoutePageGql;
import edu.rutmiit.demo.demorest.service.RouteService;
import edu.rutmiit.demo.way_finder_contract.dto.RouteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DgsComponent
public class RouteDataFetcher {
    private final RouteService routeService;

    public RouteDataFetcher(RouteService routeService) {
        this.routeService = routeService;
    }

    @DgsQuery
    public RouteResponse route(@InputArgument String id) {
        return routeService.findById(Integer.parseInt(id));
    }


    @DgsQuery
    public RoutePageGql routes(
            @InputArgument Integer page,
            @InputArgument Integer size) {
        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 10;
        Page<RouteResponse> paged = routeService.findAll(PageRequest.of(pageNum, pageSize));
        return new RoutePageGql(
                paged.getContent(),
                new PageInfoGql(paged.getPageable().getPageNumber(), paged.getPageable().getPageSize(), paged.getTotalPages(), paged.isLast()),
                (int) paged.getTotalElements()
        );
    }
}
