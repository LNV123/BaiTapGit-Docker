package fit.hutech.spring.controllers;

import fit.hutech.spring.entities.Warehouse;
import fit.hutech.spring.services.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/warehouses")
@RequiredArgsConstructor
public class AdminWarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    public String listWarehouses(Model model) {
        model.addAttribute("warehouses", warehouseService.getAllWarehouses());
        return "admin/warehouse/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("warehouse", new Warehouse());
        return "admin/warehouse/add";
    }

    @PostMapping("/add")
    public String addWarehouse(@Valid @ModelAttribute("warehouse") Warehouse warehouse, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/warehouse/add";
        }
        warehouseService.addWarehouse(warehouse);
        return "redirect:/admin/warehouses";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Warehouse warehouse = warehouseService.getWarehouseById(id).orElseThrow(() -> new IllegalArgumentException("Invalid warehouse Id:" + id));
        model.addAttribute("warehouse", warehouse);
        return "admin/warehouse/edit";
    }

    @PostMapping("/update/{id}")
    public String updateWarehouse(@PathVariable Long id, @Valid Warehouse warehouse, BindingResult result) {
        if (result.hasErrors()) {
            warehouse.setId(id);
            return "admin/warehouse/edit";
        }
        warehouseService.updateWarehouse(warehouse);
        return "redirect:/admin/warehouses";
    }

    @GetMapping("/delete/{id}")
    public String deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouseById(id);
        return "redirect:/admin/warehouses";
    }
}
