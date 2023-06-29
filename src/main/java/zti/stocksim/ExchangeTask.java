package zti.stocksim;

import jakarta.persistence.*;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import zti.entities.*;

import java.util.List;
import java.util.ListIterator;
import java.util.Random;

@Path("exchange-task")
public class ExchangeTask extends JPAResource {

    public static Long modifyNumber(Long number) {
        Random random = new Random();
        double percentage = random.nextDouble() * 0.1;
        double range = number * percentage;
        boolean increase = random.nextBoolean();
        double modifiedValue = increase ? number + range : number - range;
        int randomInt = random.nextInt(3) - 1;
        modifiedValue += randomInt;
        Long result = Math.round(modifiedValue);
        return Math.min(Math.max(1L, result), 499L);
    }

    @GET
    public String run() {
        System.out.println("Executing transactions");

        entityTransaction.begin();

        List<Copartnership> copartnerships = (List<Copartnership>) entityManager.createNamedQuery("findAll").getResultList();
        for (Copartnership copartnership : copartnerships) {
            Long salePrice = copartnership.getSalePrice();
            salePrice = modifyNumber(salePrice);
            copartnership.setSalePrice(salePrice);
            copartnership.setPurchasePrice(2*salePrice);
            entityManager.merge(copartnership);
        }

        for (Copartnership copartnership : copartnerships) {
            TypedQuery<Offer> saleOffersQuery = entityManager.createQuery("SELECT o FROM offers o WHERE (o.isSaleOffer = true) AND (o.copartnership.id = :copartnershipid) ORDER BY o.price", Offer.class);
            saleOffersQuery.setParameter("copartnershipid", copartnership.getId());
            TypedQuery<Offer> purchaseOffersQuery = entityManager.createQuery("SELECT o FROM offers o WHERE (o.isSaleOffer = false) AND (o.copartnership.id = :copartnershipid) ORDER BY o.price", Offer.class);
            purchaseOffersQuery.setParameter("copartnershipid", copartnership.getId());

            List<Offer> saleOffers = saleOffersQuery.getResultList();
            List<Offer> purchaseOffers = purchaseOffersQuery.getResultList();

            ListIterator<Offer> saleIterator = saleOffers.listIterator();
            ListIterator<Offer> purchaseIterator = purchaseOffers.listIterator();

            Offer currentSaleOffer = null;
            Offer currentPurchaseOffer = null;

            while (saleIterator.hasNext() && purchaseIterator.hasNext()) {
                if (currentSaleOffer == null || currentSaleOffer.getCount() == 0)
                    currentSaleOffer = saleIterator.next();
                if (currentPurchaseOffer == null || currentPurchaseOffer.getCount() == 0)
                    currentPurchaseOffer = purchaseIterator.next();

                if (currentSaleOffer.getPrice() <= currentPurchaseOffer.getPrice()) {
                    Long price = currentSaleOffer.getPrice();
                    User purchaseUser = currentPurchaseOffer.getUser();
                    User saleUser = currentSaleOffer.getUser();
                    Long purchaseUserCash = purchaseUser.getCash();
                    Long saleUserCash = saleUser.getCash();
                    Long saleCount = currentSaleOffer.getCount();
                    Long purchaseCount = currentPurchaseOffer.getCount();
                    Long count = Math.min(saleCount, purchaseCount);
                    count = Math.min(count, purchaseUserCash / price);
                    Long totalPrice = count * price;
                    purchaseUserCash = purchaseUserCash - totalPrice;
                    saleUserCash = saleUserCash + totalPrice;
                    purchaseUser.setCash(purchaseUserCash);
                    saleUser.setCash(saleUserCash);
                    saleCount = saleCount - count;
                    purchaseCount = purchaseCount - count;
                    currentSaleOffer.setCount(saleCount);
                    currentPurchaseOffer.setCount(purchaseCount);

                    TypedQuery<ExchangeHistory> stockHistory = entityManager.createQuery("SELECT eh FROM exchange_history eh WHERE eh.id IN (SELECT MAX(eh2.id) FROM exchange_history eh2 GROUP BY eh2.stock.id) AND eh.user.id = :userid AND eh.stock.copartnership.id = :copartnershipip", ExchangeHistory.class);
                    stockHistory.setParameter("userid", saleUser.getId());
                    stockHistory.setParameter("copartnershipip", copartnership.getId());
                    long i = count;
                    for (ExchangeHistory eh : stockHistory.getResultList()) {
                        if (i-- < 0) break;
                        ExchangeHistory eh2 = new ExchangeHistory();
                        eh2.setUser(purchaseUser);
                        eh2.setStock(eh.getStock());
                        eh2.setUser(purchaseUser);
                        entityManager.persist(eh2);
                    }

                    entityManager.merge(purchaseUser);
                    entityManager.merge(saleUser);
                    if (saleCount == 0)
                        entityManager.remove(currentSaleOffer);
                    else
                        entityManager.merge(currentSaleOffer);

                    if (purchaseCount == 0)
                        entityManager.remove(currentPurchaseOffer);
                    else
                        entityManager.merge(currentPurchaseOffer);

                    if (saleCount == 0)
                        currentSaleOffer = null;
                    else
                        currentPurchaseOffer = null;

                } else {
                    currentSaleOffer = null;
                }
            }
        }

        for (Copartnership copartnership : copartnerships) {
            TypedQuery<Offer> saleOffersQuery = entityManager.createQuery("SELECT o FROM offers o WHERE (o.isSaleOffer = true) AND (o.copartnership.id = :copartnershipid) AND (o.price <= :purchaseprice)", Offer.class);
            saleOffersQuery.setParameter("copartnershipid", copartnership.getId());
            saleOffersQuery.setParameter("purchaseprice", copartnership.getPurchasePrice());
            TypedQuery<Offer> purchaseOffersQuery = entityManager.createQuery("SELECT o FROM offers o WHERE (o.isSaleOffer = false) AND (o.copartnership.id = :copartnershipid) AND (o.price >= :saleprice)", Offer.class);
            purchaseOffersQuery.setParameter("copartnershipid", copartnership.getId());
            purchaseOffersQuery.setParameter("saleprice", copartnership.getSalePrice());

            List<Offer> saleOffers = saleOffersQuery.getResultList();
            List<Offer> purchaseOffers = purchaseOffersQuery.getResultList();

            for (Offer offer : saleOffers) {
                User user = offer.getUser();
                long count = offer.getCount();
                TypedQuery<ExchangeHistory> stockHistory = entityManager.createQuery("SELECT eh FROM exchange_history eh WHERE eh.id IN (SELECT MAX(eh2.id) FROM exchange_history eh2 GROUP BY eh2.stock.id) AND eh.user.id = :userid AND eh.stock.copartnership.id = :copartnershipip", ExchangeHistory.class);
                stockHistory.setParameter("userid", user.getId());
                stockHistory.setParameter("copartnershipip", copartnership.getId());
                for (ExchangeHistory eh : stockHistory.getResultList()) {
                    if (count-- <= 0)
                        break;
                    ExchangeHistory eh2 = new ExchangeHistory();
                    eh2.setUser(null);
                    eh2.setStock(eh.getStock());
                    eh2.setPrice(offer.getPrice());
                    entityManager.persist(eh2);
                }
                user.setCash(user.getCash() + offer.getCount() * offer.getPrice());
                entityManager.merge(user);
                entityManager.remove(offer);
            }

            for (Offer offer : purchaseOffers) {
                User user = offer.getUser();
                long count = offer.getCount();
                count = Math.min(count, user.getCash() / offer.getCount());
                for (long i=0;i<count;i++) {
                    ExchangeHistory eh = new ExchangeHistory();
                    Stock s = new Stock();
                    s.setCopartnership(offer.getCopartnership());
                    entityManager.persist(s);
                    eh.setUser(user);
                    eh.setStock(s);
                    eh.setPrice(offer.getPrice());
                    entityManager.persist(eh);
                }
                user.setCash(user.getCash() - count * offer.getPrice());
                offer.setCount(offer.getCount() - count);
                entityManager.merge(user);
                if (offer.getCount() == 0)
                    entityManager.remove(offer);
                else
                    entityManager.merge(offer);
            }

        }

        entityTransaction.commit();
        return "ok";
    }

}
