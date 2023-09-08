package org.top.librarymvcapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.top.librarymvcapp.controller.form.FindBook;
import org.top.librarymvcapp.controller.form.Message;
import org.top.librarymvcapp.controller.form.UserBook;
import org.top.librarymvcapp.entity.Book;
import org.top.librarymvcapp.service.BookService;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("user")
@RequiredArgsConstructor
@SessionAttributes({"message", "findBook", "userBook" })
public class UserController {

    private final BookService bookService;

    @GetMapping("")
    public String userBook(Model model, Message message) {
       UserBook userBook = new UserBook();
       List<Book> userBooks = (List<Book>) bookService.getAll();
        userBooks.sort(Comparator.comparing(Book::getName));
       if(!userBooks.isEmpty()) {
           userBooks.sort(Comparator.comparing(Book::getName));
           int page = 1;
           int records = 6;
           int pages;
           if ((userBooks.size()%records) == 0)
               pages = userBooks.size()/records;
           else
               pages = userBooks.size()/records+1;
           int size = userBooks.size();
           int bookNumber = 0;
           List<Book> books = userBooks.stream()
                   .limit(records)
                   .toList();
           userBook.setPage(page);
           userBook.setRecords(records);
           userBook.setBookNumber(bookNumber);
           userBook.setPages(pages);
           userBook.setSize(size);
           userBook.setUserBooks(userBooks);
           userBook.setBooks(books);
           model.addAttribute("userBook", userBook);
           message.setMessage("list");
        model.addAttribute("message",message.getMessage());
       }
        return "user/user-form";
    }
    // обработчики странц
    @GetMapping("nextPage")
    public String nextPage(@ModelAttribute("userBook") UserBook userBook,
                           Message message, Model model){
        int page=userBook.getPage();
        int pages=userBook.getPages();
        int records=userBook.getRecords();
        int bookNumber = page*records;
        if(page<pages){
            List<Book> books = userBook.getUserBooks().stream()
                    .skip((long) page *records)
                    .limit(records)
                    .toList();
            userBook.setBooks(books);
            userBook.setPage(page+1);
            userBook.setBookNumber(bookNumber);
            model.addAttribute("userBook", userBook);
            message.setMessage("list");
            model.addAttribute("message",message.getMessage());
        }
        return "user/user-form";
    }

    @GetMapping("previewPage")
    public String previewPage(@ModelAttribute("userBook") UserBook userBook,
                              Message message, Model model){
        int page=userBook.getPage();
        int records=userBook.getRecords();
        int bookNumber = userBook.getBookNumber()-records;
        List<Book> books;
        page=page-1;
        if(page==1)
            books = userBook.getUserBooks().stream()
                    .limit(records)
                    .toList();
        else
            books = userBook.getUserBooks().stream()
                    .skip((long) (page - 1) * records)
                    .limit(records)
                    .toList();
            userBook.setBooks(books);
            userBook.setPage(page);
            userBook.setBookNumber(bookNumber);
            model.addAttribute("userBook", userBook);
            message.setMessage("list");
            model.addAttribute("message",message.getMessage());

        return "user/user-form";
    }

    @GetMapping("page")
    public String bookPage(@ModelAttribute("userBook") UserBook userBook,
                              Message message, Model model){
        int page=userBook.getPage();
        int records=userBook.getRecords();
        List<Book> books;
        if(page==1)
            books = userBook.getUserBooks().stream()
                    .limit(records)
                    .toList();
        else
            books = userBook.getUserBooks().stream()
                    .skip((long) (page - 1) * records)
                    .limit(records)
                    .toList();
        userBook.setBooks(books);
        model.addAttribute("userBook", userBook);
        model.addAttribute("message",message.getMessage());

        return "user/user-form";
    }
    // обработчик получения информации о книге
    @GetMapping("details/{id}")
    public String bookDetails(@RequestParam(required = false) String anchor,
                              @PathVariable Integer id,
                              @ModelAttribute("message") Message message,
                              @ModelAttribute("userBook") UserBook userBook,
                              Model model) {
        var book = bookService.getById(id);
        if (book.isPresent()) {
            if(anchor!=null)
                model.addAttribute("anchor",anchor);
            model.addAttribute("book", book.get());
            model.addAttribute("message",message.getMessage());
            model.addAttribute("userBook", userBook);
            return "user/user-details-card";
        }
        return "redirect:/user";
    }
    // обработчики найти книгу
    @GetMapping("find")
    public String findBook(Model model){
        FindBook findBook = new FindBook();
        List<Book> books = (List<Book>)bookService.getAll();
        model.addAttribute("findBook", findBook);
        return "user/user-find";
    }

    @GetMapping("found")
    public String foundBook(Model model,
                            @ModelAttribute("findBook")FindBook findBook,
                            Message message){
        if (findBook.isEmpty()) return "redirect:/user";
        List<Book> books =(List<Book>) bookService.getFindBook(findBook);
        message.setMessage("found");
        model.addAttribute("books", books);
        model.addAttribute("message",message.getMessage());
        return "user/user-found";
    }
}
