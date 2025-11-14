package com.uagrm.ciencias_de_la_educacion.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante para transacciones

import com.uagrm.ciencias_de_la_educacion.Model.MenuEntity;
import com.uagrm.ciencias_de_la_educacion.Model.Sub_MenuEntity; // Importar Modelo SubMenu
import com.uagrm.ciencias_de_la_educacion.Repository.MenuRepository;
import com.uagrm.ciencias_de_la_educacion.Repository.Sub_MenuRepository; // Importar Repo SubMenu

@Service
public class MenuService {
    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private Sub_MenuRepository subMenuRepository; // Inyectamos el repo de SubMenú

    public List<MenuEntity> getAllMenus() {
        return menuRepository.findAll();
    }

    public MenuEntity getMenuById(Long id) {
        return menuRepository.findById(id).orElse(null);
    }

    // Método para GUARDAR cambios (usado en el PUT/Update)
    public MenuEntity saveMenu(MenuEntity menu) {
        return menuRepository.save(menu);
    }

    // --- NUEVO MÉTODO: CREAR MENÚ + SUBMENÚ POR DEFECTO ---
    @Transactional // Si falla algo, se deshace todo
    public MenuEntity createMenuWithSubMenu(MenuEntity menu) {
        // 1. Guardamos el Menú Padre
        MenuEntity menuGuardado = menuRepository.save(menu);

        // 2. Creamos el SubMenú por defecto
        Sub_MenuEntity subMenuDefault = new Sub_MenuEntity();
        subMenuDefault.setTitulo("General"); // Título por defecto
        
        // Generamos una ruta basada en el padre. Ej: Padre="/historia" -> Hijo="/historia/general"
        String rutaPadre = menuGuardado.getRuta() != null ? menuGuardado.getRuta() : "";
        subMenuDefault.setRuta(rutaPadre + "/general"); 
        
        subMenuDefault.setIcono("AppstoreOutlined"); // Icono genérico
        subMenuDefault.setOrden(1);
        subMenuDefault.setEstado(true);

        // 3. Asignamos las relaciones
        subMenuDefault.setMenu_id(menuGuardado); // Asignamos el padre
        subMenuDefault.setUsuario_id(menuGuardado.getUsuario_id()); // Mismo usuario que creó el padre

        // 4. Guardamos el SubMenú
        subMenuRepository.save(subMenuDefault);

        return menuGuardado;
    }

    public void deleteMenu(Long id) {
        // Nota: Asegúrate de que tu BD tenga ON DELETE CASCADE o borra los hijos aquí primero
        menuRepository.deleteById(id);
    }
}