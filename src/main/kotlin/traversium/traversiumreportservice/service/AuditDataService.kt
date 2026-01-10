package traversium.traversiumreportservice.service

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import traversium.audit.metrics.*
import traversium.audit.metrics.AuditMetricsServiceGrpc
import java.time.OffsetDateTime

/**
 * Service for reading audit data from audit service via gRPC
 */
@Service
class AuditDataService(
    private val auditMetricsStub: AuditMetricsServiceGrpc.AuditMetricsServiceBlockingStub
) {
    private val logger = LoggerFactory.getLogger(AuditDataService::class.java)

    /**
     * Get total users created (count of USER_CREATED events)
     */
    @CircuitBreaker(name = "auditServiceGrpc")
    @Retry(name = "auditServiceGrpc")
    fun getTotalUsersCreated(tenantId: String): Long {
        logger.debug("Getting total users created for tenant: $tenantId")
        try {
            val request = MetricsRequest.newBuilder()
                .setTenantId(tenantId)
                .build()
            
            val response = auditMetricsStub.getTotalUsersCreated(request)
            logger.debug("Total users created for tenant $tenantId: ${response.count}")
            return response.count
        } catch (e: Exception) {
            logger.error("Failed to get total users created for tenant $tenantId", e)
            throw e
        }
    }

    /**
     * Get active users (distinct users with activity in the last N days)
     */
    @CircuitBreaker(name = "auditServiceGrpc")
    @Retry(name = "auditServiceGrpc")
    fun getActiveUsers(tenantId: String, days: Int): Long {
        logger.debug("Getting active users (last $days days) for tenant: $tenantId")
        try {
            val request = ActiveUsersRequest.newBuilder()
                .setTenantId(tenantId)
                .setDays(days)
                .build()
            
            val response = auditMetricsStub.getActiveUsers(request)
            logger.debug("Active users (last $days days) for tenant $tenantId: ${response.count}")
            return response.count
        } catch (e: Exception) {
            logger.error("Failed to get active users for tenant $tenantId", e)
            throw e
        }
    }

    /**
     * Get new users created in a date range
     */
    @CircuitBreaker(name = "auditServiceGrpc")
    @Retry(name = "auditServiceGrpc")
    fun getNewUsersInPeriod(tenantId: String, startDate: OffsetDateTime, endDate: OffsetDateTime): Long {
        logger.debug("Getting new users in period for tenant: $tenantId")
        try {
            val request = DateRangeRequest.newBuilder()
                .setTenantId(tenantId)
                .setStartDate(startDate.toString())
                .setEndDate(endDate.toString())
                .build()
            
            val response = auditMetricsStub.getNewUsersInPeriod(request)
            logger.debug("New users in period for tenant $tenantId: ${response.count}")
            return response.count
        } catch (e: Exception) {
            logger.error("Failed to get new users in period for tenant $tenantId", e)
            throw e
        }
    }

    /**
     * Get total trips created
     */
    @CircuitBreaker(name = "auditServiceGrpc")
    @Retry(name = "auditServiceGrpc")
    fun getTotalTripsCreated(tenantId: String): Long {
        logger.debug("Getting total trips created for tenant: $tenantId")
        try {
            val request = MetricsRequest.newBuilder()
                .setTenantId(tenantId)
                .build()
            
            val response = auditMetricsStub.getTotalTripsCreated(request)
            logger.debug("Total trips created for tenant $tenantId: ${response.count}")
            return response.count
        } catch (e: Exception) {
            logger.error("Failed to get total trips created for tenant $tenantId", e)
            throw e
        }
    }

    /**
     * Get trips created in a date range
     */
    @CircuitBreaker(name = "auditServiceGrpc")
    @Retry(name = "auditServiceGrpc")
    fun getTripsCreatedInPeriod(tenantId: String, startDate: OffsetDateTime, endDate: OffsetDateTime): Long {
        logger.debug("Getting trips created in period for tenant: $tenantId")
        try {
            val request = DateRangeRequest.newBuilder()
                .setTenantId(tenantId)
                .setStartDate(startDate.toString())
                .setEndDate(endDate.toString())
                .build()
            
            val response = auditMetricsStub.getTripsCreatedInPeriod(request)
            logger.debug("Trips created in period for tenant $tenantId: ${response.count}")
            return response.count
        } catch (e: Exception) {
            logger.error("Failed to get trips created in period for tenant $tenantId", e)
            throw e
        }
    }

    /**
     * Get total media files uploaded
     */
    @CircuitBreaker(name = "auditServiceGrpc")
    @Retry(name = "auditServiceGrpc")
    fun getTotalMediaUploaded(tenantId: String): Long {
        logger.debug("Getting total media uploaded for tenant: $tenantId")
        try {
            val request = MetricsRequest.newBuilder()
                .setTenantId(tenantId)
                .build()
            
            val response = auditMetricsStub.getTotalMediaUploaded(request)
            logger.debug("Total media uploaded for tenant $tenantId: ${response.count}")
            return response.count
        } catch (e: Exception) {
            logger.error("Failed to get total media uploaded for tenant $tenantId", e)
            throw e
        }
    }

    /**
     * Get media uploaded in a date range
     */
    @CircuitBreaker(name = "auditServiceGrpc")
    @Retry(name = "auditServiceGrpc")
    fun getMediaUploadedInPeriod(tenantId: String, startDate: OffsetDateTime, endDate: OffsetDateTime): Long {
        logger.debug("Getting media uploaded in period for tenant: $tenantId")
        try {
            val request = DateRangeRequest.newBuilder()
                .setTenantId(tenantId)
                .setStartDate(startDate.toString())
                .setEndDate(endDate.toString())
                .build()
            
            val response = auditMetricsStub.getMediaUploadedInPeriod(request)
            logger.debug("Media uploaded in period for tenant $tenantId: ${response.count}")
            return response.count
        } catch (e: Exception) {
            logger.error("Failed to get media uploaded in period for tenant $tenantId", e)
            throw e
        }
    }

    /**
     * Get total storage bytes from file storage activities
     */
    @CircuitBreaker(name = "auditServiceGrpc")
    @Retry(name = "auditServiceGrpc")
    fun getTotalStorageBytes(tenantId: String): Long {
        logger.debug("Getting total storage bytes for tenant: $tenantId")
        try {
            val request = MetricsRequest.newBuilder()
                .setTenantId(tenantId)
                .build()
            
            val response = auditMetricsStub.getTotalStorageBytes(request)
            logger.debug("Total storage bytes for tenant $tenantId: ${response.count}")
            return response.count
        } catch (e: Exception) {
            logger.error("Failed to get total storage bytes for tenant $tenantId", e)
            throw e
        }
    }

    /**
     * Get total social interactions (likes + comments)
     */
    @CircuitBreaker(name = "auditServiceGrpc")
    @Retry(name = "auditServiceGrpc")
    fun getTotalSocialInteractions(tenantId: String): Long {
        logger.debug("Getting total social interactions for tenant: $tenantId")
        try {
            val request = MetricsRequest.newBuilder()
                .setTenantId(tenantId)
                .build()
            
            val response = auditMetricsStub.getTotalSocialInteractions(request)
            logger.debug("Total social interactions for tenant $tenantId: ${response.count}")
            return response.count
        } catch (e: Exception) {
            logger.error("Failed to get total social interactions for tenant $tenantId", e)
            throw e
        }
    }

    /**
     * Get total likes
     */
    @CircuitBreaker(name = "auditServiceGrpc")
    @Retry(name = "auditServiceGrpc")
    fun getTotalLikes(tenantId: String): Long {
        logger.debug("Getting total likes for tenant: $tenantId")
        try {
            val request = MetricsRequest.newBuilder()
                .setTenantId(tenantId)
                .build()
            
            val response = auditMetricsStub.getTotalLikes(request)
            logger.debug("Total likes for tenant $tenantId: ${response.count}")
            return response.count
        } catch (e: Exception) {
            logger.error("Failed to get total likes for tenant $tenantId", e)
            throw e
        }
    }

    /**
     * Get total comments
     */
    @CircuitBreaker(name = "auditServiceGrpc")
    @Retry(name = "auditServiceGrpc")
    fun getTotalComments(tenantId: String): Long {
        logger.debug("Getting total comments for tenant: $tenantId")
        try {
            val request = MetricsRequest.newBuilder()
                .setTenantId(tenantId)
                .build()
            
            val response = auditMetricsStub.getTotalComments(request)
            logger.debug("Total comments for tenant $tenantId: ${response.count}")
            return response.count
        } catch (e: Exception) {
            logger.error("Failed to get total comments for tenant $tenantId", e)
            throw e
        }
    }

    /**
     * Get social interactions in a date range
     */
    @CircuitBreaker(name = "auditServiceGrpc")
    @Retry(name = "auditServiceGrpc")
    fun getSocialInteractionsInPeriod(tenantId: String, startDate: OffsetDateTime, endDate: OffsetDateTime): Long {
        logger.debug("Getting social interactions in period for tenant: $tenantId")
        try {
            val request = DateRangeRequest.newBuilder()
                .setTenantId(tenantId)
                .setStartDate(startDate.toString())
                .setEndDate(endDate.toString())
                .build()
            
            val response = auditMetricsStub.getSocialInteractionsInPeriod(request)
            logger.debug("Social interactions in period for tenant $tenantId: ${response.count}")
            return response.count
        } catch (e: Exception) {
            logger.error("Failed to get social interactions in period for tenant $tenantId", e)
            throw e
        }
    }

    /**
     * Get total API calls (approximate - count all activity events)
     */
    @CircuitBreaker(name = "auditServiceGrpc")
    @Retry(name = "auditServiceGrpc")
    fun getTotalApiCalls(tenantId: String): Long {
        logger.debug("Getting total API calls for tenant: $tenantId")
        try {
            val request = MetricsRequest.newBuilder()
                .setTenantId(tenantId)
                .build()
            
            val response = auditMetricsStub.getTotalApiCalls(request)
            logger.debug("Total API calls for tenant $tenantId: ${response.count}")
            return response.count
        } catch (e: Exception) {
            logger.error("Failed to get total API calls for tenant $tenantId", e)
            throw e
        }
    }

    /**
     * Get API calls in a date range
     */
    @CircuitBreaker(name = "auditServiceGrpc")
    @Retry(name = "auditServiceGrpc")
    fun getApiCallsInPeriod(tenantId: String, startDate: OffsetDateTime, endDate: OffsetDateTime): Long {
        logger.debug("Getting API calls in period for tenant: $tenantId")
        try {
            val request = DateRangeRequest.newBuilder()
                .setTenantId(tenantId)
                .setStartDate(startDate.toString())
                .setEndDate(endDate.toString())
                .build()
            
            val response = auditMetricsStub.getApiCallsInPeriod(request)
            logger.debug("API calls in period for tenant $tenantId: ${response.count}")
            return response.count
        } catch (e: Exception) {
            logger.error("Failed to get API calls in period for tenant $tenantId", e)
            throw e
        }
    }
}
