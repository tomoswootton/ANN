import java.util.ArrayList;

//this uses the data processing and ANN objects to train, validate and test


class Main {

  //data structure to hold un-split data for processing
  ArrayList<ArrayList<Double>> data = new ArrayList<ArrayList<Double>>();

  //data structure to hold 3 lists of training, validation and testing data
  ArrayList<ArrayList<ArrayList<Double>>> split_data = new ArrayList<ArrayList<ArrayList<Double>>>();

  Data_processing dataProcessing;

  //training/validation
  ANN ANN;
  //data structure holds nodes_value_list for each number of epochs (10000,50000,100000)
  ArrayList<ArrayList<Double>> trained_ANN_nodes;
  //data structure holds weights_list for each number of epochs (10000,50000,100000)
  ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> trained_ANN_weights;
  //data structure holds each number of epoch value
  ArrayList<Integer> epoch_amount;

  public static void main(String[] args) {
    new Main();
  }

    public Main() {
      //first, create a Data_processing instance
      dataProcessing = new Data_processing("FresnoDataCOC102Student.txt");

      //let dataProcessing know where to find text data_entry
      dataProcessing.setFileName("FresnoDataCOC102Student.txt");

      //Now we can import the data
      //this method will automatically remove any incorrectly formatted or empty values from the list
      data = dataProcessing.importData();

      //next the data is to be cleansed of outliers
      //create ArrayLists of max and min values
      ArrayList<Double> max_values = new ArrayList<Double>();
      max_values.add(70.0);     //Temperature upperbound
      max_values.add(8.5);      //windspeed upperbound
      max_values.add(800.0);    //solar radiation upperbound
      max_values.add(105.0);    //air pressure upperbound
      max_values.add(105.0);    //humidity upperbound
      max_values.add(18.0);     //pan evaporation upperbound
      ArrayList<Double> min_values = new ArrayList<Double>();
      min_values.add(-50.0);    //Temperature lowerbound
      min_values.add(0.0);      //windspeed lowerbound
      min_values.add(25.0);     //solar radiation lowerbound
      min_values.add(90.0);     //air pressure lowerbound
      min_values.add(15.0);     //humidity lowerbound
      min_values.add(-0.5);     //pan evaporation lowerbound

      //cleanse data with above max/min values
      data = dataProcessing.cleanseData(data, max_values, min_values);

      //standardise
      data = dataProcessing.standardise(data, true);

      //the data is now ready to be split into training, validation and testing sets:
      //shuffle rows
      // data = dataProcessing.mixRows(data);
      //split
      split_data = dataProcessing.splitData(data,0.6,0.2,0.2);

      //code for initial training
      int[] nums_hidden_nodes = {2, 4, 6};
      Double[] step_sizes = {0.05, 0.1, 0.2};
      //for each number of hidden nodes
      // for (int j=0;j<3;j++) {
      //   //for each step size
      //   for (int i=0;i<3;i++) {
      //     train(step_sizes[i], nums_hidden_nodes[j]);
      //   }
      // }

      //selective configuration training
      // train(0.2, 6);
      // System.out.println("\nAverage error for testing data: "+validate(0.2,6));

      validateLINEST();
    }

