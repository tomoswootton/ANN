import java.util.ArrayList;
import java.io.*;
import java.util.Collections;
import java.util.Random;

/**
 * This program reads from .txt file
**/
class Data_processing {

  //file to be read
  String fileName;

  //min/max values in data setm, populated in standardise method
  ArrayList<Double> min_values = new ArrayList<Double>();
  ArrayList<Double> max_values = new ArrayList<Double>();

  public static void main(String[] args) {
    new Data_processing("TestFresnoDataCOC102Student.txt");
  }

  public Data_processing(String fileName) {
    this.fileName = fileName;
    standardise(importData());
  }

  //method returns data in array
  public ArrayList<ArrayList<Double>> importData() {

    //data structure - each entry is array of 6 values stored in order:
    //{T, W, SR,	DSP,	DRH,	PanE}
    ArrayList<ArrayList<Double>> data = new ArrayList<ArrayList<Double>>();

    // This will reference file one line at a time
    String line = null;

    try {
        // FileReader reads text files in the default encoding, bufferedReader groups into lines
        FileReader fileReader = new FileReader(fileName);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        //create data entry array


        while((line = bufferedReader.readLine()) != null) {
          //if first character of line is digit i.e. if line is data entry
          if (line.length() > 1 && line.substring(0,1).matches("\\d+")) {

            ArrayList<Double> data_entry = new ArrayList<Double>();

            System.out.println(line);
            // System.out.println("9,10: "+line.substring(9,10));
            // System.out.println("10,11: "+line.substring(10,11));
            // System.out.println("11,12: "+line.substring(11,12));
            // System.out.println("12,13: "+line.substring(12,13));
            // System.out.println(line.substring(10,11).equals("	"));
            // System.out.println(line.substring(11,12).isEmpty());

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
        }
        System.out.println(data);
        //close file
        bufferedReader.close();
    }
    catch(FileNotFoundException ex) {
        System.out.println("Unable to open file '"+fileName + "'");
    }
    catch(IOException ex) {
        System.out.println("Error reading file '"+ fileName + "'");
        ex.printStackTrace();
    }
    return data;
  }

  public ArrayList<ArrayList<Double>> mixRows(ArrayList<ArrayList<Double>> data) {
    ArrayList<ArrayList<Double>> temp = new ArrayList<ArrayList<Double>>(data);
    long seed = System.nanoTime();
    Collections.shuffle(temp, new Random(seed));
    return temp;
  }

  public void standardise(ArrayList<ArrayList<Double>> data) {
    //populate min/max arrays with first data entry
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

    System.out.println("data before standardisation: "+data);

    //loop through each data point in each data entry applying standardise function
    for (ArrayList<Double> data_entry : data) {
      for (int i=0;i<data_entry.size();i++) {
        data_entry.set(i,standardiseFunction(i, data_entry.get(i)));
      }
    }
    System.out.println("data after standardisation: "+data);
  }



  //function takes input into range [0.1,0.9] based on max and min values
  private Double standardiseFunction(int data_point_index, Double data_point) {
    return (((data_point - min_values.get(data_point_index))/(max_values.get(data_point_index)-min_values.get(data_point_index)))*0.8 + 0.1);
  }

  //TODO function not tested
  private Double deStandardiseFunction(int data_point_index, Double data_point) {
    return ((data_point-0.1)/0.8)*(max_values.get(i)-min_values.get(i))+min_values.get(i);
  }
}
