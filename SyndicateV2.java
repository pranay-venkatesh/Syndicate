import java.util.ArrayList;
import java.util.Scanner;

public class SyndicateV2 {
    ArrayList <Player> players;
    ArrayList <String> placeNames;
    ArrayList <Place> board;
    ArrayList <Stock> StockMarket;
    ArrayList <Seat> Parliament;
    ArrayList <Party> parties;
    String[] colours = new String[] {"BROWN", "LIGHT BLUE", "PINK", "ORANGE", "RED", "YELLOW", "GREEN", "DARK BLUE", "N/A"};
    Dice diceOne = new Dice();
    Dice diceTwo = new Dice();
    int numPlayers;
    int turnCount;
    int numTotalSeats;
    int totalInfluence;
    SyndicateV2()
    {
        totalInfluence = 0;
        turnCount = 0;
        Parliament = new ArrayList <Seat>();
        parties = new ArrayList <Party>();
        StockMarket = new ArrayList <Stock>();
        players = new ArrayList<Player>();
        placeNames = new ArrayList<String>();
        board = new ArrayList<Place>();
    }
    class Player
    {
        int money;
        String name;
        int position;
        boolean imprisoned;
        int prisonTerm;
        boolean lose;
        ArrayList <OwnedStock> stockPairs = new ArrayList <OwnedStock>();
        ArrayList <Place> owned = new ArrayList<Place>(); 
        Player()
        {
            money = 1500;
            int val = players.size() + 1;
            String a = Integer.toString(val);
            String p = "P";
            name = p.concat(a);
        }
        void move(int numSteps)
        {
            int value = this.position + numSteps;
            if (value < board.size())
                position = value;
            else
            {
                value -= board.size();
                position = value;
                money += 200;
                System.out.println ("You passed Go and earned 200");
            }
        }
        void stockMenu()
        {
            Scanner in = new Scanner (System.in);
            
            System.out.println ("1)Buy Stock");
            System.out.println ("2)Sell Stock");
            int choice = in.nextInt();
            if (choice == 1)
            {
                System.out.println ("MARKET INDEX");
                for (int i = 0; i < StockMarket.size(); i++)
                {                
                    System.out.print (i + ") ");
                    StockMarket.get(i).display();
                }
                System.out.println ("Enter the index of industry.");
                int indexID = in.nextInt();
                System.out.println ("Enter number of stocks to buy");
                int buyStockNum = in.nextInt();
                if (StockMarket.get(indexID).availShares < buyStockNum || money <= (StockMarket.get(indexID).marketValue * buyStockNum))
                {
                    System.out.println ("You have asked for too many stocks!");
                    stockMenu();
                }
                else
                {
                    OwnedStock pOwn = new OwnedStock (this, StockMarket.get(indexID), buyStockNum);
                    StockMarket.get(indexID).availShares -= buyStockNum;
                    money -= (StockMarket.get(indexID).marketValue * buyStockNum);
                }
            }
            if (choice == 2)
            {
                for (int i = 0; i < stockPairs.size(); i++)
                {
                    System.out.print(i + " ");
                    stockPairs.get(i).stock.display();
                }
                System.out.println ("Enter the index of industry.");
                int indexID = in.nextInt();
                OwnedStock temp1 = stockPairs.get(indexID);
                System.out.println ("Enter number of stocks to sell");
                int sellStockNum = in.nextInt();
                if (sellStockNum > temp1.numStocksOwned)      
                {
                    System.out.println ("You don't have that many stocks to sell!");
                    stockMenu();
                }
                else
                {
                    int temp2 = 0;
                    for (int i = 0; i < StockMarket.size(); i++)
                    {
                        if (temp1.stock.industryName == StockMarket.get(i).industryName)
                            temp2 = i;
                    }
                    money += (stockPairs.get(indexID).numStocksOwned) * (stockPairs.get(indexID).stock.marketValue);
                    stockPairs.get(indexID).numStocksOwned -= sellStockNum;
                    StockMarket.get(temp2).availShares += sellStockNum;
                }
            }
        }
        void election()
        {
            Scanner in = new Scanner (System.in);
            System.out.println ("It's election season!");
            System.out.println ("Here's a view of the current parliament.");
            viewParliament();
            System.out.println();
            System.out.println ("Which party do you want to send your lobbyist to?");
            for (int i = 0; i < parties.size(); i++)
            {
                System.out.print (i + " ");
                System.out.println (parties.get(i).name);
            }
            System.out.println();
            int pCh = in.nextInt();
            parties.get(pCh).endorsement(this);
        }
        int fullStockValue()
        {
            int val = 0;
            for (int i = 0; i < stockPairs.size(); i++)
            {
                val += stockPairs.get(i).numStocksOwned;
            }
            return val;
        }
        void mortgageMenu()
        {
            Scanner in = new Scanner (System.in);
            System.out.println ("1)Mortgage a place.");
            System.out.println ("2)Pay mortgages for a place.");
            int randIn = in.nextInt();
            if (randIn == 1)
            {
                for (int i = 0; i < owned.size(); i++)
                {
                    if (owned.get(i).mortgaged == false)
                    {
                        System.out.print (i + " ");
                        owned.get(i).details();
                    }
                } 
                System.out.println ("Mortgage a place? (y/n)");
                char randCh = in.next().charAt(0);
                if (randCh == 'y'||randCh == 'Y')
                {
                    System.out.println ("Enter the index of the place to mortgage.");
                    int r = in.nextInt();
                    owned.get(r).mortgage();
                    money += owned.get(r).cost;
                }
            }
            else if (randIn == 2)
            for (int i = 0; i < owned.size(); i++)
            {
                if (owned.get(i).mortgaged == true)
                {
                    System.out.print (i + " ");
                    owned.get(i).details();
                }
            }
            System.out.println ("Pay mortgages (y/n)?");
            char randCh = in.next().charAt(0);
            if (randCh == 'y'|| randCh == 'Y')
            {
                System.out.println ("Enter index of property to pay mortgage");
                int q = in.nextInt();
                if (owned.get(q).mortgageAmt >= money){
                    System.out.println ("You can't afford to pay the mortgages of this place.");
                    System.out.println ("However, if you use the local Mafia, the mortgages will be paid.");
                    System.out.println ("1) Pay 20 and use Mafia (WARNING: 5 turn prison sentence)");
                    System.out.println ("2) Back to menu");
                    int mafiaChoice = in.nextInt();
                    if (mafiaChoice == 1)
                    {
                        owned.get(q).payMortgage();
                        imprison (5);
                        money -= 20;
                        System.out.println ("You paid 0 in mortgages.");
                        System.out.println ("You paid 20 to the mafia.");
                        System.out.println ("Using the mafia is against the law. You are under arrest for 5 turns.");
                    }
                    if (mafiaChoice == 2)
                        mortgageMenu();
                }
                else
                {
                    int randVal = owned.get(q).mortgageAmt;
                    money -= owned.get(q).mortgageAmt;
                    owned.get(q).payMortgage();
                    System.out.println ("You have paid " + randVal + " in mortgages!"); 
                }
            }
            else
            {
                playNotPrison();
            }
        }
        void singleTurn()
        {
            Scanner in = new Scanner (System.in);
            boolean endTurn = false;
            if (turnCount % 5 == 0)
            {
                election();
            }
            while (!endTurn)
            {
                System.out.println ("You have " + money);
                if (!imprisoned)
                {
                    stockValueChanges();
                    endTurn = playNotPrison();
                }
                else
                {
                    System.out.println ("1)Pay fee and roll dice");
                    System.out.println ("2)Wait " + prisonTerm + " days");
                    int n = in.nextInt();
                    switch (n)
                    {
                        case 1:
                        imprisoned = false;
                        prisonTerm = 0;
                        money -= 100;
                        System.out.println("Dice rolls");
                        int a = diceOne.roll();
                        int b = diceTwo.roll();
                        System.out.println ("Dice one shows " + a + " and dice two shows " + b);    
                        int val = a + b;
                        this.move (val);
                        endTurn = true;
                        break;
                        case 2:
                        prisonTerm --;
                        System.out.println ("Your prison term is " + prisonTerm);
                        if (prisonTerm < 1)
                        {
                            imprisoned = false;
                            playNotPrison();
                            endTurn = true;
                        }
                        endTurn = true;
                        break;
                    }
                }
                if (endTurn)
                     break;
            }
        }
        void imprison (int numTurns)
        {
            this.imprisoned = true;
            this.prisonTerm = numTurns;
            this.position = 7;
        }
        boolean playNotPrison()
        {
            Scanner in = new Scanner (System.in);
            printBoard();
            boolean endTurn = false;
            System.out.println ("1) View places");
            System.out.println ("2) Roll Dice");
            System.out.println ("3) Buy house");
            System.out.println ("4) View mortgages");
            System.out.println ("5) View stock exchange");
            System.out.println ("6) View House of Commons.");
            int n = in.nextInt();
            switch (n)
            {
                case 1:
                for (int i = 0; i<owned.size(); i++)
                {
                    owned.get(i).details();
                }
                break;
                case 2:
                System.out.println ("Dice roll");
                int a = diceOne.roll();
                int b = diceTwo.roll();
                System.out.println ("Dice one shows " + a + " and dice two shows " + b);
                int val = a + b;
                this.move (val);
                System.out.println ("You are at :");
                board.get(position).details();
                
                if (board.get(this.position).reserved == false)
                {
                        if (board.get(this.position).owned)
                        {
                            if (board.get(this.position).mortgaged == true)
                            {
                                System.out.println ("This place is under mortgage. No rent.");
                                return true;
                            }
                            int temp = 0;
                            int rentCash = 0;
                            this.money -= board.get(this.position).rent;
                            for (int i = 0; i<players.size(); i++)
                            {
                                for (int j = 0; j<players.get(i).owned.size(); j++)
                                    if (board.get(this.position) == players.get(i).owned.get(j))
                                    {
                                        players.get(i).money += board.get(this.position).rent;
                                        rentCash = board.get(this.position).rent;
                                        temp = i;
                                    }
                            }
                            System.out.println ("You just paid " + rentCash + " rent to " + players.get(temp).name); 
                            endTurn = true;
                            return true;
                        }
                        else{
                            System.out.println("1)Buy place");
                            System.out.println("2)Pass");
                            int ch = in.nextInt();
                            switch (ch)
                            {
                                case 1:
                                money -= board.get(position).cost;
                                owned.add (board.get(position));
                                board.get(position).owned = true;
                                System.out.println ("You just bought " + board.get(position).name + " for " + board.get(position).cost);
                                return true;
                                case 2:
                                return true;
                            }
                        }
                }
                else if (board.get(this.position).reserved == true)
                {
                    System.out.println ("You are at a reserved location.");
                    if (this.position == 0)
                    return (true);
                    if (this.position == 7)
                        return (true);
                    if (this.position == 14)
                    {
                        money += 100;
                        System.out.println ("You made 100!");
                    }
                    if (this.position == 21)
                    {
                        this.imprison(3);
                        return (true);
                    }
                    if (this.position == 3)
                    {
                        money -= 50;
                        System.out.println ("You lost 50 by income tax");
                        return (true);
                    }
                    if (this.position == 12)
                    {
                        money -= 100;
                        System.out.println ("You lost 100 by super tax");
                        return (true);
                    }
                    if (this.position == 18)
                    {
                        money += 120;
                        System.out.println ("Lottery! You made 120");
                        return (true);
                    }
                    if (this.position == 23)
                    {
                        money += 60;
                        System.out.println ("You made 60 through estate investment");
                        return (true);
                    }
                }
                break;
                case 3:
                int y;
                System.out.println("Are you sure you want to buy a house?");
                System.out.println ("1)Yes");
                System.out.println ("2)No");
                y=in.nextInt();
                if (y==1){
                    endTurn = true;
                    break;
                }
                else
                    break;
                case 4:
                mortgageMenu();
                if (imprisoned == false)
                    playNotPrison();
                break;
                case 5:
                stockMenu();
                break;
                case 6:
                System.out.println ("Here's the view of House of Commons.");
                viewParliament();
                for (int i = 0; i < parties.size(); i++)
                {
                    if (parties.get(i).formingGovernment == true)
                    {
                        if (parties.get(i).endorsing.contains(this))
                        {
                            System.out.println ("The party you endorsed (" + parties.get(i).name + ") is forming the government.");
                        }
                        else
                        {
                            System.out.println ("A party you didn't endorse (" + parties.get(i).name + ") is forming the government.");
                        }
                    }
                }
                break;          
                default:
                System.out.println("Wrong choice");
                playNotPrison();
            }
            
            return false;
        }
    }
    class Place
    {
        String name;
        int cost;
        int rent;
        String colour;
        boolean owned;
        boolean mortgaged;
        int position;
        int numHouses;
        int rentIncPerHouse;
        boolean reserved;
        int mortgageAmt;
        Place(String n, int c, int r, int colVal, int pos, boolean res)
        {
            name = n;
            cost = c;
            rent = r;
            rentIncPerHouse = (int)((10/100) * cost) + ((1/2) * r);
            colour = colours[colVal];
            position = pos;
            reserved = res;
            mortgaged = false;
            mortgageAmt = 0;
            placeNames.add(n);
            board.add(position, this);
        }
        boolean checkHouse (Place a, Player p)
        {
            int count1 = 0;
            int count2 = 0;
            for (int i = 0; i < board.size(); i++)
            {
                if (board.get(i).colour == a.colour)
                    count1++;
            }
            for (int i = 0; i < p.owned.size(); i++)
            {
                if (p.owned.get(i).colour == a.colour)
                    count2++;
            }
            if (count1 == count2)
                return true;
            return false;
        }
        void mortgage()
        {
            mortgaged = true;
            mortgageAmt = (int) (110/100) * cost;
        }
        void payMortgage()
        {
            mortgaged = false;
            mortgageAmt = 0;
        }
        void details()
        {
            System.out.println ("NAME\t\tCOLOUR\tHOUSES\tRENT\tPRICE\tMORTGAGE");
            System.out.println (name + "       " + colour + "    " + numHouses + "     " + rent + "       " + cost + "       " + mortgageAmt);
        }
        String print()
        {
            ArrayList<String>staying = new ArrayList<String>();
            for (int i = 0; i<players.size(); i++)
                if (players.get(i).position == position)
                    staying.add(players.get(i).name);
            return (name + staying);
        }
    }
    class Dice
    {
        int roll()
        {
            int total;
            total = 1 + (int)(Math.random()*6);
            return total;
        }
    }
    class Seat {
        char displayChar;
        Party incumbent;
        Seat (Party i)
        {
            incumbent = i;
            displayChar = i.name.charAt(0);
        }
    }
    class Party {
        String name;
        int influence;
        int variationFactor;
        int numSeats;
        boolean formingGovernment;
        ArrayList <Player> endorsing = new ArrayList <Player>();
        Party (String n, int vF, int nS)
        {
            name = n;
            influence = 0;
            variationFactor = vF;
            numSeats = nS;
        }
        void endorsement (Player p)
        {
            endorsing.add (p);
            influence += p.owned.size() + 1;
            totalInfluence += influence;
        }
        void influenceVariation()
        {
            int fact = (int) Math.random() * 2;
            if (fact == 1)
                fact = -1;
            else
                fact = 1;
            influence += variationFactor * fact;
            if (influence <= 0)
                influence = 0;
        }
    }
    class Stock {
        String industryName;
        int availShares;
        int faceValue;
        int marketValue;
        int variationFactor;
        int dividend;
        Stock (String iN, int fV, int mV, int vF, int d, int aS)
        {
            industryName = iN;
            faceValue = fV;
            marketValue = mV;
            variationFactor = vF;
            dividend = d;
            availShares = aS;
            StockMarket.add(this);
        }
        void factorChange()
        {
            int factor = (int)Math.random()*2;
            if (factor == 1)
            {
                factor = 1;
            }
            if (factor == 2)
            {
                factor = -1;
            }
            marketValue = marketValue + (factor*variationFactor);
            if (marketValue <= 0)
                marketValue = 0;
        }
        void display()
        {
            System.out.println ("INDUSTRY\tSHARE FV\tSHARE MV\tDIVIDEND(%)");
            System.out.println (industryName + "\t\t" + faceValue + "\t\t" + marketValue + "\t\t" + dividend);
        }
        
    }
    class OwnedStock{
        Player owner;
        Stock stock;
        int numStocksOwned;
        OwnedStock (Player p, Stock s, int nSO)
        {
            owner = p;
            stock = s;
            numStocksOwned = nSO;
            p.stockPairs.add(this);
        }
    }
    void generalElections()
    {
        for (int i = 0; i < parties.size(); i++)
        {
            parties.get(i).numSeats = (int)(parties.get(i).influence/totalInfluence)*300;
        }
        Party compParty = parties.get(0);
        for (int i = 0; i < parties.size(); i++)
        {
            int max = compParty.numSeats;
            if (parties.get(i).numSeats > max)
            {
                compParty = parties.get(i);
            }
        }
        for (int i = 0; i < parties.size(); i++)
        {
            if (parties.get(i) == compParty)
                parties.get(i).formingGovernment = true;
        }
        refreshSeats();
    }
    void viewParliament()
    {
        for (int i = 0; i < Parliament.size(); i++)
        {
            System.out.println (Parliament.get(i).displayChar);
            if (i%30 == 0)
                System.out.println();
         }
    }
    void refreshSeats()
    {
        for (int i = 0; i < Parliament.size(); i++)
        {
            Parliament.remove(Parliament.get(i));
        }
        for (int i = 0; i < parties.size(); i++)
        {
            for (int j = 0; j < parties.get(i).numSeats; j++)
                Parliament.add(new Seat(parties.get(i)));
        }
    }
    void stockValueChanges()
    {
        for (int i = 0; i < StockMarket.size(); i++)
        {
            StockMarket.get(i).factorChange();
        }
        for (int j = 0; j < players.size(); j++)
        {
            for (int k = 0; k < players.get(j).stockPairs.size(); k++)
            {
                Player t = players.get(j);
                players.get(j).money += ((t.stockPairs.get(k).stock.dividend)*(t.stockPairs.get(k).numStocksOwned)*(t.stockPairs.get(k).stock.faceValue))/100;
            }
        }
    }
    void printBoard()
    {
        int z=19;
        for (int i = 0; i<=7; i++)
            System.out.print(board.get(i).print() + "\t");
        System.out.println();
        for (int j = 8; j<14; j++)
        {
            System.out.println(board.get(j+z).print() + "\t\t\t\t\t\t\t\t\t\t\t\t\t" + board.get(j).print());
            z=z-2;
        }
        for (int k = 21; k>=14; k--)
            System.out.print(board.get(k).print() + "\t");
        System.out.println();
    }
    void setValues()
    {
        for (int i = 0; i<numPlayers; i++)
            players.add(new Player());
        //Setting all places, i.e., those on which you can stand.
        
        Place go = new Place("GO       ", -200, 0, 8, 0, true);
        Place okr= new Place("Kent Rd  ",60,2,0,1,false);
        Place wr= new Place ("White Rd ",60,4,0,2,false);
        Place fp1 =new Place("INC. TAX ", 0, 0, 8, 3, true);
        Place ai= new Place("Angel Str",100,6,1,4,false);
        Place pr= new Place("Penton Rd",120,8,1,5,false);
        Place pm= new Place("Pall Mall",140,10,2,6,false);
        Place prison=new Place("JAIL     ", 0, 0, 8, 7, true);
        Place wh= new Place("Whitehall",140,10,2,8,false);
        Place na= new Place("North Avn.",160,12,2,9,false);
        Place bs= new Place("Bow Str. ",180,14,3,10,false);
        Place ms= new Place("Marl Str.",180,14,3,11,false);
        Place fp2 = new Place("SUPER TAX", 0, 0, 8, 12, true);
        Place vs= new Place("Vine Str.",200,16,3,13,false);
        Place free = new Place("FREE PARK", 0, 0, 8, 14, true);
        Place ts= new Place("Strand   ",220,18,4,15,false);
        Place fs= new Place("Flee Str.",220,18,4,16,false);
        Place tsq= new Place("Traf Sq. ",240,20,4,17,false);
        Place fp3 = new Place ("LOTTERY  ", 0, 0, 8, 18, true);
        Place lsq= new Place("Lester Sq",260,22,5,19,false);
        Place cs= new Place("Oven Str.",260,22,5,20,false);
        Place goToJail = new Place ("GOTO JAIL", 0, 0, 8, 21,true);
        Place py= new Place("Picadilly",280,24,5,22,false);
        Place fp4 = new Place ("ESTATE   ", 0, 0, 8, 23, true);
        Place rs= new Place("Reg. Str.",300,26,6,24,false);
        Place bos= new Place("Bond Str.",320,28,6,25,false);
        Place pl= new Place("Park Lane",350,35,7,26,false);
        Place mf= new Place("Mayfair  ",400,50,7,27,false);
        
        Stock auto = new Stock("Automobiles", 80, 120, 8, 5, 10);
        Stock ironsteel = new Stock("Iron Steel", 100, 150, 15, 7, 10);
        Stock pharma = new Stock("Pharmaceuticals", 70, 50, 30, 4, 10);
        Stock it = new Stock ("Information Tech", 130, 140, 10, 6, 10);
        Stock edu = new Stock ("Education", 50, 40, 10, 10, 10);
        Stock petro = new Stock ("Petrochemicals", 200, 230, 30, 12, 10);
        Stock agro = new Stock ("Agriculture", 120, 100, 15, 6, 10);
        Stock film = new Stock ("Film & TV", 170, 200, 20, 10, 10);
        Stock text = new Stock ("Textiles", 90, 100, 5, 10, 10);
        
        Party conservative = new Party ("Conservative Party", 10 , 60);
        Party labour = new Party ("Labour Party", 5, 60);
        Party socialist = new Party ("Socialist Party", 15, 60);
        Party green = new Party ("Green Party", 5, 60);
        Party reform = new Party ("Reform Party", 10, 60);
        
    }
    int menu()
    {
        Scanner sc = new Scanner (System.in);
        System.out.println("Syndicate");
        System.out.println("How many players?(2 or more)");
        int a = sc.nextInt();
        return (a);
    }
    public static void main(String[]args)
    {
        Scanner sc = new Scanner (System.in);
        SyndicateV2 mon = new SyndicateV2();
        int b = mon.menu();
        if (b <= 1)
        {
            System.out.println("Nice try. More than one player please...");
            mon.menu();
        }
        mon.numPlayers = b;
        mon.setValues();
        boolean gameOver = false;
       
        while (!gameOver)
        {
            mon.turnCount += 1;
            for (int i = 0; i < mon.players.size(); i++)
            {
                System.out.println ("It's " + mon.players.get(i).name + "'s turn");
                mon.players.get(i).singleTurn();
                
            }
            if (mon.turnCount % 5 == 0)
                mon.generalElections();
            for (int j = 0; j < mon.players.size(); j++)
            {
                if (mon.players.get(j).money <= 0)
                {
                    System.out.println ("Sadly, " + mon.players.get(j).name + " has lost.");
                    mon.players.remove(mon.players.get(j));
                }
            }
            if (mon.players.size() <= 1)
            {
                gameOver=true;
                System.out.println ("The game has ended.");
            }
        }
    }
}


    
