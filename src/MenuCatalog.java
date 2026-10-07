import java.util.List;

/** @author Team 8 */
public final class MenuCatalog {

  public enum Category {
    SIDE,
    ENTREE,
    DRINK
  }

  public record MenuItem(String name, Category category) {}

  private static final List<MenuItem> SIDES = List.of(
      new MenuItem("Chow Mein", Category.SIDE),
      new MenuItem("Fried Rice", Category.SIDE),
      new MenuItem("Super Greens", Category.SIDE),
      new MenuItem("White Steamed Rice", Category.SIDE),
      new MenuItem("Brown Steamed Rice", Category.SIDE));

  private static final List<MenuItem> ENTREES = List.of(
      new MenuItem("The Original Orange Chicken", Category.ENTREE),
      new MenuItem("Honey Walnut Shrimp (Premium)", Category.ENTREE),
      new MenuItem("Beijing Beef", Category.ENTREE),
      new MenuItem("Broccoli Beef", Category.ENTREE),
      new MenuItem("Grilled Teriyaki Chicken", Category.ENTREE),
      new MenuItem("Kung Pao Chicken", Category.ENTREE),
      new MenuItem("Honey Sesame Chicken Breast", Category.ENTREE),
      new MenuItem("Black Pepper Chicken", Category.ENTREE),
      new MenuItem("Mushroom Chicken", Category.ENTREE),
      new MenuItem("String Bean Chicken Breast", Category.ENTREE),
      new MenuItem("SweetFire Chicken Breast", Category.ENTREE),
      new MenuItem("Black Pepper Sirloin Steak (Premium)", Category.ENTREE));

  private static final List<MenuItem> DRINKS = List.of(
      new MenuItem("Soda", Category.DRINK));

  private MenuCatalog() {}

  /** @return the side items */
  public static List<MenuItem> sides() {
    return SIDES;
  }

  /** @return the entree items */
  public static List<MenuItem> entrees() {
    return ENTREES;
  }

  /** @return the drink items */
  public static List<MenuItem> drinks() {
    return DRINKS;
  }

  /**
   * @param category the menu category
   * @return the items in the category
   */
  public static List<MenuItem> items(Category category) {
    return switch (category) {
      case SIDE -> SIDES;
      case ENTREE -> ENTREES;
      case DRINK -> DRINKS;
    };
  }

  /**
   * @param category the menu category
   * @return the category display label
   */
  public static String label(Category category) {
    return switch (category) {
      case SIDE -> "Sides";
      case ENTREE -> "Entrees";
      case DRINK -> "Drinks";
    };
  }
}
