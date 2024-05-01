package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MonederoTest {
  private Cuenta cuenta;

  @BeforeEach
  void init() {
    cuenta = new Cuenta();
  }

  @Test
  void PonerMontoPositivo() {
    cuenta.ingreso(1500);
    assertEquals(cuenta.getSaldo(), 1500);
  }

  @Test
  void PonerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.ingreso(-1500));
  }

  @Test
  void PonerTresDepositos() {
    cuenta.ingreso(1500);
    cuenta.ingreso(456);
    cuenta.ingreso(1900);
    assertEquals(cuenta.getSaldo(), 3856);
  }

  @Test
  void PonerMasDeTresDepositos() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
          cuenta.ingreso(1500);
          cuenta.ingreso(456);
          cuenta.ingreso(1900);
          cuenta.ingreso(245);
    });
  }

  @Test
  void ExtraerMasQueElSaldo() {
    assertThrows(SaldoMenorException.class, () -> {
          cuenta.setSaldo(90);
          cuenta.extraccion(1001);
    });
  }

  @Test
  void ExtraerMasDe1000() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.setSaldo(5000);
      cuenta.extraccion(1001);
    });
  }

  @Test
  void ExtraerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.extraccion(-500));
  }
}