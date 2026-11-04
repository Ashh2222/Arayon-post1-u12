package com.universidad.catalogo.controller;

import com.universidad.catalogo.model.Producto;
import com.universidad.catalogo.service.ProductoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<Producto>> listarPorCategoria(@RequestParam String categoria) {
        return ResponseEntity.ok(service.listarPorCategoria(categoria));
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody Producto producto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(producto));
    }

    // Aporte propio: expone actualizarPrecio, que el enunciado no tenia en la API
    @PutMapping("/{id}/precio")
    public ResponseEntity<Producto> actualizarPrecio(@PathVariable Long id,
                                                     @RequestBody Map<String, Double> body) {
        Double precio = body.get("precio");
        if (precio == null)
            throw new IllegalArgumentException("El campo precio es obligatorio");
        return ResponseEntity.ok(service.actualizarPrecio(id, precio));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> noEncontrado(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> datosInvalidos(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    // Aporte propio: @NotBlank de la entidad ahora responde 400, no 500
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> validacion(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body("Datos inválidos: nombre obligatorio");
    }
}