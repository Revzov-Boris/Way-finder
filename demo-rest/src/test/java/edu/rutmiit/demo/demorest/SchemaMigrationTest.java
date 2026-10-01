package edu.rutmiit.demo.demorest;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.sql.DriverManager;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
class SchemaMigrationTest {

    @Container
    static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer("postgres:17.6-alpine");

    @Test
    void laterMigrationBackfillsRowsCreatedByTheEarlierSchema() throws Exception {
        // Применяем миграции по V2
        Flyway upToVersionTwo = Flyway.configure()
                .dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
                .locations("classpath:db/migration")
                .target("2")
                .load();

        assertThat(upToVersionTwo.migrate().migrationsExecuted).isEqualTo(2);

        // Вставляем города без created_at
        try (var connection = DriverManager.getConnection(
                POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())) {

            try (var stmt = connection.prepareStatement(
                    "insert into cities(name, address, time_zone, count_citizen) values (?, ?, ?, ?)")) {
                stmt.setString(1, "Москва");
                stmt.setString(2, "РФ, город федерального значения Москва");
                stmt.setInt(3, 3);
                stmt.setInt(4, 13_000_000);
                stmt.executeUpdate();
            }

            try (var stmt = connection.prepareStatement(
                    "insert into cities(name, address, time_zone) values (?, ?, ?)")) {
                stmt.setString(1, "Казань");
                stmt.setString(2, "РФ, республика Татарстан, г. Казань");
                stmt.setInt(3, 3);
                stmt.executeUpdate();
            }
        }

        // Применяем V3
        Flyway upToVersionThree = Flyway.configure()
                .dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
                .locations("classpath:db/migration")
                .target("3")
                .load();
        assertThat(upToVersionThree.migrate().migrationsExecuted).isOne();

        // Проверка
        try (var connection = DriverManager.getConnection(
                POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())) {

            try (var stmt = connection.prepareStatement(
                    "select name, count_citizen, created_at from cities where address = ?")) {

                stmt.setString(1, "РФ, город федерального значения Москва");
                try (var rs = stmt.executeQuery()) {
                    assertThat(rs.next()).isTrue();
                    assertThat(rs.getString("name")).isEqualTo("Москва");
                    assertThat(rs.getInt("count_citizen")).isEqualTo(13_000_000);
                    assertThat(rs.getObject("created_at")).isNotNull();
                }

                stmt.setString(1, "РФ, республика Татарстан, г. Казань");
                try (var rs = stmt.executeQuery()) {
                    assertThat(rs.next()).isTrue();
                    assertThat(rs.getString("name")).isEqualTo("Казань");
                    assertThat(rs.getObject("count_citizen")).isNull();
                    assertThat(rs.getObject("created_at")).isNotNull();
                }
            }
        }
    }
}