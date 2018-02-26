import java.util.ArrayList;
// import java.util.Math;

class ANN {

  private int num_inputs;
  private int num_outputs;
  private int num_hidden_nodes;
  private int num_hidden_layers;
  private int num_hidden_nodes_per_layer;
  private Double step_size;

  //each new input data must also set predictand variable
  private ArrayList<Double> predictands = new ArrayList<Double>();

  //array holds input values, biases of each hidden node, output values
  //first n entries are inputs, n = num_inputs, last m outputs, m=num_outputs
  private ArrayList<Double> nodes_value_List = new ArrayList<Double>();

  //array of uj values calculated in forward pass
  private ArrayList<Double> bias_values = new ArrayList<Double>();

  //3 dimensioal matrix of weights
  //wijk represents for layer i each weight from node j to node k.
  //layer 0 is input layer
  //output is final layer
  private ArrayList<ArrayList<ArrayList<Double>>> weights_list = new ArrayList<ArrayList<ArrayList<Double>>>();

  public static void main(String[] args) {
    new ANN(2,1,2,1, 0.1);
  }



  //init methods

  //needs number of init and hidden nodes to construct
  public ANN(int num_inputs, int num_outputs, int num_hidden_nodes, int num_hidden_layers, Double step_size) {
    this.num_inputs = num_inputs;
    this.num_outputs = num_outputs;
    this.num_hidden_nodes = num_hidden_nodes;
    this.num_hidden_layers = num_hidden_layers;
    this.num_hidden_nodes_per_layer = num_hidden_nodes/num_hidden_layers;
    this.step_size = step_size;
    System.out.println("\ninit info: \n");
    System.out.println("inputs: "+num_inputs+"\noutputs: "+num_outputs+"\nhidden nodes: "+num_hidden_nodes+"\nhidden layers: "+num_hidden_layers+"\n");
    //check valid number of hidden nodes given
    if (num_hidden_nodes % num_hidden_layers != 0) {
      System.out.println("ERROR: Number of hidden nodes must be divisible by number of hidden layers");
    }
    // makeNodesList();

    //test example values
    nodes_value_List.add(1.0);
    nodes_value_List.add(0.0);
    nodes_value_List.add(1.0);
    nodes_value_List.add(-6.0);
    nodes_value_List.add(-3.92);
    System.out.println("Nodes List: "+nodes_value_List);

    makeWeightsList();
    makePredictantList();
    makeBiasList();

    //wijk represents for layer i each weight from node j to node k.
    System.out.println("init Weights List: "+weights_list);
    weights_list.get(0).get(0).set(0,3.0);
    weights_list.get(0).get(0).set(1,6.0);
    weights_list.get(0).get(1).set(0,4.0);
    weights_list.get(0).get(1).set(1,5.0);
    weights_list.get(1).get(0).set(0,2.0);
    weights_list.get(1).get(1).set(0,4.0);
    System.out.println("Weights List test values: "+weights_list);



    // System.out.println("weights List: "+weights_list);
    // forwardPass();
    // System.out.println("\nforward pass new list: "+nodes_value_List);

    System.out.println("\nforward pass.");
    System.out.println("nodes list: "+nodes_value_List);
    System.out.println("weights list: "+weights_list);
    forwardPass();
    System.out.println("bias list: "+bias_values);
    System.out.println("\nbackward pass.");
    backwardPass();
    System.out.println("\nbackward pass.");
    System.out.println("weights list: "+weights_list);
    System.out.println("nodes list: "+nodes_value_List);
    System.out.println("bias list: "+bias_values);

    System.out.println("\nReady for next epoch");
    System.out.println("weights list: "+weights_list);
    System.out.println("nodes list: "+nodes_value_List);
    System.out.println("bias list: "+bias_values);

    System.out.println("\nforward pass 2: ");
    forwardPass();
    System.out.println("weights list: "+weights_list);
    System.out.println("nodes list: "+nodes_value_List);
    System.out.println("bias list: "+bias_values);

    System.out.println("\nbackward pass 2: ");
    backwardPass();
    System.out.println("weights list: "+weights_list);
    System.out.println("nodes list: "+nodes_value_List);
    System.out.println("bias list: "+bias_values);

    // ArrayList<Double> testInputs = new ArrayList<Double>();
    // testInputs.add(5.0);
    // testInputs.add(6.0);
    // testInputs.add(9.0);
    // setInputs(testInputs);
    // System.out.println("Nodes List new inputs: "+nodes_value_List);


  }

