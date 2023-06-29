package zti.stocksim;

import jakarta.ws.rs.ApplicationPath;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class Application extends jakarta.ws.rs.core.Application {

    public Set<Class<?>> getClasses() {

        HashSet<Class<?>> set = new HashSet<>();
        set.add(UserApi.class);
        set.add(CopartnershipsApi.class);
        set.add(OffersApi.class);
        set.add(StockApi.class);
        set.add(HistoryReportApi.class);
        set.add(ExchangeTask.class);
        set.add(CorsFilter.class);
        return set;
    }
}