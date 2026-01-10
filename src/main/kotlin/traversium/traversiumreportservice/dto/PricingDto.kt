package traversium.traversiumreportservice.dto

import java.time.OffsetDateTime

data class PricingDto(
    val tenantId: String,
    val baseCost: Double,
    val costPerUser: Double,
    val costPerGB: Double,
    val costPer1000ApiCalls: Double,
    val currentMonthlyCost: Double,
    val totalCost: Double,
    val costBreakdown: CostBreakdown,
    val lastUpdated: OffsetDateTime
)

data class CostBreakdown(
    val baseCost: Double,
    val userCost: Double,
    val storageCost: Double,
    val apiCost: Double,
    val totalUsers: Long,
    val totalStorageGB: Double,
    val totalApiCalls: Long
)

