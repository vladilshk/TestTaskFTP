

import java.util.*;

public class JSOnEditor {
    //this method receives a string with a json file as input
    // and returns a string with a list of students
    public static String studentsToString(String json){
        Map<Integer,String> students = new LinkedHashMap<>(getNamesFromJs(json));
        List<String> studentsList= new LinkedList<>();
        for (int id : students.keySet()){
            studentsList.add(students.get(id));
        }
        Collections.sort(studentsList);
        StringBuilder names = new StringBuilder();
        for (String name : studentsList){
            names.append(name);
            names.append("\n");
        }
        return names.toString();
    }

    public  static String getStudentById(String json, int id){
        Map<Integer,String> students = new LinkedHashMap<>(getNamesFromJs(json));
        return students.get(id);
    }

    public static String addStudent(String json,String student) {
        Map<Integer,String> students = new LinkedHashMap<>(getNamesFromJs(json));
        int lastId = 0;
        for(int key: students.keySet()){
            lastId = key;
        }
        students.put(lastId + 1, student);
        return jsonFromMap(students);
    }
    public static String deleteStudent(String json, int id) {
        Map<Integer,String> students = new LinkedHashMap<>(getNamesFromJs(json));
        if(students.containsKey(id)){
            students.remove(id);
            return jsonFromMap(students);
        }
        return null;
    }

    private static String jsonFromMap(Map<Integer ,String> students){
        //int id = 1;
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("\t\"students\": [\n");
        int counter = 0;
        for (int id: students.keySet()){
            counter++;
            json.append("\t\t{\n");
            json.append("\t\t\t\"id\": " + id + "\n");
            json.append("\t\t\t\"name\": \"" + students.get(id) + "\"\n");
            id++;
            if(counter != students.size()){
                json.append("\t\t},\n");
            }
            else {
                json.append("\t\t}\n");
            }
        }
        json.append("\t]\n");
        json.append("}\n");
        return json.toString();
    }

    private static Map<Integer, String> getNamesFromJs(String json){
        Map<Integer, String > names = new LinkedHashMap<>();
        int idx = 0;
        while((idx = json.indexOf("\"id\": ", idx)) > 0){
            StringBuilder id = new StringBuilder();
            idx += 6;
            while (json.charAt(idx) != '\n'){
                id.append(json.charAt(idx++));
            }

            idx = json.indexOf("\"name\": ", idx);
            StringBuilder name = new StringBuilder();
            idx += 9;
            while (json.charAt(idx) != '\"'){
                name.append(json.charAt(idx++));
            }
            names.put(Integer.valueOf(id.toString()), name.toString());
        }
        return names;
    }
}
