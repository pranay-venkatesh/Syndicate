/*It needs some work, but this is the initial draft.*/

import java.util.ArrayList;
import java.util.Scanner;

public class Syndicate {
    ArrayList <Player> players;
    ArrayList <String> placeNames;
    ArrayList <Place> board;
    String[] colours = new String[] {"BROWN", "LIGHT BLUE", "PINK", "ORANGE", "RED", "YELLOW", "GREEN", "DARK BLUE", "N/A"};
    Dice diceOne = new Dice();
    Dice diceTwo = new Dice();
    int numPlayers;
    Syndicate()
    {
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
                System.out.println ("You passed Go and made 200");
            }
        }
        void singleTurn()
        {
            Scanner in = new Scanner (System.in);
            boolean endTurn = false;
            while (!endTurn)
            {
                System.out.println ("You have " + money);
                if (!imprisoned)
                    endTurn = playNotPrison();
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
                        System.out.println ("Dice roll");
                        int a = diceOne.roll();
                        int b = diceTwo.roll();
                        System.out.println ("Dice one shows " + a + " and dice two shows " + b);    
                        int val = a + b;
                        this.move (val);
                        endTurn = true;
                        break;
                        case 2:
                        prisonTerm --;
                        if (prisonTerm <= 1)
                            playNotPrison();  
                        System.out.println ("Your prison term is " + prisonTerm);
                        break;
                    }
                }
                
                if (endTurn)
                     break;
            }
        }
    
        boolean playNotPrison()
        {
            Scanner in = new Scanner (System.in);
            printBoard();
            boolean endTurn = false;
            System.out.println ("1) View places");
            System.out.println ("2) Roll Dice");
            System.out.println ("3) Buy house");
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
                            break;
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
                    System.out.println ("You are at a reserved location. You cannot buy anything here.");
                    if (this.position == 7)
                        return true;
                    if (this.position == 14)
                    {
                        money += 100;
                        System.out.println ("You made 100!");
                    }
                    if (this.position == 21)
                    {
                        this.imprisoned = true;
                        this.prisonTerm = 3;
                        this.position = 7;
                        return (true);
                    }
                    if (this.position == 3)
                    {
                        money -= 50;
                        System.out.println ("You lost 50 by income tax");
                        
                    }
                    if (this.position == 12)
                    {
                        money -= 100;
                        System.out.println ("You lost 100 by super tax");
                    }
                    if (this.position == 18)
                    {
                        money += 120;
                        System.out.println ("Lottery! You made 120");
                    }
                    if (this.position == 23)
                    {
                        money += 60;
                        System.out.println ("You made 60 through stock market");
                    }
                }
                
                break;
                case 3:
                buyHouse();
            }
            
            return false;
        }
        void buyHouse()
        {
            Scanner in = new Scanner (System.in);
            System.out.println ("Select location");
            for (int i = 0; i<owned.size(); i++)
            {
                owned.get(i).details(); 
            }
            int choice = in.nextInt();
            Place h = owned.get(choice);
            if (h.checkHouse (h, this))
            {
                h.numHouses++;
                h.rent = h.rent + h.rentIncPerHouse;
                System.out.println ("You have built one house on " + h.name);
            }
            else
            {
                System.out.println ("Buy all the " + h.colour + " coloured places to build a house here.");
                buyHouse();
            }
        }
    }
    class Place
    {
        String name;
        int cost;
        int rent;
        String colour;
        boolean owned;
        int position;
        int numHouses;
        int rentIncPerHouse;
        boolean reserved;
        Place(String n, int c, int r, int colVal, int pos, boolean res)
        {
            name = n;
            cost = c;
            rent = r;
            rentIncPerHouse = (int)((10/100) * cost) + ((1/2) * r);
            colour = colours[colVal];
            position = pos;
            reserved = res;
            placeNames.add(n);
            board.add(position, this);
        }
        boolean checkHouse (Place a, Player p)
        {
            int count1 = 0;
            int count2 = 0;
            for (int i = 0; i <= board.size(); i++)
            {
                if (board.get(i).colour == a.colour)
                    count1++;
            }
            for (int i = 0; i <= p.owned.size(); i++)
            {
                if (p.owned.get(i).colour == a.colour)
                    count2++;
            }
            if (count1 == count2)
                return true;
            return false;
        }
        void details()
        {
            System.out.println ("NAME\tCOLOUR\tHOUSES\tRENT");
            System.out.println (name + " " + colour + " " + numHouses + " " + " " + rent);
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
    void printBoard()
    {
        for (int i = 0; i<=7; i++)
            System.out.print(board.get(i).print() + "\t");
        System.out.println();
        for (int j = 8; j<14; j++)
            System.out.println(board.get(j+14).print() + "\t\t\t\t\t\t\t\t\t\t\t\t\t" + board.get(j).print());
        for (int k = 14; k<=21; k++)
            System.out.print(board.get(k).print() + "\t");
        System.out.println();
    }
    void setValues()
    {
        for (int i = 0; i<numPlayers; i++)
            players.add(new Player());
        //Setting all places, i.e., those on which you have to stand but you can't buy, sell or be charged rent.
        
        Place go = new Place("GO       ", -200, 0, 8, 0, true);
        Place okr= new Place("Kent Rd  ",60,2,0,1,false);
        Place wr= new Place ("White Rd ",60,4,0,2,false);
        Place fp1 =new Place("INC. TAX ", 0, 0, 8, 3, true);
        Place ai= new Place("Angel Str",100,6,1,4,false);
        Place pr= new Place("Penton Rd",120,8,1,5,false);
        Place pm= new Place("Pall Mall",140,10,2,6,false);
        Place prison=new Place("PRISON   ", 0, 0, 8, 7, true);
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
        Place fp4 = new Place ("STOCKS   ", 0, 0, 8, 23, true);
        Place rs= new Place("Reg. Str.",300,26,6,24,false);
        Place bos= new Place("Bond Str.",320,28,6,25,false);
        Place pl= new Place("Park Lane",350,35,7,26,false);
        Place mf= new Place("Mayfair  ",400,50,7,27,false);
    }
    int menu()
    {
        Scanner sc = new Scanner (System.in);
        System.out.println("Syndicate");
        System.out.println("How many players?");
        int a = sc.nextInt();
        return (a);
    }
    public static void main(String[]args)
    {
        Scanner sc = new Scanner (System.in);
        Syndicate mon = new Syndicate();
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
            for (int i = 0; i < mon.players.size(); i++)
            {
                System.out.println ("It's " + mon.players.get(i).name + "'s turn");
                mon.players.get(i).singleTurn();
            }
            for (int j = 0; j < mon.players.size(); j++)
            {
                if (mon.players.get(j).money <= 0)
                    mon.players.remove(mon.players.get(j));
                System.out.println ("Sadly, " + mon.players.get(j).name + " has lost.");
            }
            if (mon.players.size() <=1)
            {
                gameOver=true;
                System.out.println ("The game has ended.");
            }
        }
    }
}
    
