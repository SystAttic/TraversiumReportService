package traversium.traversiumreportservice.grpc

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.grpc.client.audit")
data class GrpcProperties(
    val host: String = "localhost",
    val port: Int = 9092
)

