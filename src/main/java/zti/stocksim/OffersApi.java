package zti.stocksim;

import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import zti.entities.AuthToken;
import zti.entities.Offer;
import zti.entities.Wallet;

@Path("/offer")
public class OffersApi extends JPAResource {

    @GET
    @Path("mine")
    @Produces({MediaType.APPLICATION_JSON})
    public Offer[] getMine(@Context HttpServletRequest request) {
        AuthToken token = check_token(request);
        if (token == null)
            return new Offer[0];
        TypedQuery<Offer> query = entityManager.createQuery("SELECT o FROM offers o WHERE o.user.id = :userid", Offer.class);
        query.setParameter("userid", token.getUser().getId());
        Offer[] result = query.getResultList().toArray(new Offer[0]);
        for (Offer offer : result)
            offer.setUser(null);
        return result;
    }


    @POST
    @Path("add")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_PLAIN})
    public String add(@Context HttpServletRequest request, Offer offer) {
        AuthToken token = check_token(request);
        if (token == null)
            return "unauthorized";
        if (offer.getCount() == 0)
            return "invalid stock count";
        if (offer.getPrice() <= 0)
            return "invalid stock price";
        TypedQuery<Wallet> query = entityManager.createQuery("SELECT w FROM wallets w WHERE w.user.id = :userid AND w.copartnership.id = :copartnershipid", Wallet.class);
        query.setParameter("userid", token.getUser().getId());
        query.setParameter("copartnershipid", offer.getCopartnership().getId());
        if (offer.getSaleOffer()) {
            try {
                Wallet wallet = query.getSingleResult();
                if (wallet.getCount() < offer.getCount())
                    return "unowned count of stock";
            } catch (NoResultException exception) {
                return "unowned stock";
            }
        }
        if ((!offer.getSaleOffer()) && token.getUser().getCash() < offer.getPrice() * offer.getCount())
            return "not enough cash";

        offer.setUser(token.getUser());
        entityTransaction.begin();
        entityManager.persist(offer);
        entityManager.flush();
        entityTransaction.commit();
        return "ok";
    }

    @DELETE
    @Path("delete")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_PLAIN})
    public String delete(@Context HttpServletRequest request, Offer offer) {
        AuthToken token = check_token(request);
        if (token == null)
            return "unauthorized";
        offer = entityManager.find(Offer.class, offer.getId());
        if (offer == null)
            return "invalid offer id";
        if (offer.getUser().getId() != token.getUser().getId())
            return "unauthorized";
        entityTransaction.begin();
        entityManager.remove(offer);
        entityManager.flush();
        entityTransaction.commit();
        return "ok";
    }

}
