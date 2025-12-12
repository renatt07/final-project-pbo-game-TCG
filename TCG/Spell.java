class Spell extends Card {
    private int damage;

    public Spell(String name, int manaCost, int damage) {
        super(name, manaCost);
        this.damage = damage;
    }

    public int getDamage() {
        return this.damage;
    }

    @Override
    public void play(Player owner, Player enemy) {
        System.out.println(">>> [MAGIC] " + name + " dilontarkan!");
        System.out.println("    " + enemy.getName() + " terkena " + damage + " damage magic!");
        enemy.receiveDamage(damage);
    }
}