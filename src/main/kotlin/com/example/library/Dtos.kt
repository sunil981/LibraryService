package com.example.library

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class CreateBookDto (
    @field:NotBlank(message = "Name cannot be blank")
    val name: String,

    @field:NotBlank(message = "Author cannot be blank")
    val author: String,

    @field:Pattern(
        regexp = ISBN_REGEX,
        message = "Invalid ISBN format"
    )
    val isbn: String
)

data class UpdateBookDto (
    val name: String? = null,
    val author: String? = null,

    @field:Pattern(
        regexp = ISBN_REGEX,
        message = "Invalid ISBN format"
    )
    val isbn: String? = null
)

data class ErrorResponse(
    val message: String
)