package com.example.library.Service

import com.example.library.BookNotFoundException
import com.example.library.CreateBookDto
import com.example.library.DuplicateBookException
import com.example.library.UpdateBookDto
import com.example.library.Repository.BookRepository
import java.time.LocalDateTime
import com.example.library.models.Book
import org.springframework.stereotype.Service

/**
 * Service class for managing books.
 * This class provides methods for CRUD operations on books
 */
@Service
class BookService(
    private val bookRepository: BookRepository
) {
    /**
     * Fetches a book by its ID.
     *
     * @param bookId The ID of the book to retrieve.
     * @return [Book] requested book.
     * @throws BookNotFoundException If no book is found with the given ID.
     */
    fun getBookById(bookId: Long): Book {
        return bookRepository.findById(bookId).orElseThrow { BookNotFoundException("Book with ID $bookId not found") }
    }

    /**
     * Creates a new book.
     *
     * @param createBookDto  object containing details of the book to be created.
     * @return The newly created book.
     * @throws DuplicateBookException If a book with the same ISBN already exists.
     */
    fun createBook(createBookDto: CreateBookDto): Book {
        // Check if book already exists
        bookRepository.findByIsbn(createBookDto.isbn)?.let {
            throw DuplicateBookException("Book with isbn :${createBookDto.isbn} already exists")
        }

        val book = Book().apply {
            this.name = createBookDto.name
            this.isbn = createBookDto.isbn
            this.author = createBookDto.author
        }

        return bookRepository.save(book)
    }

    /**
     * Updates an existing book.
     *
     * @param bookId The ID of the book to update.
     * @param updateBookDto object containing the updated details of the book.
     * @return The updated book.
     */
    fun updateBook(bookId: Long, updateBookDto: UpdateBookDto): Book {
        val book = getBookById(bookId)

        updateBookDto.name?.let { book.name = it }
        updateBookDto.author?.let { book.author = it }
        updateBookDto.isbn?.let { book.isbn = it }
        book.updatedAt = LocalDateTime.now()

        return bookRepository.save(book)
    }

    /**
     * Deletes a book by its ID.
     *
     * @param bookId The ID of the book to delete.
     */
    fun deleteBook(bookId: Long) {
        bookRepository.delete(getBookById(bookId))
    }
}
