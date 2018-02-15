import java.util.ArrayList;
// import java.util.Math;

class ANN {

  int numInputs;
  int numHiddenNodes;
  int numLayers;

  ArrayList<Double> inputValuesList = new ArrayList<Double>();
  //array holds biases of each hidden node
  //first n entries are 0, n = numInputs
  ArrayList<Double> nodesBiasList = new ArrayList<Double>();
  //3 dimensioal matrix of weights
  //wijk represents weight from node i to node j. node i is in layer k
  //layer 0 is input layer
  //output is final layer
  ArrayList<Double> weightsList = new ArrayList<Double>();

  public static void main(String[] args) {
    new ANN(2,4,1);
  }

  //needs number of inpit and hidden nodes to construct
  public ANN(int numInputs, int numHiddenNodes, int numLayers) {
    this.numInputs = numInputs;
    this.numHiddenNodes = numHiddenNodes;
    this.numLayers = numLayers;
    makeNodesList();
    makeWeightsList();
    // weightsList = {{}}
  }

  //fills nodesBiasList with place holders for input/output nodes and
  //gives biases to hidden nodes with values between -2/n and 2/n. n=numInputs
  private void makeNodesList() {
    System.out.println("num inputs: "+numInputs+" num hidden: "+numHiddenNodes);

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
    //for each layer

    //for each node on layer

    //for each node on layer + 1

  }


}
