-- Insertar usuarios iniciales (password encriptado es 'password')
INSERT INTO biblioteca_user (id, name, surname, email, password, phone, address, city, role) 
VALUES (1, 'Admin', 'Sistema', 'admin@biblioteca.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', '123456789', 'Calle Admin 123', 'Ciudad Admin', 'ADMIN');

INSERT INTO biblioteca_user (id, name, surname, email, password, phone, address, city, role) 
VALUES (2, 'Bibliotecario', 'Ejemplo', 'bibliotecario@biblioteca.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', '987654321', 'Calle Biblio 456', 'Ciudad Biblio', 'LIBRARIAN');

INSERT INTO biblioteca_user (id, name, surname, email, password, phone, address, city, role) 
VALUES (3, 'Lector', 'Ejemplo', 'lector@biblioteca.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', '555555555', 'Calle Lector 789', 'Ciudad Lector', 'READER');

-- Insertar libros iniciales
INSERT INTO biblioteca_book (id, isbn, title, author, genre, publication_year, quantity) 
VALUES (1, '9788478884452', 'El Principito', 'Antoine de Saint-Exupéry', 'Ficción', 1943, 5);

INSERT INTO biblioteca_book (id, isbn, title, author, genre, publication_year, quantity) 
VALUES (2, '9788497593069', 'Don Quijote de la Mancha', 'Miguel de Cervantes', 'Novela', 1605, 3);

INSERT INTO biblioteca_book (id, isbn, title, author, genre, publication_year, quantity) 
VALUES (3, '9788420412146', 'Cien años de soledad', 'Gabriel García Márquez', 'Realismo mágico', 1967, 7);

INSERT INTO biblioteca_book (id, isbn, title, author, genre, publication_year, quantity) 
VALUES (4, '9788499089515', '1984', 'George Orwell', 'Ciencia ficción', 1949, 4);

INSERT INTO biblioteca_book (id, isbn, title, author, genre, publication_year, quantity) 
VALUES (5, '9788445071408', 'El Señor de los Anillos', 'J.R.R. Tolkien', 'Fantasía', 1954, 6);

-- Insertar préstamos iniciales
INSERT INTO biblioteca_loan (id, book_id, user_id, loan_date, due_date, status, notes) 
VALUES (1, 1, 3, CURRENT_TIMESTAMP(), DATEADD('DAY', 14, CURRENT_DATE()), 'ACTIVE', 'Préstamo de prueba');

INSERT INTO biblioteca_loan (id, book_id, user_id, loan_date, due_date, return_date, status, notes) 
VALUES (2, 2, 3, DATEADD('DAY', -30, CURRENT_TIMESTAMP()), DATEADD('DAY', -16, CURRENT_DATE()), DATEADD('DAY', -20, CURRENT_TIMESTAMP()), 'RETURNED', 'Préstamo devuelto');

INSERT INTO biblioteca_loan (id, book_id, user_id, loan_date, due_date, status, notes) 
VALUES (3, 3, 3, DATEADD('DAY', -20, CURRENT_TIMESTAMP()), DATEADD('DAY', -6, CURRENT_DATE()), 'OVERDUE', 'Préstamo vencido');
