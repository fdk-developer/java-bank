package br.com.dio.model;

public record Investiment(
        long id,
        long tax,
        long initialFunds
) {
    @Override
    public String toString() {
        return "Investiment{" +
                "id=" + id +
                ", tax=" + tax + "%" +
                ", initialFunds=" + (initialFunds / 100) + "," + (initialFunds % 100) +
                '}';
    }
}
