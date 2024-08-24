package com.qquique.kag.application.dto

import java.util.Date

class UserDTO(
    var id: Long,
    var userName: String,
    var password: String,
    var lastDateModified: Date?,
    var lastDateCreated: Date?
) {
}