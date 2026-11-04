package com.universidad.catalogo.service;

import com.universidad.catalogo.model.Producto;
import com.universidad.catalogo.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock ProductoRepository repo;
    @InjectMocks ProductoService service;

    private Producto producto(String nombre, double precio, String categoria) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setPrecio(precio);
        p.setCategoria(categoria);
        return p;
    }

    // ---- crear ----
    @Test
    void crear_conDatosValidos_guardaYRetorna() {
        Producto p = producto("Teclado mecánico", 120.0, "ELECTRONICA");
        when(repo.save(any())).thenReturn(p);

        assertThat(service.crear(p).getNombre()).isEqualTo("Teclado mecánico");
        verify(repo).save(p);
    }

    @Test
    void crear_conPrecioNegativo_lanzaIllegalArgumentException() {
        Producto p = producto("Mouse", -10.0, "ELECTRONICA");

        assertThrows(IllegalArgumentException.class, () -> service.crear(p));
        verify(repo, never()).save(any());
    }

    @Test
    void crear_conPrecioCero_lanzaIllegalArgumentException() {
        Producto p = producto("Mouse", 0.0, "ELECTRONICA");

        assertThrows(IllegalArgumentException.class, () -> service.crear(p));
        verify(repo, never()).save(any());
    }

    @Test
    void crear_conNombreVacio_lanzaIllegalArgumentException() {
        Producto p = producto("  ", 10.0, "ELECTRONICA");

        assertThrows(IllegalArgumentException.class, () -> service.crear(p));
        verify(repo, never()).save(any());
    }

    @Test
    void crear_conNombreNulo_lanzaIllegalArgumentException() {
        Producto p = producto(null, 10.0, "ELECTRONICA");

        assertThrows(IllegalArgumentException.class, () -> service.crear(p));
        verify(repo, never()).save(any());
    }

    // ---- buscarPorId ----
    @Test
    void buscarPorId_existe_retornaProducto() {
        Producto p = producto("Laptop", 1500.0, "ELECTRONICA");
        p.setId(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(p));

        assertThat(service.buscarPorId(1L).getNombre()).isEqualTo("Laptop");
    }

    @Test
    void buscarPorId_noExiste_lanzaEntityNotFoundException() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.buscarPorId(99L));
    }

    // ---- listarPorCategoria ----
    @Test
    void listarPorCategoria_conCoincidencias_retornaLista() {
        when(repo.findByCategoria("ELECTRONICA"))
            .thenReturn(List.of(producto("Laptop", 1500.0, "ELECTRONICA")));

        assertThat(service.listarPorCategoria("ELECTRONICA")).hasSize(1);
    }

    @Test
    void listarPorCategoria_sinCoincidencias_retornaListaVacia() {
        when(repo.findByCategoria("JUGUETERIA")).thenReturn(List.of());

        assertThat(service.listarPorCategoria("JUGUETERIA")).isEmpty();
    }

    // ---- actualizarPrecio ----
    @Test
    void actualizarPrecio_conValorValido_actualizaYGuarda() {
        Producto p = producto("Laptop", 100.0, "ELECTRONICA");
        p.setId(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(p));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThat(service.actualizarPrecio(1L, 150.0).getPrecio()).isEqualTo(150.0);
        verify(repo).save(p);
    }

    @Test
    void actualizarPrecio_conPrecioInvalido_lanzaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.actualizarPrecio(1L, -5.0));
        verify(repo, never()).findById(any());
        verify(repo, never()).save(any());
    }

    @Test
    void actualizarPrecio_productoInexistente_lanzaEntityNotFoundException() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.actualizarPrecio(99L, 50.0));
        verify(repo, never()).save(any());
    }
}