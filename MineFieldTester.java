package PA3;

public class MineFieldTester {
	public static void main(String[] args) {
		       boolean[][] smallMineField = 
			      {{false, false, false, false}, 
			       {true, false, false, false}, 
			       {false, true, true, false},
			       {false, true, false, true}};
			   
			   boolean[][] emptyMineField = 
			      {{false, false, false, false}, 
			       {false, false, false, false}, 
			       {false, false, false, false},
			       {false, false, false, false}};
			   
			   boolean[][] almostEmptyMineField = 
			      {{false, false, false, false}, 
			       {false, false, false, false}, 
			       {false, false, false, false},
			       {false, true, false, false}};
			   
			   
			MineField test1 = new MineField(smallMineField);
		
			MineField test2 = new MineField(emptyMineField);
	
			MineField test3 = new MineField(almostEmptyMineField);
			
			MineField test = new MineField(5, 5, 8);
			test.populateMineField(3, 1);
			
			//test.print();
			//test1.resetEmpty();
			//test1.print();
			System.out.println("-----");
			//test2.print();
			System.out.println("-----");
			//test3.print();
			int a = test.numAdjacentMines(3, 2);
			//System.out.println( a + " ");
			System.out.println(test.inRange(4, 4) + " " + test.numAdjacentMines(3, 1) + " " + test.numMines());
			
	}
	
	 


}
