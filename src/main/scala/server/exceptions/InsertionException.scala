package server.exceptions

final case class InsertionException(
    private val message: String = "",
    private val cause: Throwable = None.orNull
) extends RuntimeException