  //fills nodes_value_List with place holders for input/output nodes and
  //gives biases to hidden nodes with values between -2/n and 2/n. n=num_inputs
  private void makeNodesList() {

    //0 entries for input values(incl. predictand), these will be replaced for each pass
    for(int i=0;i<num_inputs-1;i++) {
      nodes_value_List.add(0.0);
    }

    for(int i=num_inputs;i<num_hidden_nodes+num_inputs;i++) {
      nodes_value_List.add((-2/this.num_inputs) + (Math.random() * (((2/this.num_inputs) - (-2/this.num_inputs)) + 1)));
    }

    //add output nodes
    for (int i=0;i<=num_outputs-1;i++) {
      nodes_value_List.add(0.0);
    }

    System.out.println("init nodes list: "+nodes_value_List);
  }

  //using nodes_value_List, construct 3-d matrix of weights
  private void makeWeightsList() {

    //for each layer
    for (int i=0;i<=num_hidden_layers;i++) {
      //matrix[i][j] of weights from node i to j
      ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
      //if input layer
      if(i==0) {
        //for each input
        for (int j=0;j<=num_inputs-1;j++) {
          matrix.add(new ArrayList<Double>());
          //for each hidden node
          for (int k=0;k<=num_hidden_nodes_per_layer-1;k++) {
            matrix.get(j).add(1.0);
          }
        }
        //add to full weights data structure
        weights_list.add(matrix);

      //if hidden layer to output layer
    } else if (i==num_hidden_layers) {
        //for each hidden node to output weight
        for (int j=0;j<=num_hidden_nodes_per_layer-1;j++) {
          matrix.add(new ArrayList<Double>());
          //for each output
          for (int k=0;k<=num_outputs-1;k++) {
            matrix.get(j).add(1.0);
          }
        }
        weights_list.add(matrix);

      //if hidden node to hidden node
      } else {
      //from each node
        for (int j=0;j<=num_hidden_nodes_per_layer-1;j++) {
          matrix.add(new ArrayList<Double>());
          //to each hidden node
          for (int k=0;k<=num_hidden_nodes_per_layer-1;k++) {
            matrix.get(j).add(1.0);
          }
        }
      weights_list.add(matrix);
      }
    }
  }

  private void makePredictantList() {
    for (int i=0;i<num_outputs;i++) {
      predictands.add(1.0);
    }
  }

  private void makeBiasList() {
    //list of non-input nodes new bias'
    for (int i=0;i<nodes_value_List.size()-num_inputs;i++) {
      bias_values.add(0.0);
    }
  }

  private void setNodeListValue(int node, double bias) {
    nodes_value_List.set(node, bias);
  }

  private void setWeightsListValue(int layer, int node_from, int node_to, Double value) {
    weights_list.get(layer).get(node_from).set(node_to,value);
  }

  private void setBiasListValue(int node, double value) {
    bias_values.set(node, value);
  }



  //control methods
  //receives array of inputs with predictants at the end
  public void setInputs(ArrayList<Double> data) {
    //check for correct list size
    if (data.size() != num_inputs+predictands.size()) {
      System.out.println("ERROR in setInputNodes(): incorrect inputs list size");
      return;
    }
    //set input values in nodes_value_List
    for (int i=0;i<num_inputs-num_outputs;i++) {
      setNodeListValue(i, data.get(i));
    }
    //set predictands arrays
    for (int i=num_inputs;i<data.size();i++) {
      predictands.set(i-num_inputs,data.get(i));
    }
    System.out.println("predictand: "+predictands);
  }

  public ArrayList<Double> getNodeValueList() {
    return this.nodes_value_List;
  }

  public ArrayList<ArrayList<ArrayList<Double>>> getWeightList() {
    return this.weights_list;
  }


  //forward pass methods

  public void forwardPass() {

    //set bias_values to current bias values
    for (int i=num_inputs;i<nodes_value_List.size();i++) {
      bias_values.set(i-num_inputs, nodes_value_List.get(i));
    }
    System.out.println("bias list: "+bias_values+"\n");


    //for each node that isnt input, store uj value
    for (int i=0;i<bias_values.size();i++) {
      //set new value
      setBiasListValue(i, ujFinder(i));
    }
  }

