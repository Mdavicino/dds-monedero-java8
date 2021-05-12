package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Cuenta {

  private BigDecimal saldo;
  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta() {
    saldo = BigDecimal.ZERO;
  }

  public Cuenta(BigDecimal montoInicial) {
    saldo = montoInicial;
  }

  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }

  public void realizarDeposito(BigDecimal unaCantidad) {
    validarMonto(unaCantidad);
    validarMovimientos();
    agregarMovimiento(LocalDate.now(), unaCantidad, true);
  }

  public void realizarExtraccion(BigDecimal unaCantidad) {
    validarMonto(unaCantidad);
    validarSaldo(unaCantidad);
    validarMontoExtraidoEnElDia(unaCantidad);
    agregarMovimiento(LocalDate.now(), unaCantidad, false);
  }

  public void validarMonto(BigDecimal unMonto){
    if (unMonto.compareTo(BigDecimal.valueOf(0)) != 1) {
      throw new MontoNegativoException(unMonto + ": el monto a ingresar debe ser un valor positivo");
    }
  }

  public void validarMovimientos(){
    if (getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }
  }

  public void validarSaldo(BigDecimal unaCantidad){
    if (getSaldo().compareTo(unaCantidad) == -1) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
  }

  public void validarMontoExtraidoEnElDia(BigDecimal unaCantidad){
    BigDecimal montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    BigDecimal limite = BigDecimal.valueOf(1000).subtract(montoExtraidoHoy);
    if (unaCantidad.compareTo(limite) == 1) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + limite);
    }
  }

  public void agregarMovimiento(LocalDate fecha, BigDecimal cuanto, boolean esDeposito) {
    Movimiento movimiento = new Movimiento(fecha, cuanto, esDeposito);
    movimientos.add(movimiento);
    if(esDeposito){
      setSaldo(saldo.add(cuanto));
    }else{
      setSaldo(saldo.subtract(cuanto));
    }

  }

  public BigDecimal getMontoExtraidoA(LocalDate unaFecha) {
    return getExtraccionesDe(unaFecha)
        .map(Movimiento::getMonto)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public Stream<Movimiento> getExtraccionesDe(LocalDate unaFecha){
    return getMovimientos().stream()
        .filter(movimiento -> movimiento.fueExtraidoEn(unaFecha));
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public BigDecimal getSaldo() {
    return saldo;
  }

  public void setSaldo(BigDecimal saldo) {
    this.saldo = saldo;
  }

}
