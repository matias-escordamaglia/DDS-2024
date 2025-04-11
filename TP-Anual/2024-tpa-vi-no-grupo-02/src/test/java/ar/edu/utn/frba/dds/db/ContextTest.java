package ar.edu.utn.frba.dds.db;

import io.github.flbulgarelli.jpa.extras.test.SimplePersistenceTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ContextTest implements SimplePersistenceTest {
  @Disabled
  @Test
  void contextUp() {
    assertNotNull(entityManager());
  }
  @Disabled
  @Test
  void contextUpWithTransaction() throws Exception {
    withTransaction(() -> {});
  }

}