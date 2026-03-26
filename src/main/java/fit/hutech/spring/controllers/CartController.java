package fit.hutech.spring.controllers;

import fit.hutech.spring.services.CartService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.security.Principal;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final fit.hutech.spring.services.UserService userService;

    private final fit.hutech.spring.services.BookService bookService;

    @GetMapping
    public String showCart(HttpSession session,
            @NotNull Model model) {
        model.addAttribute("cart", cartService.getCart(session));
        model.addAttribute("totalPrice", cartService.getSumPrice(session));
        model.addAttribute("totalQuantity", cartService.getSumQuantity(session));
        return "book/cart";
    }

    @GetMapping("/removeFromCart/{id}")
    public String removeFromCart(HttpSession session,
            @PathVariable Long id) {
        var cart = cartService.getCart(session);
        cart.removeItems(id);
        return "redirect:/cart";
    }

    @GetMapping("/updateCart/{id}/{quantity}")
    public String updateCart(HttpSession session,
            @PathVariable Long id,
            @PathVariable int quantity,
            @NotNull Model model) {
        var cart = cartService.getCart(session);
        var book = bookService.getBookById(id).orElseThrow();
        
        if (book.getQuantity() < quantity) {
            model.addAttribute("errorMessage", "Trong kho không đủ sách " + book.getTitle() + " vui lòng chọn sách khác.");
        } else {
            cart.updateItems(id, quantity);
        }
        
        model.addAttribute("cart", cart);
        model.addAttribute("totalPrice", cartService.getSumPrice(session));
        model.addAttribute("totalQuantity", cartService.getSumQuantity(session));
        return "book/cart";
    }

    @GetMapping("/clearCart")
    public String clearCart(HttpSession session) {
        cartService.removeCart(session);
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Principal principal, Model model) {
        if (principal == null)
            return "redirect:/login";
        var user = userService.findByUsername(principal.getName()).orElseThrow();
        try {
            cartService.saveCart(session, user);
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("cart", cartService.getCart(session));
            model.addAttribute("totalPrice", cartService.getSumPrice(session));
            model.addAttribute("totalQuantity", cartService.getSumQuantity(session));
            return "book/cart";
        }
        return "book/checkout_success";
    }
}
