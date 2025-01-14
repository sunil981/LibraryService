package com.example.library

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BookNotFoundException::class)
    fun handleBookNotFoundException(ex: BookNotFoundException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(ex.message ?: "Book not found")
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException): ResponseEntity<List<ErrorResponse>> {
        val errorResponses = ex.bindingResult.fieldErrors.map { ErrorResponse(it.defaultMessage ?: "")}
        return ResponseEntity(errorResponses, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(DuplicateBookException::class)
    fun handleGeneralException(ex: Exception): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(ex.message ?: "Book already exists")
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }
}