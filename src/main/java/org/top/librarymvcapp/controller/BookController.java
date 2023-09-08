package org.top.librarymvcapp.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.top.librarymvcapp.controller.form.BookForm;
import org.top.librarymvcapp.controller.form.FindBook;
import org.top.librarymvcapp.controller.form.InventoryForm;
import org.top.librarymvcapp.controller.form.Message;
import org.top.librarymvcapp.entity.Author;
import org.top.librarymvcapp.entity.Book;
import org.top.librarymvcapp.service.AuthorService;
import org.top.librarymvcapp.service.BookService;
import org.top.librarymvcapp.service.GenreService;
import org.top.librarymvcapp.service.PublicationService;

import java.io.IOException;
import java.text.ParseException;
import java.time.Year;
import java.util.*;

@Controller
@RequestMapping("book")
@RequiredArgsConstructor
@Transactional
@SessionAttributes({"bookForm", "inventoryForm", "message", "findBook" })
public class BookController {

    private final BookService bookService;
    private final GenreService genreService;
    private final PublicationService publicationSer;
    private final AuthorService authorService;

    @GetMapping("")
    public String bookList(Model model, Message message) {
        List<Book> books = (List<Book>) bookService.getAll();
        books.sort(Comparator.comparing(Book::getName));
        message.setMessage("list");
        model.addAttribute("books", books);
        model.addAttribute("message",message.getMessage());
        return "book/book-list";
    }

    // обработчики добавления книги
    @GetMapping("new")
    public String bookForm(Model model) {
        BookForm bookForm = new BookForm();
        modelBookForm(model, bookForm);
        Integer loop = 1;
        List<Author> selects=new ArrayList<>();
        model.addAttribute("loop",loop);
        model.addAttribute("selects", selects);
        return "book/book-form";
    }

    private void modelBookForm(Model model, BookForm bookForm) {
        var genres = genreService.getAll();
        var publications = publicationSer.getAll();
        int maxYear = Year.now().getValue();
        var authors = authorService.getAll();

        model.addAttribute("bookForm", bookForm);
        model.addAttribute("publications", publications);
        model.addAttribute("genres", genres);
        model.addAttribute("authors", authors);
        model.addAttribute("maxYear", maxYear);
    }

    @PostMapping("new")
    public String addBook(@ModelAttribute("bookForm") BookForm bookForm,
                          @RequestParam(value="action") String action,
                          @RequestParam("file") MultipartFile file,
                          Model model) throws IOException {
        if (action.equals("Добавить автора")) {
           bookService.addAuthor(bookForm, file);
           modelBookForm(model, bookForm);
           return "/book/book-form";
        }

        if (action.equals("Сохранить")) {
            bookService.add(bookForm, file);
        }
         return "redirect:/book";
    }
    // обработчик получения информации о книге
    @GetMapping("details/{id}")
    public String bookDetails(@RequestParam(required = false) String anchor,
                              @PathVariable Integer id,
                              @ModelAttribute("message") Message message,
                              Model model) {
        var book = bookService.getById(id);
        if (book.isPresent()) {
            if(anchor!=null)
                model.addAttribute("anchor",anchor);
            model.addAttribute("book", book.get());
            model.addAttribute("message",message.getMessage());

            return "book/book-details";
        }
        return "redirect:/book";
    }
    // обработчики взять - вернуть книгу
    @GetMapping("getBook/{id}")
    public String getBook(@PathVariable Integer id) {
        var getBook = bookService.getById(id);
        if (getBook.isPresent()) {
            Book changed = getBook.get();
            if(changed.getHandedOut()==null) {changed.setHandedOut(0);}
            else {
            changed.setHandedOut(changed.getHandedOut() + 1);
            changed.setQuantityStock(changed.getQuantityInstances() -
                    changed.getHandedOut());
        }
            bookService.getReturn(changed);
            return "redirect:/book/details/"+id;
        }
        return "redirect:/book";
    }

