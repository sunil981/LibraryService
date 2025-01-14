package com.example.library

import com.example.library.Repository.BookRepository
import com.example.library.Service.BookService
import com.example.library.models.Book
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
class BookServiceTest {
    @Mock
    private lateinit var bookRepository: BookRepository

    @InjectMocks
    private lateinit var bookService: BookService

    private val captor = ArgumentCaptor.forClass(Book::class.java)

    private val bookId = 1L
    private val dummyBook = Book().apply {
        this.id = bookId
        this.name = "Bible"
        this.author = "Jesus"
        this.isbn = "9781234567"
    }

    @Test
    fun test_GetBookById_whenBookDoesNotExist() {
        // WHEN
        val exception = assertThrows<BookNotFoundException> {
            bookService.getBookById(bookId)
        }

        // THEN
        verify(bookRepository, times(1)).findById(bookId)
        assertEquals(exception.message, "Book with ID $bookId not found")
    }

    @Test
    fun test_GetBookById_whenBookExist() {
        doReturn(Optional.of(dummyBook)).`when`(bookRepository).findById(bookId)
        assertEquals(bookService.getBookById(bookId), dummyBook)
    }

    // Test createBook
    @Test
    fun test_createBook_whenBookDoesNotExist() {
        // GIVEN
        val createBookDto = CreateBookDto(name = dummyBook.name, author = dummyBook.author, isbn = dummyBook.isbn)

        doReturn(null).`when`(bookRepository).findByIsbn(createBookDto.isbn)
        doReturn(dummyBook).`when`(bookRepository).save(any())

        // WHEN
        val result = bookService.createBook(createBookDto)

        // THEN
        verify(bookRepository, times(1)).save(captor.capture())
        val capturedBook = captor.value

        assertEquals(dummyBook.name, capturedBook.name)
        assertEquals(dummyBook.author, capturedBook.author)
        assertEquals(dummyBook.isbn, capturedBook.isbn)
        assertEquals(result, dummyBook)
    }

    @Test
    fun test_createBook_whenBookAlreadyExists() {
        // GIVEN
        val createBookDto = CreateBookDto(name = dummyBook.name, author = dummyBook.author, isbn = dummyBook.isbn)
        doReturn(dummyBook).`when`(bookRepository).findByIsbn(createBookDto.isbn)

        // WHEN
        val exception = assertThrows<DuplicateBookException> {
            bookService.createBook(createBookDto)
        }

        // THEN
        assertEquals("Book with isbn :${dummyBook.isbn} already exists", exception.message)
    }

    @Test
    fun test_updateBook() {
        // GIVEN
        val updateBookDto = UpdateBookDto(name = "new bible", author = " same jesus", isbn = "1234567891")

        val updatedBook = Book().apply {
            this.id = dummyBook.id
            this.name = updateBookDto.name!!
            this.author = updateBookDto.author!!
            this.isbn = updateBookDto.isbn!!
        }

        doReturn(Optional.of(dummyBook)).`when`(bookRepository).findById(bookId)
        doReturn(updatedBook).`when`(bookRepository).save(any())

        // WHEN
        bookService.updateBook(bookId, updateBookDto)

        // THEN
        verify(bookRepository, times(1)).save(captor.capture()) // Capturing the argument passed to save()
        val capturedBook = captor.value

        assertEquals(capturedBook.id, updatedBook.id)
        assertEquals(capturedBook.name, updatedBook.name)
        assertEquals(capturedBook.author, updatedBook.author)
        assertEquals(capturedBook.isbn, updatedBook.isbn)
    }

    @Test
    fun testDeleteBook() {
        doReturn(Optional.of(dummyBook)).`when`(bookRepository).findById(bookId)

        // WHEN
        bookService.deleteBook(bookId)

        // THEN
        verify(bookRepository).delete(dummyBook)
    }
}
