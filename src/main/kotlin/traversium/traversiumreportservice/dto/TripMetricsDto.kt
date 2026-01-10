package traversium.traversiumreportservice.dto

import java.time.OffsetDateTime

data class TripMetricsDto(
    val tenantId: String,
    val totalTrips: Long,
    val tripsThisMonth: Long,
    val tripsInPeriod: Long,
    val lastUpdated: OffsetDateTime,
    val metrics: List<TripMetricPoint>
)

data class TripMetricPoint(
    val date: OffsetDateTime,
    val totalTrips: Long,
    val newTrips: Long
)

