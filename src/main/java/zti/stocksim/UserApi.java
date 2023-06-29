package zti.stocksim;

import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import zti.entities.AuthToken;
import zti.entities.User;
import zti.entities.UserDataForm;

@Path("/user")
public class UserApi extends JPAResource {

    @GET
    @Path("get")
    @Produces({MediaType.APPLICATION_JSON})
    public User getUser(@Context HttpServletRequest request) {
        AuthToken token = check_token(request);
        if (token == null)
            return new User();
        User entity = token.getUser();
        if (entity == null)
            return new User();
        return entity;
    }
    @POST
    @Path("register")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_PLAIN})
    public String register(User user) {
        try {
            entityTransaction.begin();
            user.setCash(1000L);
            entityManager.persist(user);
            entityManager.flush();
            entityTransaction.commit();
            return "ok";
        } catch (PersistenceException error) {
            return "error";
        }
    }

    @POST
    @Path("login")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_PLAIN})
    public String login(@Context HttpServletRequest request, @Context HttpServletResponse response, UserDataForm loginForm) {
        entityTransaction.begin();
        AuthToken token = check_token(request);
        if (token != null)
            entityManager.remove(token);
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM users u WHERE u.username = :username", User.class);
        query.setParameter("username", loginForm.getUsername());
        User user = null;
        try {
            user = query.getSingleResult();
        } catch (NoResultException exception) {
            return "invalid data";
        }
        if (!user.checkPassword(loginForm.getPassword()))
            return "invalid data";
        token = AuthToken.createNew(user);
        Cookie cookie = new Cookie("token", token.getHash());
        cookie.setPath("/");
        cookie.setMaxAge((int)AuthToken.TOKEN_EXPIRE_TIME);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        entityManager.persist(token);
        entityManager.flush();
        entityTransaction.commit();
        return "ok";
    }

    @GET
    @Path("logout")
    @Produces({MediaType.TEXT_PLAIN})
    public String logout(@Context HttpServletRequest request, @Context HttpServletResponse response) {
        AuthToken token = check_token(request);
        if (token != null) {
            entityTransaction.begin();
            entityManager.remove(token);
            entityManager.flush();
            entityTransaction.commit();
        }
        Cookie cookie = new Cookie("token", "null");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return "ok";
    }
}
