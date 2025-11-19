public enum Accion {
    CHECK("Pasar"), CALL("Igualar"), RAISE("Subir"), ALL_IN("All-In"), FOLD("Retirarse");
    private final String descripcion;
    Accion(String descripcion) { this.descripcion = descripcion; }
    public String descripcion() { return  descripcion; }
}