package es.ucm.fdi.iw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import es.ucm.fdi.iw.service.TiendaService;
import jakarta.servlet.http.HttpSession;

@Controller
public class TiendaController {

    @Autowired
    private TiendaService tiendaService;

    @ModelAttribute
    public void populateModel(HttpSession session, Model model) {
        for (String name : new String[] { "u", "url", "ws" }) {
            model.addAttribute(name, session.getAttribute(name));
        }
    }

    @GetMapping("/tienda")
    public String tienda(Model model) {
        model.addAttribute("itemFotos", tiendaService.getItemsFotos());
        model.addAttribute("itemMarcos", tiendaService.getItemsMarcos());
        model.addAttribute("itemBanners", tiendaService.getItemsBanners());
        return "tienda";
    }

}
