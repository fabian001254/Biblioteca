package com.biblioteca.biblioteca.controller;

import com.biblioteca.biblioteca.model.Book;
import com.biblioteca.biblioteca.model.Loan;
import com.biblioteca.biblioteca.model.User;
import com.biblioteca.biblioteca.model.Enums.LoanStatus;
import com.biblioteca.biblioteca.model.Enums.Role;
import com.biblioteca.biblioteca.repository.BookRepository;
import com.biblioteca.biblioteca.repository.LoanRepository;
import com.biblioteca.biblioteca.repository.UserRepository;
import com.biblioteca.biblioteca.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Controller
public class WebController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanService loanService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Página de inicio
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // Páginas de autenticación
    @GetMapping("/login")
    public String login() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Si el usuario ya está autenticado, redirigir al dashboard
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return "redirect:/dashboard";
        }

        return "redirect:/auth/login";
    }

    @GetMapping("/register")
    public String register() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Si el usuario ya está autenticado, redirigir al dashboard
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return "redirect:/dashboard";
        }

        return "redirect:/auth/register";
    }

    // El método dashboard se ha movido a DashboardController

    // Gestión de libros
    @GetMapping("/books")
    public String listBooks(Model model,
                            @RequestParam(required = false) String title,
                            @RequestParam(required = false) String author,
                            @RequestParam(required = false) String genre) {

        List<Book> books;

        // Aplicar filtros si existen
        if (title != null && !title.isEmpty() && author != null && !author.isEmpty() && genre != null && !genre.isEmpty()) {
            books = bookRepository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndGenreContainingIgnoreCase(title, author, genre);
        } else if (title != null && !title.isEmpty() && author != null && !author.isEmpty()) {
            books = bookRepository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(title, author);
        } else if (title != null && !title.isEmpty() && genre != null && !genre.isEmpty()) {
            books = bookRepository.findByTitleContainingIgnoreCaseAndGenreContainingIgnoreCase(title, genre);
        } else if (author != null && !author.isEmpty() && genre != null && !genre.isEmpty()) {
            books = bookRepository.findByAuthorContainingIgnoreCaseAndGenreContainingIgnoreCase(author, genre);
        } else if (title != null && !title.isEmpty()) {
            books = bookRepository.findByTitleContainingIgnoreCase(title);
        } else if (author != null && !author.isEmpty()) {
            books = bookRepository.findByAuthorContainingIgnoreCase(author);
        } else if (genre != null && !genre.isEmpty()) {
            books = bookRepository.findByGenreContainingIgnoreCase(genre);
        } else {
            books = bookRepository.findAll();
        }

        model.addAttribute("books", books);
        return "books/list";
    }

    @GetMapping("/books/new")
    public String newBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "books/form";
    }

    @GetMapping("/books/{id}/edit")
    public String editBookForm(@PathVariable Long id, Model model) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            return "books/form";
        } else {
            return "redirect:/books";
        }
    }

    @PostMapping("/books")
    public String saveBook(Book book) {
        bookRepository.save(book);
        return "redirect:/books";
    }

    @PostMapping("/books/{id}")
    public String updateBook(@PathVariable Long id, Book book) {
        book.setId(id);
        bookRepository.save(book);
        return "redirect:/books";
    }

    // Gestión de préstamos
    @GetMapping("/loans")
    public String listLoans(Model model,
                            @RequestParam(required = false) String status,
                            @RequestParam(required = false) String reader,
                            @RequestParam(required = false) String book) {

        List<Loan> loans;

        // Aplicar filtros si existen
        if (status != null && !status.isEmpty() && reader != null && !reader.isEmpty() && book != null && !book.isEmpty()) {
            loans = loanRepository.findByStatusAndUserEmailContainingAndBookTitleContaining(
                    LoanStatus.valueOf(status), reader, book);
        } else if (status != null && !status.isEmpty() && reader != null && !reader.isEmpty()) {
            loans = loanRepository.findByStatusAndUserEmailContaining(
                    LoanStatus.valueOf(status), reader);
        } else if (status != null && !status.isEmpty() && book != null && !book.isEmpty()) {
            loans = loanRepository.findByStatusAndBookTitleContaining(
                    LoanStatus.valueOf(status), book);
        } else if (reader != null && !reader.isEmpty() && book != null && !book.isEmpty()) {
            loans = loanRepository.findByUserEmailContainingAndBookTitleContaining(reader, book);
        } else if (status != null && !status.isEmpty()) {
            loans = loanRepository.findByStatus(LoanStatus.valueOf(status));
        } else if (reader != null && !reader.isEmpty()) {
            loans = loanRepository.findByUserEmailContaining(reader);
        } else if (book != null && !book.isEmpty()) {
            loans = loanRepository.findByBookTitleContaining(book);
        } else {
            loans = loanRepository.findAll();
        }

        model.addAttribute("loans", loans);
        return "loans/list";
    }

    @GetMapping("/loans/new")
    public String newLoanForm(Model model, @RequestParam(required = false) Long bookId) {
        Loan loan = new Loan();

        // Si se proporciona un ID de libro, lo preseleccionamos
        if (bookId != null) {
            Optional<Book> book = bookRepository.findById(bookId);
            book.ifPresent(loan::setBook);
        }

        // Obtener todos los libros disponibles
        List<Book> books = bookRepository.findAll();

        // Obtener todos los lectores
        List<User> readers = userRepository.findByRole(Role.READER);

        model.addAttribute("loan", loan);
        model.addAttribute("books", books);
        model.addAttribute("readers", readers);

        return "loans/form";
    }

    @PostMapping("/loans")
    public String saveLoan(Loan loan) {
        loan.setLoanDate(LocalDateTime.now());
        loan.setStatus(LoanStatus.ACTIVE);
        loanRepository.save(loan);
        return "redirect:/loans";
    }

    @GetMapping("/loans/{id}/return")
    public String returnLoan(@PathVariable Long id) {
        Optional<Loan> loanOpt = loanRepository.findById(id);

        if (loanOpt.isPresent()) {
            Loan loan = loanOpt.get();
            loan.setReturnDate(LocalDateTime.now());
            loan.setStatus(LoanStatus.RETURNED);
            loanRepository.save(loan);
        }

        return "redirect:/loans";
    }

    @GetMapping("/loans/{id}/lost")
    public String markLoanAsLost(@PathVariable Long id) {
        Optional<Loan> loanOpt = loanRepository.findById(id);

        if (loanOpt.isPresent()) {
            Loan loan = loanOpt.get();
            loan.setStatus(LoanStatus.LOST);
            loanRepository.save(loan);
        }

        return "redirect:/loans";
    }

    // Gestión de bibliotecarios (solo admin)
    @GetMapping("/admin/librarians")
    public String listLibrarians(Model model) {
        List<User> librarians = userRepository.findByRole(Role.LIBRARIAN);
        model.addAttribute("librarians", librarians);
        return "admin/librarians/list";
    }

    @GetMapping("/admin/librarians/new")
    public String newLibrarianForm(Model model) {
        User librarian = new User();
        librarian.setRole(Role.LIBRARIAN);
        model.addAttribute("librarian", librarian);
        return "admin/librarians/form";
    }

    @GetMapping("/admin/librarians/{id}/edit")
    public String editLibrarianForm(@PathVariable Long id, Model model) {
        Optional<User> librarian = userRepository.findById(id);
        if (librarian.isPresent() && librarian.get().getRole() == Role.LIBRARIAN) {
            model.addAttribute("librarian", librarian.get());
            return "admin/librarians/form";
        } else {
            return "redirect:/admin/librarians";
        }
    }

    @PostMapping("/admin/librarians")
    public String saveLibrarian(User librarian) {
        librarian.setRole(Role.LIBRARIAN);
        userRepository.save(librarian);
        return "redirect:/admin/librarians";
    }

    @PostMapping("/admin/librarians/{id}")
    public String updateLibrarian(@PathVariable Long id, User librarian) {
        librarian.setId(id);
        librarian.setRole(Role.LIBRARIAN);
        userRepository.save(librarian);
        return "redirect:/admin/librarians";
    }

    // Gestión de lectores (para bibliotecarios y administradores)
    @GetMapping("/readers")
    public String listReaders(Model model,
                              @RequestParam(required = false) String name,
                              @RequestParam(required = false) String email) {

        List<User> readers;

        // Aplicar filtros si existen
        if (name != null && !name.isEmpty() && email != null && !email.isEmpty()) {
            readers = userRepository.findAll().stream()
                    .filter(user -> user.getRole() == Role.READER)
                    .filter(user -> user.getName().toLowerCase().contains(name.toLowerCase()) &&
                            user.getEmail().toLowerCase().contains(email.toLowerCase()))
                    .collect(Collectors.toList());
                } else if (name != null && !name.isEmpty()) {
            readers = userRepository.findAll().stream()
                    .filter(user -> user.getRole() == Role.READER)
                    .filter(user -> user.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
                } else if (email != null && !email.isEmpty()) {
            readers = userRepository.findAll().stream()
                    .filter(user -> user.getRole() == Role.READER)
                    .filter(user -> user.getEmail().toLowerCase().contains(email.toLowerCase()))
                    .collect(Collectors.toList());
                } else {
            readers = userRepository.findByRole(Role.READER);
        }

        // Calcular préstamos activos por lector
        Map<Long, Integer> activeLoansCount = new HashMap<>();
        for (User reader : readers) {
            int count = loanService.countActiveLoansByUser(reader.getEmail());
            activeLoansCount.put(reader.getId(), count);
        }

        model.addAttribute("readers", readers);
        model.addAttribute("activeLoansCount", activeLoansCount);
        return "readers/list";
    }

    @GetMapping("/readers/new")
    public String newReaderForm(Model model) {
        User reader = new User();
        reader.setRole(Role.READER);
        model.addAttribute("reader", reader);
        return "readers/form";
    }

    @GetMapping("/readers/{id}")
    public String viewReader(@PathVariable Long id, Model model) {
        Optional<User> readerOpt = userRepository.findById(id);
        if (!readerOpt.isPresent() || readerOpt.get().getRole() != Role.READER) {
            return "redirect:/readers";
        }

        User reader = readerOpt.get();

        // Obtener préstamos activos
        List<Loan> activeLoans = loanService.findActiveLoansForUser(reader.getEmail());

        // Obtener historial de préstamos (préstamos devueltos o perdidos)
        List<Loan> loanHistory = loanRepository.findAll().stream()
                .filter(loan -> loan.getUser() != null && loan.getUser().getId().equals(reader.getId()))
                .filter(loan -> loan.getStatus() == LoanStatus.RETURNED || loan.getStatus() == LoanStatus.LOST)
                .collect(Collectors.toList());

        // Estadísticas
        int activeLoansCount = activeLoans.size();
        int totalLoansCount = activeLoansCount + loanHistory.size();

        model.addAttribute("reader", reader);
        model.addAttribute("activeLoans", activeLoans);
        model.addAttribute("loanHistory", loanHistory);
        model.addAttribute("activeLoansCount", activeLoansCount);
        model.addAttribute("totalLoansCount", totalLoansCount);

        return "readers/view";
    }

    @GetMapping("/readers/{id}/edit")
    public String editReaderForm(@PathVariable Long id, Model model) {
        Optional<User> reader = userRepository.findById(id);
        if (reader.isPresent() && reader.get().getRole() == Role.READER) {
            model.addAttribute("reader", reader.get());
            return "readers/form";
        } else {
            return "redirect:/readers";
        }
    }

    @PostMapping("/readers")
    public String saveReader(User reader, @RequestParam(required = false) String newPassword) {
        reader.setRole(Role.READER);

        // Encriptar la contraseña
        if (reader.getPassword() != null && !reader.getPassword().isEmpty()) {
            reader.setPassword(passwordEncoder.encode(reader.getPassword()));
        }

        userRepository.save(reader);
        return "redirect:/readers";
    }

    @PostMapping("/readers/{id}")
    public String updateReader(@PathVariable Long id, User reader, @RequestParam(required = false) String newPassword) {
        Optional<User> existingReaderOpt = userRepository.findById(id);
        if (!existingReaderOpt.isPresent()) {
            return "redirect:/readers";
        }

        User existingReader = existingReaderOpt.get();

        // Actualizar campos
        existingReader.setName(reader.getName());
        existingReader.setSurname(reader.getSurname());
        existingReader.setEmail(reader.getEmail());
        existingReader.setPhone(reader.getPhone());
        existingReader.setAddress(reader.getAddress());
        existingReader.setCity(reader.getCity());

        // Actualizar contraseña solo si se proporciona una nueva
        if (newPassword != null && !newPassword.isEmpty()) {
            existingReader.setPassword(passwordEncoder.encode(newPassword));
        }

        userRepository.save(existingReader);
        return "redirect:/readers";
    }
}