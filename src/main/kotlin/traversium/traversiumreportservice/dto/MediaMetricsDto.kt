package traversium.traversiumreportservice.dto

import java.time.OffsetDateTime

data class MediaMetricsDto(
    val tenantId: String,
    val totalMedia: Long,
    val mediaThisMonth: Long,
    val mediaInPeriod: Long,
    val totalStorageGB: Double,
    val lastUpdated: OffsetDateTime,
    val metrics: List<MediaMetricPoint>
)

data class MediaMetricPoint(
    val date: OffsetDateTime,
    val totalMedia: Long,
    val newMedia: Long,
    val storageGB: Double
)

