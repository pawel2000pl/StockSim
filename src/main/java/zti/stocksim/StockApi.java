package zti.stocksim;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import zti.entities.AuthToken;
import zti.entities.Stock;

@Path("/stock")
public class StockApi extends JPAResource {

    @GET
    @Path("get/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Stock getStock(@Context HttpServletRequest request, @PathParam("id") String id) {
        AuthToken token = check_token(request);
        if (token == null)
            return new Stock();
        Stock entity = (Stock) entityManager.find(Stock.class, Long.parseLong(id));
        return entity;
    }

}
