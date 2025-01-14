package com.example.library

import com.example.library.Service.BookService
import com.example.library.models.Book
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*


@ExtendWith(SpringExtension::class)
@WebMvcTest
class BooksControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var bookService: BookService

    val bookId = 1L
    val dummyBook = Book().apply {
        this.id = bookId
        this.name = "Bible"
        this.author = "Jesus"
        this.isbn = "9781234567"
    }

    @Nested
    inner class TestGetBook {
        @Test
        fun `when book exists`() {
            // GIVEN
            doReturn(dummyBook).`when`(bookService).getBookById(bookId)

            // WHEN
            val response = mockMvc.perform(MockMvcRequestBuilders.get("/api/books/{id}", bookId))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString

            // THEN
            assertEquals(objectMapper.writeValueAsString(dummyBook), response)
        }

        @Test
        fun `when book not found`() {
            // GIVEN
            val errorMessage = "Book with ID 1 not found"
            doThrow(BookNotFoundException(errorMessage)).`when`(bookService).getBookById(bookId)

            // WHEN
            val response = mockMvc.perform(MockMvcRequestBuilders.get("/api/books/{id}", bookId))
                .andExpect(status().isNotFound)
                .andReturn().response.contentAsString

            // THEN
            assertEquals(objectMapper.writeValueAsString(ErrorResponse(message = errorMessage)), response)
        }
    }

    @Nested
    inner class TestCreateBook {
        private val createBookDto = CreateBookDto(name = dummyBook.name, author = dummyBook.author, isbn = dummyBook.isbn)

        @Test
        fun `should create a new book`() {
            doReturn(dummyBook).`when`(bookService).createBook(createBookDto)

            //WHEN
            val response = mockMvc.perform(MockMvcRequestBuilders.post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createBookDto)))
                .andExpect(status().isCreated)
                .andReturn().response.contentAsString

            assertEquals(objectMapper.writeValueAsString(dummyBook.id), response)
        }

        @Test
        fun `should not create book when book already exists`() {
            val errorMessage = "Book with isbn :${dummyBook.isbn} already exists"
            doThrow(DuplicateBookException(errorMessage)).`when`(bookService).createBook(createBookDto)

            // WHEN
            val response = mockMvc.perform(MockMvcRequestBuilders.post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createBookDto)))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString

            assertEquals(objectMapper.writeValueAsString(ErrorResponse(message = errorMessage)), response)
        }

        @Test
        fun `should not create book when invalid request fields`() {
            val invalidCreateBookDto = CreateBookDto(name = "", author = "", isbn = "12143")

            // WHEN
            val response = mockMvc.perform(MockMvcRequestBuilders.post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCreateBookDto)))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString

            // THEN-following error messages should be present in response
            listOf("Name cannot be blank", "Author cannot be blank", "Invalid ISBN format").forEach {
                assertEquals(response.contains(it), true)
            }
        }
    }

    @Nested
    inner class TestUpdateBook {
        private val updateBookDto = UpdateBookDto(name = dummyBook.name, author = dummyBook.author, isbn = dummyBook.isbn)

        @Test
        fun `should update book`() {
            doReturn(dummyBook).`when`(bookService).updateBook(bookId, updateBookDto)

            // WHEN
            val response = mockMvc.perform(
                MockMvcRequestBuilders
                    .patch("/api/books/{id}", bookId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateBookDto)))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString

            assertEquals(objectMapper.writeValueAsString(dummyBook), response)
        }

        @Test
        fun `should not update when invalid isbn`() {
            val invalidUpdateBookDto = UpdateBookDto(isbn = "12143")

            // WHEN
            val response = mockMvc.perform(
                MockMvcRequestBuilders
                    .patch("/api/books/{id}", bookId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidUpdateBookDto)))
                .andExpect(status().isBadRequest)
                .andReturn()
                .response.contentAsString

            assertEquals(objectMapper.writeValueAsString(listOf(ErrorResponse("Invalid ISBN format"))), response)
        }

        @Test
        fun `when book not found`() {
            val errorMessage = "Book with ID 1 not found"
            doThrow(BookNotFoundException(errorMessage)).`when`(bookService).updateBook(bookId, updateBookDto)

            // WHEN
            val response = mockMvc.perform(
                MockMvcRequestBuilders
                    .patch("/api/books/{id}", bookId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateBookDto)))
                .andExpect(status().isNotFound)
                .andReturn()
                .response.contentAsString

            // THEN
            assertEquals(objectMapper.writeValueAsString(ErrorResponse(errorMessage)), response)
        }
    }

    @Nested
    inner class TestDeleteBook {
        @Test
        fun `should delete a book`() {
            // WHEN
            mockMvc.perform(MockMvcRequestBuilders.delete("/api/books/{id}", bookId))
                .andExpect(status().isNoContent)

            // THEN
            verify(bookService, times(1)).deleteBook(bookId)
        }

        @Test
        fun `when book not found`() {
            // GIVEN
            val errorMessage = "Book with ID 1 not found"
            doThrow(BookNotFoundException(errorMessage)).`when`(bookService).deleteBook(bookId)

            // WHEN
            val response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/books/{id}", bookId))
                .andExpect(status().isNotFound)
                .andReturn()
                .response.contentAsString

            // THEN
            assertEquals(objectMapper.writeValueAsString(ErrorResponse(errorMessage)), response)
        }
    }
}
