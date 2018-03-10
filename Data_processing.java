import java.util.ArrayList;
import java.io.*;
import java.util.Collections;
import java.util.Random;

class Data_processing {

  //file to be read
  String file_name;

  //min/max values in data setm, populated in standardise method
  ArrayList<Double> min_values = new ArrayList<Double>();
  ArrayList<Double> max_values = new ArrayList<Double>();

  //array of (sum of squares)^1/2 for all raw values in each column
  ArrayList<Double> sqrt_sum_of_squares = new ArrayList<Double>();

  public Data_processing(String file_name) {
    this.file_name = file_name;
  }

  //setters
  public void setFileName(String file_name) {
    this.file_name = file_name;
  }

  //public control methods
  public ArrayList<ArrayList<Double>> importData() {

    //check file_name is set
    if (file_name == null) {
      System.out.println("ERROR: file name not set. Use setFileName(String).");
      return new ArrayList<ArrayList<Double>>();
    }

    //data structure - each entry is array of 6 values stored in order:
    //{T, W, SR,	DSP,	DRH,	PanE}
    ArrayList<ArrayList<Double>> data = new ArrayList<ArrayList<Double>>();

    // This will reference file one line at a time
    String line = null;

    try {
        // FileReader reads text files in the default encoding, bufferedReader groups into lines
        FileReader fileReader = new FileReader(file_name);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        //create data entry array


        while((line = bufferedReader.readLine()) != null) {
          //if first character of line is digit i.e. if line is data entry
          if (line.length() > 1 && line.substring(0,1).matches("\\d+")) {

            ArrayList<Double> data_entry = new ArrayList<Double>();
            try{
              //identify 1st column of data
              //from [11] until blank space
              int i = 11;
              int j = 12;
              while (!line.substring(i,j).equals("	")) {
                i++;
                j++;
              }
              data_entry.add(Double.parseDouble(line.substring(11,j)));

              //here, j is value index of space between col 1 and col 2
              //so next col  index starts at j+1
              int i2 = i+2;
              int j2 = j+2;
              while (!line.substring(i2,j2).equals("	")) {
                i2++;
                j2++;
              }
              data_entry.add(Double.parseDouble(line.substring(i+1,j2)));

              int i3 = i2+2;
              int j3 = j2+2;
              while (!line.substring(i3,j3).equals("	")) {
                i3++;
                j3++;
              }
              data_entry.add(Double.parseDouble(line.substring(i2+1,j3)));

              int i4 = i3+2;
              int j4 = j3+2;
              while (!line.substring(i4,j4).equals("	")) {
                i4++;
                j4++;
              }
              data_entry.add(Double.parseDouble(line.substring(i3+1,j4)));

              int i5 = i4+2;
              int j5 = j4+2;
              while (!line.substring(i5,j5).equals("	")) {
                i5++;
                j5++;
              }
              data_entry.add(Double.parseDouble(line.substring(i4+1,j5)));

              //add final entry
              data_entry.add(Double.parseDouble(line.substring(i5+1,line.length())));

              //add entry to full structure
              data.add(data_entry);
            }
            catch(NumberFormatException ex) {
              System.out.println("importData(): Non-numeric value inputted. Data entry removed: "+line.substring(11,line.length()));
            }
          }
        }
        //close file
        bufferedReader.close();
    }
    catch(FileNotFoundException ex) {
        System.out.println("Unable to open file '"+file_name + "'");
    }
    catch(IOException ex) {
        System.out.println("Error reading file '"+ file_name + "'");
        ex.printStackTrace();
    }
    return data;
  }
  public ArrayList<ArrayList<Double>> cleanseData(ArrayList<ArrayList<Double>> data, ArrayList<Double> desired_max_values, ArrayList<Double> desired_min_values) {
    //method remove data entries which have values above/below their column max/min
    //input data as outputted frim importData(). desired_min_values, desired_max_values already
    //arrays of min/max values for each data column

    //arrayList holds indexes of items to be deleted. necessary to ensure removal does not effect
    //iterator value
    ArrayList<Integer> remove_list = new ArrayList<Integer>();

    //for each data data_entry
    for (int i=0;i<data.size();i++) {
      ArrayList<Double> data_entry = data.get(i);
      //for each column
      for (int j=0;j<data_entry.size();j++) {
        if (data_entry.get(j) < desired_min_values.get(j) || data_entry.get(j) > desired_max_values.get(j)) {
          remove_list.add(i);
          break;
        }
      }
    }
    //remove entries
    for (int i=remove_list.size()-1;i>=0;i--) {
      int index = remove_list.get(i);
      System.out.println("cleanseData(): Entry removed: "+data.get(index));
      data.remove(index);
    }

    return data;
  }
  public ArrayList<ArrayList<Double>> mixRows(ArrayList<ArrayList<Double>> data) {
    ArrayList<ArrayList<Double>> temp = new ArrayList<ArrayList<Double>>(data);
    long seed = System.nanoTime();
    Collections.shuffle(temp, new Random(seed));
    return temp;
  }
  public ArrayList<ArrayList<ArrayList<Double>>> splitData(ArrayList<ArrayList<Double>> data, Double frac_of_training, Double frac_of_validation, Double frac_of_test) {
    //returns arrayList of a set of data entries for training([0]), validation([1]) and test([2])
    //frac_of values should be in range [0,1]

    //check valid values entered
    if (frac_of_training.toString().charAt(0) != '0' || frac_of_validation.toString().charAt(0) != '0' || frac_of_test.toString().charAt(0) != '0') {
      System.out.println("ERROR: Fraction values must be between 0 - 1.");
      return new ArrayList<ArrayList<ArrayList<Double>>>();
    }

    ArrayList<ArrayList<Double>> training = new ArrayList<ArrayList<Double>>();
    ArrayList<ArrayList<Double>> validation = new ArrayList<ArrayList<Double>>();
    ArrayList<ArrayList<Double>> testing = new ArrayList<ArrayList<Double>>();
    ArrayList<ArrayList<ArrayList<Double>>> new_data = new ArrayList<ArrayList<ArrayList<Double>>>();

    //run through data splitting into given sets
    for (int i=0;i<data.size();i++) {
      //training set
      if (i < data.size() * frac_of_training) {
        training.add(data.get(i));
        //validation set
      } else if (i < data.size() * (frac_of_training + frac_of_validation)) {
        validation.add(data.get(i));
        //test set
      } else {
        testing.add(data.get(i));
      }
    }

    System.out.println("\ndata size: "+data.size()+"\ntraining size: "+training.size()+"\nvalidation size: "+validation.size()+"\ntesting size: "+testing.size());
    new_data.add(training);
    new_data.add(validation);
    new_data.add(testing);

    return new_data;
  }
  public ArrayList<ArrayList<Double>> standardise(ArrayList<ArrayList<Double>> data, Boolean standardise) {
    //takes in data and var (true = standardise, false = destandardise)

    //populate min/max arrays with first data entry
    //if standardise = false, min max values have already been calculated
    if (standardise) {
      for (int i=0;i<data.get(0).size();i++) {
        min_values.add(data.get(0).get(i));
        max_values.add(data.get(0).get(i));
      }

      //loop through data entries replacing min and max values
      for (ArrayList<Double> data_entry : data) {
        //for each data point
        for (int i=0;i<data_entry.size();i++) {
          if (data_entry.get(i) < min_values.get(i)) {
              min_values.set(i, data_entry.get(i));
          }
          if (data_entry.get(i) > max_values.get(i)) {
              max_values.set(i, data_entry.get(i));
          }
        }
      }
    }

    //loop through each data point in each data entry applying standardise function
    for (ArrayList<Double> data_entry : data) {
      for (int i=0;i<data_entry.size();i++) {
        if (standardise) {
          data_entry.set(i,standardiseFunction(i, data_entry.get(i)));
        } else {
          data_entry.set(i,deStandardiseFunction(i, data_entry.get(i)));

        }
      }
    }
    return data;
  }
  public ArrayList<ArrayList<Double>> standardiseWRTSumOfAllSquaresMain(ArrayList<ArrayList<Double>> data, Boolean standardise) {
    //init sqrt_sum_of_squares array
    if (standardise) {
      for (int i=0;i<data.get(0).size();i++) {
        sqrt_sum_of_squares.add(0.0);
      }

      //find sum of all squared column raw values
      for (ArrayList<Double> data_entry : data) {
        //for each data point
        for (int i=0;i<data_entry.size();i++) {
          sqrt_sum_of_squares.set(i,sqrt_sum_of_squares.get(i)+Math.pow(data_entry.get(i),2));
        }
      }

      //square root each columns sum
      for (int i=0;i<sqrt_sum_of_squares.size();i++) {
        sqrt_sum_of_squares.set(i,Math.pow(sqrt_sum_of_squares.get(i),0.5));
      }
    }

    //loop through each data point in each data entry applying standardise function
    for (ArrayList<Double> data_entry : data) {
      for (int i=0;i<data_entry.size();i++) {
        if (standardise) {
          data_entry.set(i,standardiseWRTSumOfAllSquares(i, data_entry.get(i)));
        } else {
          data_entry.set(i,deStandardiseWRTSumOfAllSquares(i, data_entry.get(i)));
        }
      }
    }
    return data;
  }


  //standardise sub-method
  public Double standardiseFunction(int data_point_index, Double data_point) {
    //function takes input into range [0.1,0.9] based on max and min values

    return (((data_point - min_values.get(data_point_index))/(max_values.get(data_point_index)-min_values.get(data_point_index)))*0.8 + 0.1);
  }
  public Double deStandardiseFunction(int data_point_index, Double data_point) {
    //function takes input in range [0.1,0.9] based on max and min values and returns raw values

    return ((data_point-0.1)/0.8)*(max_values.get(data_point_index)-min_values.get(data_point_index))+min_values.get(data_point_index);
  }
  public Double standardiseWRTSumOfAllSquares(int data_point_index, Double data_point) {
    return data_point / sqrt_sum_of_squares.get(data_point_index);
  }
  public Double deStandardiseWRTSumOfAllSquares(int data_point_index, Double data_point) {
    return data_point * sqrt_sum_of_squares.get(data_point_index);
  }
}
