package zti.stocksim;

import jakarta.persistence.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import zti.entities.AuthToken;

public class JPAResource {

    protected EntityManagerFactory managerFactory;
    protected EntityManager entityManager;
    protected EntityTransaction entityTransaction;

    public JPAResource() {
        managerFactory = Persistence.createEntityManagerFactory("mariaDB-eclipselink");
        entityManager = managerFactory.createEntityManager();
        entityTransaction = entityManager.getTransaction();
    }

    public AuthToken check_token(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    String value = cookie.getValue();
                    AuthToken token = entityManager.find(AuthToken.class, value);
                    if (token.getExpireTime() < System.currentTimeMillis() / 1000L)
                        return null;
                    if (token != null)
                        return token;
                }
            }
        }
        return null;
    }


}
