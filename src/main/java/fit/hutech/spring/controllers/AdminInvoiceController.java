package fit.hutech.spring.controllers;

import fit.hutech.spring.entities.Invoice;
import fit.hutech.spring.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/invoices")
@RequiredArgsConstructor
public class AdminInvoiceController {
    private final InvoiceService invoiceService;

    @GetMapping
    public String listInvoices(@RequestParam(value = "search", required = false) Long search, Model model) {
        if (search != null) {
            model.addAttribute("invoices", invoiceService.getInvoiceById(search).map(java.util.List::of).orElse(java.util.Collections.emptyList()));
            model.addAttribute("search", search);
        } else {
            model.addAttribute("invoices", invoiceService.getAllInvoices());
        }
        return "admin/invoice_list";
    }

    @GetMapping("/{id}")
    public String viewInvoice(@PathVariable Long id, Model model) {
        Invoice invoice = invoiceService.getInvoiceById(id).orElse(null);
        if (invoice == null) {
            return "redirect:/admin/invoices";
        }
        model.addAttribute("invoice", invoice);
        return "admin/invoice_detail";
    }

    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable Long id, @RequestParam("status") String status) {
        invoiceService.updateInvoiceStatus(id, status);
        return "redirect:/admin/invoices/" + id;
    }
}
