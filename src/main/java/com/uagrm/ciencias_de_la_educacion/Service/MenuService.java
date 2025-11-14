package com.uagrm.ciencias_de_la_educacion.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uagrm.ciencias_de_la_educacion.Model.MenuEntity;
import com.uagrm.ciencias_de_la_educacion.Repository.MenuRepository;

@Service
public class MenuService {
    @Autowired
    private MenuRepository menuRepository;

public List<MenuEntity> getAllMenus() {
        return menuRepository.findAll();
    }

    public MenuEntity saveMenu(MenuEntity menu) {
        return menuRepository.save(menu);
    }

    public MenuEntity getMenuById(Long id) {
        return menuRepository.findById(id).orElse(null);
    }

    public void deleteMenu(Long id) {
        menuRepository.deleteById(id);
    }

}
