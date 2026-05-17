package edu.rutmiit.demo.demorest.graphql.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import edu.rutmiit.demo.demorest.service.RouteService;
import edu.rutmiit.demo.way_finder_contract.dto.RouteResponse;
import edu.rutmiit.demo.way_finder_contract.dto.HaltResponse;

@DgsComponent
public class HaltRouteDataFetcher {
    private final RouteService routeService;

    public HaltRouteDataFetcher(RouteService routeService) {
        this.routeService = routeService;
    }

    @DgsData(parentType = "Halt", field = "route")
    public RouteResponse route(DgsDataFetchingEnvironment dfe) {
        HaltResponse halt = dfe.getSource();

        if (halt.getRouteId() != null) {
            return routeService.findById(halt.getRouteId());
        }
        return null;
    }
}

