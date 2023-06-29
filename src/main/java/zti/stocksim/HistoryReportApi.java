package zti.stocksim;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import zti.entities.AuthToken;
import zti.entities.HistoryReport;

@Path("/history")
public class HistoryReportApi extends JPAResource {

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public HistoryReport[] getExchangeHistory(@Context HttpServletRequest request) {
        AuthToken token = check_token(request);
        if (token == null)
            return new HistoryReport[0];
        TypedQuery<HistoryReport> query = entityManager.createQuery("SELECT hr FROM history_report hr WHERE hr.time > :time", HistoryReport.class);
        query.setParameter("time", System.currentTimeMillis() / 1000L - HistoryReport.HISTORY_REPORT_TIME);
        return query.getResultList().toArray(new HistoryReport[0]);
    }

}
