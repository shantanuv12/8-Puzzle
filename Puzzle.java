/*
        We redefined the 8 puzzle game with a few modifications, i.e assigning weights(cost) to each every move. I used dijkstra's algorithm
        to find out the minimum cost path to reach goal state. The algorithm is very simple and modified version of dijksta's algorithm
        What I did was to build a graph from start state via appyling the dijksta's algorithm in each step. That is if the cost is minimum
        at each step then the final cost which is sum of cost of each step,will be minimum.
        Created my own PriorityQueue for customised and fast implementation of the dijksta's algorithm
        And used HashMap, HashSets, LinkedHashSet to find Possible moves for next steps.So,that computation time is significantly lowered and
        we do not go backwards while finding further Possible steps from the node you are currently present on.
*/

import java.util.*;
import java.io.*;

public class Puzzle {
  public static LinkedHashSet<String> list;
  public static HashSet<String> CLOSED;
  public static HashMap<String, Node> graph;
  public static boolean done;
  public static int[] d;

  public static void main(String args[]) throws IOException {
    long st = System.currentTimeMillis();                           //taking inputs from the input file
    File in = new File(args[0]);
    File out = new File(args[1]);
    BufferedReader br = new BufferedReader(new FileReader(in));
    BufferedWriter wr = new BufferedWriter(new FileWriter(out));
    int num = Integer.parseInt(br.readLine());
    String startstate = "";
    String goalstate = "";
    char[] s1;
    char[] s2;
    for (int i = 0; i < num; i++) {
      String[] str = br.readLine().split(" ");                      //Declaring start and end state for each test case
      startstate = str[0];
      goalstate = str[1];
      s1 = startstate.toCharArray();
      s2 = goalstate.toCharArray();
      for (int a = 0; a < s1.length; a++) {                         //Replacing G with 0 for easy computation
        if (s1[a] == 'G') {
          s1[a] = '0';
          break;
        }
      }
      for (int b = 0; b < s2.length; b++) {
        if (s2[b] == 'G') {
          s2[b] = '0';
          break;
        }
      }
      String start = new String(s1);
      String goal = new String(s2);
      String[] costfn = br.readLine().split(" ");
      d = new int[costfn.length];
      for (int j = 0; j < costfn.length; j++) {
        d[j] = Integer.parseInt(costfn[j]);
      }
      if (solvable(s1) && solvable(s2)) {                       // Check if the the goal state is reachable or not
        graph(start, goal);                                     // Proceed only if puzzle is solvable
        dijkstra(start, goal);
        int distance = graph.get(goal).dist;                    // Calculate distance, moves and the path to how you reached final state
        int moves = graph.get(goal).moves;
        String path = graph.get(goal).path;
        wr.write(moves + " " + distance + "\n");                //Write all dist, moves, path in output file
        wr.write(path + "\n");
      } else {
        wr.write("-1 -1");
        wr.write("\n");
      }
      start = "";
      goal = "";
    }
    br.close();
    wr.flush();
    wr.close();
    long en = System.currentTimeMillis();
    long to = en - st;
    System.out.println("Time :" + to + " millis");
  }

  public static boolean solvable(char[] arr) {              //Function to check if the puzzle is solvable by Calculating its parity
    boolean solvable = false;                               // solvable only if parity is even
    int parity = 0;
    for (int i = 0; i < arr.length; i++) {
      for (int j = i + 1; j < arr.length; j++) {
        if (arr[i] != '0' && arr[j] != '0' && arr[i] < arr[j]) {
          parity++;
        }
      }
    }
    if (parity % 2 == 0)
      solvable = true;
    return solvable;
  }

  static class Node {
    String done;                                   // Create node that contains data of whether it is visited or not how you reached from
    boolean visited;                               // the start state and the cost to reach that state
    int moves;                                      //Also a list of all the neighbours is present
    String path;
    int dist;
    List<Edge> edges = new Vector<>();

    Node(String done) {
      this.done = done;
      visited = false;
      dist = 0;
  }                                               // Various Possible constructors for creating object of node class

    Node() {
      this.done = "";
      visited = false;
      dist = 0;
    }

    Node(String done, int dist) {
      this.done = done;
      this.dist = dist;
      this.visited = false;
    }

    void addEdge(String done, String move, int weight) {
      Edge e = new Edge(done, move, weight);
      edges.add(e);                                   // Function to add an edge to the Node
    }
  }

  static class Edge {                               //Representing edges as a separate class \
    String done;
    String move;                                        //Every edge contains the weight assigned to it
    int weight;

    Edge(String done, String move, int weight) {
      this.done = done;
      this.move = move;
      this.weight = weight;
    }
  }

  static class PriorityQueue {                      // PriorityQueue used for faster implementation of dijkstra's algorithm
    Vector<Node> A;

    PriorityQueue() {
      A = new Vector<>();
    }

    PriorityQueue(int capacity) {
      A = new Vector<>(capacity);
    }

    int parent(int i) {
      if (i == 0)
        return 0;
      int j = (i - 1) / 2;
      return j;
    }

    int leftChild(int i) {
      return (2 * i + 1);
    }

    int rightChild(int i) {
      return (2 * i + 2);
    }

    void swap(int x, int y) {
      Node temp = A.get(x);
      temp.done = A.get(x).done;
      temp.dist = A.get(x).dist;
      temp.visited = A.get(x).visited;
      temp.moves = A.get(x).moves;
      temp.path = A.get(x).path;
      A.setElementAt(A.get(y), x);
      A.setElementAt(temp, y);
    }

    void heapify_down(int i) {
      int left = leftChild(i);
      int right = rightChild(i);
      int smallest = i;
      if (left < size() && A.get(left).dist < A.get(i).dist)
        smallest = left;
      if (right < size() && A.get(right).dist < A.get(smallest).dist)
        smallest = right;
      if (smallest != i) {
        swap(i, smallest);
        heapify_down(smallest);
      }
    }

