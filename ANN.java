import java.util.ArrayList;
// import java.util.Math;

class ANN {

  private int numInputs;
  private int numOutputs;
  private int numHiddenNodes;
  private int numHiddenLayers;

  //array holds input values, biases of each hidden node and output values
  //first n entries are inputs, n = numInputs, last m outputs, m=numOutputs
  private ArrayList<Double> nodesValueList = new ArrayList<Double>();

  //3 dimensioal matrix of weights
  //wijk represents for layer i each weight from node j to node k.
  //layer 0 is input layer
  //output is final layer
  private ArrayList<ArrayList<ArrayList<Double>>> weightsList = new ArrayList<ArrayList<ArrayList<Double>>>();

  public static void main(String[] args) {
    new ANN(2,1,4,1);
  }

  //needs number of inpit and hidden nodes to construct
  public ANN(int numInputs, int numOutputs, int numHiddenNodes, int numHiddenLayers) {
    this.numInputs = numInputs;
    this.numOutputs = numOutputs;
    this.numHiddenNodes = numHiddenNodes;
    this.numHiddenLayers = numHiddenLayers;
    System.out.println("inputs: "+numInputs+"\noutputs: "+numOutputs+"\nhidden nodes: "+numHiddenNodes+"\nhidden layers: "+numHiddenLayers);
    //check valid number of hidden nodes given
    if (numHiddenNodes % numHiddenLayers != 0) {
      System.out.println("ERROR: Number of hidden nodes must be divisible by number of hidden layers");
    }
    makeNodesList();
    makeWeightsList();

    //test inputs
    ArrayList<Double> testInputs = new ArrayList<Double>();
    testInputs.add(4.3);
    testInputs.add(7.9);
    setInputs(testInputs);
    System.out.println("nodes list after inputs change: "+nodesValueList);

    // weightsList = {{}}
  }

  //fills nodesValueList with place holders for input/output nodes and
  //gives biases to hidden nodes with values between -2/n and 2/n. n=numInputs
  private void makeNodesList() {

    //0 entries for input values, these will be replaced for each pass
    for(int i=0;i<=numInputs-1;i++) {
      nodesValueList.add(0.0);
    }

    for(int i=numInputs;i<numHiddenNodes+numInputs;i++) {
      nodesValueList.add((-2/this.numInputs) + (Math.random() * (((2/this.numInputs) - (-2/this.numInputs)) + 1)));
    }

    //add output nodes
    for (int i=0;i<=numOutputs;i++) {
      nodesValueList.add(0.0);
    }

    System.out.println("init nodes list: "+nodesValueList);
  }

  //using nodesValueList, construct 3-d matrix of weights
  private void makeWeightsList() {
    int numHiddenNodesPerLayer = numHiddenNodes/numHiddenLayers;

    //for each layer
    for (int i=0;i<=numHiddenLayers;i++) {
      //matrix[i][j] of weights from node i to j
      ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
      //if input layer
      if(i==0) {
        //for each input
        for (int j=0;j<=numInputs-1;j++) {
          matrix.add(new ArrayList<Double>());
          //for each hidden node
          for (int k=0;k<=numHiddenNodesPerLayer-1;k++) {
            matrix.get(j).add(1.0);
          }
        }
        //add to full weights data structure
        System.out.println("input to hidden matrix = "+matrix);
        weightsList.add(matrix);

      //if hidden layer to output layer
    } else if (i==numHiddenLayers) {
        //for each hidden node to output weight
        for (int j=0;j<=numHiddenNodesPerLayer-1;j++) {
          matrix.add(new ArrayList<Double>());
          //for each output
          for (int k=0;k<=numOutputs-1;k++) {
            matrix.get(j).add(1.0);
          }
        }
        System.out.println("hidden to output matrix = "+matrix);
        weightsList.add(matrix);

      //if hidden node to hidden node
    } else {
      //from each node
      for (int j=0;j<=numHiddenNodesPerLayer-1;j++) {
        matrix.add(new ArrayList<Double>());
        //to each hidden node
        for (int k=0;k<=numHiddenNodesPerLayer-1;k++) {
          matrix.get(j).add(1.0);
        }
      }
      System.out.println("hidden to hidden matrix = "+matrix);
      weightsList.add(matrix);

    }
  }
  System.out.println("init weightsList structure = "+weightsList);
  }

  private void setNodeList(int node, double bias) {
    nodesValueList.set(node, bias);
  }



  //control methods

  public void setInputs(ArrayList<Double> inputs) {
    //check for correct list size
    if (inputs.size() != numInputs) {
      System.out.println("ERROR in setInputNodes(): incorrect inputs list size");
      return;
    }
    //set input values in nodesValueList
    for (int i=0;i<=numInputs-1;i++) {
      setNodeList(i, inputs.get(i));
    }
  }

  //forward pass through network
  public Double forwardPass() {
    //for each node that isnt input

    //calculate sum

    return 0.0;
  }

  public Double backwardPass(ArrayList<Double> weightedSums, ArrayList<Double> activations) {
    return 0.0;
}
}
