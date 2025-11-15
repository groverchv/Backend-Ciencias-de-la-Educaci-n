package com.uagrm.ciencias_de_la_educacion.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uagrm.ciencias_de_la_educacion.Model.BitacoraEntity;
import com.uagrm.ciencias_de_la_educacion.Repository.BitacoraRepository;

@Service
public class BitacoraService {
    @Autowired
    private BitacoraRepository bitacoraRepository;

    public List<BitacoraEntity> getAllBitacoras() {
        return bitacoraRepository.findAll();
    }

    public BitacoraEntity saveBitacora(BitacoraEntity bitacora) {
        return bitacoraRepository.save(bitacora);
    }

    public void deleteBitacora(Long id) {
        bitacoraRepository.deleteById(id);
    }
}