      //training
      public void train(Double step_size, int num_hidden_nodes) {

        //instantiate ANN
        System.out.println("\nNumber hidden nodes: "+num_hidden_nodes);
        System.out.println("Step size: "+step_size);
        ANN = new ANN(5,1,num_hidden_nodes,1, step_size);

        //for each epoch amount save nodes_value_list and weights_list data of trained network
        epoch_amount = new ArrayList<Integer>();
        // epoch_amount.add(10000);
        // epoch_amount.add(25000);
        epoch_amount.add(50000);


        //data structure holds nodes_value_list for each number of epochs (10000,50000,100000)
        trained_ANN_nodes = new ArrayList<ArrayList<Double>>();
        //data structure holds weights_list for each number of epochs (10000,50000,100000)
        trained_ANN_weights = new ArrayList<ArrayList<ArrayList<ArrayList<Double>>>>();

        //for each amount of epochs
        for (int i=0;i<epoch_amount.size();i++) {
          //reset ANN
          ANN = new ANN(5,1,num_hidden_nodes,1, step_size);
          //for each epoch
          for (int j=0;j<epoch_amount.get(i);j++) {
            //for each data entry of training data
            for (int k=0;k<split_data.get(0).size();k++) {
              ANN.setInputs(split_data.get(0).get(k));
              ANN.forwardPass();

              //bold driver code
              if (j % 100 == 0 && j > 0) {
                //save network state
                ArrayList<Double> nodes_value_list = ANN.getNodesList();
                ArrayList<ArrayList<ArrayList<Double>>> weights_list = ANN.getWeightsList();
                //set these arrays for validate method
                trained_ANN_nodes.add(ANN.getNodesList());
                trained_ANN_weights.add(ANN.getWeightsList());

                //find error before weights update
                Double error = validate(step_size, num_hidden_nodes);
                //update weights
                ANN.backwardPass(true);
                //find error after weights update
                Double new_error = validate(step_size, num_hidden_nodes);
                //if error increases, learning rate too large
                if (new_error > error) {
                  //revert network state
                  ANN.setNodeList(nodes_value_list);
                  ANN.setWeigthsList(weights_list);
                  //set new step_size
                  ANN.setStepSize(step_size / 2);
                //if new_error =< error
                } else {
                  //revert network state
                  ANN.setNodeList(nodes_value_list);
                  ANN.setWeigthsList(weights_list);
                  //increase step_size
                  ANN.setStepSize(step_size * 1.1);
                }
                //unset arrays
                trained_ANN_nodes.clear();
                trained_ANN_weights.clear();
              }

              ANN.backwardPass(true);
            }

          }

          //save trained network state
          trained_ANN_nodes.add(ANN.getNodesList());
          trained_ANN_weights.add(ANN.getWeightsList());
        }
      }

      public Double validate(Double step_size, int num_hidden_nodes) {
        //validation
        //for each epoch amount
        for (int i=0;i<epoch_amount.size();i++) {

          //reset ANN and set to the trained data
          ANN = new ANN(5,1,num_hidden_nodes,1, step_size);
          ANN.setNodeList(trained_ANN_nodes.get(i));
          ANN.setWeigthsList(trained_ANN_weights.get(i));

          //var stores sum of the errors squared
          Double sum_of_errors_squared = 0.0;

          // System.out.println("epochs: "+epoch_amount.get(i));

          //for each data entry in validation set
          for (int j=0;j<split_data.get(1).size();j++) {
            ANN.setInputs(split_data.get(1).get(j));
            ANN.forwardPass();
            //destandardise results
            Double modelled_result = dataProcessing.deStandardiseFunction(5,ANN.getOutputValue());
            Double observed_result = dataProcessing.deStandardiseFunction(5,split_data.get(1).get(j).get(5));

            //add error to sum
            sum_of_errors_squared = sum_of_errors_squared + Math.pow(modelled_result-observed_result,2);
          }

          //calculate root mean square error and output
          // System.out.println("Average error: "+Math.pow(sum_of_errors_squared/split_data.get(1).size(),0.5));
          return Math.pow(sum_of_errors_squared/split_data.get(1).size(),0.5);

        }
        return 0.0;
      }

      public void validateLINEST() {
        Double sum_of_errors_squared = 0.0;
        //for each testing data entry
        for (int j=0;j<split_data.get(2).size();j++) {
          Double modelled_result = 12.6684*split_data.get(2).get(j).get(4)
                                  + 0.197585*split_data.get(2).get(j).get(3)
                                  + 0.487398*split_data.get(2).get(j).get(2)
                                  + 0.008215*split_data.get(2).get(j).get(1)
                                  - 0.14288*split_data.get(2).get(j).get(0)
                                   -0.01943;
         Double observed_result = split_data.get(2).get(j).get(5);

          //add error to sum
          sum_of_errors_squared = sum_of_errors_squared + Math.pow(modelled_result-observed_result,2);
        }

        //calculate root mean square error and output
        System.out.println("\nAverage error for LINEST on testing dataset: "+Math.pow(sum_of_errors_squared/split_data.get(2).size(),0.5));
      }
}
