abstract class Card {
    protected String name;
    protected int manaCost;

    public Card(String name, int manaCost) {
        this.name = name;
        this.manaCost = manaCost;
    }

    public String getName() { return name; }
    public int getManaCost() { return manaCost; }

    public abstract void play(Player owner, Player enemy);
}