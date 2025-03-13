package com.example.herewegooo.data.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    var userRole by mutableStateOf("student")
    var userName by mutableStateOf("student")
    var profilePic by mutableStateOf("")
    var joinDate by mutableStateOf("")

    var index by mutableIntStateOf(0)

    fun resetUserState(){
        userRole = ""
        userName = ""
        joinDate = ""
        index = 0
    }
}
