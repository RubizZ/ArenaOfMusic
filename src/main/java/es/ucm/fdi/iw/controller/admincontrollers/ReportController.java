package es.ucm.fdi.iw.controller.admincontrollers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.ucm.fdi.iw.model.Report;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.service.ReportService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("admin/reports")
public class ReportController {


    @Autowired
    private ReportService reportService;

    @ModelAttribute
    public void populateModel(HttpSession session, Model model) {
        for (String name : new String[] { "u", "url", "ws" }) {
            model.addAttribute(name, session.getAttribute(name));
        }
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("reports", reportService.getReports());
        return "admin/reports";
    }

    @GetMapping("/filtrar")
    public String viewReports(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                            @RequestParam(required = false, defaultValue = "all") String status,
                            Model model) {

        List<Report> reports = reportService.getReportsFiltered(startDate, endDate, status);

        model.addAttribute("reports", reports);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("status", status);

        return "admin/reports";
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> eliminarReporte(@PathVariable long id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/banear")
    @ResponseBody
    public ResponseEntity<Void> banearUsuario(@PathVariable long id, HttpSession session) {
        User u = (User) session.getAttribute("u");
        reportService.banearUsuarioFromReport(id, u);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/resolver")
    @ResponseBody
    public ResponseEntity<Void> resolverReporte(@PathVariable long id, HttpSession session) {
        User u = (User) session.getAttribute("u");
        reportService.resolverReporte(id, u);
        return ResponseEntity.ok().build();
    }

   @GetMapping("/ver-perfil/reportado/{id}")
    public String verPerfilReportado(@PathVariable Long id, Model model) {
    Report report = reportService.getReport(id);
    if (report == null) {
        return "error/404";
    }
    User user = report.getReported();
    model.addAttribute("user", user);
    return "ver-perfil";
}


@GetMapping("/ver-perfil/reportador/{id}")
    public String verPerfilReportador(@PathVariable Long id, Model model) {
    Report report = reportService.getReport(id);
    if (report == null) {
        return "error/404";
    }
    User user = report.getReporter();
    model.addAttribute("user", user);
    return "ver-perfil";
}


@GetMapping("/ver-perfil/admin/{id}")
    public String verPerfilAdmin(@PathVariable Long id, Model model) {
    Report report = reportService.getReport(id);
    if (report == null) {
        return "error/404";
    }
    User admin = report.getAdmin();
    model.addAttribute("user", admin);
    return "ver-perfil";
}


@PostMapping("/{id}/reopen")
@ResponseBody
public ResponseEntity<Void> reopenReporte(@PathVariable long id, HttpSession session) {
    reportService.reopenReport(id);
    return ResponseEntity.ok().build();
}


    
}
