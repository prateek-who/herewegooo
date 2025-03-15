package com.example.herewegooo.data.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    var userId by mutableStateOf("")
    var userRole by mutableStateOf("student")
    var userName by mutableStateOf("student")
    var profilePic by mutableStateOf("")
    var joinDate by mutableStateOf("")
    var favouriteQuote by mutableStateOf("")

    var course_names by mutableStateOf(listOf(""))

    var index by mutableIntStateOf(0)

    fun resetUserState(){
        userId = ""
        userRole = ""
        userName = ""
        joinDate = ""
        index = 0
        favouriteQuote = ""
        course_names = listOf()
    }

//    fun updateCourseNames(newCourses: List<String>) {
//        course_names.clear()
//        course_names.addAll(newCourses)
//    }
}
