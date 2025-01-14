package com.example.library.Repository

import com.example.library.models.Book
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BookRepository : CrudRepository<Book, Long> {

    fun findByIsbn(isbn: String): Book?
}