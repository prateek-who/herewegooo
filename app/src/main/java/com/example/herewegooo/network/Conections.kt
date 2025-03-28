package com.example.herewegooo.network


import androidx.compose.ui.graphics.Color
import com.example.herewegooo.BuildConfig
import com.example.herewegooo.ColorSerializer
import com.example.herewegooo.TimeSerializer
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.Realtime
import io.ktor.client.plugins.websocket.WebSockets
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.sql.Date
import java.time.LocalTime
import kotlin.uuid.Uuid


@Serializable
data class LoginDataOne(
    val user_id: String,
    val role: String,
    val username: String,
    val profile_pic: String,
    val join_date: String? = null,
    val favourite_quote: String
)

@Serializable
data class LoginDataTwo(
    val course_name: String,
)

@Serializable
data class IdColumnVerify(
    val id: Int
)

@OptIn(SupabaseInternal::class)
fun supabaseClient(): SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
    ) {
        install(Auth){
            alwaysAutoRefresh = false
            autoLoadFromStorage = false
        }
        install(Postgrest)

        httpConfig {
            this.install(WebSockets)

        }
        install(Realtime)
        //install other modules hereererere
    }
    return client
}


suspend fun singUpNewUser(client: SupabaseClient, email: String, password: String, name: String): Result<Unit> {
    return runCatching {
        client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            this.data = JsonObject(mapOf("full_name" to JsonPrimitive(name)))
        }
    }
}

suspend fun singInUser(client: SupabaseClient, email: String, password: String): Result<Unit> {
    return runCatching {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }
}

@Serializable
data class Course(
    @SerialName("course_name")
    val courseName: String
)

@Serializable
data class Faculty(
    @SerialName("users")
    val user: Users,
    @Serializable(with = ColorSerializer::class)
    val color: Color
)

@Serializable
data class Users(
    @SerialName("username")
    val username: String,
)


@Serializable
data class RawEvent(
    @SerialName("start_time")
    @Serializable(with = TimeSerializer::class)
    val startTime: LocalTime,

    @SerialName("end_time")
    @Serializable(with = TimeSerializer::class)
    val endTime: LocalTime,

    @SerialName("courses")
    val title: Course,

    @SerialName("faculties")
    val facultyName: Faculty? = null,
)

data class Event(
    val startTime: LocalTime,
    val endTime: LocalTime,
    val title: String,
    val facultyName: String? = null,
    val color: Color /*= Color(0xFFDC0D0D)*/
)


data class sendRequest(
    val classDate: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val facultyId: String,
    val classroomId: Int,
    val reason: String,
    val status: String
)

data class finalEventConformation(
    val classDate: String,
    val start_time: LocalTime,
    val end_time: LocalTime,
    val course_id: String?,
    val classroom_id: Int,
    val faculty_id: String,
    val reason: String
)

@Serializable
data class adminComment(
    val admin_comment: String
)

@Serializable
data class finalEventPushDataClass(
    val class_date: String,

    @SerialName("start_time")
    @Serializable(with = TimeSerializer::class)
    val start_time: LocalTime,

    @SerialName("end_time")
    @Serializable(with = TimeSerializer::class)
    val end_time: LocalTime,

    val course_id: String,
    val classroom_id: Int,
    val faculty_id: String,
)

@Serializable
data class SubjectList(
    val course_id: String,
    val course_name: String,
)

@Serializable
data class courseInsertion(
    val course_id: String,
    val course_name: String,
    val faculty_id: String
)

@Serializable
data class Request(
    val class_date: String,

    @SerialName("start_time")
    @Serializable(with = TimeSerializer::class)
    val start_time: LocalTime,

    @SerialName("end_time")
    @Serializable(with = TimeSerializer::class)
    val end_time: LocalTime,

    val faculty_id: String,
    val classroom_id: Int,
    val reason: String,
    val status: String
)


@Serializable
data class ReceiveRequests(
    val id: Int,
    val class_date: String,

    @SerialName("start_time")
    @Serializable(with = TimeSerializer::class)
    val start_time: LocalTime,

    @SerialName("end_time")
    @Serializable(with = TimeSerializer::class)
    val end_time: LocalTime,

    val faculty_id: String,
    val classroom_id: Int,
    val request_created: String,
    val reason: String,
    val status: String
)

data class RequestWithFacultyName(
    val request: ReceiveRequests,
    val facultyName: String
)

@Serializable
data class GetFacultyName(
    val username: String
)

@Serializable
data class DeletionId(
    val id: Int
)

@Serializable
data class CourseTableQuery(
    val course_id: String,
    val course_name: String,
    val faculty_id: String
)

fun RawEvent.toEvent(): Event = Event(
    startTime = this.startTime,
    endTime = this.endTime,
    title = this.title.courseName,
    facultyName = this.facultyName?.user?.username,
    color = this.facultyName?.color ?:Color(0xFF9B4CBF)
)


@Serializable
data class FacultyRequestHistoryItem(
    val class_date: String,

    @Serializable(with = TimeSerializer::class)
    val start_time: LocalTime,

    @Serializable(with = TimeSerializer::class)
    val end_time: LocalTime,

    val classroom_id: Int,
    val request_created: String,
    val reason: String,
    val status: String, // pending, approved, denied
    val admin_comment: String? = null
)


@Serializable
data class RawTimeTable(
    @SerialName("start_time")
    @Serializable(with = TimeSerializer::class)
    val startTime: LocalTime,

    @SerialName("end_time")
    @Serializable(with = TimeSerializer::class)
    val endTime: LocalTime,

    @SerialName("courses")
    val title: Course,

    val classroom_id: Int,
)

data class Period(
    val subjectName: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val roomNumber: String,
    val color: Color,
    val status: String
)


// Swap class bullshit here
@Serializable
data class FacultyList(
    val user_id: String,
    val username: String
)

data class swapperList(
    val from_id: String,
    val to_id: String,
    val class_date: String,
    val start_time: LocalTime,
    val end_time: LocalTime,
    val classroom_id: Int,
    val reason: String,
    val status: String,
)

@Serializable
data class SwapRequest(
    val from_id: String,
    val to_id: String,
    val class_date: String,

    @Serializable(with = TimeSerializer::class)
    val start_time: LocalTime,

    @Serializable(with = TimeSerializer::class)
    val end_time: LocalTime,

    val classroom_id: Int,
    val reason: String,
    val status: String,
)

@Serializable
data class SwapReceiveRequest(
    val id: Int,
    val from_id: String,
    val to_id: String,
    val class_date: String,

    @Serializable(with = TimeSerializer::class)
    val start_time: LocalTime,

    @Serializable(with = TimeSerializer::class)
    val end_time: LocalTime,

    val classroom_id: Int,
    val reason: String,
    val status: String,
    val created_at: String
)


@Serializable
data class TestTime(
    val id: Int
)
