package traversium.traversiumreportservice.db.model

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "tenant_metrics", schema = "public")
class TenantMetrics(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    
    @Column(nullable = false)
    var tenantId: String,
    
    @Column(nullable = false)
    var metricDate: OffsetDateTime,
    
    @Column(nullable = false)
    var totalUsers: Long = 0,
    
    @Column(nullable = false)
    var activeUsers: Long = 0,
    
    @Column(nullable = false)
    var totalTrips: Long = 0,
    
    @Column(nullable = false)
    var totalStorageBytes: Long = 0,
    
    @Column(nullable = false)
    var totalApiCalls: Long = 0,
    
    @Column(nullable = false)
    var calculatedCost: Double = 0.0,
    
    @Column(nullable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now()
) {
    constructor() : this(
        id = null,
        tenantId = "",
        metricDate = OffsetDateTime.now(),
        totalUsers = 0,
        activeUsers = 0,
        totalTrips = 0,
        totalStorageBytes = 0,
        totalApiCalls = 0,
        calculatedCost = 0.0,
        createdAt = OffsetDateTime.now()
    )
}

