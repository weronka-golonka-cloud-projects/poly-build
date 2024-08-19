import com.weronka.golonka.exceptions.BuildingSiteDoesNotExistsException
import com.weronka.golonka.exceptions.UnexpectedError
import com.weronka.golonka.http.catchExceptionsFilter
import com.weronka.golonka.service.CalculatingSplitsFailedException
import com.weronka.golonka.service.InaccurateHeightPlateausException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

class CatchExceptionsFilterTest :
    DescribeSpec({
        val request = Request(Method.GET, "http://test-url")

        context("should map exceptions to responses") {
            withData(
                listOf(
                    InaccurateHeightPlateausException("test") to Status.BAD_REQUEST,
                    CalculatingSplitsFailedException("test") to Status.BAD_REQUEST,
                    BuildingSiteDoesNotExistsException("test") to Status.NOT_FOUND,
                    UnexpectedError("test") to Status.INTERNAL_SERVER_ERROR,
                    IllegalStateException("test") to Status.INTERNAL_SERVER_ERROR,
                ),
            ) { (exception, expectedStatus) ->
                val handler = catchExceptionsFilter { _: Request -> throw exception }
                val response = handler(request)

                response.status shouldBe expectedStatus
            }
        }

        it("should propagate errors") {
            val handler = catchExceptionsFilter { _: Request -> throw OutOfMemoryError("test") }
            shouldThrow<OutOfMemoryError> {
                handler(request)
            }
        }

        it("should handle regular responses") {
            val handler = catchExceptionsFilter { _: Request -> Response(Status.OK) }
            val response = handler(request)

            response.status shouldBe Status.OK
        }
    })
