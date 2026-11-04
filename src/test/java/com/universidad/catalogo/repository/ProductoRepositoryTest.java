package com.universidad.catalogo.repository;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.universidad.catalogo.model.Producto;

@DataJpaTest
class ProductoRepositoryTest {

    @Autowired ProductoRepository repo;
    @Autowired TestEntityManager em;

    @BeforeEach
    void setUp() {
        em.persistAndFlush(crear("Laptop", 1500.0, "ELECTRONICA"));
        em.persistAndFlush(crear("Cuaderno", 5.0, "PAPELERIA"));
        em.persistAndFlush(crear("Mouse", 25.0, "ELECTRONICA"));
    }

    @Test
    void findByCategoria_electronica_retornaDosProductos() {
        assertThat(repo.findByCategoria("ELECTRONICA")).hasSize(2)
            .extracting("nombre").containsExactlyInAnyOrder("Laptop", "Mouse");
    }

    @Test
    void findByCategoria_papeleria_retornaUnProducto() {
        assertThat(repo.findByCategoria("PAPELERIA")).hasSize(1)
            .extracting("nombre").containsExactly("Cuaderno");
    }

    @Test
    void findByCategoria_sinCoincidencias_retornaListaVacia() {
        assertThat(repo.findByCategoria("JUGUETERIA")).isEmpty();
    }

    @Test
    void save_productoNuevo_asignaId() {
        Producto guardado = repo.save(crear("Teclado", 120.0, "ELECTRONICA"));
        assertThat(guardado.getId()).isNotNull();
    }

    @Test
    void findById_inexistente_retornaVacio() {
        assertThat(repo.findById(9999L)).isEmpty();
    }

    private Producto crear(String nombre, double precio, String categoria) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setPrecio(precio);
        p.setCategoria(categoria);
        return p;
    }
}