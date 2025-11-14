package com.uagrm.ciencias_de_la_educacion.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

import com.uagrm.ciencias_de_la_educacion.Model.MenuEntity;
import com.uagrm.ciencias_de_la_educacion.Service.MenuService;

@RestController
@RequestMapping("/api/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;

    @GetMapping("")
    public List<MenuEntity> getMenu() {
        return menuService.getAllMenus();
    }

    @GetMapping("/{id}")
    public MenuEntity getMenuById(@PathVariable Long id) {
        return menuService.getMenuById(id);
    }

    // --- MODIFICADO: Usamos el método especial para crear ---
    @PostMapping("")
    public MenuEntity createMenu(@RequestBody MenuEntity menu) {
        // Llama al servicio que crea el menú Y el submenú automático
        return menuService.createMenuWithSubMenu(menu);
    }

    @PutMapping("/{id}")
    public MenuEntity updateMenu(@PathVariable Long id, @RequestBody MenuEntity menuDetails) {
        MenuEntity menu = menuService.getMenuById(id);

        if (menu != null) {
            menu.setTitulo(menuDetails.getTitulo());
            menu.setRuta(menuDetails.getRuta());
            menu.setIcono(menuDetails.getIcono());
            menu.setOrden(menuDetails.getOrden());
            menu.setEstado(menuDetails.getEstado());
            // Opcional: Si quieres actualizar el usuario que modificó
            if (menuDetails.getUsuario_id() != null) {
                menu.setUsuario_id(menuDetails.getUsuario_id());
            }

            // Aquí usamos saveMenu normal, NO queremos crear otro submenú al editar
            return menuService.saveMenu(menu);
        }
        return null;
    }

    // Agregamos la anotación DeleteMapping que faltaba en tu código anterior
    @DeleteMapping("/{id}")
    public void deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
    }
}