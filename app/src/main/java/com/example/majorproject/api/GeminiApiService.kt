import com.example.majorproject.dataModel.GeminiRequest
import com.example.majorproject.dataModel.GeminiResponse
import retrofit2.Response  // ✅ Use Retrofit's Response, not OkHttp
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

interface GeminiApiService {
    @Headers("Content-Type: application/json")
    @POST
    suspend fun generateContent(
        @Url url: String,
        @Body request: GeminiRequest
    ): Response<GeminiResponse> // ✅ Correct import
}
