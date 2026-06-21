package edu.rutmiit.demo.demorest.graphql.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.rutmiit.demo.demorest.graphql.types.CreateRouteGql;
import edu.rutmiit.demo.demorest.graphql.types.PageInfoGql;
import edu.rutmiit.demo.demorest.graphql.types.RoutePageGql;
import edu.rutmiit.demo.demorest.service.RouteService;
import edu.rutmiit.demo.way_finder_contract.dto.RouteRequest;
import edu.rutmiit.demo.way_finder_contract.dto.RouteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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


    @DgsMutation
    public RouteResponse createRoute(@InputArgument CreateRouteGql input) {
        System.out.println("Дошло до метода");
        List<RouteRequest.HaltInRoute> halts = new ArrayList<>();
        if (input.halts() != null && !input.halts().isEmpty()) {
            try {
                halts = input.halts().stream().map(
                        r -> RouteRequest.HaltInRoute
                                .builder()
                                .cityId(r.cityId())
                                .date(r.date())
                                .build()
                ).collect(Collectors.toList());
                System.out.println("Создали список");
            } catch (Exception e) {
                System.out.println("Ошибка при созданиии списка " + e);
            }
        }

        RouteRequest request = RouteRequest.builder()
                .typeDistance(input.typeDistance())
                .typeTransport(input.typeTransport())
                .halts(halts)
                .build();
        System.out.println("Создали дто");
        RouteResponse response = routeService.create(request);
        System.out.println("Вернулся ответ " + response.getId());
        return response;
    }
}
