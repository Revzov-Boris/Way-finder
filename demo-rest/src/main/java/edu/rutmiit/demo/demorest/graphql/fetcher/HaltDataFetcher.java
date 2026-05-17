package edu.rutmiit.demo.demorest.graphql.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.rutmiit.demo.demorest.graphql.types.PageInfoGql;
import edu.rutmiit.demo.demorest.graphql.types.HaltPageGql;
import edu.rutmiit.demo.demorest.service.HaltService;
import edu.rutmiit.demo.way_finder_contract.dto.HaltResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DgsComponent
public class HaltDataFetcher {
    private final HaltService haltService;

    public HaltDataFetcher(HaltService haltService) {
        this.haltService = haltService;
    }

    @DgsQuery
    public HaltResponse halt(@InputArgument String id) {
        return haltService.findById(Long.parseLong(id));
    }


    @DgsQuery
    public HaltPageGql halts(
            @InputArgument Integer page,
            @InputArgument Integer size) {
        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 10;
        Page<HaltResponse> paged = haltService.findAll(PageRequest.of(pageNum, pageSize));
        return new HaltPageGql(
                paged.getContent(),
                new PageInfoGql(paged.getPageable().getPageNumber(), paged.getPageable().getPageSize(), paged.getTotalPages(), paged.isLast()),
                (int) paged.getTotalElements()
        );
    }
}
