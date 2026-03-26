package fit.hutech.spring.controllers;

import fit.hutech.spring.entities.Book;
import fit.hutech.spring.services.BookService;
import fit.hutech.spring.services.CategoryService;
import fit.hutech.spring.services.WarehouseService;
import fit.hutech.spring.utils.FileUploadUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/admin/books")
@RequiredArgsConstructor
public class AdminBookController {
    private final BookService bookService;
    private final CategoryService categoryService;
    private final WarehouseService warehouseService;
    private final FileUploadUtil fileUploadUtil;

    @GetMapping
    public String showAdminBookList(Model model) {
        model.addAttribute("books", bookService.getAllBooks(0, 100, "id"));
        return "admin/book_list";
    }

    @GetMapping("/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("warehouses", warehouseService.getAllWarehouses());
        return "admin/book_add";
    }

    @PostMapping("/add")
    public String addBook(
            @Valid @ModelAttribute("book") Book book,
            BindingResult bindingResult,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("warehouses", warehouseService.getAllWarehouses());
            return "admin/book_add";
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = fileUploadUtil.saveFile(imageFile);
                book.setImageUrl(imageUrl);
            } catch (IOException e) {
                model.addAttribute("errorMessage", "Lỗi khi tải lên hình ảnh: " + e.getMessage());
                model.addAttribute("categories", categoryService.getAllCategories());
                model.addAttribute("warehouses", warehouseService.getAllWarehouses());
                return "admin/book_add";
            }
        }

        bookService.addBook(book);
        return "redirect:/admin/books";
    }

    @GetMapping("/edit/{id}")
    public String showEditBookForm(@PathVariable Long id, Model model) {
        var book = bookService.getBookById(id).orElse(null);
        if (book == null) {
            return "redirect:/admin/books";
        }
        model.addAttribute("book", book);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("warehouses", warehouseService.getAllWarehouses());
        return "admin/book_edit";
    }

    @PostMapping("/edit/{id}")
    public String updateBook(
            @PathVariable Long id,
            @Valid @ModelAttribute("book") Book book,
            BindingResult bindingResult,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model) {
        if (bindingResult.hasErrors()) {
            book.setId(id);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("warehouses", warehouseService.getAllWarehouses());
            return "admin/book_edit";
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = fileUploadUtil.saveFile(imageFile);
                book.setImageUrl(imageUrl);
            } catch (IOException e) {
                model.addAttribute("errorMessage", "Lỗi khi tải lên hình ảnh: " + e.getMessage());
                model.addAttribute("categories", categoryService.getAllCategories());
                model.addAttribute("warehouses", warehouseService.getAllWarehouses());
                return "admin/book_edit";
            }
        }

        bookService.updateBook(book);
        return "redirect:/admin/books";
    }

    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBookById(id);
        return "redirect:/admin/books";
    }

    @GetMapping("/restore-all")
    public String restoreAllDeletedBooks() {
        bookService.restoreAllDeletedBooks();
        return "redirect:/admin/books";
    }
}
