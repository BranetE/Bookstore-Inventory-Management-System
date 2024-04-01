-- liquibase formatted sql

-- changeset OlehKulbaba:2
INSERT INTO books (title, quantity, isbn, author) VALUES
                                                      ('The Great Gatsby', 100, '978-0-7432-7356-5', 'F. Scott Fitzgerald'),
                                                      ('To Kill a Mockingbird', 150, '978-0-06-112008-4', 'Harper Lee'),
                                                      ('1984', 120, '978-0-451-52493-5', 'George Orwell'),
                                                      ('Pride and Prejudice', 90, '978-0-486-28473-6', 'Jane Austen'),
                                                      ('The Catcher in the Rye', 80, '978-0-316-76948-8', 'J.D. Salinger'),
                                                      ('The Hobbit', 110, '978-0-261-10334-4', 'J.R.R. Tolkien'),
                                                      ('Brave New World', 95, '978-0-06-085052-4', 'Aldous Huxley'),
                                                      ('Lord of the Flies', 85, '978-0-399-50148-7', 'William Golding'),
                                                      ('Animal Farm', 100, '978-0-451-52634-2', 'George Orwell'),
                                                      ('The Catcher in the Rye', 75, '978-0-316-76948-8', 'J.D. Salinger'),
                                                      ('Harry Potter and the Philosopher''s Stone', 200, '978-0-7475-3269-6', 'J.K. Rowling'),
                                                      ('The Da Vinci Code', 180, '978-0-385-50420-5', 'Dan Brown'),
                                                      ('The Alchemist', 150, '978-0-06-112008-4', 'Paulo Coelho'),
                                                      ('The Lord of the Rings: The Fellowship of the Ring', 160, '978-0-345-40251-6', 'J.R.R. Tolkien'),
                                                      ('The Hunger Games', 170, '978-0-439-02352-8', 'Suzanne Collins'),
                                                      ('The Girl with the Dragon Tattoo', 140, '978-0-307-47459-2', 'Stieg Larsson'),
                                                      ('Gone with the Wind', 130, '978-1-4165-4795-7', 'Margaret Mitchell'),
                                                      ('The Kite Runner', 110, '978-1-59448-000-3', 'Khaled Hosseini'),
                                                      ('The Shining', 120, '978-0-385-12167-5', 'Stephen King');
-- rollback DELETE FROM books