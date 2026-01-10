package traversium.traversiumreportservice.grpc

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import traversium.audit.metrics.AuditMetricsServiceGrpc
import org.springframework.cloud.context.config.annotation.RefreshScope

@Configuration
@EnableConfigurationProperties(GrpcProperties::class)
@RefreshScope
class GrpcClientConfig(
    private val grpcProperties: GrpcProperties
) {

    @Bean(name = ["auditGrpcChannel"])
    fun auditGrpcChannel(): ManagedChannel {
        return ManagedChannelBuilder
            .forAddress(grpcProperties.host, grpcProperties.port)
            .usePlaintext()
            .build()
    }

    @Bean
    fun auditMetricsStub(auditGrpcChannel: ManagedChannel): AuditMetricsServiceGrpc.AuditMetricsServiceBlockingStub {
        return AuditMetricsServiceGrpc
            .newBlockingStub(auditGrpcChannel)
    }
}

