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
    if (getMovimientos().stream().filter(movimiento -> movimiento.isDeposito() &&
        movimiento.esDeLaFecha(fecha)).count() >= MAX_DEPOSITOS_DIARIOS) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + MAX_DEPOSITOS_DIARIOS + " depositos diarios.");
    }
  }

  public void poner(double monto) {
    verificarMontoValido(monto);
    verificarLimiteDeDepositosDiarios(LocalDate.now());

    new Movimiento(LocalDate.now(), monto, true).agregateA(this);
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

  //aca veo un long method, se puede descomponer tranquilamente en 3 metodos separados
  public void sacar(double monto) {
    verificarMontoValido(monto);
    verificarExtraccionExcedeSaldo(monto);
    verificarLimiteDiario(monto);

    new Movimiento(LocalDate.now(), monto, false).agregateA(this);
  }

  public void agregarMovimiento(LocalDate fecha, double cuanto, boolean esDeposito) {
    Movimiento movimiento = new Movimiento(fecha, cuanto, esDeposito);
    movimientos.add(movimiento);
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> !movimiento.isDeposito() && movimiento.getFecha().equals(fecha))
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
