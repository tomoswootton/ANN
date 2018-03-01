import java.util.ArrayList;

class Main {

  //data structure to hold un-split data for processing
  ArrayList<ArrayList<Double>> data = new ArrayList<ArrayList<Double>>();

  //data structure to hold 3 lists of training, validation and testing data
  ArrayList<ArrayList<ArrayList<Double>>> split_data = new ArrayList<ArrayList<ArrayList<Double>>>();

  Data_processing dataProcessing;

  public static void main(String[] args) {
    new Main();
  }

    public Main() {
      //first, create a Data_processing instance
      dataProcessing = new Data_processing();

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
      data = dataProcessing.mixRows(data);
      //split
      split_data = dataProcessing.splitData(data,0.6,0.2,0.2);


      Double[] step_sizes = {0.025, 0.05, 0.1, 0.2, 0.4};
      for (int i=0;i<5;i++) {
        train(step_sizes[i]);
      }
    }
      //training
      public void train(Double step_size) {

        //instantiate ANN
        System.out.println("\nStep size: "+step_size);
        ANN ANN = new ANN(5,1,4,1, step_size);

        //for each epoch amount save nodes_value_list and weights_list data of trained network
        ArrayList<Integer> epoch_amount = new ArrayList<Integer>();
        epoch_amount.add(10000);
        epoch_amount.add(50000);
        epoch_amount.add(100000);

        //data structure holds nodes_value_list for each number of epochs (10000,50000,100000)
        ArrayList<ArrayList<Double>> trained_ANN_nodes = new ArrayList<ArrayList<Double>>();
        //data structure holds weights_list for each number of epochs (10000,50000,100000)
        ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> trained_ANN_weights = new ArrayList<ArrayList<ArrayList<ArrayList<Double>>>>();

        //for each amount of epochs
        for (int i=0;i<epoch_amount.size();i++) {
          //reset ANN
          ANN = new ANN(5,1,4,1, step_size);
          //for each epoch
          for (int j=0;j<epoch_amount.get(i);j++) {
            //for each data entry of training data
            for (int k=0;k<split_data.get(0).size();k++) {
              ANN.setInputs(split_data.get(0).get(k));
              ANN.forwardPass();
              ANN.backwardPass(true);
            }
          }
          //save trained network state
          trained_ANN_nodes.add(ANN.getNodesList());
          trained_ANN_weights.add(ANN.getWeightsList());
        }


        //validation
        //for each epoch amount
        for (int i=0;i<epoch_amount.size();i++) {

          //reset ANN and set to the trained data
          ANN = new ANN(5,1,4,1, step_size);
          ANN.setNodeList(trained_ANN_nodes.get(i));
          ANN.setWeigthsList(trained_ANN_weights.get(i));

          //var stores sum of the errors squared
          Double sum_of_errors_squared = 0.0;

          System.out.println("epochs: "+epoch_amount.get(i));

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
          System.out.println("Average error: "+Math.pow(sum_of_errors_squared/split_data.get(1).size(),0.5));

        }
      }
}
