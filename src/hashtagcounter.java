
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class hashtagcounter 
{
	final static String outputFile = "output_file.txt";											//variable to hold our output file name
	
	static BufferedReader br;																	//object used to read input file
	static FileWriter fw;																		//object used to write to output file
	static Hashtable<String, Node<String>> hashtable = new Hashtable<String,Node<String>>();	//Hashtable where key = hastag, value = Node in heap	
	static MaxFibonacciHeap<String> heap = new MaxFibonacciHeap<String>();						//Max Fibonacci Heap where data = hashtag, key = frequency of that hashtag
	
	/*
	 * The main method of our class. Most of the work has been separated out into other methods to enhance readability of the code.
	 */
	public static void main(String[] args)
	{
		String input = args[0];				
		
		//attempt to initialize the input and output variables. If they are created successfully begin reading from the input file.
		if(createInputOutput(input))
		{
			readInputFile();
		}
		
		try {
			br.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * This method reads the data from the input file and stops when it reaches the stop line in the input file.	
	 */
	private static void readInputFile() 
	{
		try
		{
			//read the first line from the input file
			String currentLine = br.readLine();

			//continue reading from the input file until the stop line is reached
			while(!currentLine.toLowerCase().equals("stop") )
			{
				readInputLine(currentLine);			//handle the current input line
				currentLine = br.readLine();		//now that the input line has been handled, read in the next line
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}

		
	}

	/*
	 * This method determines whether the given line in the input file is a hashtag
	 * or a query and then performs the appropriate action.
	 */
	private static void readInputLine(String currentLine) 
	{
		if(currentLine.substring(0,1).equals("#"))
		{
			String[] strings = currentLine.split(" ");
			String hashtag = strings[0].substring(1);
			int freq = Integer.parseInt(strings[1]);
			
			//first we check if our Hashtable already contains the hashtag we are looking for
			if(hashtable.containsKey(hashtag))
			{
				
				//our Hashtable contains the hashtag so we will increase the frequency of the corresponding Node 
				Node<String> temp = hashtable.get(hashtag);
				
				heap.increaseKey(temp, freq);
				
			}
			else
			{
				//our Hashtable does not contain the hashtag so we will need to create a new Node
				//for this hashtag and insert it into both the Hashtable and the heap
				Node<String> temp = new Node<String>(hashtag,freq);
				hashtable.put(hashtag, temp);
				
				//add Node to heap
				heap.insert(temp);
			}
			
		}
		else
		{
			//Since we have entered this else statement the given input line must be a query since it cannot be a stop or hastag
			
			//Now we will need to find the top most n hashtags and write them to the output file
			int n = Integer.parseInt(currentLine);			
			
			ArrayList<Node<String>> list = new ArrayList<Node<String>>(n);
			String s = "";
			
			for(int i = 0; i < n; i++)
			{
				
				Node<String> m = heap.removeMax();
				list.add(m);
				s += (m.getData()+",");				
			}
			s = s.substring(0, s.length()-1);
			try{
				fw.write(s+"\n");
			}catch(Exception e)
			{
				e.printStackTrace();
			}

			//reinsert max nodes
			for(int i = 0; i < list.size(); i++)
			{
				Node<String> t = new Node<String>(list.get(i).getData(),list.get(i).getFreq());
				heap.insert(t);
				hashtable.put(t.getData(), t);
			}
		}
		
		
	}

	/*
	 * This method attempts to initialize our input and output variables and returns false if it fails to do so.
	 */
	private static boolean createInputOutput(String input) 
	{		
		try
		{
			br = new BufferedReader(new FileReader(input));
			fw = new FileWriter(outputFile);
			return true;
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}		
	}
	
	static class MaxFibonacciHeap<T> 
	{
		private Node<T> maxNode;	//the max node in the heap
		int nodes;					//the number of nodes in the entire heap

		public Node<T> getMaxNode(){	return maxNode;	}
		public boolean isEmpty(){	return (nodes==0?true:false);	}
		
		//creates a new, empty heap
		public MaxFibonacciHeap()
		{
			
		}
		
		//inserts the given node into the heap
		public void insert(Node<T> node)
		{			
			//if the max node is null, our heap must be empty
			//so we can set the max node to be the node to be inserted and be done
			if(maxNode==null)
			{
				maxNode = node;
			}
			
			//when the heap is not empty, we add the new node to be a sibling of the max node
			//this involves setting the sibling pointers for the new node and
			//updating the sibling pointers of the nodes to the left and right of the new node
			//update max node pointer if new node is greater
			else
			{
				node.setLeftSibling(maxNode);
				node.setRightSibling(maxNode.getRightSibling());
				maxNode.setRightSibling(node);
				node.getRightSibling().setLeftSibling(node);
				
				if(node.getFreq() > maxNode.getFreq())
				{
					maxNode = node;
				}
			}
			
			//increment our nodes counter
			nodes++;
		}
		
		//removes the max node from the heap and determines the new max node, returns true if successful, false if unsuccessful
		public Node<T> removeMax()
		{
			Node<T> maxToBeRemoved = maxNode;
			
			if(isEmpty())
			{
				return maxToBeRemoved;
			}
			else
			{
				//we will need to add the children of the max node to the top level circular list
				//to do so, we need to know how many children the max node has and keep a pointer to its child
				Node<T> m = maxNode;					//make a copy of the max node to keep the necessary pointers when we remove the max later
				int nChildren = maxNode.getDegree();	
				Node<T> node = maxNode.getChild();
				Node<T> temp;							//temporary node that we will need to remove each of the max node's children 
				
				/*
				 * for each child of the max node we will need to
				 * 1. Remove it from the child list
				 * 2. Add it to the top level list of the heap
				 * 3. Give the node a null parent pointer since it is now at the top level
				 * 
				 */
				for(int i = 0; i < nChildren; i++)
				{
					temp = node.getRightSibling();
					
					//1. Remove it from the child list
					node.getLeftSibling().setRightSibling(node.getRightSibling());
					node.getRightSibling().setLeftSibling(node.getLeftSibling());
					
					//2. Add it to the top level list of the heap
					node.setRightSibling(maxNode.getRightSibling());
					node.getRightSibling().setLeftSibling(node);
					node.setLeftSibling(maxNode);
					maxNode.setRightSibling(node);
					
					//3. Give the node a null parent pointer
					node.setParent(null);
					
					//TODO
					//Maybe reset the childcut field though it may not matter since it is in a top level list, should be set when it becomes the child of something
					
					//change the node pointer for the next loop iteration
					node = temp;
					
				}
				
				//Now we need to find the new max node and we do so by traversing all of the nodes in the top level list.
				//We will reuse our temp node from before to do so
				
				temp = m.getRightSibling();
				Node<T> tempMax = temp;
				while(temp.getRightSibling() != maxNode)
				{
					if(tempMax.getFreq() < temp.getRightSibling().getFreq())
					{
						tempMax = temp.getRightSibling();
					}
					temp = temp.getRightSibling();
				}
				
				//By this point tempMax should be the new max node
				
				//By now we have added all of the max node's children to the top level list
				//Now we have to actually remove the max node from the top level list
				m.getLeftSibling().setRightSibling(m.getRightSibling());
				m.getRightSibling().setLeftSibling(m.getLeftSibling());
				
				maxNode = tempMax;
								
				//we've successfully removed the maxnode so let's decrease the node count before returning
				nodes--;
				combineTopLevelTrees();
				
				return maxToBeRemoved;
			}
		}
		
		//combines top level trees of the same degree after a remove
		private void combineTopLevelTrees() 
		{
			ArrayList<Node<T>> list = new ArrayList<Node<T>>(nodes);
			for(int i=0; i<nodes; i++)
			{
				list.add(null);
			}
			
			Node<T> temp = maxNode;
			Node<T> tempR = temp.getRightSibling();
			
			while(tempR != maxNode)
			{
				
				//if a node with a certain degree is not in the list add it
				if(list.get(temp.getDegree()) == null)
				{
					list.add(temp.getDegree(), temp);
				}
				//combine the two nodes with the same degree
				//add the combined node to the table if the degree does not already
				else
				{
					pairwiseCombine(list, temp);
				}
				
				temp = tempR;
				tempR = tempR.getRightSibling();
			}
		}
		
		/*
		 * This method is given the tree table (list) and a node in which the given node should be combined with a node
		 * in the list. After the combine finishes, the method recursviely goes through the list in case the new node
		 * can be combined with another node of the same degree in the list. The method completes when no more combines can be
		 * made with nodes that have been previously seen.
		 */
		private void pairwiseCombine(ArrayList<Node<T>> list, Node<T> temp)
		{
			if(temp == list.get(temp.getDegree()) || temp.getParent()!=null || list.get(temp.getDegree()).getParent()!=null)
				return;
			
			Node<T> t = combineNodes(temp,list.get(temp.getDegree()));
			
			list.set(temp.getDegree(), null);
			
			if(list.get(t.getDegree()) == null)
			{
				list.set(t.getDegree(), t);
			}
			else
			{
				pairwiseCombine(list,t);
			}
			
			
		}
		
		/*
		 * Combines the two given nodes with the node having the greater frequency as the parent.
		 * Whichever node becomes the parent node is returned.
		 */
		private Node<T> combineNodes(Node<T> one, Node<T> two) 
		{
			if(one.getFreq()>=two.getFreq())
			{
				Node<T> c = one.getChild();
				
				//remove two from the sibling list of one
				two.getLeftSibling().setRightSibling(two.getRightSibling());
				two.getRightSibling().setLeftSibling(two.getLeftSibling());
				
				//if one does not have a child
				if(c == null)
				{
					//add two to be its only child
					one.setChild(two);
					two.setLeftSibling(two);
					two.setRightSibling(two);
					
				}
				else
				{
					two.setLeftSibling(c);
					two.setRightSibling(c.getRightSibling());
					c.setRightSibling(two);
					two.getRightSibling().setLeftSibling(two);
				}
				
				//since one is the new parent of two, we need to set childCut false and update two's parent pointer
				two.setChildCut(false);
				two.setParent(one);
				
				//we will also need to update one's degree
				one.setDegree(one.getDegree()+1);
				
				if(one.getFreq()==two.getFreq()&&one.getFreq()==maxNode.getFreq())
				{
					maxNode = one;
				}
				
				return one;
			}
			else
			{
				Node<T> c = two.getChild();
				
				//remove one from the sibling list of two
				one.getLeftSibling().setRightSibling(one.getRightSibling());
				one.getRightSibling().setLeftSibling(one.getLeftSibling());
				
				//if two does not have a child
				if(c == null)
				{
					//add one to be its only child
					two.setChild(one);
					one.setLeftSibling(one);
					one.setRightSibling(one);
					
				}
				else
				{
					one.setLeftSibling(c);
					one.setRightSibling(c.getRightSibling());
					c.setRightSibling(one);
					one.getRightSibling().setLeftSibling(one);
				}
				
				one.setChildCut(false);
				one.setParent(two);
				
				two.setDegree(two.getDegree()+1);
				
				return two;
			}
			
		}
		
		//combine two top level circular lists, also was not used
		public MaxFibonacciHeap<T> meld(MaxFibonacciHeap<T> one, MaxFibonacciHeap<T> two)
		{	
			
			if(one.isEmpty()) return two;
			else if(two.isEmpty()) return one;
			
			Node<T> oneRight = one.getMaxNode().getRightSibling();
			one.getMaxNode().setRightSibling(two.getMaxNode().getRightSibling());
			two.getMaxNode().setRightSibling(oneRight);
			
			if(two.getMaxNode().getFreq() > one.getMaxNode().getFreq())
			{
				one.setMax(two.getMaxNode());
			}
			
			return one;
		}
		
		//TODO was not needed so was not implemented
		//removes a specified node from the heap, returns true if successful, false if unsuccessful
		public boolean remove(Node<T> node)
		{
			return false;
		}
		
		
		//increases the key of a specified node
		public void increaseKey(Node<T> node, int amount)
		{
			//if the node we are altering does not have a parent meaning it is in the top level list
			if(node.getParent()==null)
			{
				//we can simply add the amount given to the node's frequency
				node.setFreq(node.getFreq()+amount);
				
			}
			//if the node we are altering does have a parent
			else
			{
				//we must first check that increasing the frequency of the node does not make it exceed its parent's frequency
				if(node.getFreq()+amount > node.getParent().getFreq())
				{
					//since the increase will make the node greater than its parent, we must remove the node from the list of its parent's children and
					//add the node to the top level list
					
					//if the node we are trying to remove from its parent's child list is the child pointer we must change it
					if(node.getParent().getChild() == node)
					{
						//if the node is the only node in the parent's children list, we can just set the child null
						if(node.getRightSibling() == node)
						{
							node.getParent().setChild(null);
						}
						//otherwise we need to give the parent a different child pointer
						else
						{
							node.getParent().setChild(node.getRightSibling());
						}
					}
					
					//now we need to remove of node from its parent's child list
					node.getLeftSibling().setRightSibling(node.getRightSibling());
					node.getRightSibling().setLeftSibling(node.getLeftSibling());
					
					//decrease the degree of the parent
					node.getParent().setDegree(node.getParent().getDegree()-1);
					
					//add the node to the top level list
					node.setLeftSibling(maxNode);
					node.setRightSibling(maxNode.getRightSibling());
					maxNode.setRightSibling(node);
					node.getRightSibling().setLeftSibling(node);
					
					//give the node a null parent pointer
					Node<T> p = node.getParent();
					node.setParent(null);
					node.setFreq(node.getFreq()+amount);
					cascadingCut(p);
				}
				//if it doesn't exceed its parent, we can simply add the amount to the node
				else
				{
					
					node.setFreq(node.getFreq()+amount);
				}
			}
			//we may need to update the maxNode if this increase has created a new max
			if(node.getFreq()>maxNode.getFreq())
			{
				maxNode = node;
			}
			
		}
		
		/*
		 * When a node is cut out of its sibling list in an increase key operation, this method will follow a path
		 * from the removed node's parent to the root. Along the way, if a node is found to have had a child removed before,
		 * the node is also removed from its sibling list and inserted into the top-level list.
		 */
		private void cascadingCut(Node<T> node)
		{
			//if the node is a root we can stop
			if(node.getParent()==null )
				return;
			else
			{
				//if the node has not had a child removed from it before, we can stop here after updating child cut
				if(node.getChildCut()==false)
				{
					node.setChildCut(true);
				}
				//otherwise we need to remove the node from its sibling list and repeat the method with this node's parent
				else
				{
					//if the node doesn't have any siblings, we can just add it to the top level list thus skipping this step
					if(node.getRightSibling()!=node)
					{
						node.getLeftSibling().setRightSibling(node.getRightSibling());
						node.getRightSibling().setLeftSibling(node.getLeftSibling());
					}
					
					//if the node we are trying to remove from its parent's child list is the child pointer we must change it
					if(node.getParent().getChild() == node)
					{
						//if the node is the only node in the parent's children list, we can just set the child null
						if(node.getRightSibling() == node)
						{
							node.getParent().setChild(null);
						}
						//otherwise we need to give the parent a different child pointer
						else
						{
							node.getParent().setChild(node.getRightSibling());
						}
					}
					
					//add node to top level list
					node.setLeftSibling(maxNode);
					node.setRightSibling(maxNode.getRightSibling());
					maxNode.setRightSibling(node);
					node.getRightSibling().setLeftSibling(node);
					
					//update child cut
					node.setChildCut(false);
					
					//repeat with this node's parent until we hit a root
					cascadingCut(node.getParent());
				}
				
			}
			
		}
		
		//sets the max node to a different node, used from the meld operation
		public boolean setMax(Node<T> node) 
		{
			
			if(maxNode.getFreq() > node.getFreq())
			{
				return false;
			}
			else
			{
				maxNode = node;
			}
			
			return true;				
		}
	}
	
	static class Node<T>
	{
		private int degree = 0;			//number of children
		private Node<T> child;			//child node if it exists
		private T data;					//the data being stored
		private int freq;				//frequency of the node
		private Node<T> leftSibling;	//this node's left sibling if it exists
		private Node<T> rightSibling;	//this node's right sibling if it exists
		private Node<T> parent;			//this node's parent if it exists
		private boolean childCut;		//true if this node has lost a child since it became a child of its current parent
		
		public Node(T data,int freq)
		{
			this.data = data;
			this.freq = freq;
			leftSibling = this;
			rightSibling = this;
		}
		
		public int getDegree(){	return degree;	}
		public Node<T> getChild(){	return child;	}
		public T getData(){	return data;	}
		public int getFreq(){	return freq;	}
		public Node<T> getLeftSibling(){ return leftSibling;	}
		public Node<T> getRightSibling(){	return rightSibling;	}
		public Node<T> getParent(){ return parent;	}
		public boolean getChildCut(){	return childCut;	}
		
		public void setDegree(int degree){ this.degree = degree;	}
		public void setChild(Node<T> child){	this.child = child;	}
		public void setData(T data){	this.data = data;	}
		public void setFreq(int freq){	this.freq = freq;	}
		public void setLeftSibling(Node<T> leftSibling){	this.leftSibling = leftSibling;	}
		public void setRightSibling(Node<T> rightSibling){	this.rightSibling = rightSibling;	}
		public void setParent(Node<T> parent){	this.parent = parent;	}
		public void setChildCut(boolean childCut){	this.childCut = childCut;	}
		
		public String toString()
		{
			return "Data: " + data + ". Freq: " + freq + ".";
		}
	}

}


