package ar.edu.utn.frba.dds.repositories;

import ar.edu.utn.frba.dds.model.heladeras.Comida;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import java.util.List;

public class RepositorioComidas implements WithSimplePersistenceUnit {

  public static final RepositorioComidas INSTANCE = new RepositorioComidas();

  private EntityManagerFactory entityManagerFactory;

  private RepositorioComidas() {
    this.entityManagerFactory = Persistence.createEntityManagerFactory("simple-persistence-unit");
  }

  public List<Comida> findAll() {
    EntityManager entityManager = getEntityManager();
    try {
      return entityManager.createQuery("from Comida", Comida.class).getResultList();
    } finally {
      entityManager.close();
    }
  }

  public Comida findById(int id) {
    EntityManager entityManager = getEntityManager();
    try {
      return entityManager.find(Comida.class, id);
    } finally {
      entityManager.close();
    }
  }

  public Comida findByName(String name) {
    EntityManager entityManager = getEntityManager();
    try {
      return entityManager
          .createQuery("SELECT c FROM Comida c WHERE c.nombre = :name", Comida.class)
          .setParameter("name", name)
          .getSingleResult();
    } catch (NoResultException e) {
      return null;
    } finally {
      entityManager.close();
    }
  }

  private EntityManager getEntityManager() {
    return entityManagerFactory.createEntityManager();
  }
}
