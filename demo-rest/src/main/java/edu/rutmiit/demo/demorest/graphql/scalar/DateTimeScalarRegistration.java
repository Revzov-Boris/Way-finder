package edu.rutmiit.demo.demorest.graphql.scalar;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsRuntimeWiring;
import graphql.language.StringValue;
import graphql.scalars.ExtendedScalars;
import graphql.schema.*;
import graphql.schema.idl.RuntimeWiring;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Регистрация пользовательских скалярных типов для GraphQL.
 *
 * В GraphQL по умолчанию есть только пять скалярных типов:
 * String, Int, Float, Boolean, ID. Для дат и времени нужно регистрировать
 * дополнительные скаляры из библиотеки graphql-java-extended-scalars.
 *
 * DateTime маппится на java.time.OffsetDateTime / LocalDateTime.
 * Date маппится на java.time.LocalDate.
 *
 * Аннотация @DgsRuntimeWiring позволяет программно настроить runtime wiring —
 * механизм GraphQL-Java для связывания схемы с реализацией.
 */
@DgsComponent
public class DateTimeScalarRegistration {

    /**
     * Регистрируем скаляры DateTime и Date в runtime wiring.
     *
     * ExtendedScalars — это коллекция готовых скаляров от graphql-java.
     * Они уже содержат логику сериализации/десериализации, нам достаточно
     * лишь зарегистрировать их.
     */

    // Создаем кастомный скаляр для LocalDateTime
    public static final GraphQLScalarType LOCAL_DATE_TIME_SCALAR = GraphQLScalarType.newScalar()
            .name("LocalDateTime")
            .description("Кастомный скаляр для java.time.LocalDateTime")
            .coercing(new Coercing<LocalDateTime, String>() {
                @Override
                public String serialize(Object dataFetcherResult) {
                    if (dataFetcherResult instanceof LocalDateTime) {
                        return ((LocalDateTime) dataFetcherResult).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    }
                    throw new CoercingSerializeException("Невозможно сериализовать " + dataFetcherResult + " в LocalDateTime");
                }

                @Override
                public LocalDateTime parseValue(Object input) {
                    try {
                        return LocalDateTime.parse(input.toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    } catch (Exception e) {
                        throw new CoercingParseValueException("Ошибка парсинга значения в LocalDateTime: " + input);
                    }
                }

                @Override
                public LocalDateTime parseLiteral(Object input) {
                    if (input instanceof StringValue) {
                        try {
                            return LocalDateTime.parse(((StringValue) input).getValue(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        } catch (Exception e) {
                            throw new CoercingParseLiteralException("Ошибка парсинга литерала в LocalDateTime");
                        }
                    }
                    throw new CoercingParseLiteralException("Ожидалась строка для LocalDateTime");
                }
            })
            .build();
    @DgsRuntimeWiring
    public RuntimeWiring.Builder addScalars(RuntimeWiring.Builder builder) {
        return builder
                .scalar(ExtendedScalars.DateTime)
                .scalar(LOCAL_DATE_TIME_SCALAR)
                .scalar(ExtendedScalars.Date);
    }
}
