package org.top.librarymvcapp.rdb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.top.librarymvcapp.controller.form.BookForm;
import org.top.librarymvcapp.controller.form.FindBook;
import org.top.librarymvcapp.controller.form.InventoryForm;
import org.top.librarymvcapp.entity.*;
import org.top.librarymvcapp.rdb.repository.*;
import org.top.librarymvcapp.service.BookService;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DbBookService implements BookService {

    private final BookRepository bookRepository;
    private final BookAuthorRepository bookAuthorRepository;
    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;
    private final PublicationRepository publicationRepository;

    @Override
    public Book add(BookForm bookForm, MultipartFile file) throws IOException {
        // 0 Построить книгу
        Book book = addForm(bookForm);

        // 2. установить жанр книги и издательство
        Integer genreId = bookForm.getGenreId();
        Genre genre = genreRepository.findById(genreId).get();
        book.setGenre(genre);
        Integer publicationId = bookForm.getPublicationId();
        Publication publication = publicationRepository.findById(publicationId).get();
        book.setPublication(publication);
        // 3. извлечь картинку
        if(file.getSize()>0)
            book.setImageFile(Base64.getEncoder().encodeToString(file.getBytes()));
        else
        if(bookForm.getImageFile()!=null)
            book.setImageFile(bookForm.getImageFile());
        book = bookRepository.save(book);
        // 4. извлечь авторов
        List<Author> authors = new ArrayList<>();
        if(bookForm.getSelects() != null)
            authors = bookForm.getSelects();
        addAuthorFromBookForm(bookForm, authors, false);
        // 5. добавить авторов для этой книги
        for (Author a : authors) {
            if(a!=null) {
                BookAuthor bookAuthor = new BookAuthor();
                bookAuthor.setBook(book);
                bookAuthor.setAuthor(a);
                bookAuthorRepository.save(bookAuthor);
            }
        }
        return book;
    }

    @Override
    public Book addForm(BookForm bookForm) {
        // 1. извлечь данные книги
        Book book = new Book();
        book.setCode(bookForm.getCode());
        book.setName(bookForm.getTitle());
        book.setYearRelease(bookForm.getYear());
        book.setQuantityPages(bookForm.getPages());
        book.setQuantityInstances(bookForm.getQuantityInstances());
        book.setQuantityStock(book.getQuantityInstances());
        book.setHandedOut(0);
        // 2. установить дату книги
        var now = new Date(System.currentTimeMillis());
        book.setCreationDate(now);

        return book;
    }
    // добавить автора
    @Override
    public BookForm addAuthor(BookForm bookForm, MultipartFile file) throws IOException {
        if(file.getSize()>0)
            bookForm.setImageFile(Base64.getEncoder().encodeToString(file.getBytes()));

        List<Author> authors = new ArrayList<>();
        if(bookForm.getSelects() != null)
            authors = bookForm.getSelects();

        addAuthorFromBookForm(bookForm, authors, true);
        bookForm.setAuthorId(null);
        return bookForm;
    }
    // взять - вернуть книгу
    @Override
    public Book getReturn(Book book) {
        book = bookRepository.save(book);
        return book;
    }

    @Override
    public Iterable<Book> getAll() {
        var books = bookRepository.findAll();
        if (books.iterator().hasNext()) {
            return books;
        }
        return null;
    }

    @Override
    public Optional<Book> getById(Integer id) { return bookRepository.findById(id);}

    @Override
    public void deleteById(Integer id) {
        Integer bookId = bookRepository.findById(id).get().getId();
        bookAuthorRepository.deleteAllByBookId(bookId);
        bookRepository.deleteById(id);
    }
    @Override
    public BookForm updateForm(Optional<Book> bookUp, Set<BookAuthor> bookAuthors) {
            Book book = bookUp.get();
            BookForm bookForm = new BookForm();
            bookForm.setId(book.getId());
            bookForm.setCode(book.getCode());
            bookForm.setTitle(book.getName());
            bookForm.setYear(book.getYearRelease());
            bookForm.setPages(book.getQuantityPages());
            bookForm.setQuantityInstances(book.getQuantityInstances());
            bookForm.setGenreId(book.getGenre().getId());
            bookForm.setPublicationId(book.getPublication().getId());
            bookForm.setSelects(new ArrayList<>());
            bookForm.setImageFile(book.getImageFile());

        return bookForm;
    }

    @Override
    public Book update(BookForm bookForm, MultipartFile file) throws IOException {
        // 1. извлечь данные книги
        Integer bookId = bookForm.getId();

        Book book =bookRepository.findById(bookId).get();
        book.setId(bookId);
        book.setCode(bookForm.getCode());
        book.setName(bookForm.getTitle());
        book.setYearRelease(bookForm.getYear());
        book.setQuantityPages(bookForm.getPages());
        book.setQuantityInstances(bookForm.getQuantityInstances());
        if(book.getHandedOut() == null) book.setHandedOut(0);
        book.setQuantityStock(book.getQuantityInstances()-book.getHandedOut());
        // 2. извлечь жанр и издательство
        Integer genreId = bookForm.getGenreId();
        Genre genre = genreRepository.findById(genreId).get();
        book.setGenre(genre);

        Integer publicationId = bookForm.getPublicationId();
        Publication publication = publicationRepository.findById(publicationId).get();
        book.setPublication(publication);
        book.setBookAuthors(new HashSet<>());
        // 3. извлечь картинку
        if(file.getSize()>0)
            book.setImageFile(Base64.getEncoder().encodeToString(file.getBytes()));
        else
            if(bookForm.getImageFile()!=null)
                book.setImageFile(bookForm.getImageFile());
        // 4. извлечь авторов
        List<Author> authors = bookForm.getSelects();
        addAuthorFromBookForm(bookForm, authors, false);
        // 5. добавляем книгу
        bookAuthorRepository.deleteAllByBookId(bookId);
        if(authors.size()>0)
            for (var author : authors) {
                if(author!=null) {
                    BookAuthor bookAuthor = new BookAuthor();
                    bookAuthor.setBook(book);
                    bookAuthor.setAuthor(author);
                    bookAuthorRepository.save(bookAuthor);
                }
            }
        Book bookUpdated = bookRepository.save(book);
        return book;
    }

    private void addAuthorFromBookForm(BookForm bookForm,
                                       List<Author> authors,
                                       boolean setBookForm) {
        if(bookForm.getAuthorId() != null) {
            Author author = authorRepository.findById(bookForm.getAuthorId()).get();
            Integer authorId = author.getId();
            boolean find = false;
            for (Author a: authors)
                if(Objects.equals(authorId, a.getId())){
                    find = true;
                    break;
                }
            if(!find) {
                authors.add(author);
                if(setBookForm){
                    bookForm.setSelects(authors);
                    bookForm.setLoop(bookForm.getLoop() + 1);
                }
            }
        }
    }
    // Инвентаризация
    @Override
    public void inventory(InventoryForm inventoryForm) throws ParseException {
        if (!inventoryForm.getDate().equals("")) {
            String date = inventoryForm.getDate();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date dateInventory = format.parse(date);
            for (Book b: inventoryForm.getBooks())
                if(b.getDateInventory()==null &&
                        b.getQuantityInventory()!= null) {
                    b.setDateInventory(dateInventory);
                    bookRepository.save(b);
                }
        }
    }

    @Override
    public InventoryForm clearInventory(InventoryForm inventoryForm) {
        for(Book b: inventoryForm.getBooks()){
            b.setDateInventory(null);
            b.setQuantityInventory(null);
            bookRepository.save(b);
        }
        return inventoryForm;
    }
    // найти книгу
    @Override
    public List<Book> getFindBook(FindBook findBook) {
        var books = new ArrayList<>((Collection<Book>) bookRepository.findAll());
        String title = findBook.getTitle();
        String author = findBook.getAuthor();
        String code = findBook.getCode();
        if (!Objects.equals(title, "") &&
                Objects.equals(author, "") &&
                Objects.equals(code, "")) {
            return books.stream()
                    .filter(book -> book.getName()
                            .toLowerCase()
                            .contains(title.toLowerCase()))
                    .toList();
        }
        if (Objects.equals(title, "") &&
                !Objects.equals(author, "") &&
                Objects.equals(code, "")) {
            return books.stream()
                    .filter(book -> book
                            .getBookAuthors()
                            .stream()
                            .anyMatch(a -> a.getAuthor()
                                    .getFullName()
                                    .toLowerCase()
                                    .contains(author.toLowerCase())
                            )
                    )
                    .toList();
        }
        if (Objects.equals(title, "") &&
                Objects.equals(author, "") &&
                !Objects.equals(code, "")) {
            return books.stream()
                    .filter(book -> book.getCode()
                            .toLowerCase()
                            .contains(code.toLowerCase()))
                    .toList();
        }
        if (!Objects.equals(title, "") &&
                !Objects.equals(author, "") &&
                Objects.equals(code, "")) {
                return books.stream()
                    .filter(book -> book
                            .getBookAuthors()
                            .stream()
                            .anyMatch(a -> a.getAuthor()
                                .getFullName()
                                .toLowerCase()
                                .contains(author.toLowerCase())
                            ) &&
                            book.getName()
                                .toLowerCase()
                                .contains(title.toLowerCase()
                                )
                    )
                    .toList();
        }
        if (Objects.equals(title, "") &&
                !Objects.equals(author, "") &&
                !Objects.equals(code, "")) {
            return books.stream()
                    .filter(book -> book
                            .getBookAuthors()
                            .stream()
                            .anyMatch(a -> a.getAuthor()
                                    .getFullName()
                                    .toLowerCase()
                                    .contains(author.toLowerCase())
                            ) &&
                            book.getCode()
                                    .toLowerCase()
                                    .contains(code.toLowerCase()
                                    )
                    )
                    .toList();
        }
        if (!Objects.equals(title, "") &&
                Objects.equals(author, "") &&
                !Objects.equals(code, "")) {
            return books.stream()
                    .filter(book -> book.getName()
                            .toLowerCase()
                            .contains(title.toLowerCase()) &&
                    book.getCode()
                            .toLowerCase()
                            .contains(code.toLowerCase()
                            )
                    )
                    .toList();
        }

        return books.stream()
                .filter(book -> book
                        .getBookAuthors()
                        .stream()
                        .anyMatch(a -> a.getAuthor()
                                .getFullName()
                                .toLowerCase()
                                .contains(author.toLowerCase())
                        ) &&
                        book.getName()
                                .toLowerCase()
                                .contains(title.toLowerCase()
                                ) &&
                        book.getCode()
                                .toLowerCase()
                                .contains(code.toLowerCase()
                                )
                )
                .toList();
    }
}
