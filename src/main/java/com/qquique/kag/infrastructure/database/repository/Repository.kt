package com.qquique.kag.infrastructure.database.repository

import java.util.Optional

interface Repository<T> {
    fun findAll(): List<T>
    fun findById(id: Long): Optional<T>
    fun save(model: T): T
    fun deleteById(id: Long)
}