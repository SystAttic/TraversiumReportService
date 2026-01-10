package traversium.traversiumreportservice.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Service for user-related metrics
 * Delegates to AuditDataService for actual data retrieval
 */
@Service
class UserMetricsService(
    private val auditDataService: AuditDataService
) {
    private val logger = LoggerFactory.getLogger(UserMetricsService::class.java)

    fun getTotalUsers(tenantId: String): Long {
        logger.debug("Getting total users for tenant: $tenantId")
        return auditDataService.getTotalUsersCreated(tenantId)
    }

    fun getActiveUsers(tenantId: String, days: Int): Long {
        logger.debug("Getting active users (last $days days) for tenant: $tenantId")
        return auditDataService.getActiveUsers(tenantId, days)
    }

    fun getTotalTrips(tenantId: String): Long {
        logger.debug("Getting total trips for tenant: $tenantId")
        return auditDataService.getTotalTripsCreated(tenantId)
    }

    fun getTotalStorageBytes(tenantId: String): Long {
        logger.debug("Getting total storage bytes for tenant: $tenantId")
        return auditDataService.getTotalStorageBytes(tenantId)
    }

    fun getTotalApiCalls(tenantId: String): Long {
        logger.debug("Getting total API calls for tenant: $tenantId")
        return auditDataService.getTotalApiCalls(tenantId)
    }
}

