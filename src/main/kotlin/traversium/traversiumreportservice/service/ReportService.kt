package traversium.traversiumreportservice.service

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import traversium.commonmultitenancy.TenantContext
import traversium.traversiumreportservice.db.model.TenantMetrics
import traversium.traversiumreportservice.db.repository.TenantMetricsRepository
import traversium.traversiumreportservice.dto.MetricPoint
import traversium.traversiumreportservice.dto.TenantReportDto
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

@Service
class ReportService(
    private val metricsRepository: TenantMetricsRepository,
    private val userMetricsService: UserMetricsService,
    private val auditDataService: AuditDataService
) {
    private val logger = LoggerFactory.getLogger(ReportService::class.java)

    // Cost calculation constants (per unit per month)
    private val COST_PER_USER = 5.0 // $5 per user per month
    private val COST_PER_GB_STORAGE = 0.10 // $0.10 per GB per month
    private val COST_PER_1000_API_CALLS = 1.0 // $1 per 1000 API calls
    private val BASE_COST = 50.0 // $50 base cost per tenant per month

    fun getTenantReport(tenantId: String, days: Int = 30): TenantReportDto {
        val currentTenant = TenantContext.getTenant()
        
        // Set tenant context for this operation
        TenantContext.setTenant(tenantId)
        
        try {
            val endDate = OffsetDateTime.now()
            val startDate = endDate.minusDays(days.toLong())
            
            // Get latest metrics
            val latestMetrics = metricsRepository.findTopByTenantIdOrderByMetricDateDesc(tenantId)
                ?: calculateAndSaveCurrentMetrics(tenantId)
            
            // Get historical metrics for the period
            val historicalMetrics = metricsRepository.findByTenantIdAndMetricDateBetween(
                tenantId, startDate, endDate
            )
            
            // Calculate total cost for the period
            val totalCost = metricsRepository.getTotalCostForPeriod(tenantId, startDate) ?: 0.0
            
            // Get current user count
            val currentUserCount = userMetricsService.getTotalUsers(tenantId)
            val activeUserCount = userMetricsService.getActiveUsers(tenantId, 30)
            
            // Build metric points for graphs
            val metricPoints = historicalMetrics.map { metric ->
                MetricPoint(
                    date = metric.metricDate,
                    users = metric.totalUsers,
                    trips = metric.totalTrips,
                    storageGB = metric.totalStorageBytes / (1024.0 * 1024.0 * 1024.0),
                    cost = metric.calculatedCost
                )
            }
            
            // Add current metrics point if not in historical data
            val finalMetricPoints = if (latestMetrics != null && (metricPoints.isEmpty() || 
                metricPoints.lastOrNull()?.date?.isBefore(latestMetrics.metricDate.minus(1, ChronoUnit.DAYS)) == true)) {
                metricPoints + MetricPoint(
                    date = latestMetrics.metricDate,
                    users = latestMetrics.totalUsers,
                    trips = latestMetrics.totalTrips,
                    storageGB = latestMetrics.totalStorageBytes / (1024.0 * 1024.0 * 1024.0),
                    cost = latestMetrics.calculatedCost
                )
            } else {
                metricPoints
            }
            
            return TenantReportDto(
                tenantId = tenantId,
                totalUsers = currentUserCount,
                activeUsers = activeUserCount,
                totalTrips = latestMetrics?.totalTrips ?: 0L,
                totalStorageGB = latestMetrics?.let { it.totalStorageBytes / (1024.0 * 1024.0 * 1024.0) } ?: 0.0,
                totalApiCalls = latestMetrics?.totalApiCalls ?: 0L,
                monthlyCost = latestMetrics?.calculatedCost ?: 0.0,
                totalCost = totalCost,
                lastUpdated = latestMetrics?.metricDate ?: OffsetDateTime.now(),
                metrics = finalMetricPoints.sortedBy { it.date }
            )
        } finally {
            // Restore original tenant context
            TenantContext.setTenant(currentTenant)
        }
    }

    /**
     * Calculate and save current metrics for a tenant
     * Aggregates data from audit service
     */
    @Transactional
    fun calculateAndSaveCurrentMetrics(tenantId: String): TenantMetrics {
        logger.info("=== Starting calculateAndSaveCurrentMetrics() for tenant: $tenantId ===")
        
        val currentTenant = TenantContext.getTenant()
        TenantContext.setTenant(tenantId)
        
        try {
            logger.info("Step 1: Getting total users...")
            val totalUsers = userMetricsService.getTotalUsers(tenantId)
            logger.info("Step 1 SUCCESS: Total users = $totalUsers")
            
            logger.info("Step 2: Getting active users (last 30 days)...")
            val activeUsers = userMetricsService.getActiveUsers(tenantId, 30)
            logger.info("Step 2 SUCCESS: Active users = $activeUsers")
            
            logger.info("Step 3: Getting total trips...")
            val totalTrips = userMetricsService.getTotalTrips(tenantId)
            logger.info("Step 3 SUCCESS: Total trips = $totalTrips")
            
            logger.info("Step 4: Getting total storage bytes...")
            val totalStorageBytes = userMetricsService.getTotalStorageBytes(tenantId)
            logger.info("Step 4 SUCCESS: Total storage bytes = $totalStorageBytes")
            
            logger.info("Step 5: Getting total API calls...")
            val totalApiCalls = userMetricsService.getTotalApiCalls(tenantId)
            logger.info("Step 5 SUCCESS: Total API calls = $totalApiCalls")
            
            logger.info("Step 6: Calculating cost...")
            val cost = calculateCost(totalUsers, totalStorageBytes, totalApiCalls)
            logger.info("Step 6 SUCCESS: Calculated cost = $$cost")
            
            logger.info("Step 7: Saving metrics...")
            val metrics = TenantMetrics(
                tenantId = tenantId,
                metricDate = OffsetDateTime.now(),
                totalUsers = totalUsers,
                activeUsers = activeUsers,
                totalTrips = totalTrips,
                totalStorageBytes = totalStorageBytes,
                totalApiCalls = totalApiCalls,
                calculatedCost = cost
            )
            
            val saved = metricsRepository.save(metrics)
            logger.info("Step 7 SUCCESS: Metrics saved with id=${saved.id}")
            logger.info("=== calculateAndSaveCurrentMetrics() COMPLETED SUCCESSFULLY ===")
            
            return saved
        } catch (e: Exception) {
            logger.error("=== calculateAndSaveCurrentMetrics() FAILED ===", e)
            throw e
        } finally {
            TenantContext.setTenant(currentTenant)
        }
    }
    
    /**
     * Scheduled job to calculate and save daily metrics for all tenants
     * Runs once per day at midnight
     */
    @Scheduled(cron = "0 0 0 * * ?") // Run at midnight every day
    fun calculateDailyMetricsForAllTenants() {
        logger.info("=== Starting daily metrics calculation job ===")
        // TODO: Get list of all tenants and calculate metrics for each
        // For now, this is a placeholder
        logger.info("Daily metrics calculation job completed")
    }

    private fun calculateCost(users: Long, storageBytes: Long, apiCalls: Long): Double {
        val storageGB = storageBytes / (1024.0 * 1024.0 * 1024.0)
        val apiCallsInThousands = apiCalls / 1000.0
        
        return BASE_COST +
            (users * COST_PER_USER) +
            (storageGB * COST_PER_GB_STORAGE) +
            (apiCallsInThousands * COST_PER_1000_API_CALLS)
    }
    
    /**
     * Get user metrics with time series data
     */
    fun getUserMetrics(tenantId: String, days: Int = 30): traversium.traversiumreportservice.dto.UserMetricsDto {
        logger.info("=== Starting getUserMetrics() for tenant: $tenantId, days: $days ===")
        
        val currentTenant = TenantContext.getTenant()
        TenantContext.setTenant(tenantId)
        
        try {
            val endDate = OffsetDateTime.now()
            val startDate = endDate.minusDays(days.toLong())
            val monthStart = endDate.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
            
            val totalUsers = auditDataService.getTotalUsersCreated(tenantId)
            val activeUsers = auditDataService.getActiveUsers(tenantId, 30)
            val newUsersThisMonth = auditDataService.getNewUsersInPeriod(tenantId, monthStart, endDate)
            val newUsersInPeriod = auditDataService.getNewUsersInPeriod(tenantId, startDate, endDate)
            
            // Get historical metrics
            val historicalMetrics = metricsRepository.findByTenantIdAndMetricDateBetween(tenantId, startDate, endDate)
            val metricPoints = historicalMetrics.map { metric ->
                traversium.traversiumreportservice.dto.UserMetricPoint(
                    date = metric.metricDate,
                    totalUsers = metric.totalUsers,
                    activeUsers = metric.activeUsers,
                    newUsers = 0L // Would need to calculate from daily deltas
                )
            }
            
            val latestMetrics = metricsRepository.findTopByTenantIdOrderByMetricDateDesc(tenantId)
            
            return traversium.traversiumreportservice.dto.UserMetricsDto(
                tenantId = tenantId,
                totalUsers = totalUsers,
                activeUsers = activeUsers,
                newUsersThisMonth = newUsersThisMonth,
                newUsersInPeriod = newUsersInPeriod,
                lastUpdated = latestMetrics?.metricDate ?: OffsetDateTime.now(),
                metrics = metricPoints.sortedBy { it.date }
            )
        } finally {
            TenantContext.setTenant(currentTenant)
        }
    }
    
    /**
     * Get trip metrics with time series data
     */
    fun getTripMetrics(tenantId: String, days: Int = 30): traversium.traversiumreportservice.dto.TripMetricsDto {
        logger.info("=== Starting getTripMetrics() for tenant: $tenantId, days: $days ===")
        
        val currentTenant = TenantContext.getTenant()
        TenantContext.setTenant(tenantId)
        
        try {
            val endDate = OffsetDateTime.now()
            val startDate = endDate.minusDays(days.toLong())
            val monthStart = endDate.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
            
            val totalTrips = auditDataService.getTotalTripsCreated(tenantId)
            val tripsThisMonth = auditDataService.getTripsCreatedInPeriod(tenantId, monthStart, endDate)
            val tripsInPeriod = auditDataService.getTripsCreatedInPeriod(tenantId, startDate, endDate)
            
            // Get historical metrics
            val historicalMetrics = metricsRepository.findByTenantIdAndMetricDateBetween(tenantId, startDate, endDate)
            val metricPoints = historicalMetrics.map { metric ->
                traversium.traversiumreportservice.dto.TripMetricPoint(
                    date = metric.metricDate,
                    totalTrips = metric.totalTrips,
                    newTrips = 0L // Would need to calculate from daily deltas
                )
            }
            
            val latestMetrics = metricsRepository.findTopByTenantIdOrderByMetricDateDesc(tenantId)
            
            return traversium.traversiumreportservice.dto.TripMetricsDto(
                tenantId = tenantId,
                totalTrips = totalTrips,
                tripsThisMonth = tripsThisMonth,
                tripsInPeriod = tripsInPeriod,
                lastUpdated = latestMetrics?.metricDate ?: OffsetDateTime.now(),
                metrics = metricPoints.sortedBy { it.date }
            )
        } finally {
            TenantContext.setTenant(currentTenant)
        }
    }
    
    /**
     * Get media metrics with time series data
     */
    fun getMediaMetrics(tenantId: String, days: Int = 30): traversium.traversiumreportservice.dto.MediaMetricsDto {
        logger.info("=== Starting getMediaMetrics() for tenant: $tenantId, days: $days ===")
        
        val currentTenant = TenantContext.getTenant()
        TenantContext.setTenant(tenantId)
        
        try {
            val endDate = OffsetDateTime.now()
            val startDate = endDate.minusDays(days.toLong())
            val monthStart = endDate.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
            
            val totalMedia = auditDataService.getTotalMediaUploaded(tenantId)
            val mediaThisMonth = auditDataService.getMediaUploadedInPeriod(tenantId, monthStart, endDate)
            val mediaInPeriod = auditDataService.getMediaUploadedInPeriod(tenantId, startDate, endDate)
            val totalStorageBytes = auditDataService.getTotalStorageBytes(tenantId)
            val totalStorageGB = totalStorageBytes / (1024.0 * 1024.0 * 1024.0)
            
            // Get historical metrics
            val historicalMetrics = metricsRepository.findByTenantIdAndMetricDateBetween(tenantId, startDate, endDate)
            val metricPoints = historicalMetrics.map { metric ->
                traversium.traversiumreportservice.dto.MediaMetricPoint(
                    date = metric.metricDate,
                    totalMedia = 0L, // Would need to track this separately
                    newMedia = 0L,
                    storageGB = metric.totalStorageBytes / (1024.0 * 1024.0 * 1024.0)
                )
            }
            
            val latestMetrics = metricsRepository.findTopByTenantIdOrderByMetricDateDesc(tenantId)
            
            return traversium.traversiumreportservice.dto.MediaMetricsDto(
                tenantId = tenantId,
                totalMedia = totalMedia,
                mediaThisMonth = mediaThisMonth,
                mediaInPeriod = mediaInPeriod,
                totalStorageGB = totalStorageGB,
                lastUpdated = latestMetrics?.metricDate ?: OffsetDateTime.now(),
                metrics = metricPoints.sortedBy { it.date }
            )
        } finally {
            TenantContext.setTenant(currentTenant)
        }
    }
    
    /**
     * Get social metrics with time series data
     */
    fun getSocialMetrics(tenantId: String, days: Int = 30): traversium.traversiumreportservice.dto.SocialMetricsDto {
        logger.info("=== Starting getSocialMetrics() for tenant: $tenantId, days: $days ===")
        
        val currentTenant = TenantContext.getTenant()
        TenantContext.setTenant(tenantId)
        
        try {
            val endDate = OffsetDateTime.now()
            val startDate = endDate.minusDays(days.toLong())
            val monthStart = endDate.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
            
            // Get likes and comments separately
            val totalLikes = getTotalLikes(tenantId)
            val totalComments = getTotalComments(tenantId)
            val totalInteractions = totalLikes + totalComments
            val interactionsThisMonth = auditDataService.getSocialInteractionsInPeriod(tenantId, monthStart, endDate)
            val interactionsInPeriod = auditDataService.getSocialInteractionsInPeriod(tenantId, startDate, endDate)
            
            val latestMetrics = metricsRepository.findTopByTenantIdOrderByMetricDateDesc(tenantId)
            
            return traversium.traversiumreportservice.dto.SocialMetricsDto(
                tenantId = tenantId,
                totalLikes = totalLikes,
                totalComments = totalComments,
                totalInteractions = totalInteractions,
                interactionsThisMonth = interactionsThisMonth,
                interactionsInPeriod = interactionsInPeriod,
                lastUpdated = latestMetrics?.metricDate ?: OffsetDateTime.now(),
                metrics = emptyList() // Would need to track daily social metrics
            )
        } finally {
            TenantContext.setTenant(currentTenant)
        }
    }
    
    /**
     * Get pricing information with cost breakdown
     */
    fun getPricing(tenantId: String): traversium.traversiumreportservice.dto.PricingDto {
        logger.info("=== Starting getPricing() for tenant: $tenantId ===")
        
        val currentTenant = TenantContext.getTenant()
        TenantContext.setTenant(tenantId)
        
        try {
            val latestMetrics = metricsRepository.findTopByTenantIdOrderByMetricDateDesc(tenantId)
                ?: calculateAndSaveCurrentMetrics(tenantId)
            
            val totalUsers = latestMetrics.totalUsers
            val totalStorageGB = latestMetrics.totalStorageBytes / (1024.0 * 1024.0 * 1024.0)
            val totalApiCalls = latestMetrics.totalApiCalls
            
            val userCost = totalUsers * COST_PER_USER
            val storageCost = totalStorageGB * COST_PER_GB_STORAGE
            val apiCost = (totalApiCalls / 1000.0) * COST_PER_1000_API_CALLS
            val currentMonthlyCost = latestMetrics.calculatedCost
            
            // Get total cost (sum of all historical costs)
            val totalCost = metricsRepository.getTotalCostForPeriod(tenantId, OffsetDateTime.now().minusYears(1)) ?: 0.0
            
            val costBreakdown = traversium.traversiumreportservice.dto.CostBreakdown(
                baseCost = BASE_COST,
                userCost = userCost,
                storageCost = storageCost,
                apiCost = apiCost,
                totalUsers = totalUsers,
                totalStorageGB = totalStorageGB,
                totalApiCalls = totalApiCalls
            )
            
            return traversium.traversiumreportservice.dto.PricingDto(
                tenantId = tenantId,
                baseCost = BASE_COST,
                costPerUser = COST_PER_USER,
                costPerGB = COST_PER_GB_STORAGE,
                costPer1000ApiCalls = COST_PER_1000_API_CALLS,
                currentMonthlyCost = currentMonthlyCost,
                totalCost = totalCost,
                costBreakdown = costBreakdown,
                lastUpdated = latestMetrics.metricDate
            )
        } finally {
            TenantContext.setTenant(currentTenant)
        }
    }
    
    private fun getTotalLikes(tenantId: String): Long {
        return auditDataService.getTotalLikes(tenantId)
    }
    
    private fun getTotalComments(tenantId: String): Long {
        return auditDataService.getTotalComments(tenantId)
    }
}

