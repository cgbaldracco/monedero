package dds.monedero.model;

import java.time.LocalDate;

public class Deposito extends Movimiento {
  public Deposito(LocalDate fecha, double monto) {
    super(fecha, monto);
  }

  @Override
  public double obtenerMonto() {
    return this.getMonto();
  }
}
