/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mpirimpa;

import com.sun.org.apache.bcel.internal.generic.AALOAD;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 *
 * @author NICK
 */
public class GameLogic {
    
    private Deck deck;
    private CardStack st;
    private ArrayList<ScoredCards> team1Scored ;
    private ArrayList<ScoredCards> team2Scored ;
    private HashMap<String,Integer> cardPoints;
    private ScoredCards bufferedCards;
    private Player[] player;  //representing the 4 players (0 for human)
    private ArrayList<Card> biribakia; //has 22 cards (11 for each biribaki)
    
    private boolean tookBiribaki[];
    
    private boolean gameEnd;
    
    public GameLogic()
    {
        tookBiribaki=  new boolean[2];
        tookBiribaki[0] = false;
        tookBiribaki[1] = false;
        gameEnd = false;
        deck = new Deck(2);
        st = new CardStack();
        team1Scored = new ArrayList<>();
        team2Scored = new ArrayList<>();
        cardPoints = new HashMap<>();
        player = new Player[4];
        biribakia = new ArrayList<>();
        player[0] = new Player(2,0); //coPlayer is the opposite one on the table
        player[1] = new PlayerAgent(3,1); //Player(3);
        player[2] = new PlayerAgent(0,2); //Player(0);
        player[3] = new PlayerAgent(1,3); //Player(1);
        initCardPoints();
        dealCards();
        //Deal Cards to the 2 side-piles(biribakia)
        for (int i = 0; i < 22; i++) {
            biribakia.add(deck.drawCard());
        }
        st.putCardToStack(deck.drawCard());
    }
    
    /**
     * Initialing the point values of cards for the game mpirimba.
     */
    public void initCardPoints()
    {
        cardPoints.put("Jok", 20); //joker
        cardPoints.put("2", 10);
        cardPoints.put("A", 15); //ace
        for (int i = 3; i <= 7; i++) {
            cardPoints.put(String.valueOf(i), 5);
        }
        cardPoints.put("8", 10);
        cardPoints.put("9", 10);
        cardPoints.put("10", 10);
        cardPoints.put("J", 10);
        cardPoints.put("Q", 10);
        cardPoints.put("K", 10);
    }
    
