class Minion extends Card {
    private int attack;

    public Minion(String name, int manaCost, int attack) {
        super(name, manaCost);
        this.attack = attack;
    }

    public int getAttack() {
        return this.attack;
    }

    @Override
    public void play(Player owner, Player enemy) {
        System.out.println(">>> [SERANGAN FISIK] " + name + " maju menerjang!");
        System.out.println("    " + enemy.getName() + " terkena " + attack + " damage!");
        enemy.receiveDamage(attack);
    }
}