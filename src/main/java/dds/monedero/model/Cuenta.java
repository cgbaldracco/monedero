package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo = 0;
  private List<Movimiento> movimientos = new ArrayList<>();
  private static Integer MAX_DEPOSITOS_DIARIOS = 3;

  public Cuenta() {
    saldo = 0;
  }

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }

  private void verificarMontoValido(double monto) {
    if (monto < 0) {
      throw new MontoNegativoException("El monto a extraer debe ser un valor positivo");
    }
  }

  private void verificarLimiteDeDepositosDiarios(LocalDate fecha) {
    if (getMovimientos().stream().filter(movimiento -> movimiento instanceof Deposito &&
        movimiento.esDeLaFecha(fecha)).count() >= MAX_DEPOSITOS_DIARIOS) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + MAX_DEPOSITOS_DIARIOS + " depositos diarios.");
    }
  }

  public void depositar(double monto) {
    verificarMontoValido(monto);
    verificarLimiteDeDepositosDiarios(LocalDate.now());

    agregarDeposito(LocalDate.now(), monto);
  }

  private void verificarExtraccionExcedeSaldo(double monto) {
    if (getSaldo() - monto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
  }

  private void verificarLimiteDiario(double monto) {
    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    double limite = 1000 - montoExtraidoHoy;
    if (monto > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + limite);
    }
  }

  public void extraer(double monto) {
    verificarMontoValido(monto);
    verificarExtraccionExcedeSaldo(monto);
    verificarLimiteDiario(monto);

    agregarExtraccion(LocalDate.now(), monto);
  }

  public void agregarDeposito(LocalDate fecha, double monto) {
    Deposito deposito = new Deposito(fecha, monto);
    agregarMovimiento(deposito);
  }

  public void agregarExtraccion(LocalDate fecha, double monto) {
    Extraccion extraccion = new Extraccion(fecha, monto);
    agregarMovimiento(extraccion);
  }

  public void agregarMovimiento(Movimiento movimiento) {
    movimientos.add(movimiento);
    setSaldo(saldo + movimiento.obtenerMonto());
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> movimiento instanceof Extraccion && movimiento.getFecha().equals(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }
}
