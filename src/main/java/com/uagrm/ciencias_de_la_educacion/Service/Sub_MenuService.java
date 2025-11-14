package com.uagrm.ciencias_de_la_educacion.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uagrm.ciencias_de_la_educacion.Model.Sub_MenuEntity;
import com.uagrm.ciencias_de_la_educacion.Repository.Sub_MenuRepository;

@Service
public class Sub_MenuService {
    @Autowired
    private Sub_MenuRepository sub_MenuRepository;

   public List<Sub_MenuEntity> getAllSubMenu() {
        return sub_MenuRepository.findAll();
    }

    public Sub_MenuEntity saveSubMenu(Sub_MenuEntity subMenu) {
        return sub_MenuRepository.save(subMenu);
    }

    public Sub_MenuEntity getSubMenuById(Long id) {
        return sub_MenuRepository.findById(id).orElse(null);
    }

    public void deleteSubMenu(Long id) {
        sub_MenuRepository.deleteById(id);
    }

}
