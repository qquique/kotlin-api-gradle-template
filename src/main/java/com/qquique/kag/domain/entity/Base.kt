package com.qquique.kag.domain.entity

import jakarta.persistence.*

import java.util.Date

@MappedSuperclass
abstract class Base {
    @Id
    @GeneratedValue
    var id: Long? = null

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_date_created")
    lateinit var lastDateCreated: Date

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_date_modified")
    lateinit var lastDateModified: Date

    @PrePersist
    fun prePersist() {
        this.lastDateCreated = Date()
        this.lastDateModified = Date()
    }

    @PreUpdate
    fun preUpdate() {
        this.lastDateModified = Date()
    }

    override fun toString(): String {
        return "id=$id, lastDateCreated=$lastDateCreated, lastDateModified=$lastDateModified"
    }
}