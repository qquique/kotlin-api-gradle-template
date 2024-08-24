package com.qquique.kag.domain.entity

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Column(name = "user_name") var userName: String,
    var password: String) : Base() {
    override fun toString(): String {
        return "Model(${super.toString()}, userName=$userName)"
    }
}