    void heapify_up(int i) {
      if (i > 0 && A.get(parent(i)).dist > A.get(i).dist) {
        swap(i, parent(i));
        heapify_up(parent(i));
      }
    }

    int size() {
      return A.size();
    }

    Boolean isEmpty() {
      return A.isEmpty();
    }

    void add(Node v, int dist) {
      Node key = new Node(v.done, v.dist);
      key.path = v.path;
      key.moves = v.moves;
      A.addElement(key);
      int index = size() - 1;
      heapify_up(index);
    }

    Node remove() {
      Node root = A.get(0);
      A.setElementAt(A.lastElement(), 0);
      A.remove(size() - 1);
      heapify_down(0);
      return root;
    }
  }

  public static void graph(String st, String en) {       //Representing the whole puzzle as a graph which we build from the final state
    list = new LinkedHashSet<String>();                  // And build till the goal state containing all the information of path and cost
    CLOSED = new HashSet<String>();                      // to reach every node
    graph = new HashMap<String, Node>();
    done = false;
    String X = "";
    String temp = "";
    Node start = new Node(st);
    Node end = new Node(en);
    //graph.put(en, new Node(en));
    list.add(start.done);
    while (list.isEmpty() == false && done == false) {
      X = list.iterator().next();
      Node vert = new Node(X);
      list.remove(X);
      int pos = X.indexOf('0');
      if (X.equals(end.done)) {
        graph.put(X, vert);
        done = true;
      }
      else {
        CLOSED.add(X);
        temp = up(X, pos);
        if (!(temp.equals("-1"))) {
          list.add(temp);
          String mv = X.substring(pos - 3, pos - 2);
          int i = Integer.parseInt(mv);
          vert.addEdge(temp, mv + "D", d[i - 1]);
        }
        temp = left(X, pos);
        if (!(temp.equals("-1"))) {
          list.add(temp);
          String mv = X.substring(pos - 1, pos);
          int i = Integer.parseInt(mv);
          vert.addEdge(temp, mv + "R", d[i - 1]);
        }
        temp = down(X, pos);
        if (!(temp.equals("-1"))) {
          list.add(temp);
          String mv = X.substring(pos + 3, pos + 4);
          int i = Integer.parseInt(mv);
          vert.addEdge(temp, mv + "U", d[i - 1]);
        }
        temp = right(X, pos);
        if (!(temp.equals("-1"))) {
          list.add(temp);
          String mv = X.substring(pos + 1, pos + 2);
          int i = Integer.parseInt(mv);
          vert.addEdge(temp, mv + "L", d[i - 1]);
        }
        graph.put(X, vert);
      }
    }
    while (list.isEmpty() == false) {
      X = list.iterator().next();
      list.remove(X);
      Node ver = new Node(X);
      graph.put(X, ver);
    }
  }

  public static void dijkstra(String start, String end) {                   // Applying dijksta to find minimum distance/cost to reach final state
    for (String keys : graph.keySet()) {
      graph.get(keys).visited = false;
      graph.get(keys).dist = Integer.MAX_VALUE;
    }
    PriorityQueue pq = new PriorityQueue();
    graph.get(start).dist = 0;
    graph.get(start).path = "";
    graph.get(start).moves = 0;
    pq.add(graph.get(start), 0);
    while (pq.isEmpty() == false) {
      Node node = pq.remove();
      if (node.done.equals(end)) {
        break;
      }
      if (node.visited) {
        continue;
      }
      node.visited = true;
      for (Edge edge : graph.get(node.done).edges) {
        int weight = edge.weight;
        Node v = graph.get(edge.done);
        if (node.dist + weight < v.dist) {
          v.dist = node.dist + weight;
          v.moves = node.moves + 1;
          v.path = node.path + edge.move + " ";
          pq.add(v, v.dist);
        }
      }
    }
  }

  public static String up(String s, int p) {
    String str = s;                                      // Function to print the move performed, Used to print if we moved up
    if (!(p < 3)) {
      char a = str.charAt(p - 3);
      String newS = str.substring(0, p) + a + str.substring(p + 1);
      str = newS.substring(0, (p - 3)) + '0' + newS.substring(p - 2);
    }
    if (!list.contains(str) && CLOSED.contains(str) == false)
      return str;
    else
      return "-1";
  }

  public static String down(String s, int p) {          //Similarly for down
    String str = s;
    if (!(p > 5)) {
      char a = str.charAt(p + 3);
      String newS = str.substring(0, p) + a + str.substring(p + 1);
      str = newS.substring(0, (p + 3)) + '0' + newS.substring(p + 4);
    }
    if (!list.contains(str) && CLOSED.contains(str) == false)
      return str;
    else
      return "-1";
  }

  public static String left(String s, int p) {          //for left
    String str = s;
    if (p != 0 && p != 3 && p != 7) {
      char a = str.charAt(p - 1);
      String newS = str.substring(0, p) + a + str.substring(p + 1);
      str = newS.substring(0, (p - 1)) + '0' + newS.substring(p);
    }
    if (!list.contains(str) && CLOSED.contains(str) == false)
      return str;
    else
      return "-1";
  }

  public static String right(String s, int p) {
    String str = s;
    if (p != 2 && p != 5 && p != 8) {                   //for right
      char a = str.charAt(p + 1);
      String newS = str.substring(0, p) + a + str.substring(p + 1);
      str = newS.substring(0, (p + 1)) + '0' + newS.substring(p + 2);
    }
    if (!list.contains(str) && CLOSED.contains(str) == false)
      return str;
    else
      return "-1";
  }
}
