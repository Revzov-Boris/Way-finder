package edu.rutmiit.demo.grpcanalytics.service;

import edu.rutmiit.demo.grpc.AnalyzeRouteRequest;
import edu.rutmiit.demo.grpc.RouteAnalysisResponse;
import edu.rutmiit.demo.grpc.RouteAnalyticsGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * Оценка времени чтения на основе жанра.
     * В реальном приложении — ML-модель или API внешнего сервиса.
     */
    private int estimateWayTime(AnalyzeRouteRequest request) {
        int base = switch (request.getTypeDistance() != null ? request.getTypeDistance() : "") {
            case "пригородный"       ->  60;
            case "междугородний"     -> 130;
            case "международный"     -> 500;
            default                  ->  90;
        };

        return base;
    }


    /**
     * Рекомендательный балл (0.0—10.0).
     * Демонстрационная формула: классика получает высокий балл.
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
        if (request.getTypeTransport().equals("автобус")) return 10;
        if (request.getTypeTransport().equals("поезд")) return 2;
        if (request.getTypeTransport().equals("самолёт")) return 6;
        if (request.getTypeTransport().equals("корабль")) return 8;
        return 5;
    }
}
