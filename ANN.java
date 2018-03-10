import java.util.ArrayList;

class ANN {

  private int num_inputs;
  private int num_outputs;
  private int num_hidden_nodes;
  private int num_hidden_layers;
  private int num_hidden_nodes_per_layer;
  private Double step_size;
  private Double alpha = 0.9;

  //each new input data must also set predictand variable
  private ArrayList<Double> predictand_list = new ArrayList<Double>();

  //array holds input values, biases of each hidden node, output values
  //first n entries are inputs, n = num_inputs, last m outputs, m=num_outputs
  private ArrayList<Double> nodes_value_list = new ArrayList<Double>();

  //array of u values calculated in forward pass
  private ArrayList<Double> u_value_list = new ArrayList<Double>();

  //3 dimensioal matrix of weights
  //wijk represents for layer i each weight from node j to node k.
  //layer 0 is input layer
  //output is final layer
  private ArrayList<ArrayList<ArrayList<Double>>> weights_list = new ArrayList<ArrayList<ArrayList<Double>>>();


  //init methods

  //needs number of init and hidden nodes to construct
  public ANN(int num_inputs, int num_outputs, int num_hidden_nodes, int num_hidden_layers, Double step_size) {
    this.num_inputs = num_inputs;
    this.num_outputs = num_outputs;
    this.num_hidden_nodes = num_hidden_nodes;
    this.num_hidden_layers = num_hidden_layers;
    this.num_hidden_nodes_per_layer = num_hidden_nodes/num_hidden_layers;
    this.step_size = step_size;

    //init info used for testing
    // System.out.println("\nNew ANN");
    // System.out.println("\ninit info: ");
    // System.out.println("inputs: "+num_inputs+"\noutputs: "+num_outputs+"\nhidden nodes: "+num_hidden_nodes+"\nhidden layers: "+num_hidden_layers+"\n");

    //check valid number of hidden nodes given
    if (num_hidden_nodes % num_hidden_layers != 0) {
      System.out.println("ERROR: Number of hidden nodes must be divisible by number of hidden layers");
      System.exit(0);
    }
    //check num_inputs, num_outputs > 1
    if (num_inputs < 1 || num_outputs < 1) {
      System.out.println("ERROR: Invalid number of inputs or outputs");
      System.exit(0);
    }

    makeNodesList();
    makeWeightsList();
    makePredictantList();
    makeuiList();

  }

