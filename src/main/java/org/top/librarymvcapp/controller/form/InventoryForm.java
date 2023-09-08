package org.top.librarymvcapp.controller.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.top.librarymvcapp.entity.Book;

import java.util.List;

@Data
@NoArgsConstructor
public class InventoryForm {
    private List<Book> books;
    String date;
}
