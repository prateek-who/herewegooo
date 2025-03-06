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
import io.ktor.client.plugins.websocket.WebSockets
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.time.LocalTime


@Serializable
data class ProfileRole(
    val role: String,
    val username: String
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
//        install(Realtime)

        httpConfig {
            this.install(WebSockets)

        }
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

fun RawEvent.toEvent(): Event = Event(
    startTime = this.startTime,
    endTime = this.endTime,
    title = this.title.courseName,
    facultyName = this.facultyName?.user?.username,
    color = this.facultyName?.color ?:Color(0xFF9B4CBF)
)

//suspend fun getEvent(client: SupabaseClient): Result<Unit> {
//    return runCatching {
//        client.from("timetable")
//            .select(columns = Columns.ALL){
//                filter {
//                    eq("id", 5)
//                }
//            }.decodeList<Event>()
//    }
//}

//suspend fun getTimeTable(client: SupabaseClient): Result<Unit> {
//    return runCatching {
//        client.from("timetable")
//            .select(columns = Columns.ALL) {
//                filter {
//                    eq("faculty_id", 5)
//                }
//            }.decodeList<Events>()
//    }
//}