  //calculate sumValue=Sj for node j
  //Sj = SUMi(wij*uj)
  private Double ujFinder(int node) {
    //wijk represents for layer i each weight from node j to node k.

    //check for invalid node values
    if (node > num_hidden_nodes+1) {
      System.out.println("ERROR: invalid node ID passed to sumSjujFinder.");
      System.exit(0);
    }

    //find which layer node is in
    int layer=1;
    for (int i=1;i<=num_hidden_layers+1;i++) {
      if (node < i*num_hidden_nodes_per_layer) {
        layer = i;
        // System.out.println("\nnode "+node+" in layer "+layer);
        break;
      }
    }
    System.out.println("layer: "+layer);


    //find which number in layer node is
    // System.out.println("nodeNumInLayer: "+nodeNumInLayer);
    int nodeNumInLayer = node % num_hidden_nodes_per_layer;

    Double Sj=0.0;
    Double uj=0.0;
    //Sj = SUMi(wij*uj)
    //if layer 1, do for each input
    if (layer==1) {
      //for each input
      for (int i=0;i<num_inputs;i++) {
            double wij = weights_list.get(layer-1).get(i).get(nodeNumInLayer);
            double ui = nodes_value_List.get(i);
            Sj = Sj + wij*ui;
            System.out.println("wij: "+wij+" ui:"+ui);
            System.out.println("Sj: "+Sj);

      }
      //if hidden or output layer
    } else {
      //for each hidden layer node
      System.out.println("output layer");
      System.out.println("uj list: "+bias_values);
      for (int i=0;i<num_hidden_nodes_per_layer;i++) {
            double wij = weights_list.get(layer-1).get(i).get(nodeNumInLayer);
            double ui = bias_values.get(i+((layer-2)*num_hidden_nodes_per_layer));
            Sj = Sj + wij*ui;
            System.out.println("wij: "+wij+" ui:"+ui);
            System.out.println("Sj: "+Sj);

      }
    }
      Sj = Sj + bias_values.get(node);
      System.out.println("bias: "+nodes_value_List.get(node+num_inputs));
      uj = sigmoid(Sj);
      System.out.println("total: "+uj+"\n");

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
    for (int i=0;i<nodes_value_List.size()-num_inputs;i++) {
      delta_values.add(0.0);
    }

    //find delta values
    //deltaj = wjO*deltaO*(uj(1-uj)) for hidden cell
    //deltaj = (C-uO)*(uO(1-uO)), C=correct output for output cell
    for (int i=nodes_value_List.size()-1;i>=num_inputs;i--) {
      //if output
      if (i == nodes_value_List.size()-1) {
        Double deltaO;
        Double uO = bias_values.get(i-num_inputs);
        deltaO = (predictands.get(0)-uO)*(uO*(1-uO));
        delta_values.set(delta_values.size()-1,deltaO);
        //if not output
      } else {
        Double wjO;
        wjO = (weights_list.get(1).get(i-num_hidden_nodes_per_layer).get(0));
        Double uj;
        uj = bias_values.get(i-num_inputs);
        Double deltaj;
        deltaj = wjO*delta_values.get(delta_values.size()-1)*(uj*(1-uj));
        delta_values.set(i-num_inputs,deltaj);
      }
    }
    System.out.println("delta values: "+delta_values);

    //Now, for each weight calculate new weights
    //wij = wij+step_size*deltaj*ui
    //for each layer
    for (int i=0;i<weights_list.size();i++) {
      //for each node
      for (int j=0;j<weights_list.get(i).size();j++) {
        //for each weight
        for (int k=0;k<weights_list.get(i).get(j).size();k++) {
          Double wij = weights_list.get(i).get(j).get(k);
          Double deltaj = delta_values.get((i)*num_hidden_nodes_per_layer + k);
          Double ui;
          //find ui
          //if input layer
          if (i==0) {
            ui = nodes_value_List.get(j);
          }
          //else ui is in hidden layer
          else {
            ui = bias_values.get(j+(i-1)*num_hidden_nodes_per_layer);
          }

          Double wij_new = wij + step_size * deltaj * ui;
          System.out.println("wij: "+wij+" deltaj: "+deltaj+" ui: "+ui);

          System.out.println("new wij: "+wij_new);
          //set new weight
          setWeightsListValue(i,j,k,wij_new);
        }
      }
    }

    //for each bias, calculate new bias values
    // wij = wij+step_size*deltaj*ui
    //ui for bias' is alwasy 1
    for (int i=0;i<bias_values.size();i++) {
      System.out.println("wij: "+nodes_value_List.get(i+num_inputs)+" deltaj: "+delta_values.get(i));
      bias_values.set(i, nodes_value_List.get(i+num_inputs)+step_size*delta_values.get(i));
      System.out.println("new bias: "+bias_values.get(i));
    }

    //set nodes_value_List to new bias values
    for (int i=num_inputs;i<nodes_value_List.size();i++) {
      nodes_value_List.set(i,bias_values.get(i-num_inputs));
    }
  }

}
