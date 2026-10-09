import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public enum ComboType {
  BOWL(1, "Bowl", 1, 1, 0, 0, 0f),
  PLATE(2, "Plate", 1, 2, 0, 0, 0f),
  BIGGER_PLATE(3, "Bigger Plate", 1, 3, 0, 0, 0f),
  A_LA_CARTE(4, "A La Carte", 0, 0, 0, 0, 0f),
  DRINK(5, "Drink", 0, 0, 0, 1, 0f),
  BOWL_COMBO(6, "Bowl Combo", 1, 1, 0, 1, 0f),
  PLATE_COMBO(7, "Plate Combo", 1, 2, 0, 1, 0f),
  BIGGER_PLATE_COMBO(8, "Bigger Plate Combo", 1, 3, 0, 1, 0f),
  APPETIZER(9, "Appetizer", 0, 0, 1, 0, 0f);

  private final int comboId;
  private float price;
  private final String displayName;
  private final int requiredSides;
  private final int requiredEntrees;
  private final int requiredAppetizers;
  private final int requiredDrinks;

  ComboType(
      int comboId,
      String displayName,
      int requiredSides,
      int requiredEntrees,
      int requiredAppetizers,
      int requiredDrinks,
      float price) {
    this.comboId = comboId;
    this.price = price;
    this.displayName = displayName;
    this.requiredSides = requiredSides;
    this.requiredEntrees = requiredEntrees;
    this.requiredAppetizers = requiredAppetizers;
    this.requiredDrinks = requiredDrinks;
  }

  public int comboId() {
    return comboId;
  }

  /** Returns the cached combo price (seed/default until refreshed from the database). */
  public float price() {
    return price;
  }

  /** Queries the database for this combo's price and caches it on the enum constant. */
  public float getPrice(Connection conn) {
    if (conn == null) {
      return price;
    }
    try {
      Statement stmt = conn.createStatement();
      String query = "SELECT combo_price FROM combos WHERE combo_id = " + comboId;
      ResultSet result = stmt.executeQuery(query);
      if (result.next()) {
        price = result.getFloat("combo_price");
      }
      result.close();
      stmt.close();
    } catch (Exception e) {
      System.out.println("Error fetching price for " + displayName + ":\n" + e.getMessage());
    }
    return price;
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

  public int requiredAppetizers() {
    return requiredAppetizers;
  }

  public int requiredDrinks() {
    return requiredDrinks;
  }

  public int limitFor(MenuCatalog.Category category) {
    return switch (category) {
      case SIDE -> requiredSides;
      case ENTREE -> requiredEntrees;
      case APPETIZER -> requiredAppetizers;
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
    if (this == APPETIZER) {
      return "Pick 1 appetizer";
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
