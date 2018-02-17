import java.util.ArrayList;
// import java.util.Math;

class ANN {

  int numInputs;
  int numOutputs;
  int numHiddenNodes;
  int numHiddenLayers;

  ArrayList<Double> inputValuesList = new ArrayList<Double>();
  //array holds biases of each hidden node
  //first n entries are 0, n = numInputs
  ArrayList<Double> nodesBiasList = new ArrayList<Double>();
  //3 dimensioal matrix of weights
  //wijk represents for layer i each weight from node j to node k.
  //layer 0 is input layer
  //output is final layer
  ArrayList<ArrayList<ArrayList<Double>>> weightsList = new ArrayList<ArrayList<ArrayList<Double>>>();

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
    // weightsList = {{}}
  }

  //fills nodesBiasList with place holders for input/output nodes and
  //gives biases to hidden nodes with values between -2/n and 2/n. n=numInputs
  private void makeNodesList() {

    //0 entries for input values, these will be replaced for each pass
    for(int i=0;i<=numInputs-1;i++) {
      nodesBiasList.add(0.0);
    }

    for(int i=numInputs;i<numHiddenNodes+numInputs;i++) {
      nodesBiasList.add((-2/this.numInputs) + (Math.random() * (((2/this.numInputs) - (-2/this.numInputs)) + 1)));
    }

    //add output node
    nodesBiasList.add(0.0);

    System.out.println("init nodes list: "+nodesBiasList);
  }

  //using nodesBiasList, construct 3-d matrix of weights
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
        System.out.println("each input matrix = "+matrix);
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
    System.out.println("weights = "+weightsList);

  }



    //for each node on layer + 1

  }
  //forward pass through network returns
  public Double forwardPass(ArrayList<Double> input) {
    return 0.0;
  }

  public Double backwardPass(ArrayList<Double> weightedSums, ArrayList<Double> activations) {
    return 0.0;
}
}
