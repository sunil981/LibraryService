package com.example.library.models

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "books")
class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?= null

    @Column(nullable = false)
    lateinit var name: String

    @Column(nullable = false, unique = true)
    lateinit var isbn: String

    @Column(nullable = false)
    lateinit var author: String

    @Column
    val createdAt: LocalDateTime = LocalDateTime.now()

    @Column
    var updatedAt: LocalDateTime = LocalDateTime.now()
}
