package com.example.library

class BookNotFoundException(message: String) : RuntimeException(message)

class DuplicateBookException(message: String) : RuntimeException(message)