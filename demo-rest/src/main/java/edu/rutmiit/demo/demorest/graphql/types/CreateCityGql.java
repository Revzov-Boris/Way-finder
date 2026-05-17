package edu.rutmiit.demo.demorest.graphql.types;

public record CreateCityGql (
        String name,
        String address,
        Integer timeZone
) {
}
