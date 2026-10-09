import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/** Hardcoded menu items from seed data (sides / entrees / drinks). */
public final class MenuCatalog {

  public enum Category {
    SIDE,
    ENTREE,
    APPETIZER,
    DRINK
  }

  public record MenuItem(int id, String name, Category category) {
    /** Queries the database for this item's premium charge (0 if unavailable). */
    public float getPremium(Connection conn) {
      float premium = 0f;
      if (conn == null) {
        return premium;
      }
      try {
        Statement stmt = conn.createStatement();
        String query = "SELECT premium_charge FROM menu_items WHERE menu_item_id = " + id;
        ResultSet result = stmt.executeQuery(query);
        if (result.next()) {
          premium = result.getFloat("premium_charge");
        }
        result.close();
        stmt.close();
      } catch (Exception e) {
        System.out.println("Error fetching premium for " + name + ":\n" + e.getMessage());
      }
      return premium;
    }
  }

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

  private static final List<MenuItem> APPETIZERS = List.of(
      new MenuItem(18, "Chicken Egg Roll", Category.APPETIZER),
      new MenuItem(19, "Veggie Spring Roll", Category.APPETIZER),
      new MenuItem(20, "Cream Cheese Rangoon", Category.APPETIZER),
      new MenuItem(21, "Chicken Potstickers", Category.APPETIZER),
      new MenuItem(22, "Apple Pie Roll", Category.APPETIZER));

  private static final List<MenuItem> DRINKS = List.of(
      new MenuItem(23, "Soda", Category.DRINK));

  private MenuCatalog() {}

  public static List<MenuItem> sides() {
    return SIDES;
  }

  public static List<MenuItem> entrees() {
    return ENTREES;
  }

  public static List<MenuItem> appetizers() {
    return APPETIZERS;
  }

  public static List<MenuItem> drinks() {
    return DRINKS;
  }

  public static List<MenuItem> items(Category category) {
    return switch (category) {
      case SIDE -> SIDES;
      case ENTREE -> ENTREES;
      case APPETIZER -> APPETIZERS;
      case DRINK -> DRINKS;
    };
  }

  public static String label(Category category) {
    return switch (category) {
      case SIDE -> "Sides";
      case ENTREE -> "Entrees";
      case APPETIZER -> "Appetizers";
      case DRINK -> "Drinks";
    };
  }
}
