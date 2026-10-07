import java.util.List;

/** Hardcoded menu items from seed data (sides / entrees / drinks). */
public final class MenuCatalog {

  public enum Category {
    SIDE,
    ENTREE,
    DRINK
  }

  public record MenuItem(int id, String name, Category category) {}

  private static final List<MenuItem> SIDES = List.of(
      new MenuItem(13, "Chow Mein", Category.SIDE),
      new MenuItem(14, "Fried Rice", Category.SIDE),
      new MenuItem(15, "Super Greens", Category.SIDE),
      new MenuItem(16, "White Steamed Rice", Category.SIDE),
      new MenuItem(17, "Brown Steamed Rice", Category.SIDE));

  private static final List<MenuItem> ENTREES = List.of(
      new MenuItem(1, "The Original Orange Chicken", Category.ENTREE),
      new MenuItem(2, "Honey Walnut Shrimp (Premium)", Category.ENTREE),
      new MenuItem(3, "Beijing Beef", Category.ENTREE),
      new MenuItem(4, "Broccoli Beef", Category.ENTREE),
      new MenuItem(5, "Grilled Teriyaki Chicken", Category.ENTREE),
      new MenuItem(6, "Kung Pao Chicken", Category.ENTREE),
      new MenuItem(7, "Honey Sesame Chicken Breast", Category.ENTREE),
      new MenuItem(8, "Black Pepper Chicken", Category.ENTREE),
      new MenuItem(9, "Mushroom Chicken", Category.ENTREE),
      new MenuItem(10, "String Bean Chicken Breast", Category.ENTREE),
      new MenuItem(11, "SweetFire Chicken Breast", Category.ENTREE),
      new MenuItem(12, "Black Pepper Sirloin Steak (Premium)", Category.ENTREE));

  private static final List<MenuItem> DRINKS = List.of(
      new MenuItem(23, "Soda", Category.DRINK));

  private MenuCatalog() {}

  public static List<MenuItem> sides() {
    return SIDES;
  }

  public static List<MenuItem> entrees() {
    return ENTREES;
  }

  public static List<MenuItem> drinks() {
    return DRINKS;
  }

  public static List<MenuItem> items(Category category) {
    return switch (category) {
      case SIDE -> SIDES;
      case ENTREE -> ENTREES;
      case DRINK -> DRINKS;
    };
  }

  public static String label(Category category) {
    return switch (category) {
      case SIDE -> "Sides";
      case ENTREE -> "Entrees";
      case DRINK -> "Drinks";
    };
  }
}