    /**
     * Deals 11 cards to each player.
     */
    public void dealCards()
    {
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 4; j++) {
                player[j].addCard(deck.drawCard());
            }
        }
        
    }
    
    public int getRemainingCards()
    {
        return deck.getRemainingCards();
    }
    
    /**
     * Get the topmost card from the deck and remove it.
     * 
     * @return first card
     */
    public Card drawCard()
    {
        Card card = deck.drawCard();
        if(card == null) gameEnd = true;
        return card;
    }
    
    public void discardCard(Card c)
    {
        st.putCardToStack(c);
    }
    
    public void newStraightCombo(int type)
    {
        bufferedCards = new StraightCombination(type);
    }
    
    public void newXofAkindCombo(int type,int value)
    {
        bufferedCards = new XofAKindCombination(type, value);
    }
    
    public void clearBufferedCards()
    {
        bufferedCards = null;
    }
    
    public ScoredCards getBufferedCards()
    {
        return bufferedCards;
    }
    
    /**
     * Returns 1 for "success", -1 for "cannot be placed", 0 for "could be placed by replacing a wildcard".
     * 
     * @param ncard
     * @return 
     */
    public int addCardToBufferedCombo(Card ncard)
    {
        return bufferedCards.addNewCard(ncard);
        
    }
    
    /**
     * Returns 1 for "success", -1 for "cannot be placed", 0 for "could be placed by replacing a wildcard".
     * 
     * @param ncard
     * @return 
     */
    public int addCardToExistingCombo(Card ncard,int team,int i)
    {
        if(team==1)
        {
            return team1Scored.get(i).addNewCard(ncard);
        }
        else
        {
            return team2Scored.get(i).addNewCard(ncard);
        }
    }
    
    public Player getPlayer(int i)
    {
        return player[i];
    }
    
    /**
     * View the faceup stack of cards
     * 
     * @return 
     */
    public CardStack getStack()
    {
        return st;
    }
    
    /**
     * Adds the buffered combo to the team's scored cards.
     * Returns true if scoring was successful.
     * 
     * 
     * @param cardsToScore
     * @param team
     * @return 
     */
    public boolean scoreCards(int team)
    {
        
        if(team==1)
        {
            if(bufferedCards!=null)
            {
                if(bufferedCards.getCards().size()>2)
                {
                    team1Scored.add(bufferedCards);
                    return true;
                }
            }
        }
        else if(team==2)
        {
            if(bufferedCards!=null)
            {
                if(bufferedCards.getCards().size()>2)
                {
                    team2Scored.add(bufferedCards);
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean scoreCards(ScoredCards com, int team)
    {
        if(team==1)
        {
            team1Scored.add(com);
            return true;
        }
        else if(team==2)
        {
            team2Scored.add(com);
            return true;
        }
        
        return false;
    }
    
    /**
     * To check if all the cards are in "Straight" combo and have the same suit.
     * 
     * @param cards
     * @return 
     */
    public boolean isOrderedScore(ArrayList<Card> cards)
    {
        Card tmp = cards.get(0);
        for (Card card : cards) {
            if( (card.getCardValue() - tmp.getCardValue() != 1) || (card.getCardSuit()!=(tmp.getCardSuit())) ) return false;
            tmp = card;
        }
        return true;
    }
    
    /**
     * To check if the cards are X of a kind.
     * 
     * @param cards
     * @return 
     */
    public boolean isXofaKindScore(ArrayList<Card> cards)
    {
        Card tmp = cards.get(0);
        for (Card card : cards) {
            if(card.getCardValue() - tmp.getCardValue() != 0) return false;
            tmp = card;
        }
        return true;
    }
    
    public ScoredCards getScoredCards1(int i)
    {
        return team1Scored.get(i);
    }
    
    public ScoredCards getScoredCards2(int i)
    {
        return team2Scored.get(i);
    }
    
    public ArrayList<ScoredCards> getScoredCards1()
    {
        return team1Scored;
    }
    
    public ArrayList<ScoredCards> getScoredCards2()
    {
        return team2Scored;
    }
    
    /**
     * Checks if the game is over, either by no cards remaining in deck, or one player "got out".
     * 
     * @return 
     */
    public boolean isGameOver()
    {
        if(deck.getRemainingCards()<=0)
        {
            gameEnd = true;
            return true;
        }
        else
        {
            for (Player player1 : player) {
                if(player1.getHand().size()<=0 && tookBiribaki[player1.team-1])
                {
                    gameEnd = true ;
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Checks if the player needs to take a biribaki, and adds it if needed.
     * 
     * @param pl
     * @return 
     */
    public boolean checkForBiribaki(int pli)
    {
        if(player[pli].getHand().size()<=0 && !tookBiribaki[player[pli].team-1])
        {
            //Add the cards of one biribaki to players hand
            for (int i = 0; i < 11; i++) {
                player[pli].addCard(new Card(biribakia.get(0)));
                biribakia.remove(0);
            }
            tookBiribaki[player[pli].team-1] = true;
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * Calculates and returns the sum of current points on table for given team.
     * 
     * @param team
     * @return 
     */
    public int calculatePointsOnTable(int team)
    {
        int pointsSum = 0;
        ArrayList<ScoredCards> teamCards ;
        if(team == 1) teamCards = team1Scored;
        else if(team == 2) teamCards =team2Scored;
        else return -1;
        
        int curPoints;
        for (ScoredCards combo : teamCards) {
            if(combo.getCards().size()==13) //Thousand points combination
            {
                curPoints = 1000;
                if(combo.getWildCardPos()>=0) curPoints = curPoints/2;
            }
            else if(combo.getCards().size()>=7) //Biriba
            {
                if(combo.getTypeOfScore()<4)
                {
                    curPoints = 300;
                }
                else
                {
                    curPoints = 400;
                }
                if(combo.getWildCardPos()>=0) curPoints = curPoints/2;
            }
            else //Just count the points
            {
                ArrayList<Card> cards = combo.getCards();
                curPoints = 0;
                for (Card card : cards) {
                    curPoints += cardPoints.get(card.getCardSymbol());
                }
            }
            
            pointsSum += curPoints ;
        }
        
        return pointsSum;
    }
    
    public int calculatePointsInHand(int pl)
    {
        ArrayList<Card> handCards = player[pl].getHand();
        int sumPoints = 0;
        for (Card card : handCards) {
            sumPoints+=cardPoints.get(card.getCardSymbol());
        }
        return sumPoints;
    }
    
    /**
     * Returns the given team's total score. Use after a game ends.
     * 
     * @param team
     * @return 
     */
    public int calculateTotalTeamPoints(int team)
    {
        int pl1,pl2,enemyTeam;
        if(team==1) 
        {
            pl1=0;
            pl2=2;
            enemyTeam = 2;
        }
        else if(team==2)
        {
            pl1=1;
            pl2=3;
            enemyTeam=1;
        }
        else return -1;
        
        int totalPoints = 0;
        totalPoints+= calculatePointsOnTable(team);
        totalPoints-= calculatePointsInHand(pl2);
        totalPoints-= calculatePointsInHand(pl1);
        
        //Also others !!! (If one team finished before other team took biribaki)
        if(tookBiribaki(team) && !tookBiribaki(enemyTeam) && getRemainingCards()>0)
        {
            totalPoints+=100;
        }
        else if(!tookBiribaki(team) && tookBiribaki(enemyTeam) && getRemainingCards()>0)
        {
            totalPoints-=100;
        }
        
        return totalPoints;
    }
    
    public int getPlayerRemainingCards(int pl)
    {
        return player[pl].getRemainingCards();
    }
    
    public ArrayList<Card> getBiribakia()
    {
        return biribakia;
    }
    
    public HashMap<String,Integer> getCardPoints()
    {
        return cardPoints;
    }
    
    public boolean tookBiribaki(int team)
    {
        return tookBiribaki[team-1];
    }
    
}