    @GetMapping("returnBook/{id}")
    public String returnBook(@PathVariable Integer id) {
        var book = bookService.getById(id);
        if (book.isPresent()) {
            Book changed = book.get();
            if(changed.getHandedOut()==null) {changed.setHandedOut(0);}
            else {
                changed.setHandedOut(changed.getHandedOut() - 1);
                changed.setQuantityStock(changed.getQuantityInstances() -
                        changed.getHandedOut());
                bookService.getReturn(changed);
            }
            return "redirect:/book/details/"+id;
        }
        return "redirect:/book";
    }

    @GetMapping("delete/{id}")
    public String deleteBook(@PathVariable Integer id,
                             @ModelAttribute("message") Message message){
        Optional<Book> deleted = bookService.getById(id);
        if(deleted.isPresent())
            bookService.deleteById(id);
        if (message.getMessage().equals("found"))
            return "redirect:/book/found";

        return "redirect:/book";
    }
    // обработчики обновления книги
    @GetMapping("update/{id}")
    public String updateBook(@PathVariable Integer id, Model model) {
        Optional<Book> bookUp = bookService.getById(id);
        if(bookUp.isPresent()) {
            var bookAuthors = bookUp.get().getBookAuthors();
            BookForm bookForm = bookService.updateForm(bookUp,bookAuthors);
            modelBookForm(model, bookForm);
            model.addAttribute("bookAuthors", bookAuthors);

            return "book/book-form-update";
        }
        return "redirect:/book";
    }

    @PostMapping("update")
    public String updateBook(@ModelAttribute("bookForm") BookForm bookForm,
                             @ModelAttribute("message") Message message,
                             @RequestParam(value="action") String action,
                             @RequestParam("file") MultipartFile file,
                             Model model) throws IOException {
        if (action.equals("Добавить автора")) {
            bookService.addAuthor(bookForm, file);

            Optional<Book> bookUp = bookService.getById(bookForm.getId());
            var bookAuthors = bookUp.get().getBookAuthors();

            modelBookForm(model, bookForm);
            model.addAttribute("bookAuthors", bookAuthors);
            return "book/book-form-update";
        }
        if (action.equals("Сохранить")) {
            if(bookForm.getId()!=null){
                if(bookForm.getYear()> Year.now().getValue()) {
                  return "book/book-form-update";
                }
                 bookService.update(bookForm, file);
            }
        }
        if (message.getMessage().equals("found"))
            return "redirect:/book/found";
        return "redirect:/book";
    }
    // обработчики инвентаризации
    @GetMapping("inventory")
    public String newInventory(Model model){
        InventoryForm inventoryForm = new InventoryForm();
        List<Book> books = (List<Book>)bookService.getAll();
        books.sort(Comparator.comparing(Book::getName));
        inventoryForm.setBooks(books);
        model.addAttribute("inventoryForm", inventoryForm);
        return "book/book-inventory";
    }
    @PostMapping("inventory")
    public String addInventory(@ModelAttribute("inventoryForm") InventoryForm inventoryForm,
                               @RequestParam(value="action") String action,
                               Model model) throws ParseException {
        if (action.equals("Очистить список")) {
            bookService.clearInventory(inventoryForm);
            model.addAttribute("inventoryForm", inventoryForm);
            return "book/book-inventory";
        }
        if (action.equals("Сохранить"))
            bookService.inventory(inventoryForm);

        return "redirect:/book";
    }
    // обработчики найти книгу
    @GetMapping("find")
    public String findBook(Model model){
        FindBook findBook = new FindBook();
        model.addAttribute("findBook", findBook);
        return "book/book-find";
    }
    @GetMapping("found")
    public String foundBook(Model model,
                            @ModelAttribute("findBook")FindBook findBook,
                            Message message){
        if (findBook.isEmpty()) return "redirect:/book";
        List<Book> books = bookService.getFindBook(findBook);
        message.setMessage("found");
        model.addAttribute("books", books);
        model.addAttribute("message",message.getMessage());
        return "book/book-found";
    }
}
