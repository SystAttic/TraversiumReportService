package traversium.traversiumreportservice.dto

import java.time.OffsetDateTime

data class TenantReportDto(
    val tenantId: String,
    val totalUsers: Long,
    val activeUsers: Long,
    val totalTrips: Long,
    val totalStorageGB: Double,
    val totalApiCalls: Long,
    val monthlyCost: Double,
    val totalCost: Double,
    val lastUpdated: OffsetDateTime,
    val metrics: List<MetricPoint>
)

data class MetricPoint(
    val date: OffsetDateTime,
    val users: Long,
    val trips: Long,
    val storageGB: Double,
    val cost: Double
)

