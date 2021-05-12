package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MonederoTest {
  private Cuenta cuenta;

  @BeforeEach
  void init() {
    cuenta = new Cuenta();
  }

  @Test
  void Poner() {
    cuenta.realizarDeposito(BigDecimal.valueOf(1500));
    assertEquals(BigDecimal.valueOf(1500), cuenta.getSaldo());
  }

  @Test
  void PonerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.realizarDeposito(BigDecimal.valueOf(-1500)));
  }

  @Test
  void TresDepositos() {
    cuenta.realizarDeposito(BigDecimal.valueOf(1500));
    cuenta.realizarDeposito(BigDecimal.valueOf(456));
    cuenta.realizarDeposito(BigDecimal.valueOf(1900));
    assertEquals(3, cuenta.getMovimientos().toArray().length);
  }

  @Test
  void MasDeTresDepositos() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
          cuenta.realizarDeposito(BigDecimal.valueOf(1500));
          cuenta.realizarDeposito(BigDecimal.valueOf(456));
          cuenta.realizarDeposito(BigDecimal.valueOf(1900));
          cuenta.realizarDeposito(BigDecimal.valueOf(245));
    });
  }

  @Test
  void ExtraerMasQueElSaldo() {
    assertThrows(SaldoMenorException.class, () -> {
          cuenta.setSaldo(BigDecimal.valueOf(90));
          cuenta.realizarExtraccion(BigDecimal.valueOf(1001));
    });
  }

  @Test
  public void ExtraerMasDe1000() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.setSaldo(BigDecimal.valueOf(5000));
      cuenta.realizarExtraccion(BigDecimal.valueOf(1001));
    });
  }

  @Test
  public void ExtraerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.realizarExtraccion(BigDecimal.valueOf(-500)));
  }

  @Test
  public void SeCreaLaCuentaConSaldo(){
    Cuenta cuentaSaldo = new Cuenta(BigDecimal.valueOf(1000));
    assertEquals(BigDecimal.valueOf(1000), cuentaSaldo.getSaldo());
  }

}