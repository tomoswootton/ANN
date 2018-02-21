import java.util.ArrayList;
import java.io.*;
/**
 * This program reads from .txt file
**/
class Data_input {

  //each data entry is array of 6 values stored in order:
  //{T, W, SR,	DSP,	DRH,	PanE}

  ArrayList<ArrayList<Double>> data = new ArrayList<ArrayList<Double>>();


  public static void main(String[] args) {
    new Data_input();
  }

  public Data_input() {
    importData();
  }

  private void importData() {
    System.out.println("hello");
    // The name of the file to open.
    String fileName = "TestFresnoDataCOC102Student.txt";
    // This will reference one line at a time
    String line = null;

    try {
        // FileReader reads text files in the default encoding, bufferedReader groups into lines
        FileReader fileReader = new FileReader(fileName);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        //create data entry array
        ArrayList<Double> data_entry = new <ArrayList<Double>(6);


        while((line = bufferedReader.readLine()) != null) {
          //if first character of line is digit i.e. if line is data entry
          if (line.length() > 1 && line.substring(0,1).matches("\\d+")) {
            System.out.println(line);

          }
            //dont process start and end of JSON

            if(line.length() > 1) {

              // //find end of name in line
              // int j = 3;
              // while (line.charAt(j) != '\"') {
              //   j++;
              // }
              // //find name
              // String nodeName = line.substring(3,j);
              // //find number of blocks mined in timepspan
              // String numberBlocksMined = line.substring(j+3,line.length());
              // if (numberBlocksMined.charAt(numberBlocksMined.length()-1) == ',') {
              //   numberBlocksMined = numberBlocksMined.substring(0,numberBlocksMined.length()-1);
              // }
              //
              // nodeNamesList.add(nodeName);
              // hashShareList.add(Double.parseDouble(numberBlocksMined));
            }
        }
        //sum up total blocks mined
        // Double sum = 0.0;
        // for (Double hashShare : hashShareList) {
        //   sum += hashShare;
        // }
        // System.out.println("sum ="+sum);
        // //change each value in hashShare to percentage of blocks (therefore hashShare value)
        // for (int i=0;i<hashShareList.size();i++) {
        //   Double temp = Math.floor((hashShareList.get(i)/sum) * 1000) / 1000;
        //   temp.shortValue();
        //   hashShareList.set(i,temp);
        // }
        // //add each node
        // for (int i=0;i<nodeNamesList.size();i++) {
        //   addNode(nodeNamesList.get(i),Double.toString(hashShareList.get(i)));
        // }

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
  }
}
