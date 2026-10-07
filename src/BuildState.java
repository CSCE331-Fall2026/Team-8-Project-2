import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** In-progress combo selections for the cashier build panel. */
public final class BuildState {

  private final ComboType type;
  private final List<MenuCatalog.MenuItem> selected = new ArrayList<>();

  public BuildState(ComboType type) {
    this.type = type;
  }

  public ComboType type() {
    return type;
  }

  public void toggle(MenuCatalog.MenuItem item) {
    if (selected.contains(item)) {
      selected.remove(item);
      return;
    }
    if (!canAdd(item)) {
      return;
    }
    selected.add(item);
  }

  public boolean isComplete() {
    if (type.isALaCarte()) {
      return selected.size() == 1;
    }
    return count(MenuCatalog.Category.SIDE) == type.requiredSides()
        && count(MenuCatalog.Category.ENTREE) == type.requiredEntrees()
        && count(MenuCatalog.Category.DRINK) == type.requiredDrinks();
  }

  public String summary() {
    if (selected.isEmpty()) {
      return "(none)";
    }
    return selected.stream().map(MenuCatalog.MenuItem::name).collect(Collectors.joining(", "));
  }

  public String cartLine() {
    return type.displayName() + ": " + summary();
  }

  public List<MenuCatalog.Category> visibleCategories() {
    if (type.isALaCarte()) {
      return List.of(MenuCatalog.Category.SIDE, MenuCatalog.Category.ENTREE);
    }
    List<MenuCatalog.Category> categories = new ArrayList<>();
    for (MenuCatalog.Category category : MenuCatalog.Category.values()) {
      if (type.limitFor(category) > 0) {
        categories.add(category);
      }
    }
    return categories;
  }

  public List<MenuCatalog.MenuItem> items() {
    return List.copyOf(selected);
  }

  private boolean canAdd(MenuCatalog.MenuItem item) {
    if (type.isALaCarte()) {
      return selected.isEmpty() && item.category() != MenuCatalog.Category.DRINK;
    }
    return count(item.category()) < type.limitFor(item.category());
  }

  private long count(MenuCatalog.Category category) {
    return selected.stream().filter(item -> item.category() == category).count();
  }
}
