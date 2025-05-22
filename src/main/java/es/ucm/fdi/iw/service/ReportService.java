package es.ucm.fdi.iw.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.ucm.fdi.iw.model.Report;
import es.ucm.fdi.iw.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Service
public class ReportService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserService userService;

    public List<Report> getReports() {
        return entityManager.createQuery("SELECT r FROM Report r", Report.class).getResultList();
    }


    public Report getReport(long id) {
        return entityManager.find(Report.class, id);
    }


    @Transactional
    public void deleteReport(long id) {
        Report r = entityManager.find(Report.class, id);
        if (r != null) {
            entityManager.remove(r);
        }
    }

    @Transactional
    public void banearUsuarioFromReport(long id, User currentAdmin) {
        Report r = entityManager.find(Report.class, id);
        if (r != null && r.getReported() != null) {
            r.setSolved(true);
            r.setBanned(true);
            r.setAdmin(currentAdmin);
            r.setResolutionDate(LocalDateTime.now());

            User u = r.getReported();
            u.setBanned(true);

            entityManager.merge(u);
            entityManager.merge(r);
        }
    }

    @Transactional
    public void resolverReporte(long id, User currentAdmin) {
        Report r = entityManager.find(Report.class, id);
        if (r != null) {
            r.setSolved(true);
            r.setBanned(false);
            r.setAdmin(currentAdmin);
            r.setResolutionDate(LocalDateTime.now());
            User u = r.getReported();
            u.setBanned(false);
            entityManager.merge(r);
        }
    }

    public List<Report> getReportsFiltered(LocalDate startDate, LocalDate endDate, String status) {
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (startDate != null) {
            start = startDate.atStartOfDay();
        }

        if (endDate != null) {
            end = endDate.plusDays(1).atStartOfDay().minusSeconds(1); // Hasta el final del día
        }

        StringBuilder jpql = new StringBuilder("SELECT r FROM Report r WHERE 1=1");

        if (start != null) {
            jpql.append(" AND r.creationDate >= :start");
        }

        if (end != null) {
            jpql.append(" AND r.creationDate <= :end");
        }

        switch (status) {
            case "pending":
                jpql.append(" AND r.solved = false");
                break;
            case "resolved":
                jpql.append(" AND r.solved = true AND r.banned = false");
                break;
            case "banned":
                jpql.append(" AND r.solved = true AND r.banned = true");
                break;
            case "all":
            default:
                // no se añade filtro adicional
                break;
        }

        jpql.append(" ORDER BY r.creationDate DESC");

        TypedQuery<Report> query = entityManager.createQuery(jpql.toString(), Report.class);

        if (start != null) {
            query.setParameter("start", start);
        }

        if (end != null) {
            query.setParameter("end", end);
        }

        return query.getResultList();
    }


    @Transactional
    public void reopenReport(long id) {
        Report r = entityManager.find(Report.class, id);
        if (r != null) {
            // Si estaba baneado, desbanear al usuario reportado
            if (r.isBanned() && r.getReported() != null) {
                User u = r.getReported();
                u.setBanned(false);
                entityManager.merge(u);
            }
            // Revertir campos del reporte
            r.setSolved(false);
            r.setBanned(false);
            r.setAdmin(null);
            r.setResolutionDate(null);
            entityManager.merge(r);
        }
    }

    @Transactional
    public void createReport(String reporterUsername, String reportedUsername, int reason)
    {
        Report report = new Report();
        report.setReporter(userService.findByUsername(reporterUsername));
        report.setReported(userService.findByUsername(reportedUsername));
        report.setReason(reason);
        report.setCreationDate(LocalDateTime.now());

        entityManager.persist(report);
    }
}
