package traversium.traversiumreportservice.rest

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import traversium.traversiumreportservice.dto.*
import traversium.traversiumreportservice.service.ReportService

@RestController
@RequestMapping("/rest/v1/reports")
@Tag(name = "Reports", description = "Endpoints for tenant metrics and reports")
class ReportController(
    private val reportService: ReportService
) {
    private val logger = LoggerFactory.getLogger(ReportController::class.java)

    @GetMapping("/tenant/{tenantId}")
    @Operation(
        operationId = "getTenantReport",
        summary = "Get tenant report",
        description = "Retrieves comprehensive metrics and cost report for a tenant"
    )
    fun getTenantReport(
        @PathVariable tenantId: String,
        @RequestParam(defaultValue = "30") days: Int
    ): ResponseEntity<TenantReportDto> {
        logger.info("Getting tenant report for tenantId: $tenantId, days: $days")
        val report = reportService.getTenantReport(tenantId, days)
        return ResponseEntity.ok(report)
    }

    @GetMapping("/tenant/{tenantId}/users")
    @Operation(
        operationId = "getUserMetrics",
        summary = "Get user metrics",
        description = "Retrieves user-related metrics for a tenant"
    )
    fun getUserMetrics(
        @PathVariable tenantId: String,
        @RequestParam(defaultValue = "30") days: Int
    ): ResponseEntity<UserMetricsDto> {
        logger.info("Getting user metrics for tenantId: $tenantId, days: $days")
        val metrics = reportService.getUserMetrics(tenantId, days)
        return ResponseEntity.ok(metrics)
    }

    @GetMapping("/tenant/{tenantId}/trips")
    @Operation(
        operationId = "getTripMetrics",
        summary = "Get trip metrics",
        description = "Retrieves trip-related metrics for a tenant"
    )
    fun getTripMetrics(
        @PathVariable tenantId: String,
        @RequestParam(defaultValue = "30") days: Int
    ): ResponseEntity<TripMetricsDto> {
        logger.info("Getting trip metrics for tenantId: $tenantId, days: $days")
        val metrics = reportService.getTripMetrics(tenantId, days)
        return ResponseEntity.ok(metrics)
    }

    @GetMapping("/tenant/{tenantId}/media")
    @Operation(
        operationId = "getMediaMetrics",
        summary = "Get media metrics",
        description = "Retrieves media and storage metrics for a tenant"
    )
    fun getMediaMetrics(
        @PathVariable tenantId: String,
        @RequestParam(defaultValue = "30") days: Int
    ): ResponseEntity<MediaMetricsDto> {
        logger.info("Getting media metrics for tenantId: $tenantId, days: $days")
        val metrics = reportService.getMediaMetrics(tenantId, days)
        return ResponseEntity.ok(metrics)
    }

    @GetMapping("/tenant/{tenantId}/social")
    @Operation(
        operationId = "getSocialMetrics",
        summary = "Get social metrics",
        description = "Retrieves social interaction metrics for a tenant"
    )
    fun getSocialMetrics(
        @PathVariable tenantId: String,
        @RequestParam(defaultValue = "30") days: Int
    ): ResponseEntity<SocialMetricsDto> {
        logger.info("Getting social metrics for tenantId: $tenantId, days: $days")
        val metrics = reportService.getSocialMetrics(tenantId, days)
        return ResponseEntity.ok(metrics)
    }

    @GetMapping("/tenant/{tenantId}/pricing")
    @Operation(
        operationId = "getPricing",
        summary = "Get pricing information",
        description = "Retrieves pricing and cost breakdown for a tenant"
    )
    fun getPricing(
        @PathVariable tenantId: String
    ): ResponseEntity<PricingDto> {
        logger.info("Getting pricing for tenantId: $tenantId")
        val pricing = reportService.getPricing(tenantId)
        return ResponseEntity.ok(pricing)
    }
}

