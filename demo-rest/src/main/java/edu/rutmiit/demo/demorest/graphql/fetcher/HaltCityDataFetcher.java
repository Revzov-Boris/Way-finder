package edu.rutmiit.demo.demorest.graphql.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import edu.rutmiit.demo.demorest.service.CityService;
import edu.rutmiit.demo.way_finder_contract.dto.CityResponse;
import edu.rutmiit.demo.way_finder_contract.dto.HaltResponse;

@DgsComponent
public class HaltCityDataFetcher {
    private final CityService cityService;

    public HaltCityDataFetcher(CityService cityService) {
        this.cityService = cityService;
    }

    @DgsData(parentType = "Halt", field = "city")
    public CityResponse city(DgsDataFetchingEnvironment dfe) {
        HaltResponse halt = dfe.getSource();

        if (halt.getCityId() != null) {
            return cityService.findById(halt.getCityId());
        }
        return null;
    }
}
