package com.uagrm.ciencias_de_la_educacion.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uagrm.ciencias_de_la_educacion.Model.Sub_MenuEntity;
import com.uagrm.ciencias_de_la_educacion.Service.Sub_MenuService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("api/sub_menu")
public class Sub_MenuController {
    @Autowired
    private Sub_MenuService sub_MenuService;

    @GetMapping("")
    public List<Sub_MenuEntity> getSubMenu() {
        return sub_MenuService.getAllSubMenu();
    }

    @GetMapping("/{id}")
    public Sub_MenuEntity getSubMenuById(@PathVariable Long id) {
        return sub_MenuService.getSubMenuById(id);
    }

    @PostMapping("")
    public Sub_MenuEntity createSubMenu(@RequestBody Sub_MenuEntity sub_Menu) {
        return sub_MenuService.saveSubMenu(sub_Menu);
    }

    @PutMapping("/{id}")
    public Sub_MenuEntity updateSubMenu(@PathVariable Long id, @RequestBody Sub_MenuEntity sub_MenuDetails) {
        Sub_MenuEntity sub_Menu = sub_MenuService.getSubMenuById(id);
        sub_Menu.setTitulo(sub_MenuDetails.getTitulo());
        sub_Menu.setRuta(sub_MenuDetails.getRuta());
        sub_Menu.setIcono(sub_MenuDetails.getIcono());
        sub_Menu.setOrden(sub_MenuDetails.getOrden());
        sub_Menu.setEstado(sub_MenuDetails.getEstado());

        Sub_MenuEntity updatedSubMenu = sub_MenuService.saveSubMenu(sub_Menu);
        return updatedSubMenu;
    }

    @DeleteMapping("/{id}")
    public void deleteSubMenu(@PathVariable Long id) {
        sub_MenuService.deleteSubMenu(id);
    }

}
