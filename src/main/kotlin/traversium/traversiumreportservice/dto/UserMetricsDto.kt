package traversium.traversiumreportservice.dto

import java.time.OffsetDateTime

data class UserMetricsDto(
    val tenantId: String,
    val totalUsers: Long,
    val activeUsers: Long,
    val newUsersThisMonth: Long,
    val newUsersInPeriod: Long,
    val lastUpdated: OffsetDateTime,
    val metrics: List<UserMetricPoint>
)

data class UserMetricPoint(
    val date: OffsetDateTime,
    val totalUsers: Long,
    val activeUsers: Long,
    val newUsers: Long
)

