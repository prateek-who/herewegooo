package com.example.herewegooo.data.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    var userRole by mutableStateOf("student")
    var userName by mutableStateOf("student")
}
