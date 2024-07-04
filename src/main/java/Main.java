import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Задача 1
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");

        // Задача 2

        List<Employee> list2 = parseXML("data.xml");
//        String json1 = listToJson(list2);
        json = listToJson(list2);
        writeString(json, "data2.json");

        // Задача 3

        json = readString("new_data.json");
        list = jsonToList(json);
        list.forEach(System.out::println);

    }

    private static List<Employee> jsonToList(String str) {
        List<Employee> list = new ArrayList<>();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try {
            Object obj = new JSONParser().parse(str);
            JSONArray array = (JSONArray) obj;
            for (Object arr : array) {
                list.add(gson.fromJson(arr.toString(), Employee.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private static String readString(String filename) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s.trim());
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return sb.toString();
    }

    private static List<Employee> parseXML(String s) {
        List<Employee> l = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(s));

            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                List<String> elements = new ArrayList<>();

                if (node.getNodeName().equals("employee")) {
                    NodeList nodeList1 = node.getChildNodes();
                    for (int j = 0; j < nodeList1.getLength(); j++) {
                        Node node_ = nodeList1.item(j);
                        if (Node.ELEMENT_NODE == node_.getNodeType()) {
                            elements.add(node_.getTextContent());
                        }
                    }
                    l.add(new Employee(
                            Long.parseLong(elements.get(0)),
                            elements.get(1),
                            elements.get(2),
                            elements.get(3),
                            Integer.parseInt(elements.get(4))));

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;

    }

    private static void writeString(String str, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(str);
            file.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        String json = gson.toJson(list, listType);
        return json;
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> people = null;
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            people = csv.parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return people;

    }
}
