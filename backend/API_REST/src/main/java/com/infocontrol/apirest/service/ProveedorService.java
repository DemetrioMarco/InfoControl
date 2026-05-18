package com.infocontrol.apirest.service;

import com.infocontrol.apirest.dto.request.ProveedorRequest;
import com.infocontrol.apirest.dto.response.ProveedorResponse;
import com.infocontrol.apirest.entity.Proveedor;
import com.infocontrol.apirest.repository.ProveedorRepository;
import com.infocontrol.apirest.exception.base.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    @Transactional(readOnly = true)
    public List<ProveedorResponse> findAll() {
        return proveedorRepository.findAll(Sort.by("razonSocial")).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProveedorResponse> findAllActivos() {
        return proveedorRepository.findAllActivos().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProveedorResponse findById(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con id: " + id));
        return toResponse(proveedor);
    }

    @Transactional(readOnly = true)
    public List<ProveedorResponse> findByRazonSocialOrNombreFantasia(String busqueda) {
        return proveedorRepository.findByRazonSocialOrNombreFantasiaContaining(busqueda).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProveedorResponse create(ProveedorRequest.Create request) {
        if (request.getRut() != null && !request.getRut().isEmpty()) {
            validateUniqueRut(request.getRut(), null);
        }
        if (request.getContactoEmail() != null && !request.getContactoEmail().isEmpty()) {
            validateUniqueEmail(request.getContactoEmail(), null);
        }

        Proveedor proveedor = Proveedor.builder()
                .rut(request.getRut())
                .razonSocial(request.getRazonSocial())
                .nombreFantasia(request.getNombreFantasia())
                .giro(request.getGiro())
                .contactoNombre(request.getContactoNombre())
                .contactoTelefono(request.getContactoTelefono())
                .contactoEmail(request.getContactoEmail())
                .direccion(request.getDireccion())
                .comuna(request.getComuna())
                .ciudad(request.getCiudad())
                .pais(request.getPais() != null ? request.getPais() : "Chile")
                .observaciones(request.getObservaciones())
                .build();
        return toResponse(proveedorRepository.save(proveedor));
    }

    @Transactional
    public ProveedorResponse update(Long id, ProveedorRequest.Update request) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con id: " + id));

        if (request.getRut() != null && !request.getRut().isEmpty()) {
            validateUniqueRut(request.getRut(), id);
        }
        if (request.getContactoEmail() != null && !request.getContactoEmail().isEmpty()) {
            validateUniqueEmail(request.getContactoEmail(), id);
        }

        proveedor.setRut(request.getRut());
        proveedor.setRazonSocial(request.getRazonSocial());
        proveedor.setNombreFantasia(request.getNombreFantasia());
        proveedor.setGiro(request.getGiro());
        proveedor.setContactoNombre(request.getContactoNombre());
        proveedor.setContactoTelefono(request.getContactoTelefono());
        proveedor.setContactoEmail(request.getContactoEmail());
        proveedor.setDireccion(request.getDireccion());
        proveedor.setComuna(request.getComuna());
        proveedor.setCiudad(request.getCiudad());
        proveedor.setPais(request.getPais() != null ? request.getPais() : proveedor.getPais());
        proveedor.setObservaciones(request.getObservaciones());
        proveedor.setActivo(request.getActivo());

        return toResponse(proveedorRepository.save(proveedor));
    }

    @Transactional
    public void delete(Long id) {
        if (!proveedorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Proveedor no encontrado con id: " + id);
        }
        proveedorRepository.deleteById(id);
    }

    @Transactional
    public ProveedorResponse toggleActivo(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con id: " + id));
        proveedor.setActivo(!proveedor.getActivo());
        return toResponse(proveedorRepository.save(proveedor));
    }

    private void validateUniqueRut(String rut, Long id) {
        Optional<Proveedor> existing = proveedorRepository.findByRutIgnoreCase(rut);
        if (existing.isPresent() && (id == null || !existing.get().getId().equals(id))) {
            throw new ResourceNotFoundException("Ya existe un proveedor con el RUT: " + rut);
        }
    }

    private void validateUniqueEmail(String email, Long id) {
        Optional<Proveedor> existing = proveedorRepository.findByContactoEmailIgnoreCase(email);
        if (existing.isPresent() && (id == null || !existing.get().getId().equals(id))) {
            throw new ResourceNotFoundException("Ya existe un proveedor con el email: " + email);
        }
    }

    private ProveedorResponse toResponse(Proveedor proveedor) {
        return ProveedorResponse.builder()
                .id(proveedor.getId())
                .rut(proveedor.getRut())
                .razonSocial(proveedor.getRazonSocial())
                .nombreFantasia(proveedor.getNombreFantasia())
                .giro(proveedor.getGiro())
                .contactoNombre(proveedor.getContactoNombre())
                .contactoTelefono(proveedor.getContactoTelefono())
                .contactoEmail(proveedor.getContactoEmail())
                .direccion(proveedor.getDireccion())
                .comuna(proveedor.getComuna())
                .ciudad(proveedor.getCiudad())
                .pais(proveedor.getPais())
                .observaciones(proveedor.getObservaciones())
                .activo(proveedor.getActivo())
                .fechaCreacion(proveedor.getFechaCreacion())
                .fechaModificacion(proveedor.getFechaModificacion())
                .build();
    }
}
