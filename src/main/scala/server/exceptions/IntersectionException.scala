package server.exceptions

final case class IntersectionException(
    private val message: String = "",
    private val cause: Throwable = None.orNull
) extends RuntimeException
