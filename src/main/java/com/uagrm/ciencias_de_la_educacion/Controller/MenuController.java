package com.uagrm.ciencias_de_la_educacion.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uagrm.ciencias_de_la_educacion.Model.MenuEntity;
import com.uagrm.ciencias_de_la_educacion.Service.MenuService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;





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

    @PostMapping("")
    public MenuEntity createMenu(@RequestBody MenuEntity menu) {
        return menuService.saveMenu(menu);
    }
    
    @PutMapping("/{id}")
    public MenuEntity updateMenu(@PathVariable Long id, @RequestBody MenuEntity menuDetails) {
        MenuEntity menu = menuService.getMenuById(id);
        menu.setTitulo(menuDetails.getTitulo());
        menu.setRuta(menuDetails.getRuta());
        menu.setIcono(menuDetails.getIcono());
        menu.setOrden(menuDetails.getOrden());
        menu.setEstado(menuDetails.getEstado());

        MenuEntity updatedMenu = menuService.saveMenu(menu);
        return updatedMenu;
    }

    public void deleteMenu(Long id) {
        menuService.deleteMenu(id);
    }
    
    

    
}
