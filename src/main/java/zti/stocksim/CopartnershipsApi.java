package zti.stocksim;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import zti.entities.Copartnership;

import java.util.List;

@Path("/copartnership")
public class CopartnershipsApi extends JPAResource {

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Copartnership[] doGet() {
        List<Copartnership> copartnerships = null;
        try {
            copartnerships = (List<Copartnership>) entityManager.createNamedQuery("findAll").getResultList();
        } catch (Exception e) {
            return new Copartnership[0];
        }
        return copartnerships.toArray(new Copartnership[0]);
    }

}
