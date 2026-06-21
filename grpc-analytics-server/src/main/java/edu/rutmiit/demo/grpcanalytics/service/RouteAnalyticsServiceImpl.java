package edu.rutmiit.demo.grpcanalytics.service;

import edu.rutmiit.demo.grpc.AnalyzeRouteRequest;
import edu.rutmiit.demo.grpc.HaltInfo;
import edu.rutmiit.demo.grpc.RouteAnalysisResponse;
import edu.rutmiit.demo.grpc.RouteAnalyticsGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


public class RouteAnalyticsServiceImpl extends RouteAnalyticsGrpc.RouteAnalyticsImplBase {
    private static final Logger log = LoggerFactory.getLogger(RouteAnalyticsServiceImpl.class);

    /**
     * Обрабатывает запрос на анализ маршрута.
     *
     * Паттерн gRPC: метод получает request и StreamObserver для ответа.
     * StreamObserver — это callback-интерфейс:
     *   - onNext(response) — отправить ответ (для unary RPC вызывается один раз)
     *   - onCompleted()    — завершить RPC
     *   - onError(t)       — сообщить об ошибке
     *
     * Для unary RPC (один запрос → один ответ) всегда:
     *   responseObserver.onNext(response);
     *   responseObserver.onCompleted();
     */
    @Override
    public void analyzeRoute(AnalyzeRouteRequest request,
                            StreamObserver<RouteAnalysisResponse> responseObserver) {

        log.info("gRPC запрос: анализ маршрута id={}",
                request.getRouteId());

        // ─── Вычисление метрик (демонстрационная логика) ─────────────
        int time = estimateWayTime(request);
        double chanceOfCancellation = calculateChanceOfCancellation(request);
        int difficultyLevel = difLevel(request);

        // ─── Формируем ответ ─────────────────────────────────────────
        RouteAnalysisResponse response = RouteAnalysisResponse.newBuilder()
                .setRouteId(request.getRouteId())
                .setChanceOfCancellation(chanceOfCancellation)
                .setDifficultyLevel(difficultyLevel)
                .setTimeInWay(time)
                .build();

        log.info("gRPC ответ: маршрут id={}, время в пути = {}мин, шанс отмены = {}",
                response.getRouteId(), time, difficultyLevel, chanceOfCancellation);

        // Отправляем ответ клиенту и завершаем RPC
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // ─── Демонстрационная бизнес-логика ──────────────────────────────

    /**
     * Вычисление времени в пути.
     */
    private int estimateWayTime(AnalyzeRouteRequest request) {
        int minutes = 90;
        if (request.getHaltsList().isEmpty() || request.getHaltsList().size() < 2) {
            minutes = switch (request.getTypeDistance() != null ? request.getTypeDistance() : "") {
                case "пригородный"       ->  60;
                case "междугородний"     -> 130;
                case "международный"     -> 500;
                default                  ->  90;
            };
        } else {
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            List<HaltInfo> sortHalt = new ArrayList<>(request.getHaltsList());
            sortHalt.sort((h1, h2) -> {
                LocalDateTime t1 = LocalDateTime.parse(h1.getDate(), dateFormat);
                LocalDateTime t2 = LocalDateTime.parse(h2.getDate(), dateFormat);
                return t1.compareTo(t2);
            });
            LocalDateTime t1 = LocalDateTime.parse(sortHalt.getFirst().getDate(), dateFormat);
            LocalDateTime t2 = LocalDateTime.parse(sortHalt.getLast().getDate(), dateFormat);
            int secunds = (int) t1.until(t2, ChronoUnit.SECONDS);
            minutes = secunds / 60;
        }
        return minutes;
    }


    /**
     * Уровень сложности маршрута балл (0—10).
     */
    private double calculateChanceOfCancellation(AnalyzeRouteRequest request) {
        double change = switch (request.getTypeDistance() != null ? request.getTypeDistance() : "") {
            case "пригородный"       -> 0.05;
            case "междугородний"     -> 0.04;
            case "международный"     -> 0.01;
            default                  -> 0.02;
        };
        return change;
    }

    /**
     * Классификация эпохи по году публикации.
     */
    private int difLevel(AnalyzeRouteRequest request) {
        if (request.getHaltsList().isEmpty() || request.getHaltsList().size() < 2) {
            if (request.getTypeTransport().toLowerCase().contains("автобус")) return 9;
            if (request.getTypeTransport().toLowerCase().contains("поезд")) return 2;
            if (request.getTypeTransport().toLowerCase().contains("самолёт")) return 6;
            if (request.getTypeTransport().toLowerCase().contains("корабль")) return 10;
            return 5;
        } else {
            double level;
            int minutes = estimateWayTime(request);
            if (minutes <= 20) {
                level = 0;
            } else if (minutes <= 60) {
                level = 2;
            } else if (minutes < 120) {
                level = 3;
            } else if (minutes < 360) {
                level = 5;
            } else if (minutes < 720) {
                level = 7;
            } else {
                level = 8;
            }
            if (request.getTypeTransport().toLowerCase().contains("автобус")) {
                level *= 2;
            }
            else if (request.getTypeTransport().toLowerCase().contains("поезд")) {
                level *= 1;
            }
            if (request.getTypeTransport().toLowerCase().contains("самолёт")) {
                level += 0.5;
                level *= 1.5;
            }
            if (request.getTypeTransport().toLowerCase().contains("корабль")) {
                level *= 2;
            }
            int intLevel = (int) Math.round(level);
            if (intLevel > 10)
                intLevel = 10;
            return intLevel;
        }
    }
}
