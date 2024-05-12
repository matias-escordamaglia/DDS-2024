public PseudoCodigo {

  public class Prenda {
    TipoPrenda tipo
    Color colorPrimario
    Color colorSecundario
    Categoria categoria
    Trama trama
    Material material

    public Prenda (TipoPrenda tipo,
                   Color colorPrimario,
                   Color colorSecundario,
                   Categoria categoria,
                   Trama trama,
                   Material material) {
      this.tipo = tipo;
      this.colorPrimario = colorPrimario;
      this.colorSecundario = colorSecundario;
      this.categoria = categoria;
      this.trama = trama;
      this.material = trama;
    }
  }

  enum TipoPrenda {
    REMERA,
    BUZO,
    CAMISA,
    PANTALON
  }

  enum Color {
    AMARILLO,
    AZUL,
    BLANCO,
    ROJO,
    VERDE
  }

  enum Categoria {
    SUPERIOR,
    CALZADO,
    ACCESORIO,
    PARTE_INFERIOR
  }

  enum Trama {
    LISA,
    RAYADA,
    CON_LUNARES,
    A_CUADROS,
    ESTAMPADO
  }

  enum Material {
    ALGODON,
    POLIESTER,
    LANA,
    CUERO
  }

  public class Borrador {
    private TipoPrenda tipoPrenda
    private Color colorPrimario
    private Color colorSecundario
    private Categoria categoria
    private Material material
    private Trama trama = trama.LISA;
    private String caracteristicasNecesariasError = "Para generar la prenda necesario que" +
        " especifique: Tipo, Material, Categoria, Color Primario y Trama";

    public void setTipoPrenda(TipoPrenda tipoPrenda) {
      this.tipoPrenda = tipoPrenda;
    }

    public void setColorPrimario(Color colorPrimario) {
      if (this.tipoPrenda == null) throw new RuntimeException(
          "Primero debe especificar el tipo de prenda");
      this.colorPrimario = colorPrimario;
    }

    public void setColorSecundario(Color colorSecundario) {
      if (this.tipoPrenda == null) throw new RuntimeException(
          "Primero debe especificar el tipo de prenda");
      this.colorSecundario = colorSecundario;
    }

    public void setCategoria(Categoria categoria) {
      if (this.tipoPrenda == null) throw new RuntimeException(
          "Primero debe especificar el tipo de prenda");
      this.categoria = categoria;
    }

    public void setTrama(Trama trama) {
      if (this.tipoPrenda == null) throw new RuntimeException(
          "Primero debe especificar el tipo de prenda");
      this.trama = trama;
    }

    public void setMaterial(Material material) {
      if (this.tipoPrenda == null) throw new RuntimeException(
          "Primero debe especificar el tipo de prenda");
      this.material = material;
    }

    public Prenda generarPrenda() {
      if (this.tipoPrenda == null) throw new RuntimeException(
          "No especificó el Tipo de la prenda. " + caracteristicasNecesariasError);

      if (this.tipoPrenda == null) throw new RuntimeException(
          "No especificó el Material de la prenda. " + caracteristicasNecesariasError);

      //Inserte los demás if menos el de la Trama y Color Secundario; este último no era necesario

      return new Prenda(TipoPrenda tipo,
          Color colorPrimario,
          Color colorSecundario,
          Categoria categoria,
          Trama trama,
          Material material);
    }
  }

  public class Uniforme {
    public Prenda prendaSuperior
    public Prenda prendaInferior
    public Prenda calzado

    public Uniforme(Prenda prendaSuperior,
                    Prenda prendaInferior,
                    Prenda calzado) {
      this.prendaSuperior = prendaSuperior;
      this.prendaInferior = prendaInferior;
      this.calzado = calzado;
    }
  }

  public class BorradorUniforme {
    private Prenda prendaSuperior
    private Prenda prendaInferior
    private Prenda calzado
    public String caracteristicasNecesariasError = "Para crear un uniforme es necesario que" +
        "especifique Prenda Superior, Inferior y Calzado."

    public void setCalzado(Prenda calzado) {
      this.calzado = calzado;
    }

    public void setPrendaInferior(Prenda prendaInferior) {
      this.prendaInferior = prendaInferior;
    }

    public void setPrendaSuperior(Prenda prendaSuperior) {
      this.prendaSuperior = prendaSuperior;
    }

    public Uniforme generarUniforme() {
      if (this.prendaSuperior == null) throw new RuntimeException(
          "No especificó la Prenda Superior. " + caracteristicasNecesariasError);
      if (this.prendaInferior == null) throw new RuntimeException(
          "No especificó la Prenda Inferior. " + caracteristicasNecesariasError);
      if (this.calzado == null) throw new RuntimeException(
          "No especificó el Calzado. " + caracteristicasNecesariasError);

      return new Uniforme(Prenda prendaSuperior,
                          Prenda prendaInferior,
                          Prenda calzado);
    }
  }
}
