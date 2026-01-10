package traversium.traversiumreportservice.dto

import java.time.OffsetDateTime

data class SocialMetricsDto(
    val tenantId: String,
    val totalLikes: Long,
    val totalComments: Long,
    val totalInteractions: Long,
    val interactionsThisMonth: Long,
    val interactionsInPeriod: Long,
    val lastUpdated: OffsetDateTime,
    val metrics: List<SocialMetricPoint>
)

data class SocialMetricPoint(
    val date: OffsetDateTime,
    val likes: Long,
    val comments: Long,
    val totalInteractions: Long
)

