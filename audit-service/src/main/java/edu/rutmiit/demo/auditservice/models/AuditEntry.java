package edu.rutmiit.demo.auditservice.models;

import java.time.Instant;

public record AuditEntry(

        // Порядковый номер записи в журнале (для сортировки и пагинации)
        long sequenceNumber,

        // Идентификатор события из EventMetadata — связь с исходным сообщением
        String eventId,

        // Тип события: "book.created", "author.deleted" и т.д.
        String eventType,

        // Сервис-источник события
        String source,

        // Момент создания события (на стороне publisher)
        Instant eventTimestamp,

        // Момент получения события audit-service (разница показывает задержку доставки)
        Instant receivedAt,

        // Человекочитаемое описание: "Создана книга «Война и мир» (ISBN: 978-...)"
        String description
) {}

