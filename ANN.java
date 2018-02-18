import java.util.ArrayList;
// import java.util.Math;

class ANN {

  private int numInputs;
  private int numOutputs;
  private int numHiddenNodes;
  private int numHiddenLayers;
  private int numHiddenNodesPerLayer;

  //array holds input values, biases of each hidden node and output values
  //first n entries are inputs, n = numInputs, last m outputs, m=numOutputs
  private ArrayList<Double> nodesValueList = new ArrayList<Double>();

  //3 dimensioal matrix of weights
  //wijk represents for layer i each weight from node j to node k.
  //layer 0 is input layer
  //output is final layer
  private ArrayList<ArrayList<ArrayList<Double>>> weightsList = new ArrayList<ArrayList<ArrayList<Double>>>();

  public static void main(String[] args) {
    new ANN(2,1,2,1);
  }



  //init methods

  //needs number of inpit and hidden nodes to construct
  public ANN(int numInputs, int numOutputs, int numHiddenNodes, int numHiddenLayers) {
    this.numInputs = numInputs;
    this.numOutputs = numOutputs;
    this.numHiddenNodes = numHiddenNodes;
    this.numHiddenLayers = numHiddenLayers;
    this.numHiddenNodesPerLayer = numHiddenNodes/numHiddenLayers;
    System.out.println("\ninit info: \n");
    System.out.println("inputs: "+numInputs+"\noutputs: "+numOutputs+"\nhidden nodes: "+numHiddenNodes+"\nhidden layers: "+numHiddenLayers+"\n");
    //check valid number of hidden nodes given
    if (numHiddenNodes % numHiddenLayers != 0) {
      System.out.println("ERROR: Number of hidden nodes must be divisible by number of hidden layers");
    }
    // makeNodesList();
    makeWeightsList();

    //test example values
    nodesValueList.add(1.0);
    nodesValueList.add(0.0);
    nodesValueList.add(1.0);
    nodesValueList.add(-6.0);
    nodesValueList.add(-3.92);
    System.out.println("Nodes List: "+nodesValueList);

    //wijk represents for layer i each weight from node j to node k.
    System.out.println("init Weights List: "+weightsList);
    weightsList.get(0).get(0).set(0,3.0);
    weightsList.get(0).get(0).set(1,6.0);
    weightsList.get(0).get(1).set(0,4.0);
    weightsList.get(0).get(1).set(1,5.0);
    weightsList.get(1).get(0).set(0,2.0);
    weightsList.get(1).get(1).set(0,4.0);

    System.out.println("weights List: "+weightsList);

    System.out.println("\nsumSj for node 0: "+sumSj(2));

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
    for (int i=0;i<=numOutputs-1;i++) {
      nodesValueList.add(0.0);
    }

    System.out.println("init nodes list: "+nodesValueList);
  }

  //using nodesValueList, construct 3-d matrix of weights
  private void makeWeightsList() {

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
        System.out.println("input to hidden matrix: "+matrix);
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
        System.out.println("hidden to output matrix: "+matrix);
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
      System.out.println("hidden to hidden matrix: "+matrix);
      weightsList.add(matrix);
      }
    }
    System.out.println("init weightsList structure: "+weightsList+"\n");
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



  //forward pass methods

  public Double forwardPass() {
    //array of sum values for non-input nodes
    //sumValues index 0 is beginning of NON-INPUT nodes, final index is outputs
    ArrayList<Double> sumValues = new ArrayList<Double>();

    //for each node that isnt input
    for (int i=numInputs;i<=nodesValueList.size()-numInputs;i++) {
      sumValues.add(sumSj(i-numInputs));
    }
    //calculate sum

    return 0.0;
  }

  //calculate sumValue=Sj for node j
  //Sj = SUMi(wij*uj)
  //TODO abstract method for >1 hidden layer and >1 output
  private Double sumSj(int node) {
    //wijk represents for layer i each weight from node j to node k.

    //check for invalid node values
    if (node > numHiddenNodes+1) {
      System.out.println("ERROR: invalide node ID passed to sumSj.");
      System.exit(0);
    }

    //find which layer node is in
    int layer=1;
    for (int i=1;i<=numHiddenLayers+1;i++) {
      if (node < i*numHiddenNodesPerLayer) {
        layer = i;
        System.out.println("\nnode "+node+" in layer "+layer);
        break;
      }
    }
    //find which number in layer node is
    int nodeNumInLayer = node % numHiddenNodesPerLayer;
    System.out.println("nodeNumInLayer: "+nodeNumInLayer);

    Double Sj;
    //Sj = SUMi(wij*uj)
    //if layer 1, do for each input
    if (layer==1) {
      //for each input
      for (int i=0;i<numInputs;i++) {
            double wij = weightsList.get(layer-1).get(i).get(nodeNumInLayer);
            System.out.println("wij: "+wij);
            double uj = nodesValueList.get(i);
            System.out.println("uj: "+uj);
            // System.out.println("Sj value for i="+i+" is ="+Sj);

      }
    }

    //TODO if hidden layer to hidden layer

    //if hidden layer to output
    if (layer==numHiddenLayers+1) {
      //for each hidden layer node
      for (int i=0;i<numHiddenNodesPerLayer;i++) {
            double wij = weightsList.get(layer-1).get(i).get(nodeNumInLayer);
            System.out.println("wij: "+wij);
            double uj = nodesValueList.get(i+((layer-1)*numHiddenNodesPerLayer));
            System.out.println("uj: "+uj);
            // System.out.println("Sj value for i="+i+" is ="+Sj);

      }
    }

    //if hidden layer to hidden layer

    //if hidden layer to output


    // if (node)
    // weightsList.get()
    return 0.0;
  }

  public Double backwardPass(ArrayList<Double> weightedSums, ArrayList<Double> activations) {
    return 0.0;
}
}
