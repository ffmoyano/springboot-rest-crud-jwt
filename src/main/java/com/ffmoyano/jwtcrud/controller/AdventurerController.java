package com.ffmoyano.jwtcrud.controller;

import com.ffmoyano.jwtcrud.dto.AdventurerDto;
import com.ffmoyano.jwtcrud.service.AdventurerService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/adventurer/")
public class AdventurerController {

    private final AdventurerService service;

    public AdventurerController(AdventurerService service) {
        this.service = service;
    }

    @GetMapping("/")
    public ResponseEntity<List<AdventurerDto>> findAll() {
        return new ResponseEntity<>(service.findAll(), HttpStatus.ACCEPTED);
    }

    // path example: localhost:8080/adventurer/1
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable(value = "id", required = true) Long id) {
        AdventurerDto adventurerDto;
        Map<String, Object> response = new HashMap<>();
        try {
            adventurerDto = service.findById(id);
        } catch (DataAccessException e) {
            response.put("error", String.format("error", "%s: %s", e.getMessage(), e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (adventurerDto == null) {
            response.put("error", "No se encontró el personaje");
            response.put("mensaje", String.format("El personaje ID: %d no existe en la base de datos", id));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(adventurerDto, HttpStatus.ACCEPTED);
    }

    @PostMapping("/")
    public ResponseEntity<?> create(@Valid @ModelAttribute AdventurerDto adventurerDto, BindingResult result) {

        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(e -> String.format("El campo '%s' %s", e.getField(), e.getDefaultMessage()))
                    .collect(Collectors.toList());
            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            adventurerDto = service.save(adventurerDto);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al crear el personaje en la base de datos");
            response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "El personaje ha sido creado con éxito");
        response.put("personaje", adventurerDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id", required = true) Long id) {
        Map<String, Object> response = new HashMap<>();
        AdventurerDto adventurerDto = service.findById(id);
        try {
            service.delete(adventurerDto);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al borrar el personaje de la base de datos");
            response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "El personaje ha sido eliminado con éxito");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @PutMapping("/")
    public ResponseEntity<?> update(@Valid @ModelAttribute AdventurerDto adventurerDto, BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(e -> String.format("El campo '%s' %s", e.getField(), e.getDefaultMessage()))
                    .collect(Collectors.toList());
            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            if (adventurerDto.getId() != null) {
                service.save(adventurerDto);
            } else {
                response.put("mensaje", "Falta el Id del personaje");
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (DataAccessException e) {
            response.put("mensaje", "Error al actualizar el personaje en la base de datos");
            response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "El personaje ha sido actualizado con éxito");
        response.put("personaje", adventurerDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
