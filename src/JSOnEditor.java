import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class JSOnEditor {

    private Set<String> students = new TreeSet<>();
    public JSOnEditor(){
    }
    public String createJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("\tstudents: [\n");

        json.append("\t]\n");
        json.append("}\n");

        return json.toString();
    }

    //this method receives a string with a json file as input
    // and returns a string with a list of students
    public String studentsToString(String json){
        getNamesFromJson(json);
        StringBuilder names = new StringBuilder();
        Iterator iterator = students.iterator();
        while (iterator.hasNext()){
            names.append(iterator.next());
            names.append("\n");
        }
        return names.toString();
    }

    public String getStudentById(String json, int id){
        getNamesFromJson(json);
        int idx = 0;
        String student = null;
        Iterator<String> iterator = students.iterator();
        while (iterator.hasNext()){
            idx++;
            if(idx == id){
                student = iterator.next();
                break;
            }
            iterator.next();
        }
        return student;
    }

    public String addStudent(String json,String student) {
        getNamesFromJson(json);
        students.add(student);
        return jsonFromString(students);
    }
    public String deleteStudent(String json, int id) {
        getNamesFromJson(json);
        if(id - 1 > students.size()){
            System.out.println("Error, there is no students with such id");
        }
        int idx = 0;
        Iterator<String> iterator = students.iterator();
        while (iterator.hasNext()){
            iterator.next();
            idx++;
            if(idx == id){
                iterator.remove();
                break;
            }
        }
        return jsonFromString(students);
    }

    private String jsonFromString(Set<String> students){
        int id = 1;
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("\t\"students\": [\n");

        Iterator<String> it = students.iterator();
        while (it.hasNext()){
            json.append("\t\t{\n");
            json.append("\t\t\t\"id\": " + id + "\n");
            json.append("\t\t\t\"name\": \"" + it.next() + "\"\n");
            id++;
            if(id - 1 == students.size()){
                json.append("\t\t}\n");
            }
            else {
                json.append("\t\t},\n");
            }
        }
        json.append("\t]\n");
        json.append("}\n");
        return json.toString();
    }
    private void getNamesFromJson(String json){
        Set<String> names = new TreeSet<>();
        int idx = 0;
        while((idx = json.indexOf("\"name\": ", idx + 1)) > 0){
            StringBuilder name = new StringBuilder();
            idx += 9;
            while (json.charAt(idx) != '\"'){
                name.append(json.charAt(idx++));
            }
            names.add(name.toString());
        }
        students = names;
    }
}
