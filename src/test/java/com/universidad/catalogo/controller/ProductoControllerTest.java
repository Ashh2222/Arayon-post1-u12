package com.universidad.catalogo.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universidad.catalogo.model.Producto;
import com.universidad.catalogo.service.ProductoService;

import jakarta.persistence.EntityNotFoundException;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean ProductoService service;

    private Producto producto(Long id, String nombre, double precio) {
        Producto p = new Producto();
        p.setId(id);
        p.setNombre(nombre);
        p.setPrecio(precio);
        return p;
    }

    @Test
    void get_productoExiste_retorna200() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(producto(1L, "Laptop", 1500.0));

        mockMvc.perform(get("/api/productos/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Laptop"));
    }

    @Test
    void get_noExiste_retorna404() throws Exception {
        when(service.buscarPorId(99L)).thenThrow(new EntityNotFoundException("no encontrado"));

        mockMvc.perform(get("/api/productos/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void get_porCategoria_retorna200ConLista() throws Exception {
        when(service.listarPorCategoria("ELECTRONICA"))
            .thenReturn(List.of(producto(1L, "Laptop", 1500.0)));

        mockMvc.perform(get("/api/productos").param("categoria", "ELECTRONICA"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nombre").value("Laptop"));
    }

    @Test
    void post_conDatosValidos_retorna201() throws Exception {
        when(service.crear(any())).thenReturn(producto(1L, "Mouse", 50.0));

        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto(null, "Mouse", 50.0))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void post_conPrecioInvalido_retorna400() throws Exception {
        when(service.crear(any()))
            .thenThrow(new IllegalArgumentException("El precio debe ser mayor a cero"));

        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto(null, "Auricular", -5.0))))
            .andExpect(status().isBadRequest());
    }

    @Test
    void post_conNombreVacio_retorna400() throws Exception {
        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto(null, "", 50.0))))
            .andExpect(status().isBadRequest());
    }

    @Test
    void put_precioValido_retorna200() throws Exception {
        when(service.actualizarPrecio(eq(1L), eq(150.0)))
            .thenReturn(producto(1L, "Laptop", 150.0));

        mockMvc.perform(put("/api/productos/1/precio")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"precio\":150.0}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.precio").value(150.0));
    }

    @Test
    void put_sinCampoPrecio_retorna400() throws Exception {
        mockMvc.perform(put("/api/productos/1/precio")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void put_productoInexistente_retorna404() throws Exception {
        when(service.actualizarPrecio(eq(99L), eq(50.0)))
            .thenThrow(new EntityNotFoundException("no encontrado"));

        mockMvc.perform(put("/api/productos/99/precio")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"precio\":50.0}"))
            .andExpect(status().isNotFound());
    }
}