package info.digitalpoet.auth.utils

import java.time.*

fun Instant.toLocalDate(): LocalDate = LocalDate.ofInstant(this, ZoneOffset.UTC)

fun Instant.toLocalDateTime(): LocalDateTime = LocalDateTime.ofInstant(this, ZoneOffset.UTC)
