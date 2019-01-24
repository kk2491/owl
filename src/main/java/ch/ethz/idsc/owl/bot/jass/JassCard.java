// code by astoll
package ch.ethz.idsc.owl.bot.jass;

// TODO outsource
/** Creates a card of the game jassen with the attributes color and card type
 * 
 * @author astoll */
/* package */ class JassCard {
  static enum Color {
    ROSEN, EICHEL, SCHILTEN, SCHELLE
  }

  static enum Type {
    SIX(1), SEVEN(2), EIGHT(3), NINE(8), TEN(4), JACK(9), QUEEN(5), KING(6), ACE(7),;
    private final int trumpfOrdering;

    private Type(int trumpfOrdering) {
      this.trumpfOrdering = trumpfOrdering;
    }
  }

  final Color color;
  final Type type;
  final boolean isTrumpf;

  JassCard(Color color, Type type, boolean isTrumpf) {
    this.color = color;
    this.type = type;
    this.isTrumpf = isTrumpf;
  }

  /** checks whether the two cards are exactly the same or if two different colors were assigned Trumpf
   * 
   * @param jassCard to check for eligibility
   * @throws RunTimeException */
  public void cheatChecker(JassCard jassCard) {
    if (this.color.equals(jassCard.color) && this.type.equals(jassCard.type)) {
      System.err.println("There are no duplicate cards in a deck!");
      throw new RuntimeException("cards not from the same deck");
    }
    if (this.isTrumpf && jassCard.isTrumpf && !this.color.equals(jassCard.color)) {
      System.err.println("Only card color can be assigned Trumpf!");
      throw new RuntimeException("cards not from the same deck");
    }
  }

  /** checks whether the card is less (worse) than another card.
   * 
   * @param jassCard to be compared to
   * @return true if this card is less (according to the rules) than the given jassCard, false otherwise */
  public boolean isLess(JassCard jassCard) {
    this.cheatChecker(jassCard);
    if (this.isTrumpf && jassCard.isTrumpf) {
      if (this.type.trumpfOrdering < jassCard.type.trumpfOrdering) {
        return true;
      }
    }
    if (!this.isTrumpf && jassCard.isTrumpf) {
      return true;
    }
    if (!this.isTrumpf && !jassCard.isTrumpf) {
      if ((this.color.equals(jassCard.color)) && (this.type.compareTo(jassCard.type) < 0)) {
        return true;
      }
    }
    return false;
  }
}