  private void makeNodesList() {
    //fills nodes_value_list with place holders for input/output nodes and
    //gives biases to hidden nodes with values between -2/n and 2/n. n=num_inputs

    //0 entries for input values(incl. predictand), these will be replaced for each pass
    for(int i=0;i<num_inputs;i++) {
      nodes_value_list.add(0.0);
    }

    for(int i=num_inputs;i<num_hidden_nodes+num_inputs;i++) {
      nodes_value_list.add((-2/this.num_inputs) + (Math.random() * (((2/this.num_inputs) - (-2/this.num_inputs)) + 1)));
    }

    //add output nodes
    for (int i=0;i<=num_outputs-1;i++) {
      nodes_value_list.add(0.0);
    }

  }
  private void makeWeightsList() {
    //using nodes_value_list, construct 3-d matrix of weights

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
      predictand_list.add(1.0);
    }
  }
  private void makeuiList() {
    //list of non-input nodes new bias'
    for (int i=0;i<nodes_value_list.size()-num_inputs;i++) {
      u_value_list.add(0.0);
    }
  }

  //setters
  private void setNodeListValue(int node, double bias) {
    nodes_value_list.set(node, bias);
  }
  private void setWeightsListValue(int layer, int node_from, int node_to, Double value) {
    weights_list.get(layer).get(node_from).set(node_to,value);
  }
  private void setuListValue(int node, double value) {
    u_value_list.set(node, value);
  }
  public void setAlpha(Double value) {
    this.alpha = value;
  }
  public void setStepSize(Double value) {
    this.step_size = value;
  }

  //control methods
  public void setInputs(ArrayList<Double> data) {
    //receives array of inputs with predictants at the end

    //check for correct list size
    if (data.size() != num_inputs+predictand_list.size()) {
      System.out.println("ERROR in setInputNodes(): incorrect inputs list size");
      return;
    }
    //set input values in nodes_value_list
    for (int i=0;i<num_inputs-num_outputs;i++) {
      setNodeListValue(i, data.get(i));
    }
    //set predictand_list arrays
    for (int i=num_inputs;i<data.size();i++) {
      predictand_list.set(i-num_inputs,data.get(i));
    }
  }
  public Double getOutputValue() {
    return u_value_list.get(u_value_list.size()-1);
  }
  public void setNodeList(ArrayList<Double> nodes_value_list) {
    this.nodes_value_list = nodes_value_list;
  }
  public void setWeigthsList(ArrayList<ArrayList<ArrayList<Double>>> weights_list) {
    this.weights_list = weights_list;
  }
  public ArrayList<Double> getNodesList() {
    return this.nodes_value_list;
  }
  public ArrayList<ArrayList<ArrayList<Double>>> getWeightsList() {
    return this.weights_list;
  }
  //control testing

  //forward pass methods
  public void forwardPass() {

    //set u_value_list to current bias values
    for (int i=num_inputs;i<nodes_value_list.size();i++) {
      u_value_list.set(i-num_inputs, nodes_value_list.get(i));
    }


    //for each node that isnt input, store uj value
    for (int i=0;i<u_value_list.size();i++) {
      //set new value
      setuListValue(i, uFinder(i));
    }
  }
  private Double uFinder(int node) {
    //calculate sumValue=Sj for node j
    //Sj = SUMi(wij*uj)

    //wijk represents for layer i each weight from node j to node k.

    //check for invalid node values
    if (node > num_hidden_nodes+1) {
      System.out.println("ERROR: invalid node ID passed to sumSjuFinder.");
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


    //find which number in layer node is
    // System.out.println("nodeNumInLayer: "+nodeNumInLayer);
    int nodeNumInLayer = node % num_hidden_nodes_per_layer;

    Double Sj=0.0;
    Double uj=0.0;
    //Sj = SUMi(wij*ui)
    //if layer 1, do for each input
    if (layer==1) {
      //for each input
      for (int i=0;i<num_inputs;i++) {
            double wij = weights_list.get(layer-1).get(i).get(nodeNumInLayer);
            double ui = nodes_value_list.get(i);
            Sj = Sj + wij*ui;

      }
      //if hidden or output layer
    } else {
      //for each hidden layer node
      for (int i=0;i<num_hidden_nodes_per_layer;i++) {
            double wij = weights_list.get(layer-1).get(i).get(nodeNumInLayer);
            double ui = u_value_list.get(i+((layer-2)*num_hidden_nodes_per_layer));
            Sj = Sj + wij*ui;
      }
    }
      Sj = Sj + u_value_list.get(node);
      uj = sigmoid(Sj);

    return uj;
  }
  private Double sigmoid(Double Sj) {
    return 1/(1+(Math.exp(-Sj)));
  }

  //backward pass methods
  public void backwardPass() {
    backwardPass(false);
  }
  public void backwardPass(Boolean momentum) {
    //for each node that isnt input go backwards from output calculating  delta vales

    //init empty array of size to fit delta values of every non-input node
    ArrayList<Double> delta_values = new ArrayList<Double>();
    for (int i=0;i<nodes_value_list.size()-num_inputs;i++) {
      delta_values.add(0.0);
    }

    //find delta values
    //deltaj = wjk*deltaO*(uj(1-uj)) for hidden cell
    //deltaj = (C-uO)*(uO(1-uO)), C=correct output for output cell
    for (int i=nodes_value_list.size()-1;i>=num_inputs;i--) {
      //if output node
      if (i > nodes_value_list.size()-1-num_outputs) {
        Double deltaO;
        Double uO = u_value_list.get(i-num_inputs);
        deltaO = (predictand_list.get(0)-uO)*(uO*(1-uO));
        delta_values.set(delta_values.size()-1,deltaO);
        //if not output
      } else {
        //find which layer node is in
        int layer=1;
        for (int j=1;j<=num_hidden_layers+1;j++) {
          if (i - num_inputs < j*num_hidden_nodes_per_layer) {
            layer = j;
            break;
          }
        }


        Double wjk;
        wjk = (weights_list.get(layer).get(i-num_inputs).get(0));
        Double uj;
        uj = u_value_list.get(i-num_inputs);
        Double deltaj;
        deltaj = wjk*delta_values.get(delta_values.size()-1)*(uj*(1-uj));
        delta_values.set(i-num_inputs,deltaj);
      }
    }

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
            ui = nodes_value_list.get(j);
          }
          //else ui is in hidden layer
          else {
            ui = u_value_list.get(j+(i-1)*num_hidden_nodes_per_layer);
          }

          //find new wij value
          Double wij_new = wij + step_size * deltaj * ui;

          //if momentum is requested
          if (momentum) {
            wij_new = wij + (step_size * deltaj * ui) + (alpha * (wij_new - wij));
          }

          //set new weight
          setWeightsListValue(i,j,k,wij_new);
        }
      }
    }

    //for each bias, calculate new bias values
    // wij = wij+step_size*deltaj*ui
    //ui for bias' is alwasy 1
    for (int i=0;i<u_value_list.size();i++) {
      u_value_list.set(i, nodes_value_list.get(i+num_inputs)+step_size*delta_values.get(i));
    }

    //set nodes_value_list to new bias values
    for (int i=num_inputs;i<nodes_value_list.size();i++) {
      nodes_value_list.set(i,u_value_list.get(i-num_inputs));
    }
  }

}
