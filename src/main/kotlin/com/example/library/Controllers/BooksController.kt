package com.example.library.Controllers

import com.example.library.CreateBookDto
import com.example.library.UpdateBookDto
import com.example.library.Service.BookService
import com.example.library.models.Book
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/books")
class BooksController(
    private val bookService: BookService
) {
    @GetMapping("/{id}")
    fun getBook(@PathVariable id: Long): Book {
        return bookService.getBookById(id)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createBook(@RequestBody @Valid requestDto: CreateBookDto): Long {
        return bookService.createBook(requestDto).id!!
    }

    @PatchMapping("/{id}")
    fun updateBook(@PathVariable id: Long, @RequestBody @Valid updateBookDto: UpdateBookDto): Book {
        return bookService.updateBook(id, updateBookDto)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteBook(@PathVariable id: Long) {
        bookService.deleteBook(id)
    }
}