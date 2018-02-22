import java.util.ArrayList;
// import java.util.Math;

class ANN {

  private int numInputs;
  private int numOutputs;
  private int numHiddenNodes;
  private int numHiddenLayers;
  private int numHiddenNodesPerLayer;

  //each new input data must also set predictand variable
  private ArrayList<Double> predictands = new ArrayList<Double>();

  //array holds input values, biases of each hidden node, output values
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

  //needs number of init and hidden nodes to construct
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
    makePredictantList();

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

    // System.out.println("weights List: "+weightsList);
    // forwardPass();
    // System.out.println("\nforward pass new list: "+nodesValueList);
    forwardPass();
    backwardPass();

    // ArrayList<Double> testInputs = new ArrayList<Double>();
    // testInputs.add(5.0);
    // testInputs.add(6.0);
    // testInputs.add(9.0);
    // setInputs(testInputs);
    // System.out.println("Nodes List new inputs: "+nodesValueList);


  }

  //fills nodesValueList with place holders for input/output nodes and
  //gives biases to hidden nodes with values between -2/n and 2/n. n=numInputs
  private void makeNodesList() {

    //0 entries for input values(incl. predictand), these will be replaced for each pass
    for(int i=0;i<numInputs-1;i++) {
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

  private void makePredictantList() {
    for (int i=0;i<numOutputs;i++) {
      predictands.add(1.0);
    }
  }

  private void setNodeListValue(int node, double bias) {
    nodesValueList.set(node, bias);
  }



  //control methods
  //receives array of inputs with predictants at the end
  public void setInputs(ArrayList<Double> data) {
    //check for correct list size
    if (data.size() != numInputs+predictands.size()) {
      System.out.println("ERROR in setInputNodes(): incorrect inputs list size");
      return;
    }
    //set input values in nodesValueList
    for (int i=0;i<numInputs-numOutputs;i++) {
      setNodeListValue(i, data.get(i));
    }
    //set predictands arrays
    for (int i=numInputs;i<data.size();i++) {
      predictands.set(i-numInputs,data.get(i));
    }
    System.out.println("predictand: "+predictands);
  }

  public ArrayList<Double> getNodeValueList() {
    return this.nodesValueList;
  }

  public ArrayList<ArrayList<ArrayList<Double>>> getWeightList() {
    return this.weightsList;
  }


  //forward pass methods

  public Double forwardPass() {
    //for each node that isnt input, replace bias with uj value
    for (int i=0;i<nodesValueList.size()-numInputs;i++) {
      //set new bias value
      setNodeListValue(i+numInputs, ujFinder(i));
    }

    return 0.0;
  }

  //calculate sumValue=Sj for node j
  //Sj = SUMi(wij*uj)
  private Double ujFinder(int node) {
    //wijk represents for layer i each weight from node j to node k.

    //check for invalid node values
    if (node > numHiddenNodes+1) {
      System.out.println("ERROR: invalid node ID passed to sumSjujFinder.");
      System.exit(0);
    }

    //find which layer node is in
    int layer=1;
    for (int i=1;i<=numHiddenLayers+1;i++) {
      if (node < i*numHiddenNodesPerLayer) {
        layer = i;
        // System.out.println("\nnode "+node+" in layer "+layer);
        break;
      }
    }

    //find which number in layer node is
    // System.out.println("nodeNumInLayer: "+nodeNumInLayer);
    int nodeNumInLayer = node % numHiddenNodesPerLayer;

    Double Sj=0.0;
    Double uj=0.0;
    //Sj = SUMi(wij*uj)
    //if layer 1, do for each input
    if (layer==1) {
      //for each input
      for (int i=0;i<numInputs;i++) {
            double wij = weightsList.get(layer-1).get(i).get(nodeNumInLayer);
            double ui = nodesValueList.get(i);
            Sj = Sj + wij*ui;
      }
      //if hidden or output layer
    } else {
      //for each hidden layer node
      for (int i=0;i<numHiddenNodesPerLayer;i++) {
            double wij = weightsList.get(layer-1).get(i).get(nodeNumInLayer);
            double ui = nodesValueList.get(i+((layer-1)*numHiddenNodesPerLayer));
            Sj = Sj + wij*ui;
      }
    }
      Sj = Sj + nodesValueList.get(node+numInputs);
      System.out.println("Sj: "+Sj);
      uj = sigmoid(Sj);
      System.out.println("uj: "+uj);

    return uj;
  }

  private Double sigmoid(Double Sj) {
    return 1/(1+(Math.exp(-Sj)));
  }



  //backward pass methods

  //TODO abstract method to multiple hidden layer, multiple output
  public void backwardPass() {
    //for each node that isnt input go backwards from output calculating  delta vales

    //init empty array of size to fit delta values of every non-input node
    ArrayList<Double> delta_values = new ArrayList<Double>();
    for (int i=0;i<nodesValueList.size()-numInputs;i++) {
      delta_values.add(0.0);
    }

    System.out.println("nodesList: "+nodesValueList);
    //find delta values
    //deltaj = wjO*deltaO*(uj(1-uj)) for hidden cell
    //deltaj = (C-uO)*(uO(1-uO)), C=correct output for output cell
    for (int i=nodesValueList.size()-1;i>=numInputs;i--) {
      //if output
      if (i == nodesValueList.size()-1) {
        Double deltaO;
        Double uO = nodesValueList.get(i);
        deltaO = (predictands.get(0)-uO)*(uO*(1-uO));
        delta_values.set(delta_values.size()-1,deltaO);
        //if not output
      } else {
        Double wjO;
        wjO = (weightsList.get(1).get(i-numHiddenNodesPerLayer).get(0));
        Double uj;
        uj = nodesValueList.get(i);
        Double deltaj;
        deltaj = wjO*delta_values.get(delta_values.size()-1)*(uj*(1-uj));
        delta_values.set(i-numInputs,deltaj);
      }
    }
    System.out.println("delta values: "+delta_values);
}
}
