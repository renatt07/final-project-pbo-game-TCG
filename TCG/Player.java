import java.util.ArrayList;

public class Player
{
    private String name;
    private int health = 50;
    private int mana = 0;
    
    private ArrayList<Card> deck = new ArrayList<>();
    private ArrayList<Card> hand = new ArrayList<>();

    public Player(String name) {
        this.name = name;
    }

    public void addManaTurn() {
        this.mana += 2;
    }
    
    // --- UTILS ---
    public String getName() { return name; }
    public int getHealth() { return health; }
    public boolean isAlive() { return health > 0; }
    public void addCardToDeck(Card c) { deck.add(c); }

    public void drawCard() {
        if(deck.size() > 0) {
            Card c = deck.remove(0);
            hand.add(c);
        } else {
            System.out.println(">> Deck habis! Tidak ada kartu ditarik.");
        }
    }

    public void receiveDamage(int dmg) {
        health -= dmg;
        if(health < 0) health = 0;
    }

    public boolean playCard(int index, Player enemy) {
        if(index < 0 || index >= hand.size()) return false;

        Card c = hand.get(index);
        
        if(mana >= c.getManaCost()) {
            mana -= c.getManaCost();
            hand.remove(index);
            c.play(this, enemy);
            return true;
        } else {
            return false;
        }
    }
    
    public int getMana() { return this.mana; }
    
    public ArrayList<Card> getHand() { return this.hand; }
    
    public void payMana(int amount) { this.mana -= amount; }
}