package es.ucm.fdi.iw.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class TiendaService {

    public List<Map<String, Object>> getItemsFotos() {
        return Arrays.asList(Map.of(
                "title", "Foto 1",
                "foto", "img/iconos/usuario.png"),
                Map.of(
                        "title", "Foto 2",
                        "foto", "/img/iconos/perro.png"),
                Map.of(
                        "title", "Foto 3",
                        "foto", "img/iconos/gato.png"),
                Map.of(
                        "title", "Foto 4",
                        "foto", "img/iconos/pez.png"),
                Map.of(
                        "title", "Foto 5",
                        "foto", "img/iconos/pajaro.png"),
                Map.of(
                        "title", "Foto 6",
                        "foto", "img/iconos/conejo.png"));
    }

    public List<Map<String, Object>> getItemsMarcos() {
        return Arrays.asList(
                Map.of(
                        "title", "Marco 1",
                        "foto", "img/marco/marco1.png"),
                Map.of(
                        "title", "Marco 2",
                        "foto", "img/marco/marco2.png"),
                Map.of(
                        "title", "Marco 3",
                        "foto", "img/marco/marco3.png"),
                Map.of(
                        "title", "Marco 4",
                        "foto", "img/marco/marco4.png"),
                Map.of(
                        "title", "Marco 5",
                        "foto", "img/marco/marco5.png"),
                Map.of(
                        "title", "Marco 6",
                        "foto", "img/marco/marco6.png"));

    }

    public List<Map<String, Object>> getItemsBanners() {
        return Arrays.asList(
                Map.of(
                        "title", "Banner 1",
                        "foto", "img/banners/banner1.png"),
                Map.of(
                        "title", "Banner 2",
                        "foto", "img/banners/banner2.png"),
                Map.of(
                        "title", "Banner 3",
                        "foto", "img/banners/banner3.png"),
                Map.of(
                        "title", "Banner 4",
                        "foto", "img/banners/banner4.png"),
                Map.of(
                        "title", "Banner 5",
                        "foto", "img/banners/banner5.png"),
                Map.of(
                        "title", "Banner 6",
                        "foto", "img/banners/banner6.png"));

    }
}
