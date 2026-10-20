package no.nav.dagpenger.migrering.api

import io.ktor.http.HttpStatusCode
import java.net.URI

class FeatureNotEnabledException(
    message: String,
) : BehandlingException(
        httpStatus = HttpStatusCode.Forbidden,
        type = URI("urn:error:feature-not-enabled"),
        title = "Feature ikke aktivert",
        message = message,
    )
