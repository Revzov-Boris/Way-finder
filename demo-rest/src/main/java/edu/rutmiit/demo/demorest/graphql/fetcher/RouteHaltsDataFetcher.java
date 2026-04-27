package edu.rutmiit.demo.demorest.graphql.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.InputArgument;
import edu.rutmiit.demo.demorest.graphql.types.HaltPageGql;
import edu.rutmiit.demo.demorest.graphql.types.PageInfoGql;
import edu.rutmiit.demo.demorest.service.HaltService;
import edu.rutmiit.demo.way_finder_contract.dto.HaltResponse;
import edu.rutmiit.demo.way_finder_contract.dto.RouteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DgsComponent
public class RouteHaltsDataFetcher {
    private final HaltService haltService;

    public RouteHaltsDataFetcher(HaltService haltService) {
        this.haltService = haltService;
    }


    @DgsData(parentType = "Route", field = "halts")
    public HaltPageGql hatls(
            DgsDataFetchingEnvironment dfe,
            @InputArgument Integer page,
            @InputArgument Integer size) {
        RouteResponse route = dfe.getSource();

        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 10;

        Page<HaltResponse> paged = haltService.findAllByRoute(PageRequest.of(pageNum, pageSize), route.getId());
        return new HaltPageGql(
                paged.getContent(),
                new PageInfoGql(paged.getPageable().getPageNumber(), paged.getPageable().getPageSize(), paged.getTotalPages(), paged.isLast()),
                (int) paged.getTotalElements()
        );
    }
}
