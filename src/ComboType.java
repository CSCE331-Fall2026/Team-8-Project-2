public enum ComboType {
  BOWL(1, "Bowl", 1, 1, 0),
  PLATE(2, "Plate", 1, 2, 0),
  BIGGER_PLATE(3, "Bigger Plate", 1, 3, 0),
  A_LA_CARTE(4, "A La Carte", 0, 0, 0),
  DRINK(5, "Drink", 0, 0, 1),
  BOWL_COMBO(6, "Bowl Combo", 1, 1, 1),
  PLATE_COMBO(7, "Plate Combo", 1, 2, 1),
  BIGGER_PLATE_COMBO(8, "Bigger Plate Combo", 1, 3, 1);

  private final int comboId;
  private final String displayName;
  private final int requiredSides;
  private final int requiredEntrees;
  private final int requiredDrinks;

  ComboType(int comboId, String displayName, int requiredSides, int requiredEntrees, int requiredDrinks) {
    this.comboId = comboId;
    this.displayName = displayName;
    this.requiredSides = requiredSides;
    this.requiredEntrees = requiredEntrees;
    this.requiredDrinks = requiredDrinks;
  }

  public int comboId() {
    return comboId;
  }

  public String displayName() {
    return displayName;
  }

  public int requiredSides() {
    return requiredSides;
  }

  public int requiredEntrees() {
    return requiredEntrees;
  }

  public int requiredDrinks() {
    return requiredDrinks;
  }

  public int limitFor(MenuCatalog.Category category) {
    return switch (category) {
      case SIDE -> requiredSides;
      case ENTREE -> requiredEntrees;
      case DRINK -> requiredDrinks;
    };
  }

  public boolean isALaCarte() {
    return this == A_LA_CARTE;
  }

  public String progressText() {
    if (this == DRINK) {
      return "Pick 1 drink";
    }
    if (isALaCarte()) {
      return "Pick 1 side or entree";
    }
    String text = "Pick " + requiredSides + " side" + (requiredSides == 1 ? "" : "s")
        + ", " + requiredEntrees + " entree" + (requiredEntrees == 1 ? "" : "s");
    if (requiredDrinks > 0) {
      text += ", then " + requiredDrinks + " drink";
    }
    return text;
  }
}
