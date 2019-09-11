package business;

import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
@Local
public class UserRepository {

    @PersistenceContext(unitName = "UserPU")
    protected EntityManager entityManager;

    public Object getData() {
        // implement your query
        return entityManager != null ? "data" : null;
    }
}
