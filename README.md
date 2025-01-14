# LibraryService
CRUD operations for book management
Features
    -Fetch a book by its ID.
    -Create a new book with unique ISBN validation.
    -Update an existing book's details.
    -Delete a book by its ID

 - change database configuration in application.yaml
   - run this to add books table
        CREATE TABLE books (
            id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
            name VARCHAR(100) NOT NULL,
            isbn VARCHAR(20) NOT NULL UNIQUE,
            author VARCHAR(100) NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );

