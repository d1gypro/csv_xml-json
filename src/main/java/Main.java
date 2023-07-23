import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        String csvFileName = "scv/data.csv";
        String jsonFileName = "json/data.json";
        String jsonFileName1 = "json/data1.json";
        String xmlFileName = "xml/data.xml";

        List<Employee> list = parseCSV(columnMapping, csvFileName);
        String json = listToJson(list);
        writeString(json, jsonFileName);

        writeString(listToJson(parseXML(xmlFileName)), jsonFileName1);
    }

    public static List<Employee> parseCSV(String[] columnMapping, String csvFileName) {

        ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
        strategy.setType(Employee.class);
        strategy.setColumnMapping(columnMapping);

        List<Employee> list = null;

        try (CSVReader reader = new CSVReader(new FileReader(csvFileName))) {

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            list = csv.parse();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        Type listType = new TypeToken<List<Employee>>() {}.getType();
        String json = gson.toJson(list, listType);

        return json;
    }

    public static void writeString(String parserString, String jsonFileName) {
        try (FileWriter file = new FileWriter(jsonFileName)) {
            file.write(parserString);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> parseXML(String xmlFileName) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse( new File(xmlFileName));

        Node node = doc.getDocumentElement();
        NodeList nodeList = node.getChildNodes();

        List<Employee> list = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node1 = nodeList.item(i);
            if (Node.ELEMENT_NODE == node1.getNodeType()) {
                Element element = (Element) node1;
                list.add(new Employee(Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent()),
                        element.getElementsByTagName("firstName").item(0).getTextContent(),
                        element.getElementsByTagName("lastName").item(0).getTextContent(),
                        element.getElementsByTagName("country").item(0).getTextContent(),
                        Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent())));
           }
        }
        return list;
    }
}
