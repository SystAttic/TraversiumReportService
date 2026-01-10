package traversium.traversiumreportservice.db.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import traversium.traversiumreportservice.db.model.TenantMetrics
import java.time.OffsetDateTime

@Repository
interface TenantMetricsRepository : JpaRepository<TenantMetrics, Long> {
    fun findByTenantIdAndMetricDateBetween(
        tenantId: String,
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): List<TenantMetrics>
    
    fun findTopByTenantIdOrderByMetricDateDesc(tenantId: String): TenantMetrics?
    
    @Query("SELECT SUM(tm.calculatedCost) FROM TenantMetrics tm WHERE tm.tenantId = :tenantId AND tm.metricDate >= :startDate")
    fun getTotalCostForPeriod(tenantId: String, startDate: OffsetDateTime): Double?
}

