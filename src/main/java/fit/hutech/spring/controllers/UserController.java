package fit.hutech.spring.controllers;

import fit.hutech.spring.entities.User;
import fit.hutech.spring.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final fit.hutech.spring.repositories.IInvoiceRepository invoiceRepository;

    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        log.info("Accessing registration page");
        model.addAttribute("user", new User());
        return "user/register";
    }

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("user") User user,
            BindingResult bindingResult,
            Model model) {
        log.info("Processing registration for user: {}", user.getUsername());

        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "user/register";
        }

        // Check for duplicates
        if (userService.existsByUsername(user.getUsername())) {
            model.addAttribute("errors", new String[] { "Tên đăng nhập đã tồn tại!" });
            return "user/register";
        }
        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("errors", new String[] { "Email đã được sử dụng!" });
            return "user/register";
        }
        if (userService.existsByPhone(user.getPhone())) {
            model.addAttribute("errors", new String[] { "Số điện thoại đã được sử dụng!" });
            return "user/register";
        }

        try {
            userService.save(user);
            userService.setDefaultRole(user.getUsername());
        } catch (Exception e) {
            log.error("Registration error: ", e);
            model.addAttribute("errors", new String[] { "Có lỗi xảy ra trong quá trình đăng ký. Vui lòng thử lại!" });
            return "user/register";
        }

        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String profile(Model model, java.security.Principal principal) {
        if (principal != null) {
            var user = userService.findByUsername(principal.getName()).orElseThrow();
            model.addAttribute("user", user);
            model.addAttribute("invoices", invoiceRepository.findAllByUserId(user.getId()));
        }
        return "user/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfile(@NotNull Model model, java.security.Principal principal) {
        if (principal != null) {
            userService.findByUsername(principal.getName()).ifPresent(user -> {
                model.addAttribute("user", user);
            });
        }
        return "user/edit_profile";
    }

    @PostMapping("/profile/edit")
    public String editProfile(@Valid @ModelAttribute("user") User user,
            @NotNull BindingResult bindingResult,
            Model model,
            java.security.Principal principal) {
        if (bindingResult.hasErrors()) {
            // Check if errors are only related to password (which might be empty)
            boolean onlyPasswordError = bindingResult.getFieldErrors().stream()
                    .allMatch(error -> error.getField().equals("password"));

            if (!onlyPasswordError || (user.getPassword() != null && !user.getPassword().isEmpty())) {
                var errors = bindingResult.getAllErrors()
                        .stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .toArray(String[]::new);
                model.addAttribute("errors", errors);
                log.warn("Validation errors during profile edit for user {}: {}", principal.getName(), errors); // Added
                                                                                                                // logging
                return "user/edit_profile";
            }
        }

        var existingUser = userService.findByUsername(principal.getName()).orElseThrow();
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());

        // Update password only if provided
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        userService.save(existingUser);
        return "redirect:/profile";
    }

    @GetMapping("/orders")
    public String orderHistory(@NotNull Model model, java.security.Principal principal) {
        if (principal != null) {
            var user = userService.findByUsername(principal.getName()).orElseThrow();
            model.addAttribute("invoices", invoiceRepository.findAllByUserId(user.getId()));
        }
        return "user/orders";
    }

    @GetMapping("/orders/detail/{id}")
    public String orderDetail(@PathVariable Long id, @NotNull Model model, java.security.Principal principal) {
        if (principal == null)
            return "redirect:/login";
        var invoice = invoiceRepository.findById(id).orElseThrow();

        // Security check: ensure invoice belongs to user
        if (!invoice.getUser().getUsername().equals(principal.getName())) {
            return "redirect:/orders";
        }

        model.addAttribute("invoice", invoice);
        return "user/order_detail";
    }
}
