package Booking

final case class IntersectionException(
    private val message: String = "",
    private val cause: Throwable = None.orNull
) extends RuntimeException
